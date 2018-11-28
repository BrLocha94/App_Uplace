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
    protected EditText local;
    protected EditText carga_horaria;
    protected CheckBox segunda;
    protected EditText horario_segunda_i;
    protected EditText horario_segunda_f;
    protected CheckBox terca;
    protected EditText horario_terca_i;
    protected EditText horario_terca_f;
    protected CheckBox quarta;
    protected EditText horario_quarta_i;
    protected EditText horario_quarta_f;
    protected CheckBox quinta;
    protected EditText horario_quinta_i;
    protected EditText horario_quinta_f;
    protected CheckBox sexta;
    protected EditText horario_sexta_i;
    protected EditText horario_sexta_f;
    protected CheckBox sabado;
    protected EditText horario_sabado_i;
    protected EditText horario_sabado_f;

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

        cria_materia = (Button) findViewById(R.id.botao_gerencia_materia_cria);

        cria_materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        local.setError(null);
        carga_horaria.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = nome.getText().toString();
        String professor_string = professor.getText().toString();
        String carga_string = carga_horaria.getText().toString();
        String local_string = local.getText().toString();


        //checa nome da matéria
        //primeiro check: se não foi informado o nome
        if(TextUtils.isEmpty(nome_string)) {
            nome.setError(getString(R.string.erro_vazio));
            foco = nome;
            prossegue = false;
        }

        //segundo check: se o nome é muito grande
        if(chave_tamanho(nome_string, 32)){
            nome.setError(getString(R.string.erro_tamanho));
            foco = nome;
            prossegue = false;
        }

        //checa local
        //primeiro check: se não foi informado o local
        if(TextUtils.isEmpty(local_string)){
            local.setError(getString(R.string.erro_vazio));
            foco = local;
            prossegue = false;
        }

        //segundo check: se o local é muito grande
        if(chave_tamanho(local_string, 20)){
            local.setError(getString(R.string.erro_tamanho_local));
            foco = local;
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
        if(chave_caracteres(professor_string)){
            professor.setError(getString(R.string.erro_numeros));
            foco = professor;
            prossegue = false;
        }

        //terceiro check: se o nome do professor é muito grandde
        if(chave_tamanho(professor_string,32)){
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

        /*
        //segundo check: se não foram informados números
        if(chave_caracteres(carga_string)){
            carga_horaria.setError(getString(R.string.erro_numeros));
            foco = carga_horaria;
            prossegue = false;
        }
        */

        //terceiro check: se o tamanho é muito grande
        if(chave_tamanho(carga_string,3)){
            carga_horaria.setError(getString(R.string.erro_tamanho_ch));
            foco = carga_horaria;
            prossegue = false;
        }



        boolean informou = false;

        if(prossegue){

            horario_segunda_i.setError(null);
            horario_segunda_f.setError(null);

            horario_terca_i.setError(null);
            horario_terca_f.setError(null);

            horario_quarta_i.setError(null);
            horario_quarta_f.setError(null);

            horario_quinta_i.setError(null);
            horario_quinta_f.setError(null);

            horario_sexta_i.setError(null);
            horario_sexta_f.setError(null);

            horario_sabado_i.setError(null);
            horario_sabado_f.setError(null);

            materia = new Materia(nome.getText().toString(), professor.getText().toString(),
                    local.getText().toString(), Integer.parseInt(carga_horaria.getText().toString()));
            if(segunda.isChecked()){
                informou = true;

                //segunda_i
                if(TextUtils.isEmpty(horario_segunda_i.getText().toString())){
                    horario_segunda_i.setError(getString(R.string.erro_vazio));
                    foco = horario_segunda_i;
                    prossegue = false;
                }

                //segunda_f
                if(TextUtils.isEmpty(horario_segunda_f.getText().toString())){
                    horario_segunda_f.setError(getString(R.string.erro_vazio));
                    foco = horario_segunda_f;
                    prossegue = false;
                }

                materia.add_dia("SEG");
                materia.add_horario(horario_segunda_i.getText().toString() + " ~ " + horario_segunda_f.getText().toString());
            }
            if(terca.isChecked()){
                informou = true;

                //terça_i
                if(TextUtils.isEmpty(horario_terca_i.getText().toString())){
                    horario_terca_i.setError(getString(R.string.erro_vazio));
                    foco = horario_terca_i;
                    prossegue = false;
                }

                //terça_f
                if(TextUtils.isEmpty(horario_terca_f.getText().toString())){
                    horario_terca_f.setError(getString(R.string.erro_vazio));
                    foco = horario_terca_f;
                    prossegue = false;
                }

                materia.add_dia("TER");
                materia.add_horario(horario_terca_i.getText().toString() + " ~ " + horario_terca_f.getText().toString());

            }
            if(quarta.isChecked()){
                informou = true;

                //quarta_i
                if(TextUtils.isEmpty(horario_quarta_i.getText().toString())){
                    horario_quarta_i.setError(getString(R.string.erro_vazio));
                    foco = horario_quarta_i;
                    prossegue = false;
                }

                //quarta_f
                if(TextUtils.isEmpty(horario_quarta_f.getText().toString())){
                    horario_quarta_f.setError(getString(R.string.erro_vazio));
                    foco = horario_quarta_f;
                    prossegue = false;
                }

                materia.add_dia("QUA");
                materia.add_horario(horario_quarta_i.getText().toString() + " ~ " + horario_quarta_f.getText().toString());

            }
            if(quinta.isChecked()){
                informou = true;

                //quinta_i
                if(TextUtils.isEmpty(horario_quinta_i.getText().toString())){
                    horario_quinta_i.setError(getString(R.string.erro_vazio));
                    foco = horario_quinta_i;
                    prossegue = false;
                }

                //quinta_f
                if(TextUtils.isEmpty(horario_quinta_f.getText().toString())){
                    horario_quinta_f.setError(getString(R.string.erro_vazio));
                    foco = horario_quinta_f;
                    prossegue = false;
                }

                materia.add_dia("QUI");
                materia.add_horario(horario_quinta_i.getText().toString() + " ~ " + horario_quinta_f.getText().toString());

            }
            if(sexta.isChecked()){
                informou = true;

                //sexta_i
                if(TextUtils.isEmpty(horario_sexta_i.getText().toString())){
                    horario_sexta_i.setError(getString(R.string.erro_vazio));
                    foco = horario_sexta_i;
                    prossegue = false;
                }

                //sexta_i
                if(TextUtils.isEmpty(horario_sexta_f.getText().toString())){
                    horario_sexta_f.setError(getString(R.string.erro_vazio));
                    foco = horario_sexta_f;
                    prossegue = false;
                }

                materia.add_dia("SEX");
                materia.add_horario(horario_sexta_i.getText().toString() + " ~ " + horario_sexta_f.getText().toString());

            }
            if(sabado.isChecked()){
                informou = true;

                //sabado_i
                if(TextUtils.isEmpty(horario_sabado_i.getText().toString())){
                    horario_sabado_i.setError(getString(R.string.erro_vazio));
                    foco = horario_sabado_i;
                    prossegue = false;
                }

                //sabado_f
                if(TextUtils.isEmpty(horario_sabado_f.getText().toString())){
                    horario_sabado_f.setError(getString(R.string.erro_vazio));
                    foco = horario_sabado_f;
                    prossegue = false;
                }

                materia.add_dia("SAB");
                materia.add_horario(horario_sabado_i.getText().toString() + " ~ " + horario_sabado_f.getText().toString());
            }


            if(!informou) {

                Toast.makeText(getApplicationContext(), R.string.informe_um_dia , Toast.LENGTH_LONG).show();
                prossegue = false;
            }

        }

        if(prossegue){

            return true;
        }
        else{
            //Chama a atenção para o campo incorreto
            foco.requestFocus();
            //materia.reset_arrays();
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


        nome = (EditText) findViewById(R.id.et_gerencia_materia_nome);
        professor = (EditText) findViewById(R.id.ed_gerencia_materia_professor);
        local = (EditText) findViewById(R.id.ed_gerencia_localiz);
        carga_horaria = (EditText) findViewById(R.id.ed_gerencia_materia_ch);

        segunda = (CheckBox) findViewById(R.id.checkBoxseg);
        horario_segunda_i = (EditText) findViewById(R.id.horarioi_seg);
        horario_segunda_f = (EditText) findViewById(R.id.horariof_seg);

        terca = (CheckBox) findViewById(R.id.checkBoxter);
        horario_terca_i = (EditText) findViewById(R.id.horarioi_ter);
        horario_terca_f  = (EditText) findViewById(R.id.horariof_ter);

        quarta = (CheckBox) findViewById(R.id.checkBoxqua);
        horario_quarta_i = (EditText) findViewById(R.id.horarioi_qua);
        horario_quarta_f = (EditText) findViewById(R.id.horariof_qua);

        quinta = (CheckBox) findViewById(R.id.checkBoxqui);
        horario_quinta_i  = (EditText) findViewById(R.id.horarioi_qui);
        horario_quinta_f  = (EditText) findViewById(R.id.horariof_qui);

        sexta = (CheckBox) findViewById(R.id.checkBoxsex);
        horario_sexta_i = (EditText) findViewById(R.id.horarioi_sex);
        horario_sexta_f = (EditText) findViewById(R.id.horariof_sex);

        sabado = (CheckBox) findViewById(R.id.checkBoxsab);
        horario_sabado_i = (EditText) findViewById(R.id.horarioi_sab);
        horario_sabado_f = (EditText) findViewById(R.id.horariof_sab);

    }
}
