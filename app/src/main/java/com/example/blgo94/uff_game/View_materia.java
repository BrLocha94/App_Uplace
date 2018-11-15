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
import java.util.ArrayList;
import java.util.Calendar;

public class View_materia extends AppCompatActivity {

    /*
       ESTA ACTIVITY DEVE ADICIONAR FALTAS COM OS MOTIVOS ESPECIFICADOS PELO USUARIO
       CRIE UM OBJETO FALTAS
       DENTRO DELE DEVEM TER TODAS AS ESPECIFICAÇÕES NECESSÁRIAS
       ID DO USUARIO AINDA ASSIM É NECESSÁRIO
       UMA FALTA DEVE TER SEUS CAMPOS PREENCHIDOS ANTES DE SER DEVIDAMENTE MANIPULADA
     */


    //Botao
    public Button add_falta;
    public Button add_falta_mesmo;

    //Materia recebida pelo intent
    private Materia materia;
    private String id;

    //Database usado
    DatabaseReference data;

    //Parametros de exibição da matéria
    protected TextView nome_materia;
    protected TextView nome_professor;
    protected TextView data_hora_local;
    protected TextView faltas_permitidas;
    protected TextView faltas_obtidas;

    //Edittexts para editar a falta
    protected EditText falta_data;
    protected EditText falta_motivo;
    protected EditText falta_perdida;

    public int layout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_materia);

        materia = (Materia) getIntent().getParcelableExtra("materia");
        id = (String) getIntent().getStringExtra("ID_USUARIO");

        nome_materia = (TextView) findViewById(R.id.view_nome);
        nome_professor = (TextView) findViewById(R.id.view_professor);
        data_hora_local = (TextView) findViewById(R.id.view_dia_hora_loca);
        faltas_permitidas = (TextView) findViewById(R.id.view_faltas_permitidas);
        faltas_obtidas = (TextView) findViewById(R.id.view_faltas_obtidas);

        falta_data = (EditText) findViewById(R.id.falta_data);
        falta_motivo = (EditText) findViewById(R.id.falta_motivo);
        falta_perdida = (EditText) findViewById(R.id.falta_perdida);

        set_view_materias();

    }

    public void set_view_materias(){
        nome_materia.setText(materia.getNome());
        nome_professor.setText(materia.getProfessor());
        data_hora_local.setText(materia.toString());
        faltas_permitidas.setText(Integer.toString(materia.faltas_permitidas()));
        faltas_obtidas.setText(Integer.toString(materia.total_faltas()));

        add_falta = (Button) findViewById(R.id.botao_add_falta);

        add_falta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troca_UI();
            }
        });
    }

    public void set_editar_falta(){

        falta_data = (EditText) findViewById(R.id.falta_data);
        falta_motivo = (EditText) findViewById(R.id.falta_motivo);
        falta_perdida = (EditText) findViewById(R.id.falta_perdida);

        falta_data.setText(get_data_atual());
        falta_motivo.setText("CAGUEI");
        falta_perdida.setText("Não ligo");

        add_falta_mesmo = (Button) findViewById(R.id.botao_add_falta_mesmo);

        add_falta_mesmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fdata = falta_data.getText().toString();
                String fmotivo = falta_motivo.getText().toString();
                String fperdida = falta_perdida.getText().toString();

                //Falta falta = new Falta(fdata, fmotivo, fperdida);
                /*
                ArrayList<Falta> faltas = materia.getFaltas();
                if(faltas == null){
                    faltas = new ArrayList<Falta>();
                }
                faltas.add(falta);
                materia.setFaltas(faltas);
                */

                ArrayList<String> faltas = materia.getFaltas();
                if(faltas == null){
                    faltas = new ArrayList<String>();
                }
                faltas.add(fdata);
                materia.setFaltas(faltas);

                atualiza_database();

                finish();
            }
        });

    }

    public void atualiza_database(){
        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.child(materia.getNome()).child("faltas").setValue(materia.getFaltas());
    }

    public String get_data_atual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd / MM / yyyy");

        return formato.format(calendar.getTime());
    }

    public void troca_UI(){
        if(layout == 0){
            setContentView(R.layout.editar_falta);
            layout = 1;
            set_editar_falta();
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
