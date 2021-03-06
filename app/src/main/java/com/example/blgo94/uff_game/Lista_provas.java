package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista_provas extends AppCompatActivity {

    //RECEBE O STRING EXTRA DO INTENT
    private String id_usuario;

    //Database usado
    DatabaseReference data;
    DatabaseReference data_02;

    //Recebe as materias que o usuario está inscrito
    private ArrayList<String> array_ids_materias;
    private ArrayList<Prova> array_provas;

    private ListView lista;
    private TextView mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_provas);

        id_usuario = (String) getIntent().getStringExtra("ID_USUARIO");

        lista = (ListView) findViewById(R.id.lista_provas);

        mensagem = (TextView) findViewById(R.id.lista_provas_mensagem);

        data = FirebaseDatabase.getInstance().getReference("provas").child(id_usuario);

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
        data_02 = FirebaseDatabase.getInstance().getReference("provas").child(id_usuario);

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
            Log.d("mimimimimimimimimimimi", "set_array: " + snapshot.getValue(Prova.class).getId() + " " + snapshot.getValue(Prova.class).getNome());
        }
    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_ids_materias.add(snapshot.getKey());
        }
    }

    private void carrega_lista(){

        if(array_provas.size() > 0){
            mensagem.setVisibility(View.GONE);
        }

        List_adapter_provas adapter = new List_adapter_provas(this, R.layout.estilo_lista_provas, array_provas);

        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = array_provas.get(position);

                Intent intent = new Intent(Lista_provas.this, Edita_prova.class);
                intent.putExtra("ID_USUARIO", id_usuario);
                intent.putExtra("position", Integer.toString(position));
                intent.putExtra("prova", prova);

                startActivity(intent);
            }
        });


    }
}
