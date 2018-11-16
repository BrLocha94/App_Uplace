package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView avatar_perfil;
    ImageView bagde_1;
    ImageView badge_2;
    ImageView badge_3;
    TextView texto_nome;

    private Usuario user;

    //botao teste
    private Button add_amigo;

    //Caso 0 = perfil próprio ou amigo, Caso 1 = desconhecido
    private int caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        user = (Usuario) getIntent().getParcelableExtra("objeto");

        caso = Integer.parseInt((String) getIntent().getStringExtra("caso"));

        carrega_imagens(user.getProfilePic(), user.getIdBadge1(), user.getIdBadge2(),
                user.getIdBadge3());

        texto_nome = (TextView) findViewById(R.id.perfil_texto);
        texto_nome.setText(user.getUser_name());

        add_amigo = (Button) findViewById(R.id.perfil_botao_addamigo);

        if(caso == 0){
            add_amigo.setVisibility(View.GONE);
        }
        else{
            add_amigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requisita_add_amigo();
                }
            });
        }

    }

    private void requisita_add_amigo(){

    }

    private void carrega_imagens(String ProfilePic, String IdBadge1, String IdBadge2,
                                 String IdBadge3){

        ProfilePic = "gs://uffgame-cec71.appspot.com/Avatar/" + ProfilePic;
        IdBadge1 = "gs://uffgame-cec71.appspot.com/Badges/" + IdBadge1;
        IdBadge1 = "gs://uffgame-cec71.appspot.com/Badges/" + IdBadge2;
        IdBadge1 = "gs://uffgame-cec71.appspot.com/Badges/" + IdBadge3;

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
                .apply(new RequestOptions().override(64,64))
                .into(bagde_1);

        //Bagde2
        Glide.with(this)
                .load(mStorageRef_badge_2)
                .apply(new RequestOptions().override(64,64))
                .into(bagde_1);

        //Bagde3
        Glide.with(this)
                .load(mStorageRef_badge_3)
                .apply(new RequestOptions().override(64,64))
                .into(bagde_1);
    }
}
