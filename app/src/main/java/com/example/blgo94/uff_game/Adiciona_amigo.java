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

public class Adiciona_amigo extends AppCompatActivity {

    private DatabaseReference data;
    private DatabaseReference data_users;


    private String id;

    private ArrayList<String> pedidos;
    private ArrayList<Usuario> pessoas;

    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_amigo);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

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

            }
        });
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            pedidos.add(snapshot.getKey());
        }
    }
}
