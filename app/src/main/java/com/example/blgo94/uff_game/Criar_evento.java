package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    DatabaseReference data_atualizacao;

    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);

        criador = (String) getIntent().getStringExtra("nome_usuario");

        id_criador = (String) getIntent().getStringExtra("ID_USUARIO");

        get_usuario(id_criador);

        criador_text = (TextView) findViewById(R.id.et_cria_evento_org);
        data_text = (TextView) findViewById(R.id.et_cria_evento_data);
        nome_evento = (EditText) findViewById(R.id.et_cria_evento_nome);
        local = (EditText) findViewById(R.id.et_cria_evento_local);
        descricao = (EditText) findViewById(R.id.et_cria_evento_desc);

        botao_cria_evento = (Button) findViewById(R.id.botao_cria_evento);

        criador_text.setText(criador);
        data_text.setText(get_data_atual());

        botao_cria_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Evento evento = new Evento(criador,nome_evento.getText().toString(),data_text.getText().toString(),
                        descricao.getText().toString(),local.getText().toString(), id_criador);
                data = FirebaseDatabase.getInstance().getReference("events");
                data.child(evento.getNome()).setValue(evento);

                Toast.makeText(Criar_evento.this, R.string.evento_criado , Toast.LENGTH_SHORT).show();

                data_atualizacao = FirebaseDatabase.getInstance().getReference("atualizacao");

                data_atualizacao.child(id_criador).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Atualizacoes ata = dataSnapshot.getValue(Atualizacoes.class);
                    ata.add_info("Criou o evento " + nome_evento.getText().toString() + " " + get_data_atual() + " - " + get_hora_atual());
                        data_atualizacao.child(id_criador).setValue(ata);

                        Modfica_usuario mod = new Modfica_usuario(id_criador, user);
                        mod.somente_add_score(10);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                finish();
            }
        });


    }

    private void get_usuario(String id){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("users");

        data.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = (dataSnapshot.getValue(Usuario.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String get_hora_atual(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");

        return formato.format(calendar.getTime());
    }

    public String get_data_atual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        return formato.format(calendar.getTime());
    }
}
