package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

                boolean ok = checa_problemas();

                if(ok) {
                    Evento evento = new Evento(criador, nome_evento.getText().toString(), data_text.getText().toString(),
                            descricao.getText().toString(), local.getText().toString(), id_criador);
                    data = FirebaseDatabase.getInstance().getReference("events");
                    data.child(evento.getNome()).setValue(evento);

                    Toast.makeText(Criar_evento.this, R.string.evento_criado, Toast.LENGTH_SHORT).show();

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

    public boolean checa_problemas(){
        //falta ver o tamanho máximo das strings
        //reseta os erros
        nome_evento.setError(null);
        local.setError(null);
        descricao.setError(null);


        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = nome_evento.getText().toString();
        String descricao_string = descricao.getText().toString();
        String local_string = local.getText().toString();


        //checa nome
        //primeiro check: se não foi informado o nome
        if(TextUtils.isEmpty(nome_string)) {
            nome_evento.setError(getString(R.string.erro_vazio));
            foco = nome_evento;
            prossegue = false;
        }

        //segundo check: se o nome é muito grande
        if(!chave_tamanho(nome_string, 32)){
            nome_evento.setError(getString(R.string.erro_tamanho));
            foco = nome_evento;
            prossegue = false;
        }

        //checa local
        //primeiro check: se não foi informado o local
        if(TextUtils.isEmpty(local_string)){
            local.setError(getString(R.string.erro_vazio));
            foco = local;
            prossegue = false;
        }

        //segundo check: se o nome é muito grande
        if(!chave_tamanho(local_string, 50)){
            local.setError(getString(R.string.erro_tamanho_evento_local));
            foco = local;
            prossegue = false;
        }



        //checa descricao
        //primeiro check: se não foi informada descricao
        if(TextUtils.isEmpty(descricao_string)){
            descricao.setError(getString(R.string.erro_vazio));
            foco = descricao;
            prossegue = false;
        }

        //segundo check: se a descricao é muito grandde
        if(!chave_tamanho(descricao_string,140)){
            descricao.setError(getString(R.string.erro_tamanho_evento_descricao));
            foco = descricao;
            prossegue = false;
        }


        if(prossegue){

            return true;
        }
        else{
            //Chama a atenção para o campo incorreto
            foco.requestFocus();
            return false;
        }
    }

    public boolean chave_tamanho(String nome, int tamanho){
        if(nome.length() > tamanho){
            return true;
        }
        return false;
    }
}
