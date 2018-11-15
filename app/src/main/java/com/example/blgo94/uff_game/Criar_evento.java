package com.example.blgo94.uff_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Criar_evento extends AppCompatActivity {

    public String criador;
    public String id_criador;

    TextView criador_text;
    TextView data_text;
    EditText nome_evento;
    EditText local;
    EditText descricao;

    Button botao_cria_evento;

    //Database usado
    DatabaseReference data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);

        criador = (String) getIntent().getStringExtra("nome_usuario");

        id_criador = (String) getIntent().getStringExtra("ID_USUARIO");

        criador_text = (TextView) findViewById(R.id.cria_evento_criador);
        data_text = (TextView) findViewById(R.id.cria_evento_data);
        nome_evento = (EditText) findViewById(R.id.cria_evento_nome);
        local = (EditText) findViewById(R.id.cria_evento_local);
        descricao = (EditText) findViewById(R.id.cria_evento_descricao);

        botao_cria_evento = (Button) findViewById(R.id.cria_evento_botao);

        criador_text.setText(criador);
        data_text.setText(get_data_atual());

        botao_cria_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento evento = new Evento(criador,nome_evento.getText().toString(),data_text.getText().toString(),
                        descricao.getText().toString(),local.getText().toString(), id_criador);
                data = FirebaseDatabase.getInstance().getReference("events");
                data.child(evento.getNome()).setValue(evento);

                Toast.makeText(Criar_evento.this, "Evento criado com sucesso", Toast.LENGTH_SHORT).show();

                finish();
            }
        });

    }

    public String get_data_atual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd / MM / yyyy");

        return formato.format(calendar.getTime());
    }
}
