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

public class Lista_provas extends AppCompatActivity {

    //RECEBE O STRING EXTRA DO INTENT
    private String id;

    //Database usado
    DatabaseReference data;
    DatabaseReference data_02;

    //Recebe as materias que o usuario está inscrito
    private ArrayList<String> array_ids_materias;
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
                array_ids_materias = new ArrayList<String>();
                set_array(dataSnapshot);
                set_provas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void set_provas(){
        data_02 = FirebaseDatabase.getInstance().getReference("provas").child(id);

        array_provas = new ArrayList<Prova>();

        for(int i = 0; i < array_ids_materias.size(); i++){
            data_02.child(array_ids_materias.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    set_array_provas(dataSnapshot);
                    carrega_lista();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void set_array_provas(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            array_provas.add(snapshot.getValue(Prova.class));
            Log.d("mimimmimimimimimimimim", "set_array_provas: " + array_provas.get(0).getData());
        }
    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_ids_materias.add(snapshot.getKey());
            Log.d("mimimimimimimimimimimi", "set_array: " + snapshot.getKey());
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
