package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    //botao teste
    private Button add_amigo;

    private ListView lista;

    //Caso 0 = perfil próprio ou amigo, Caso 1 = desconhecido
    private int caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        user = (Usuario) getIntent().getParcelableExtra("objeto");

        caso = Integer.parseInt((String) getIntent().getStringExtra("caso"));

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


        if(caso == 0){

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
            lista = (ListView) findViewById(R.id.lista_5_ultimas);
            lista.setVisibility(View.VISIBLE);


        }


    }

    private void requisita_add_amigo(){

    }

    private void carrega_imagens(String ProfilePic, String IdBadge1, String IdBadge2,
                                 String IdBadge3){

        ProfilePic = "gs://uplace-ff0b3.appspot.com/avatar/" + ProfilePic;
        IdBadge1 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge1;
        IdBadge2 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge2;
        IdBadge3 = "gs://uplace-ff0b3.appspot.com/badge/" + IdBadge3;

        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);
        mStorageRef_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge1);
        mStorageRef_badge_2 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge2);
        mStorageRef_badge_3 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge3);

        //Avatar
        Glide.with(this /* context */)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(128,128))
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
