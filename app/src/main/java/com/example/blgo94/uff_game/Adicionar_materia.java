package com.example.blgo94.uff_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Adicionar_materia extends AppCompatActivity {

    //Parametros usados na criação/edição da matéria
    protected EditText nome;
    protected EditText professor;
    protected EditText carga_horaria;
    protected CheckBox segunda;
    protected EditText local_segunda;
    protected EditText horario_segunda;
    protected CheckBox terca;
    protected EditText local_terca;
    protected EditText horario_terca;
    protected CheckBox quarta;
    protected EditText local_quarta;
    protected EditText horario_quarta;
    protected CheckBox quinta;
    protected EditText local_quinta;
    protected EditText horario_quinta;
    protected CheckBox sexta;
    protected EditText local_sexta;
    protected EditText horario_sexta;
    protected CheckBox sabado;
    protected EditText local_sabado;
    protected EditText horario_sabado;

    //Botão
    public Button cria_materia;

    //RECEBE O STRING EXTRA DO INTENT
    private String id;

    //Parametro de controle
    private boolean ok;

    //Materia a ser adicionada de fato
    private Materia materia;

    //Database usado
    DatabaseReference data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerencia_materia);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        inicializa_UI();

        cria_materia = (Button) findViewById(R.id.botao_cria_materia);

        cria_materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materia = new Materia(nome.getText().toString(), professor.getText().toString(),
                        Integer.parseInt(carga_horaria.getText().toString()));

                ok = checa_problemas();

                if(ok){
                    altera_database();
                    finish();
                }

            }
        });

    }

    public void altera_database(){

        /*
            AQUI DEVE-SE CRIAR UM OBJETO MATERIA COM TODOS OS CAMPOS NECESSÁRIOS
            DEPOIS SUBIR ESTE OBJETO PARA O DATABASE
         */

        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.child(materia.getNome()).setValue(materia);
    }

    public boolean checa_problemas(){

        //reseta os erros
        nome.setError(null);
        professor.setError(null);
        carga_horaria.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = nome.getText().toString();
        String professor_string = professor.getText().toString();
        String carga_string = carga_horaria.getText().toString();

        //checa nome da matéria
        //primeiro check: se não foi informado o nome
        if(TextUtils.isEmpty(nome_string)) {
            nome.setError(getString(R.string.erro_vazio));
            foco = nome;
            prossegue = false;
        }

        //segundo check: se o nome é muito grande
        if(!chave_tamanho(nome_string, 28)){
            nome.setError(getString(R.string.erro_tamanho));
            foco = nome;
            prossegue = false;
        }

        //checa nome do professor
        //primeiro check: se não foi informado o professor
        if(TextUtils.isEmpty(professor_string)){
            professor.setError(getString(R.string.erro_vazio));
            foco = professor;
            prossegue = false;
        }

        //segundo check: se o nome do professor contém números
        if(!chave_caracteres(professor_string)){
            professor.setError(getString(R.string.erro_numeros));
            foco = professor;
            prossegue = false;
        }

        //terceiro check: se o nome do professor é muito grandde
        if(!chave_tamanho(professor_string,28)){
            professor.setError(getString(R.string.erro_tamanho));
            foco = professor;
            prossegue = false;
        }

        //chega carga horaria
        //primeiro check: se não foi informada a carga horária
        if(TextUtils.isEmpty(carga_string)){
            carga_horaria.setError(getString(R.string.erro_vazio));
            foco = carga_horaria;
            prossegue = false;
        }

        //segundo check: se não foram informados números
        if(chave_caracteres(carga_string)){
            carga_horaria.setError(getString(R.string.erro_numeros));
            foco = carga_horaria;
            prossegue = false;
        }

        //terceiro check: se o tamanho é muito grande
        if(chave_tamanho(carga_string,3)){
            carga_horaria.setError(getString(R.string.erro_tamanho));
            foco = carga_horaria;
            prossegue = false;
        }

        boolean informou = false;


        if(segunda.isChecked()){
            informou = true;

            materia.add_dia("SEG");
            materia.add_local(local_segunda.getText().toString());
            materia.add_horario(horario_segunda.getText().toString());
        }
        if(terca.isChecked()){
            informou = true;

            materia.add_dia("TER");
            materia.add_local(local_terca.getText().toString());
            materia.add_horario(horario_terca.getText().toString());

        }
        if(quarta.isChecked()){
            informou = true;

            materia.add_dia("QUA");
            materia.add_local(local_quarta.getText().toString());
            materia.add_horario(horario_quarta.getText().toString());

        }
        if(quinta.isChecked()){
            informou = true;

            materia.add_dia("QUI");
            materia.add_local(local_quinta.getText().toString());
            materia.add_horario(horario_quinta.getText().toString());

        }
        if(sexta.isChecked()){
            informou = true;

            materia.add_dia("SEX");
            materia.add_local(local_sexta.getText().toString());
            materia.add_horario(horario_sexta.getText().toString());

        }
        if(sabado.isChecked()){
            informou = true;

            materia.add_dia("SAB");
            materia.add_local(local_sabado.getText().toString());
            materia.add_horario(horario_sabado.getText().toString());
        }


        if(!informou) {

            Toast.makeText(getApplicationContext(), "INFORME PELO MENOS UM DIA", Toast.LENGTH_LONG).show();
            prossegue = false;
        }


        if(prossegue){

            return true;
        }
        else{
            //Chama a atenção para o campo incorreto
            foco.requestFocus();
            materia.reset_arrays();
            return false;
        }
    }

    public boolean chave_caracteres(String nome){
        String numRegex   = ".*[0-9].*";
        if(nome.matches(numRegex)){
            return true;
        }
        return false;
    }

    public boolean chave_tamanho(String nome, int tamanho){
        if(nome.length() > tamanho){
            return true;
        }
        return false;
    }

    public void inicializa_UI(){
        nome = (EditText) findViewById(R.id.nome_informado);
        professor = (EditText) findViewById(R.id.professor_informado);
        carga_horaria = (EditText) findViewById(R.id.carga_informada);

        segunda = (CheckBox) findViewById(R.id.checkBoxseg);
        local_segunda = (EditText) findViewById(R.id.local_seg);
        horario_segunda = (EditText) findViewById(R.id.horario_seg);

        terca = (CheckBox) findViewById(R.id.checkBoxter);
        local_terca = (EditText) findViewById(R.id.local_ter);
        horario_terca  = (EditText) findViewById(R.id.horario_ter);

        quarta = (CheckBox) findViewById(R.id.checkBoxqua);
        local_quarta = (EditText) findViewById(R.id.local_qua);
        horario_quarta = (EditText) findViewById(R.id.horario_qua);

        quinta = (CheckBox) findViewById(R.id.checkBoxqui);
        local_quinta  = (EditText) findViewById(R.id.local_qui);
        horario_quinta  = (EditText) findViewById(R.id.horario_qui);

        sexta = (CheckBox) findViewById(R.id.checkBoxsex);
        local_sexta = (EditText) findViewById(R.id.local_sex);
        horario_sexta = (EditText) findViewById(R.id.horario_sex);

        sabado = (CheckBox) findViewById(R.id.checkBoxsab);
        local_sabado = (EditText) findViewById(R.id.local_sab);
        horario_sabado = (EditText) findViewById(R.id.horario_sab);
    }
}
