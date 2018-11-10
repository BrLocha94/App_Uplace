package com.example.blgo94.uff_game;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Remove_materia extends AppCompatActivity {

    //Lista
    public ListView lista;
    public TextView nome_remove;

    //Id usado como chave
    private String id;

    //Database usado
    DatabaseReference data;

    private ArrayList<String> ids_materia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_materia);

        /*
            AQUI VAI ACONTECER O SEGUINTE:
            FAÇÃ O MESMO MÉTODO DE VIZUALIZAÇÃO DA ACTIVITY ANTERIOR, PORÉM SÓ COM OS NOMES
            ESSES NOMES SERÃO USADOS PARA PEGAR A CHAVE QUE SERÁ EXCLUIDA JUNTO COM OS SEUS
            FILHOS DO DATABASE
         */

        ids_materia = new ArrayList<String>();

        lista = (ListView) findViewById(R.id.lista_remove);


        id = (String) getIntent().getStringExtra("ID_USUARIO");

        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recebe_ids_materia(dataSnapshot);
                cria_lista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void recebe_ids_materia(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            ids_materia.add(snapshot.getKey());
        }
    }

    public void cria_lista(){
        /*
            RECEBE O ids_materia E SETA ELE NUM ARRAY ADAPTER JUNTO DA LISTA
            CASO CLIQUEM NA MATERIA, ELA SERÁ EXCLUIDA DA BASE DO BANCO DE DADOS
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, ids_materia);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                remove_materia(chave);
            }
        });
    }

    public void remove_materia(String chave){
        data.child(chave).removeValue();
        Toast.makeText(getApplicationContext(),"MATERIA " + chave + " REMOVIDA", Toast.LENGTH_LONG).show();
        finish();
    }

}