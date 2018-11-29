package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
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
import java.util.List;

public class View_materia extends AppCompatActivity {

    //Botao
    public Button add_falta;
    public Button add_prova_mesmo;
    public Button add_prova;
    public Button edita_materia;
    public Button cria_materia;

    //Materia recebida pelo intent
    private Materia materia;
    private Materia nova_materia;
    private String id;

    //Database usado
    DatabaseReference data;

    ArrayList<Prova> provas;

    //Parametros de exibição da matéria
    //protected TextView nome_materia;
    protected TextView nome_professor;
    protected TextView data_hora_local;
    protected TextView carga_horaria_materia;
    protected TextView faltas_permitidas;
    protected TextView faltas_obtidas;

    //Edittexts para editar a prova
    protected EditText prova_data;
    protected EditText prova_materia;
    protected EditText prova_nota;
    protected EditText prova_nome;

    //Parametros para editar a matéria
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

    public int layout = 0;

    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_materia);


        materia = (Materia) getIntent().getParcelableExtra("materia");
        id = (String) getIntent().getStringExtra("ID_USUARIO");

        setTitle(materia.getNome());

        set_view_materias();
    }

    public void set_view_materias(){

        nome_professor = (TextView) findViewById(R.id.tv_materia_professor);
        data_hora_local = (TextView) findViewById(R.id.tv_materia_horario);
        carga_horaria_materia = (TextView) findViewById(R.id.tv_materia_ch);
        faltas_permitidas = (TextView) findViewById(R.id.tv_materia_numero_faltas);
        faltas_obtidas = (TextView) findViewById(R.id.tv_materia_numero_faltasf);

        lista = (ListView) findViewById(R.id.lista_post);

        nome_professor.setText(materia.getProfessor());
        data_hora_local.setText(materia.get_format_local_hora());
        carga_horaria_materia.setText(materia.get_format_carga());
        faltas_permitidas.setText(getString(R.string.faltas_permitidas) + " " + Integer.toString(materia.faltas_permitidas()));
        faltas_obtidas.setText(getString(R.string.faltas) + ": " +Integer.toString(materia.total_faltas()));

        /*
        ArrayList<String> faltas = materia.getFaltas();

        if(faltas != null) {

            List_adapter_faltas adaptador = new List_adapter_faltas(this, R.layout.estilo_post_it, materia.getFaltas());

            lista.setAdapter(adaptador);
        }
        */

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
                troca_UI(0);
            }
        });

        edita_materia = (Button) findViewById(R.id.b_materia_edita);

        edita_materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troca_UI(2);
            }
        });

    }

    public void set_editar_prova(){

        prova_data = (EditText) findViewById(R.id.et_edita_falta_data);
        prova_materia = (EditText) findViewById(R.id.ed_edita_falta_nome);
        prova_nota = (EditText) findViewById(R.id.ed_edita_falta_nota);
        prova_nome = (EditText) findViewById(R.id.ed_edita_falta_titulo);

        prova_data.setText(get_data_atual());
        prova_materia.setText("CAGUEI");
        prova_nota.setText("0.0");
        prova_nome.setText("P1");

        add_prova_mesmo = (Button) findViewById(R.id.botao_add_prova_mesmo);

        add_prova_mesmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok = checa_problemas_prova();

                if(ok) {
                    Prova prova = new Prova(materia.getNome(), prova_materia.getText().toString(),
                            prova_data.getText().toString(), prova_nota.getText().toString(), prova_nome.getText().toString());

                    atualiza_database_provas(prova);
                }
            }
        });

    }

    public void set_editar_materia(){
        nome = (EditText) findViewById(R.id.et_gerencia_materia_nome);
        professor = (EditText) findViewById(R.id.ed_gerencia_materia_professor);
        local = (EditText) findViewById(R.id.ed_gerencia_localiz);
        carga_horaria = (EditText) findViewById(R.id.ed_gerencia_materia_ch);

        //O NOME NÃO DEVE SER EDITADO PARA MANTER A CONSISTENCIA DE DADOS
        nome.setVisibility(View.GONE);

        professor.setText(materia.getProfessor());
        local.setText(materia.getLocal());
        carga_horaria.setText(Integer.toString(materia.getCarga_horaria()));

        segunda = (CheckBox) findViewById(R.id.checkBoxseg);
        horario_segunda_i = (EditText) findViewById(R.id.horarioi_seg);
        horario_segunda_f = (EditText) findViewById(R.id.horariof_seg);

        if(materia.procura_dia("SEG")){
            segunda.setChecked(true);
        }

        terca = (CheckBox) findViewById(R.id.checkBoxter);
        horario_terca_i = (EditText) findViewById(R.id.horarioi_ter);
        horario_terca_f  = (EditText) findViewById(R.id.horariof_ter);

        if(materia.procura_dia("TER")){
            terca.setChecked(true);
        }

        quarta = (CheckBox) findViewById(R.id.checkBoxqua);
        horario_quarta_i = (EditText) findViewById(R.id.horarioi_qua);
        horario_quarta_f = (EditText) findViewById(R.id.horariof_qua);

        if(materia.procura_dia("QUA")){
            quarta.setChecked(true);
        }

        quinta = (CheckBox) findViewById(R.id.checkBoxqui);
        horario_quinta_i  = (EditText) findViewById(R.id.horarioi_qui);
        horario_quinta_f  = (EditText) findViewById(R.id.horariof_qui);

        if(materia.procura_dia("QUI")){
            quinta.setChecked(true);
        }

        sexta = (CheckBox) findViewById(R.id.checkBoxsex);
        horario_sexta_i = (EditText) findViewById(R.id.horarioi_sex);
        horario_sexta_f = (EditText) findViewById(R.id.horariof_sex);

        if(materia.procura_dia("SEX")){
            sexta.setChecked(true);
        }

        sabado = (CheckBox) findViewById(R.id.checkBoxsab);
        horario_sabado_i = (EditText) findViewById(R.id.horarioi_sab);
        horario_sabado_f = (EditText) findViewById(R.id.horariof_sab);

        if(materia.procura_dia("SAB")){
            sabado.setChecked(true);
        }

        cria_materia = (Button) findViewById(R.id.botao_gerencia_materia_cria);

        cria_materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok_materia = checa_problemas();
                Log.d("MODIFICA MATERIA  ", "onClick: " + "AQUIAQUIAUQIAUQIAUQIAUIQAUQIAUQI");
                if(ok_materia){
                    Log.d("MODIFICA MATERIA  ", "onClick: " + "entroi");
                    altera_database_materia();
                    finish();
                }

            }
        });
    }

    public void altera_database_materia(){

        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.child(materia.getNome()).setValue(nova_materia);

        Toast.makeText(View_materia.this, R.string.materia_modificada , Toast.LENGTH_SHORT).show();

        finish();
    }

    public boolean checa_problemas(){

        //reseta os erros
        //nome.setError(null);
        professor.setError(null);
        local.setError(null);
        carga_horaria.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        //String nome_string = nome.getText().toString();
        String professor_string = professor.getText().toString();
        String carga_string = carga_horaria.getText().toString();
        String local_string = local.getText().toString();

        /*
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
        */

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

            nova_materia = new Materia(nome.getText().toString(), professor.getText().toString(),
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

                nova_materia.add_dia("SEG");
                nova_materia.add_horario(horario_segunda_i.getText().toString() + " ~ " + horario_segunda_f.getText().toString());
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

                nova_materia.add_dia("TER");
                nova_materia.add_horario(horario_terca_i.getText().toString() + " ~ " + horario_terca_f.getText().toString());

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

                nova_materia.add_dia("QUA");
                nova_materia.add_horario(horario_quarta_i.getText().toString() + " ~ " + horario_quarta_f.getText().toString());

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

                nova_materia.add_dia("QUI");
                nova_materia.add_horario(horario_quinta_i.getText().toString() + " ~ " + horario_quinta_f.getText().toString());

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

                nova_materia.add_dia("SEX");
                nova_materia.add_horario(horario_sexta_i.getText().toString() + " ~ " + horario_sexta_f.getText().toString());

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

                nova_materia.add_dia("SAB");
                nova_materia.add_horario(horario_sabado_i.getText().toString() + " ~ " + horario_sabado_f.getText().toString());
            }


            if(!informou) {

                Toast.makeText(getApplicationContext(), R.string.informe_um_dia , Toast.LENGTH_LONG).show();
                prossegue = false;
            }

        }

        if(prossegue){
            nova_materia.setNome(materia.getNome());
            nova_materia.setFaltas(materia.getFaltas());
            return true;
        }
        else{
            //Chama a atenção para o campo incorreto
            foco.requestFocus();
            Toast.makeText(getApplicationContext(), R.string.corrija_os_erros , Toast.LENGTH_LONG).show();
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

    public boolean checa_problemas_prova(){
        //falta ver o tamanho máximo das strings
        //reseta os erros
        prova_data.setError(null);
        prova_materia.setError(null);
        prova_nota.setError(null);
        prova_nome.setError(null);


        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String data_string = prova_data.getText().toString();
        String nome_string = prova_nome.getText().toString();
        String materia_string = prova_materia.getText().toString();
        String nota_string = prova_nota.getText().toString();


        if(TextUtils.isEmpty(data_string)) {
            prova_data.setError(getString(R.string.erro_vazio));
            foco = prova_data;
            prossegue = false;
        }

        if(TextUtils.isEmpty(nome_string)) {
            prova_nome.setError(getString(R.string.erro_vazio));
            foco = prova_nome;
            prossegue = false;
        }

        if(TextUtils.isEmpty(materia_string)){
            prova_materia.setError(getString(R.string.erro_vazio));
            foco = prova_materia;
            prossegue = false;
        }

        if(TextUtils.isEmpty(nota_string)){
            prova_nota.setError(getString(R.string.erro_vazio));
            foco = prova_nota;
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

    public void troca_UI(int param){
        if(param == 0){
            setContentView(R.layout.editar_falta);
            layout = 1;
            set_editar_prova();
        }
        else if(param == 1){
            setContentView(R.layout.view_materia);
            layout = 0;
            set_view_materias();
        }
        else if(param == 2){
            setContentView(R.layout.gerencia_materia);
            layout = 2;
            set_editar_materia();
        }
    }

    public void onBackPressed(){
        if(layout == 1){
            troca_UI(1);
        }
        else if(layout == 2){
            troca_UI(1);
        }
        else{
            finish();
        }
    }
}
