package com.example.blgo94.uff_game;

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

public class Lista_provas extends AppCompatActivity {

    //RECEBE O STRING EXTRA DO INTENT
    private String id;

    //Database usado
    DatabaseReference data;

    //Recebe as materias que o usuario está inscrito
    private ArrayList<Prova> array_provas;

    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_provas);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        lista = (ListView) findViewById(R.id.lista_provas);

        data = FirebaseDatabase.getInstance().getReference("provas").child(id);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_provas = new ArrayList<Prova>();
                set_array(dataSnapshot);
                carrega_lista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_provas.add(snapshot.getValue(Prova.class));
        }
    }

    private void carrega_lista(){

        List_adapter_provas adapter = new List_adapter_provas(this, R.layout.estilo_lista_provas, array_provas);

        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


    }
}
