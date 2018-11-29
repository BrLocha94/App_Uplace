package com.example.blgo94.uff_game;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class Editar_perfil extends AppCompatActivity {

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    //Storage do firebase
    private StorageReference mStorageRef_avatar;

    //Locais de carregamento das imagens
    private ImageView avatar;

    //Usuário logado
    public Usuario usuario;

    //Edittexts da edição do perfil
    private EditText perfil_nome_informado;
    private EditText perfil_curso;

    //Botao
    private Button edita_perfil;
    private Button edita_avatar;
    private Button edita_badge;

    private ListView lista_avatar;
    private ListView lista_badge;

    private ArrayList<String> avatares;
    private ArrayList<String> ids_badges;
    private ArrayList<Badge> badges;

    private int layout = 0;
    private int count_badge = 1;

    private TextView texto_troca_badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        usuario = (Usuario) getIntent().getParcelableExtra("objeto");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        carrega_avatares();

        carrega_ids_badges();

        set_principal();

    }

    private void set_principal(){
        avatar = (ImageView) findViewById(R.id.editar_perfil_avatar);

        carrega_imagens(usuario.getProfilePic());

        perfil_nome_informado = (EditText) findViewById(R.id.perfil_edita_nome);
        perfil_nome_informado.setText(usuario.getUser_name());

        perfil_curso = (EditText) findViewById(R.id.perfil_edita_curso);
        perfil_curso.setText(usuario.getCourse());

        edita_perfil = (Button) findViewById(R.id.botao_edita_perfil);

        edita_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok = checa_problemas();

                if(ok) {
                    usuario.setUser_name(perfil_nome_informado.getText().toString());
                    usuario.setCourse(perfil_curso.getText().toString());

                    mDatabase.child(usuario.getID()).setValue(usuario);

                    finish();
                }
            }
        });

        edita_avatar = (Button) findViewById(R.id.edita_avatar);

        edita_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troca_UI(1);
            }
        });

        edita_badge = (Button) findViewById(R.id.edita_badges);

        edita_badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                troca_UI(2);
            }
        });
    }

    private void set_avatar(){
        lista_avatar = (ListView) findViewById(R.id.lista_avatar);

        List_adapter_avatar adapter_avatar = new List_adapter_avatar(this, R.layout.estilo_lista_avatar, avatares);

        lista_avatar.setAdapter(adapter_avatar);

        lista_avatar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuario.setProfilePic(avatares.get(position));
                troca_UI(0);
            }
        });
    }

    private void set_badges(){
        texto_troca_badge = (TextView) findViewById(R.id.texto_troca_badge);

        texto_troca_badge.setText(Integer.toString(count_badge));

        lista_badge = (ListView) findViewById(R.id.lista_badges);

        List_adapter_badges adapter_badges = new List_adapter_badges(this, R.layout.estilo_lista_badges, badges);

        lista_badge.setAdapter(adapter_badges);

        lista_badge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (count_badge){
                    case 1: usuario.setIdBadge1(badges.get(position).getId());
                            break;
                    case 2: usuario.setIdBadge2(badges.get(position).getId());
                            break;
                    case 3: usuario.setIdBadge3(badges.get(position).getId());
                            break;
                }

                count_badge ++;
                texto_troca_badge.setText(Integer.toString(count_badge));
                if(count_badge > 3){
                    count_badge = 1;
                    troca_UI(0);
                }
            }
        });
    }

    private void carrega_badges(){
        DatabaseReference data_badges = FirebaseDatabase.getInstance().getReference("badges");

        data_badges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                badges = new ArrayList<Badge>();
                preenche_array_badges(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_badges(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            for(int i = 0; i < ids_badges.size(); i++){
                if(ids_badges.get(i).equals(snapshot.getKey())){
                    badges.add(snapshot.getValue(Badge.class));
                    break;
                }
            }
        }
    }

    private void carrega_ids_badges(){
        DatabaseReference data_ids_badges = FirebaseDatabase.getInstance().getReference("badge_acess");

        data_ids_badges.child(usuario.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ids_badges = new ArrayList<String>();
                preenche_array_ids_badges(dataSnapshot);
                carrega_badges();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_ids_badges(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            ids_badges.add(snapshot.getValue(String.class));

        }
    }

    private void carrega_avatares(){
        DatabaseReference data_avatar = FirebaseDatabase.getInstance().getReference("avatar");

        data_avatar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avatares = new ArrayList<String>();
                preenche_array_avatares(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_avatares(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            avatares.add(snapshot.getValue(String.class));
        }
    }

    private void carrega_imagens(String ProfilePic){

        ProfilePic = "gs://uplace-ff0b3.appspot.com/avatar/" + ProfilePic;

        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);

        //Avatar
        Glide.with(this /* context */)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(128,128))
                .into(avatar);

    }

    private boolean checa_problemas() {


        //reseta os erros
        perfil_nome_informado.setError(null);
        perfil_curso.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = perfil_nome_informado.getText().toString();
        String curso_string = perfil_curso.getText().toString();

        //nome
        //primeiro check: se não foi informado o nome
        if (TextUtils.isEmpty(nome_string)) {
            perfil_nome_informado.setError(getString(R.string.erro_vazio));
            foco = perfil_nome_informado;
            prossegue = false;
        }
        //Segundo check: tamanho do nome
        else if (chave_tamanho(nome_string,28)) {
            perfil_nome_informado.setError(getString(R.string.erro_tamanho));
            foco = perfil_nome_informado;
            prossegue = false;
        }

        //curso
        //primeiro check: se não foi informado o curso
        if (TextUtils.isEmpty(curso_string)) {
            perfil_curso.setError(getString(R.string.erro_vazio));
            foco = perfil_curso;
            prossegue = false;
        }
        //segundo check: tamanho do curso
        else if (chave_tamanho(curso_string,32)) {
            perfil_curso.setError(getString(R.string.erro_tamanho_curso));
            foco = perfil_curso;
            prossegue = false;
        }


        if (prossegue) {
            return true;
        } else {
            //Chama a atenção para o campo incorreto
            foco.requestFocus();
            return false;
        }
    }

    private boolean chave_tamanho(String nome, int tamanho){
        Log.d("mimimimimimimim", "chave_tamanho: " + nome + " " + Integer.toString(nome.length()) + " " + Integer.toString(tamanho));
        if(nome.length() > tamanho){
            return true;
        }
        return false;
    }

    private void troca_UI(int caso){
        switch (caso){
            case 0: setContentView(R.layout.editar_perfil);
                    layout = 0;
                    set_principal();
                    break;
            case 1: setContentView(R.layout.activity_lista_avatar);
                    layout = 1;
                    set_avatar();
                    break;
            case 2: setContentView(R.layout.activity_lista_badge);
                    layout = 2;
                    set_badges();
                    break;
        }
    }

    public void onBackPressed(){

        if(layout == 2 && count_badge > 1){
            count_badge --;
            texto_troca_badge = (TextView) findViewById(R.id.texto_troca_badge);

            texto_troca_badge.setText(Integer.toString(count_badge));
        }
        else if(layout == 2 || layout == 1){
            troca_UI(0);
        }
        else{
            finish();
        }
    }
}
