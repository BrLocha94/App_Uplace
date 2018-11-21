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
import android.widget.Toast;

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
    private TextView level_usuario;
    private TextView score_usuario;

    //PROVISÓRIO
    public Button lista_materias;
    public Button leitor_QR;
    public Button edita_perfil;

    private boolean ok = false;


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
                ok = true;
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
            //level_usuario.setText("Level: " + user.getLevel());
            //score_usuario.setText("Score: " + user.getScore());
        }
    }

    private void set_UI(){
        avatar = (ImageView) findViewById(R.id.main_avatar);

        nome_usuario = (TextView) findViewById(R.id.main_nome);

        //level_usuario = (TextView) findViewById(R.id.main_level);

        //score_usuario = (TextView) findViewById(R.id.main_score);

    }

    private void carrega_imagens(String ProfilePic, String IdBadge1){

        ProfilePic = "gs://uplace-ff0b3.appspot.com/avatar/" + ProfilePic;

        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);

        //Avatar
        Glide.with(this /* context */)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(128,128))
                .into(avatar);

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
            if(ok) {
                //Vai para a tela de adicionar materias
                Intent intent = new Intent(MainActivity.this, Editar_perfil.class);
                intent.putExtra("objeto", user);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_perfil){
            //Vai para a tela de lista de amigos
            if(ok) {
                Intent intent = new Intent(MainActivity.this, Perfil.class);
                intent.putExtra("objeto", user);
                intent.putExtra("caso", "0");
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_lista_amigos){
            if(ok) {
                //Vai para a tela de lista de amigos
                Intent intent = new Intent(MainActivity.this, Lista_amigos.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_perfil) {
            if (ok) {
                Intent intent = new Intent(MainActivity.this, Lista_materias.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_cria_evento){
            if(ok) {
                //Vai para a criação de eventos
                Intent intent = new Intent(MainActivity.this, Criar_evento.class);
                intent.putExtra("ID_USUARIO", user.getID());
                intent.putExtra("nome_usuario", user.getUser_name());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_mapa_uff){
            if(ok) {
                //Vai para a a palicação de mapas
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_pessoas_proximas){
            if(ok) {
                //Vai para a tela de pessoas proximas
                Intent intent = new Intent(MainActivity.this, Localiza.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, "Aguarde as informações serem carregadas...", Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == R.id.menu_logout){

            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

}
