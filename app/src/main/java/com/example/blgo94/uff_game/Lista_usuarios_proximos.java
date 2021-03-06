package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class Lista_usuarios_proximos extends AppCompatActivity {

    private String id_user;

    private Localizacao loc_usuario = new Localizacao();
    private Localizacao loc_proximo = new Localizacao();

    private ArrayList<String> ids_amigos = new ArrayList<String>();

    private ArrayList<String> ids_proximos = new ArrayList<String>();

    private ArrayList<Usuario> users = new ArrayList<Usuario>();

    private double raio = 1;

    DatabaseReference data;
    DatabaseReference data_users;
    DatabaseReference data_amigos;

    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios_proximos);

        id_user = (String) getIntent().getStringExtra("ID_USUARIO");

        data_amigos = FirebaseDatabase.getInstance().getReference("amigos");

        data_amigos.child(id_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array_amigos(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        data = FirebaseDatabase.getInstance().getReference("loc");

        data.child(id_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loc_usuario = dataSnapshot.getValue(Localizacao.class);
                checa_usuarios_proximos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            ids_amigos.add(snapshot.getKey());
        }
    }

    private void checa_usuarios_proximos(){
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array(dataSnapshot);
                busca_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void busca_users(){

        data_users = FirebaseDatabase.getInstance().getReference("users");

        data_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_users(dataSnapshot);
                set_adaptador();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void set_adaptador(){
        lista = (ListView) findViewById(R.id.lista_usuarios_proximos);

        List_adapter_usuarios adaptador = new List_adapter_usuarios(this, R.layout.estilo_amigos_lista, users);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Usuario user_ref = (Usuario) parent.getItemAtPosition(position);

                Intent intent = new Intent(Lista_usuarios_proximos.this, Perfil.class);
                intent.putExtra("objeto", user_ref);
                if(valida(user_ref.getID())){
                    intent.putExtra("caso", "0");
                }
                else {
                    intent.putExtra("caso", "1");
                }
                intent.putExtra("ID_USUARIO", id_user);
                startActivity(intent);
            }
        });
    }

    private boolean valida(String alvo_id){
        for(int i = 0; i < ids_amigos.size(); i++){
            if (ids_amigos.get(i).equals(alvo_id)){
                return true;
            }
        }
        return false;
    }

    public void set_users(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for(int i = 0; i < ids_proximos.size(); i++){
                if(snapshot.getKey().equals(ids_proximos.get(i))){
                    users.add(snapshot.getValue(Usuario.class));
                }
            }
        }
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            loc_proximo = snapshot.getValue(Localizacao.class);
            if(!loc_proximo.getId().equals(loc_usuario.getId())) {
                if (distance(Double.parseDouble(loc_usuario.getLocalizacoes().get(0)), Double.parseDouble(loc_usuario.getLocalizacoes().get(1)),
                        Double.parseDouble(loc_proximo.getLocalizacoes().get(0)), Double.parseDouble(loc_proximo.getLocalizacoes().get(1)))
                        <= raio) {
                    ids_proximos.add(loc_proximo.getId());
                }
            }
        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
