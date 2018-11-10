package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.blgo94.uff_game.MyAppGlideModule;


public class MainActivity extends AppCompatActivity {

    public final String TAG = "MAINACTIVITY";

    //Autenticação do firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Storage do firebase
    private StorageReference mStorageRef_avatar;
    private StorageReference mStorageRef_badge_1;
    private StorageReference mStorageRef_badge_2;
    private StorageReference mStorageRef_badge_3;

    //Locais de carregamento das imagens
    ImageView avatar;
    ImageView bagde_1;
    ImageView badge_2;
    ImageView badge_3;

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    //Usuario atual do app
    public String id_usuario = "";
    public Usuario user;

    //Locais de exibição das info
    private TextView nome_usuario;
    private TextView curso_usuario;

    //PROVISÓRIO
    public Button lista_materias;
    public Button leitor_QR;
    public Button edita_perfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set_UI();

        FirebaseApp.initializeApp(this);

        //Instancias de autenticação do usuário
        mAuth = FirebaseAuth.getInstance();

        get_current_user();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    set_database();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, Login_Cadastro.class);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };


    }

    private void set_database(){
        //Carrega a instancia do Database
        //Depois, cria um leitor para puxar o objeto Usuario do database requisitado
        //Isto é feito a partir do ID
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.child(id_usuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_info_usuario(dataSnapshot);
                carrega_imagens(user.getProfilePic(), user.getIdBadge1());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_info_usuario(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            user = dataSnapshot.getValue(Usuario.class);
            nome_usuario.setText(user.getUser_name());
            curso_usuario.setText(user.getCourse());
        }
    }

    private void set_UI(){
        avatar = (ImageView) findViewById(R.id.avatar);
        bagde_1 = (ImageView) findViewById(R.id.badge_01);

        nome_usuario = (TextView) findViewById(R.id.usuario_nome);
        curso_usuario = (TextView) findViewById(R.id.usuario_curso);

        lista_materias = (Button) findViewById(R.id.botao_lista_materias);

        lista_materias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Lista_materias.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
        });

        leitor_QR = (Button) findViewById(R.id.botao_QR);
        /*
        leitor_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Leitor_QR.class);
                startActivity(intent);
            }
        });
        */

    }

    private void carrega_imagens(String ProfilePic, String IdBadge1){

        ProfilePic = "gs://uffgame-cec71.appspot.com/Avatar/" + ProfilePic;
        IdBadge1 = "gs://uffgame-cec71.appspot.com/Badges/" + IdBadge1;

        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);
        mStorageRef_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge1);

        //Avatar
        Glide.with(this /* context */)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(128,128))
                .into(avatar);


        //Bagde
        Glide.with(this)
                .load(mStorageRef_badge_1)
                .apply(new RequestOptions().override(64,64))
                .into(bagde_1);
    }

    private void updateUI(FirebaseUser user){

        if(user != null){


        } else{

        }
    }

    private void get_current_user(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            String email = user.getEmail();

            String array [] = new String[2];
            array = email.split("@");
            this.id_usuario = array[0];
        }
        else{
            //O usuario não está conectado, logo deve fazer o login
            //Finaliza esta activity e inicializa a de login
            Intent intent = new Intent(MainActivity.this, Login_Cadastro.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onBackPressed(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_editar_perfil)
        {
            //Vai para a tela de adicionar materias
            Intent intent = new Intent(MainActivity.this, Editar_perfil.class);
            intent.putExtra("objeto", user);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_lista_amigos){
            //Vai para a tela de lista de amigos
            Intent intent = new Intent(MainActivity.this, Lista_amigos.class);
            intent.putExtra("ID_USUARIO", user.getID());
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.menu_logout){

            //Vai para a tela de remover materias
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

}
