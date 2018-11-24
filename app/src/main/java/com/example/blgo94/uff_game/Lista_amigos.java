package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista_amigos extends AppCompatActivity {

    public static final String TAG = "LISTA_AMIGOS";

    //Database Realtime do firebase
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase_2;

    String id;

    ArrayList<String> ids_amigos;

    ArrayList<Usuario> amigos;

    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        ids_amigos = new ArrayList<String>();
        amigos = new ArrayList<Usuario>();

        get_ids_amigos();
    }

    public void set_adaptador(){
        lista = (ListView) findViewById(R.id.lista_amigos);

        List_adapter_usuarios adaptador = new List_adapter_usuarios(this, R.layout.estilo_amigos_lista, amigos);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //VAI PARA A VISÃO DO PERFIL
                //PASSA O INT CASO = 0

            }
        });
    }

    public void set_ids_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            ids_amigos.add(snapshot.getKey());
        }
    }

    public void get_ids_amigos(){
        mDatabase = FirebaseDatabase.getInstance().getReference("amigos");

        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_ids_amigos(dataSnapshot);
                get_amigos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void get_amigos(){
        mDatabase_2 = FirebaseDatabase.getInstance().getReference("users");

        //Log.d(TAG, "talkei "+ ids_amigos.size());

        mDatabase_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_amigos(dataSnapshot);
                set_adaptador();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void set_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for(int i = 0; i < ids_amigos.size(); i++){
                if(snapshot.getKey().equals(ids_amigos.get(i))){
                    amigos.add(snapshot.getValue(Usuario.class));
                }
            }
            Log.d(TAG, "aqui "+ amigos);
        }
    }
}
