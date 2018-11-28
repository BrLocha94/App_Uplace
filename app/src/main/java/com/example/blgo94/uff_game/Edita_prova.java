package com.example.blgo94.uff_game;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Edita_prova extends AppCompatActivity {

    private String id;
    private String position;
    private Prova prova;

    private ArrayList<String> array_ids_materias;
    private ArrayList<Prova> array_provas;

    protected EditText prova_data;
    protected EditText prova_materia;
    protected EditText prova_nota;
    protected EditText prova_nome;

    private Button add_prova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_falta);

        id = (String) getIntent().getStringExtra("ID_USUARIO");

        position = (String) getIntent().getStringExtra("position");

        prova = (Prova) getIntent().getParcelableExtra("prova");

        Log.d("mimimimimimimimimimimi", "POSITION " + position);

        prova_data = (EditText) findViewById(R.id.et_edita_falta_data);
        prova_materia = (EditText) findViewById(R.id.ed_edita_falta_nome);
        prova_nota = (EditText) findViewById(R.id.ed_edita_falta_nota);
        prova_nome = (EditText) findViewById(R.id.ed_edita_falta_titulo);

        prova_data.setText(prova.getData());
        prova_materia.setText(prova.getMateria());
        prova_nota.setText(prova.getNota());
        prova_nome.setText(prova.getNome());

        set_database();

        add_prova = (Button) findViewById(R.id.botao_add_prova_mesmo);

        add_prova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok = checa_problemas();

                if(ok) {
                    prova.setData(prova_data.getText().toString());
                    prova.setMateria(prova_materia.getText().toString());
                    prova.setNota(prova_nota.getText().toString());
                    prova.setNome(prova_nome.getText().toString());

                    salva_tudo();
                }
            }
        });

    }

    public boolean checa_problemas(){
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

    private void set_database(){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("provas").child(id);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_ids_materias = new ArrayList<String>();
                set_array(dataSnapshot);
                set_provas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_ids_materias.add(snapshot.getKey());
        }
    }

    private void set_provas(){
        DatabaseReference data_02 = FirebaseDatabase.getInstance().getReference("provas").child(id);

        array_provas = new ArrayList<Prova>();

        for(int i = 0; i < array_ids_materias.size(); i++){
            data_02.child(array_ids_materias.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    set_array_provas(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void set_array_provas(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            array_provas.add(snapshot.getValue(Prova.class));
        }
    }

    private void salva_tudo(){

        DatabaseReference data_02 = FirebaseDatabase.getInstance().getReference("provas").child(id);

        //TROCA A PROVA SELECIONADA
        array_provas.get(Integer.parseInt(position)).troca_prova(prova);
        //SALVA TUDO
        int j = 0;
        for(int i = 0; i < array_ids_materias.size(); i++){
            int k = 0;
            while (array_ids_materias.get(i).equals(array_provas.get(j).getId())){
                data_02.child(array_ids_materias.get(i)).child(Integer.toString(k)).setValue(array_provas.get(j));
                k ++;
                j ++;
                if (j >= array_provas.size()) break;
            }
        }
        //FINALIZA A ACTIVITY
        finish();
    }
}
