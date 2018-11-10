package com.example.blgo94.uff_game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Editar_perfil extends AppCompatActivity {

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    //Usuário logado
    public Usuario usuario;

    //Edittexts da edição do perfil
    private EditText perfil_nome;
    private EditText perfil_curso;

    //Botao
    private Button edita_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        usuario = (Usuario) getIntent().getParcelableExtra("objeto");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        perfil_nome = (EditText) findViewById(R.id.perfil_nome_informado);
        perfil_curso = (EditText) findViewById(R.id.perfil_curso_informado);

        edita_perfil = (Button) findViewById(R.id.botao_perfil);

        edita_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setUser_name(perfil_nome.getText().toString());
                usuario.setCourse(perfil_curso.getText().toString());

                mDatabase.child(usuario.getID()).setValue(usuario);

                finish();
            }
        });

    }
}
