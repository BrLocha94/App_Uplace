package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class View_materia extends AppCompatActivity {

    //Botao
    public Button add_falta;
    public Button add_prova_mesmo;
    public Button add_prova;

    //Materia recebida pelo intent
    private Materia materia;
    private String id;

    //Database usado
    DatabaseReference data;

    ArrayList<Prova> provas;

    //Parametros de exibição da matéria
    //protected TextView nome_materia;
    protected TextView nome_professor;
    protected TextView data_hora_local;
    protected TextView carga_horaria;
    protected TextView faltas_permitidas;
    protected TextView faltas_obtidas;

    //Edittexts para editar a falta
    protected EditText prova_data;
    protected EditText prova_materia;
    protected EditText prova_nota;

    public int layout = 0;

    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_materia);


        materia = (Materia) getIntent().getParcelableExtra("materia");
        id = (String) getIntent().getStringExtra("ID_USUARIO");

        setTitle(materia.getNome());

        nome_professor = (TextView) findViewById(R.id.tv_materia_professor);
        data_hora_local = (TextView) findViewById(R.id.tv_materia_horario);
        carga_horaria = (TextView) findViewById(R.id.tv_materia_ch);
        faltas_permitidas = (TextView) findViewById(R.id.tv_materia_numero_faltas);
        faltas_obtidas = (TextView) findViewById(R.id.tv_materia_numero_faltasf);

        lista = (ListView) findViewById(R.id.lista_post);

        set_view_materias();

        ArrayList<String> faltas = materia.getFaltas();

        if(faltas != null) {

            List_adapter_faltas adaptador = new List_adapter_faltas(this, R.layout.estilo_post_it, materia.getFaltas());

            lista.setAdapter(adaptador);
        }
    }

    public void set_view_materias(){
        nome_professor.setText(materia.getProfessor());
        data_hora_local.setText(materia.get_format_local_hora());
        carga_horaria.setText(materia.get_format_carga());
        faltas_permitidas.setText(Integer.toString(materia.faltas_permitidas()));
        faltas_obtidas.setText(Integer.toString(materia.total_faltas()));


        add_falta = (Button) findViewById(R.id.b_materia_nova_falta);

        add_falta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adiciona_falta();
            }
        });

        add_prova = (Button) findViewById(R.id.b_materia_nova_prova);

        add_prova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troca_UI();
            }
        });

    }

    public void adiciona_falta(){
        ArrayList<String> faltas = materia.getFaltas();
        if(faltas == null){
            faltas = new ArrayList<String>();
        }
        faltas.add(get_data_atual());
        materia.setFaltas(faltas);

        atualiza_database_faltas();

        finish();
    }

    public void set_editar_prova(){

        prova_data = (EditText) findViewById(R.id.et_edita_falta_data);
        prova_materia = (EditText) findViewById(R.id.ed_edita_falta_nome);
        prova_nota = (EditText) findViewById(R.id.ed_edita_falta_nota);

        prova_data.setText(get_data_atual());
        prova_materia.setText("CAGUEI");
        prova_nota.setText("0.0");

        add_prova_mesmo = (Button) findViewById(R.id.botao_add_prova_mesmo);

        add_prova_mesmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Prova prova = new Prova(materia.getNome(), prova_materia.getText().toString(),
                        prova_data.getText().toString(), prova_nota.getText().toString());

                atualiza_database_provas(prova);


            }
        });

    }

    public void atualiza_database_provas(final Prova prova){
        data = FirebaseDatabase.getInstance().getReference("provas").child(id).child(materia.getNome());

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                provas = new ArrayList<Prova>();
                set_array_provas(dataSnapshot);
                provas.add(prova);
                data.setValue(provas);
                Toast.makeText(View_materia.this, R.string.prova_adicionada , Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_array_provas(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            provas.add(snapshot.getValue(Prova.class));
        }
    }

    public void atualiza_database_faltas(){
        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.child(materia.getNome()).child("faltas").setValue(materia.getFaltas());
    }

    public String get_data_atual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        return formato.format(calendar.getTime());
    }

    public void troca_UI(){
        if(layout == 0){
            setContentView(R.layout.editar_falta);
            layout = 1;
            set_editar_prova();
        }
        else if(layout == 1){
            setContentView(R.layout.view_materia);
            layout = 0;
            set_view_materias();
        }
    }

    public void onBackPressed(){
        if(layout == 1){
            troca_UI();
        }
        else{
            finish();
        }
    }
}
