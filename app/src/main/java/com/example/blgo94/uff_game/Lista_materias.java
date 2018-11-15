package com.example.blgo94.uff_game;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lista_materias extends AppCompatActivity {

    public final String TAG = "materias";

    //RECEBE O STRING EXTRA DO INTENT
    private String id;

    //Lista
    public ListView lista_seg;
    public ListView lista_ter;
    public ListView lista_qua;
    public ListView lista_qui;
    public ListView lista_sex;
    public ListView lista_sab;

    //Database usado
    DatabaseReference data;

    //Recebe as materias que o usuario está inscrito
    private ArrayList<Materia> array_materias;

    //Organiza as materias do inscrito nos diferentes arrays para serem adaptados
    private ArrayList<String> array_seg;
    private ArrayList<String> array_ter;
    private ArrayList<String> array_qua;
    private ArrayList<String> array_qui;
    private ArrayList<String> array_sex;
    private ArrayList<String> array_sab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_materias);

        lista_seg = (ListView)findViewById(R.id.lista_seg);
        lista_ter = (ListView)findViewById(R.id.lista_ter);
        lista_qua = (ListView)findViewById(R.id.lista_qua);
        lista_qui = (ListView)findViewById(R.id.lista_qui);
        lista_sex = (ListView)findViewById(R.id.lista_sex);
        lista_sab = (ListView)findViewById(R.id.lista_sab);


        id = (String) getIntent().getStringExtra("ID_USUARIO");

        lista_principal();
    }

    void lista_principal(){

        /*
            EXIBIR TODAS AS MATÉRIAS DE ACORDO COM AS ESECIFICAÇÕES DO DESIGN
            PEGAR TODAS USANDO A CHAVE DO USUARIO NO DATABASE
         */

        data = FirebaseDatabase.getInstance().getReference("materias").child(id);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_materias = new ArrayList<Materia>();
                set_array(dataSnapshot);
                carrega_lista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_materias.add(snapshot.getValue(Materia.class));
        }
        organiza_arrays();
    }

    private void organiza_arrays(){
        String DIAS [] = {"SEG", "TER", "QUA", "QUI", "SEX", "SAB"};

        array_seg = new ArrayList<String>();
        array_ter = new ArrayList<String>();
        array_qua = new ArrayList<String>();
        array_qui = new ArrayList<String>();
        array_sex = new ArrayList<String>();
        array_sab = new ArrayList<String>();

        for(int j = 0; j < array_materias.size(); j++) {
            ArrayList<String> aux = array_materias.get(j).getDias();
            for (int k = 0; k < aux.size(); k++) {
                for(int w = 0; w < 6; w++) {
                    if (aux.get(k).equals(DIAS[w])) {
                        switch (w) {
                            case 0:
                                array_seg.add(array_materias.get(j).getNome());
                                break;
                            case 1:
                                array_ter.add(array_materias.get(j).getNome());
                                break;
                            case 2:
                                array_qua.add(array_materias.get(j).getNome());
                                break;
                            case 3:
                                array_qui.add(array_materias.get(j).getNome());
                                break;
                            case 4:
                                array_sex.add(array_materias.get(j).getNome());
                                break;
                            case 5:
                                array_sab.add(array_materias.get(j).getNome());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

    }

    private void carrega_lista(){
        //CARREGAR O ARRAYLISTA GERADO, SEPARADAMENTE POR DIA DAS MATÉRIAS, DE ACORDO COM
        //O ESTILO ESCOLHIDO

        ArrayAdapter<String> adapter_seg = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_seg);
        lista_seg.setAdapter(adapter_seg);

        lista_seg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA"+chave);
                inicia_view_materia(chave);
            }
        });

        ArrayAdapter<String> adapter_ter = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_ter);
        lista_ter.setAdapter(adapter_ter);

        lista_ter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                inicia_view_materia(chave);
            }
        });

        ArrayAdapter<String> adapter_qua = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_qua);
        lista_qua.setAdapter(adapter_qua);

        lista_qua.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                inicia_view_materia(chave);
            }
        });

        ArrayAdapter<String> adapter_qui = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_qui);
        lista_qui.setAdapter(adapter_qui);

        lista_qui.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                inicia_view_materia(chave);
            }
        });

        ArrayAdapter<String> adapter_sex = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_sex);
        lista_sex.setAdapter(adapter_sex);

        lista_sex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                inicia_view_materia(chave);
            }
        });

        ArrayAdapter<String> adapter_sab = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, array_sab);
        lista_sab.setAdapter(adapter_sab);

        lista_sab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chave = (String) parent.getItemAtPosition(position);
                inicia_view_materia(chave);
            }
        });

    }

    public void inicia_view_materia(String chave){
        Intent intent = new Intent(Lista_materias.this, View_materia.class);
        intent.putExtra("ID_USUARIO", id);
        intent.putExtra("materia", array_materias.get(acha_materia(chave)));
        startActivity(intent);
    }

    public int acha_materia(String chave){
        for(int i = 0; i < array_materias.size(); i++){
            if(array_materias.get(i).getNome().equals(chave)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_materias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_add)
        {
            //Vai para a activity de adicionar matéria
            Intent intent = new Intent(Lista_materias.this, Adicionar_materia.class);
            intent.putExtra("ID_USUARIO",id);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_lista_provas){
            //Vai para a activity de lista de provas
            Intent intent = new Intent(Lista_materias.this, Lista_provas.class);
            intent.putExtra("ID_USUARIO", id);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_rem){

            //Vai para a Activity de remover materias
            Intent intent = new Intent(Lista_materias.this, Remove_materia.class);
            intent.putExtra("ID_USUARIO", id);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}

