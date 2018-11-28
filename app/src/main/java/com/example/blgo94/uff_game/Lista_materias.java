package com.example.blgo94.uff_game;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    DatabaseReference data_badges;
    DatabaseReference data_badge_acess;

    Badge badge;

    Usuario user;

    boolean ok = true;

    private ArrayList<String> badges;

    //Recebe as materias que o usuario está inscrito
    private ArrayList<Materia> array_materias;

    //Organiza as materias do inscrito nos diferentes arrays para serem adaptados
    private ArrayList<Materia> array_seg;
    private ArrayList<Materia> array_ter;
    private ArrayList<Materia> array_qua;
    private ArrayList<Materia> array_qui;
    private ArrayList<Materia> array_sex;
    private ArrayList<Materia> array_sab;

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

        array_seg = new ArrayList<Materia>();
        array_ter = new ArrayList<Materia>();
        array_qua = new ArrayList<Materia>();
        array_qui = new ArrayList<Materia>();
        array_sex = new ArrayList<Materia>();
        array_sab = new ArrayList<Materia>();

        for(int j = 0; j < array_materias.size(); j++) {
            ArrayList<String> aux = array_materias.get(j).getDias();
            for (int k = 0; k < aux.size(); k++) {
                for(int w = 0; w < 6; w++) {
                    if (aux.get(k).equals(DIAS[w])) {
                        switch (w) {
                            case 0:
                                array_seg.add(array_materias.get(j));
                                break;
                            case 1:
                                array_ter.add(array_materias.get(j));
                                break;
                            case 2:
                                array_qua.add(array_materias.get(j));
                                break;
                            case 3:
                                array_qui.add(array_materias.get(j));
                                break;
                            case 4:
                                array_sex.add(array_materias.get(j));
                                break;
                            case 5:
                                array_sab.add(array_materias.get(j));
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

        List_adapter_materias adapter_seg = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_seg,"SEG");
        lista_seg.setAdapter(adapter_seg);


        lista_seg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA"+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        List_adapter_materias adapter_ter = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_ter,"TER");
        lista_ter.setAdapter(adapter_ter);

        lista_ter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA"+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        List_adapter_materias adapter_qua = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_qua,"QUA");
        lista_qua.setAdapter(adapter_qua);

        lista_qua.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA    "+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        List_adapter_materias adapter_qui = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_qui,"QUI");
        lista_qui.setAdapter(adapter_qui);

        lista_qui.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA    "+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        List_adapter_materias adapter_sex = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_sex,"SEX");
        lista_sex.setAdapter(adapter_sex);

        lista_sex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA    "+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        List_adapter_materias adapter_sab = new List_adapter_materias(this,R.layout.estilo_lista_materias,array_sab,"SAB");
        lista_sab.setAdapter(adapter_sab);

        lista_sab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Materia chave = (Materia) parent.getItemAtPosition(position);
                Log.d(TAG,"CHAVE PASSADA    "+chave.getNome());
                inicia_view_materia(chave.getNome());
            }
        });

        checa_badges();

    }

    private void checa_badges(){

        data_badge_acess = FirebaseDatabase.getInstance().getReference("badge_acess");

        data_badge_acess.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                badges = new ArrayList<String>();
                preenche_array_badges(dataSnapshot);
                ok = true;
                for(int i = 0; i < badges.size(); i++){
                    if(badges.get(i).equals("aula_sabado")){
                        ok = false;
                        break;
                    }
                }

                if(ok){
                    badge_aula_sabado();
                    //checa_badges();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void badge_aula_sabado(){
        if(array_sab.size() > 0){
            badges.add("aula_sabado");
            data_badge_acess.child(id).setValue(badges);

            get_usuario(id);

            //LIBERA BADGE DIALOG
            data_badges = FirebaseDatabase.getInstance().getReference("badges");
            data_badges.child("aula_sabado").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    badge = dataSnapshot.getValue(Badge.class);
                    pop_up_badges(badge);
                    Modfica_usuario mod = new Modfica_usuario(id, user);
                    Log.d(TAG, "onDataChange: "+ mod.getUser().getScore());
                    boolean level_up = mod.add_score(Integer.parseInt(badge.getPontuacao()));

                    if(level_up){
                        pop_up_level_up(mod.getUser());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
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

    private void pop_up_level_up(Usuario usuario){
        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("LEVEL_UP");

        TextView descricao_badge = settingsDialog.findViewById(R.id.descricao_badge);
        descricao_badge.setText("PARABENS POR SUBIR DE LV!!!");

        TextView ponto_badge = settingsDialog.findViewById(R.id.ponto_badge);
        String mensagem = "Level: " + usuario.getLevel() + " Score: " + usuario.getScore();
        ponto_badge.setText(mensagem);

        settingsDialog.show();
    }

    private void pop_up_badges(Badge badge){
        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("BADGE");

        TextView descricao_badge = settingsDialog.findViewById(R.id.descricao_badge);
        descricao_badge.setText(badge.getDescricao());

        TextView ponto_badge = settingsDialog.findViewById(R.id.ponto_badge);
        String mensagem = badge.getPontuacao() + " " + getString(R.string.pontos);
        ponto_badge.setText(mensagem);

        ImageView imagem_badge = settingsDialog.findViewById(R.id.imagem_badge);
        String IdBadge = "gs://uplace-ff0b3.appspot.com/badge/" + badge.getId() +".gif";
        StorageReference Ref_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge);

        //Bagde
        Glide.with(this)
                .load(Ref_badge_1)
                .apply(new RequestOptions().override(350,350))
                .into(imagem_badge);

        settingsDialog.show();
    }

    private void preenche_array_badges(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            badges.add(snapshot.getValue(String.class));
        }
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

