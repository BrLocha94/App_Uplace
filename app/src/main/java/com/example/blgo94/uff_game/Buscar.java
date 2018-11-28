package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Buscar extends AppCompatActivity {

    private String id_usuario;

    private EditText caixa;
    private ListView lista;

    private String busca;
    private String check;

    private ArrayList<Usuario> users;
    private ArrayList<String> ids_amigos;

    private DatabaseReference data;
    private DatabaseReference data_amigos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        id_usuario = (String) getIntent().getStringExtra("ID_USUARIO");

        caixa = (EditText) findViewById(R.id.et_busca);
        lista = (ListView) findViewById(R.id.lista_busca);

        ids_amigos = new ArrayList<String>();

        data_amigos = FirebaseDatabase.getInstance().getReference("amigos");

        data_amigos.child(id_usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array_amigos(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        caixa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                busca = caixa.getText().toString();
                if(!TextUtils.isEmpty(busca)){
                    busca_usuario(busca);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void busca_usuario(String texto){

        users = new ArrayList<Usuario>();

        data = FirebaseDatabase.getInstance().getReference("users");

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array(dataSnapshot);
                set_adaptador();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            check = snapshot.getKey();
            if(check.contains(busca)){
                users.add(snapshot.getValue(Usuario.class));
            }
        }
    }

    public void set_adaptador(){

        List_adapter_usuarios adaptador = new List_adapter_usuarios(this, R.layout.estilo_amigos_lista, users);

        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Usuario user_ref = (Usuario) parent.getItemAtPosition(position);

                Intent intent = new Intent(Buscar.this, Perfil.class);
                intent.putExtra("objeto", user_ref);
                if(valida(user_ref.getID())){
                    intent.putExtra("caso", "0");
                }
                else if(user_ref.getID().equals(id_usuario)){
                    intent.putExtra("caso", "0");
                }
                else {
                    intent.putExtra("caso", "1");
                }
                intent.putExtra("ID_USUARIO", id_usuario);
                startActivity(intent);

            }
        });
    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            ids_amigos.add(snapshot.getKey());
        }
    }

    private boolean valida(String alvo_id){
        for(int i = 0; i < ids_amigos.size(); i++){
            if (ids_amigos.get(i).equals(alvo_id)){
                return true;
            }
        }
        return false;
    }
}
