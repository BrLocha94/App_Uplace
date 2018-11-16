package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista_usuarios_proximos extends AppCompatActivity {

    private String id;

    private Localizacao loc_usuario = new Localizacao();
    private Localizacao loc_proximos = new Localizacao();

    private ArrayList<Usuario> users = new ArrayList<Usuario>();

    DatabaseReference data;
    DatabaseReference data_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios_proximos);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        data = FirebaseDatabase.getInstance().getReference("loc");

        data.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loc_usuario = dataSnapshot.getValue(Localizacao.class);
                //checa_usuarios_proximos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checa_usuarios_proximos(){

    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
    //        loc.add(snapshot.getValue().toString());
        }
    }


}
