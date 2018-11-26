package com.example.blgo94.uff_game;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class Perfil extends AppCompatActivity {

    //Storage do firebase
    private StorageReference mStorageRef_avatar;
    private StorageReference mStorageRef_badge_1;
    private StorageReference mStorageRef_badge_2;
    private StorageReference mStorageRef_badge_3;

    //Locais de carregamento das imagens
    private ImageView avatar_perfil;
    private ImageView badge_1;
    private ImageView badge_2;
    private ImageView badge_3;
    private TextView texto_nome;
    private TextView texto_lv;
    private TextView texto_score;

    private Usuario user;

    private DatabaseReference data_atualizacoes;
    private ArrayList<String> atualizacoes;
    private Atualizacoes ata;

    private DatabaseReference data_amigo;
    private DatabaseReference data;
    private DatabaseReference data_badges;

    //botao teste
    private Button add_amigo;
    private Button ver_mais;

    private ListView lista;

    private String id_view;

    //Caso 0 = perfil próprio ou amigo, Caso 1 = desconhecido
    private int caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        id_view = (String) getIntent().getStringExtra("ID_USUARIO");

        user = (Usuario) getIntent().getParcelableExtra("objeto");

        caso = Integer.parseInt((String) getIntent().getStringExtra("caso"));

        Log.d("mimimimimimimimimimim", "onCreate: id_view "+ id_view);

        Log.d("mimimimimimimimimimim", "onCreate: user_id "+ user.getID());


        avatar_perfil = (ImageView) findViewById(R.id.perfil_avatar);

        badge_1 = (ImageView) findViewById(R.id.perfil_badge_1);
        badge_2 = (ImageView) findViewById(R.id.perfil_badge_2);
        badge_3 = (ImageView) findViewById(R.id.perfil_badge_3);


        carrega_imagens(user.getProfilePic(), user.getIdBadge1(), user.getIdBadge2(),
                user.getIdBadge3());

        texto_nome = (TextView) findViewById(R.id.perfil_nome_informado);
        texto_nome.setText(user.getUser_name());

        texto_lv = (TextView) findViewById(R.id.perfil_level);
        texto_lv.setText("LEVEL: " + user.getLevel());

        texto_score = (TextView) findViewById(R.id.perfil_score);
        texto_score.setText("SCORE: " + user.getScore());

        ver_mais = (Button) findViewById(R.id.perfil_ver_mais);

        if(caso == 1){

            ver_mais.setVisibility(View.GONE);

            add_amigo = (Button) findViewById(R.id.perfil_adicionar_amigo);
            add_amigo.setVisibility(View.VISIBLE);

            add_amigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requisita_add_amigo();
                }
            });

        }
        else{

            if(user.getID().equals(id_view)){
                ver_mais.setVisibility(View.VISIBLE);
            }
            else {
                ver_mais.setVisibility(View.GONE);
            }

            lista = (ListView) findViewById(R.id.lista_5_ultimas);
            lista.setVisibility(View.VISIBLE);

            set_database_atualizacoes();
        }

        badge_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_badges = FirebaseDatabase.getInstance().getReference("badges");
                data_badges.child(user.getIdBadge1()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Badge badge = dataSnapshot.getValue(Badge.class);
                        pop_up_badges(badge);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        badge_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_badges = FirebaseDatabase.getInstance().getReference("badges");
                data_badges.child(user.getIdBadge2()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Badge badge = dataSnapshot.getValue(Badge.class);
                        pop_up_badges(badge);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        badge_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_badges = FirebaseDatabase.getInstance().getReference("badges");
                data_badges.child(user.getIdBadge3()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Badge badge = dataSnapshot.getValue(Badge.class);
                        pop_up_badges(badge);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void pop_up_badges(Badge badge){
        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("BADGE");

        TextView nome_badge = settingsDialog.findViewById(R.id.nome_badge);
        nome_badge.setText(badge.getNome());

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

    private void requisita_add_amigo(){

        data = FirebaseDatabase.getInstance().getReference("users");

        data.child(id_view).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario view_usuario = dataSnapshot.getValue(Usuario.class);
                manda_req(view_usuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void manda_req(Usuario view_usuario){
        Amigo amigo = new Amigo(view_usuario.getID(), view_usuario.getUser_name(), view_usuario.getCourse());
        data_amigo = FirebaseDatabase.getInstance().getReference("req_amigo");

        data_amigo.child(user.getID()).child(amigo.getId()).setValue(amigo);

        finish();
    }

    private void set_database_atualizacoes(){
        data_atualizacoes = FirebaseDatabase.getInstance().getReference("atualizacao");

        data_atualizacoes.child(user.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                atualizacoes = new ArrayList<String>();
                preenche_array_atualizacoes(dataSnapshot);
                set_lista_atualizacoes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void set_lista_atualizacoes(){
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.estilo_atualizacoes,
                R.id.text_atualizacoes, atualizacoes);

        lista.setAdapter(adaptador);
    }

    private void preenche_array_atualizacoes(DataSnapshot dataSnapshot){
        ata = new Atualizacoes();
        ata = dataSnapshot.getValue(Atualizacoes.class);
        atualizacoes = ata.getAtualiz();
    }

    private void carrega_imagens(String ProfilePic, String IdBadge1, String IdBadge2,
                                 String IdBadge3){

        ProfilePic = "gs://uplace-ff0b3.appspot.com/avatar/" + ProfilePic;
        IdBadge1 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge1 +".gif";
        IdBadge2 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge2 +".gif";
        IdBadge3 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge3 +".gif";

        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);
        mStorageRef_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge1);
        mStorageRef_badge_2 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge2);
        mStorageRef_badge_3 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge3);

        //Avatar
        Glide.with(this /* context */)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(140,140))
                .into(avatar_perfil);


        //Bagde
        Glide.with(this)
                .load(mStorageRef_badge_1)
                .apply(new RequestOptions().override(200,200))
                .into(badge_1);

        //Bagde2
        Glide.with(this)
                .load(mStorageRef_badge_2)
                .apply(new RequestOptions().override(200,200))
                .into(badge_2);

        //Bagde3
        Glide.with(this)
                .load(mStorageRef_badge_3)
                .apply(new RequestOptions().override(200,200))
                .into(badge_3);
    }
}
