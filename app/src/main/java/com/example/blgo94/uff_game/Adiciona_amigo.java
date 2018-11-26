package com.example.blgo94.uff_game;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Adiciona_amigo extends AppCompatActivity {

    private DatabaseReference data;
    private DatabaseReference data_users;
    private DatabaseReference data_add_amigo;
    private DatabaseReference data_atualizacao;


    private String id;

    private ArrayList<String> pedidos;
    private ArrayList<String> pedidos_amigo;
    private ArrayList<Usuario> pessoas;
    private ArrayList<Amigo> amigos;

    private ListView lista;

    private Usuario usuario;
    private Usuario user_amigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_amigo);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        usuario = (Usuario) getIntent().getParcelableExtra("objeto");

        data = FirebaseDatabase.getInstance().getReference("req_amigo");

        data.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos = new ArrayList<String>();
                preenche_array(dataSnapshot);
                get_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void get_users(){
        data_users = FirebaseDatabase.getInstance().getReference("users");

        pessoas = new ArrayList<Usuario>();

        for(int i = 0; i < pedidos.size(); i++){
            data_users.child(pedidos.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pessoas.add(dataSnapshot.getValue(Usuario.class));
                    set_adaptador();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void set_adaptador(){
        lista = (ListView) findViewById(R.id.lista_pedidos_amizades);

        List_adapter_usuarios adaptador = new List_adapter_usuarios(this, R.layout.estilo_amigos_lista, pessoas);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                set_user((Usuario) parent.getItemAtPosition(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(Adiciona_amigo.this);
                builder.setTitle( getString(R.string.deseja_adicionar) + user_amigo.getUser_name() + getString(R.string.aos_seus_amigos) );

                //if the response is positive in the alert
                builder.setPositiveButton( R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Adiciona
                        add_amigo();

                    }
                });

                //if response is negative nothing is being done
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //creating and displaying the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void add_amigo(){
        data_add_amigo = FirebaseDatabase.getInstance().getReference("amigos");

        data_add_amigo.child(user_amigo.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amigos = new ArrayList<Amigo>();
                preenche_array_amigos(dataSnapshot);
                amigos.add(new Amigo(usuario.getID(), usuario.getUser_name(), usuario.getCourse()));
                grava_amigos_database_amigo();

                Modfica_usuario mod = new Modfica_usuario(user_amigo.getID(), user_amigo);
                mod.somente_add_score(5);

                add_amigo_02();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void grava_amigos_database_amigo(){
        for(int i = 0; i < amigos.size(); i++){
            data_add_amigo.child(user_amigo.getID()).child(amigos.get(i).getId()).setValue(amigos.get(i));
        }
    }

    private void add_amigo_02(){
        data_add_amigo = FirebaseDatabase.getInstance().getReference("amigos");

        data_add_amigo.child(usuario.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amigos = new ArrayList<Amigo>();
                preenche_array_amigos(dataSnapshot);
                amigos.add(new Amigo(user_amigo.getID(), user_amigo.getUser_name(), user_amigo.getCourse()));
                grava_amigos_database();

                Modfica_usuario mod = new Modfica_usuario(usuario.getID(), usuario);
                mod.somente_add_score(5);

                Toast.makeText(Adiciona_amigo.this, R.string.voces_sao_amigos , Toast.LENGTH_SHORT);
                remove_req();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void grava_amigos_database(){
        for(int i = 0; i < amigos.size(); i++){
            data_add_amigo.child(usuario.getID()).child(amigos.get(i).getId()).setValue(amigos.get(i));
        }
    }


    private void remove_req(){
        data.child(user_amigo.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos_amigo = new ArrayList<String>();
                preenche_array_pedidos_amigo(dataSnapshot);
                if(pedidos_amigo.size() > 0){
                    checa_pedidos_amigo();
                }
                checa_pedidos();
                //finish();
                atualizacao_amigo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void atualizacao_amigo(){
        data_atualizacao = FirebaseDatabase.getInstance().getReference("atualizacao");

        data_atualizacao.child(user_amigo.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Atualizacoes ata = dataSnapshot.getValue(Atualizacoes.class);
                ata.add_info(getString(R.string.comecou_amizade) + " " + usuario.getUser_name());
                data_atualizacao.child(user_amigo.getID()).setValue(ata);

                atualizacao_user();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void atualizacao_user(){
        data_atualizacao = FirebaseDatabase.getInstance().getReference("atualizacao");

        data_atualizacao.child(usuario.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Atualizacoes ata = dataSnapshot.getValue(Atualizacoes.class);
                ata.add_info(getString(R.string.comecou_amizade) + " " + user_amigo.getUser_name());
                data_atualizacao.child(usuario.getID()).setValue(ata);

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_user(Usuario user){
        this.user_amigo = user;
    }

    private void checa_pedidos(){
        for(int i = 0; i < pedidos.size(); i++){
            if(pedidos.get(i).equals(user_amigo.getID())){
                pedidos.remove(i);
                data.child(usuario.getID()).setValue(pedidos);
                break;
            }
        }
    }

    private void checa_pedidos_amigo(){
        for(int i = 0; i < pedidos_amigo.size(); i++){
            if(pedidos_amigo.get(i).equals(usuario.getID())){
                pedidos_amigo.remove(i);
                data.child(user_amigo.getID()).setValue(pedidos_amigo);
                break;
            }
        }
    }

    private void preenche_array_pedidos_amigo(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            pedidos_amigo.add(snapshot.getKey());
        }
    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            amigos.add(snapshot.getValue(Amigo.class));
        }
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            pedidos.add(snapshot.getKey());
        }
    }
}
