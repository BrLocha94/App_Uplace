package com.example.blgo94.uff_game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;


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
    private DatabaseReference data_amigos;
    private DatabaseReference data_atualizacoes;
    private DatabaseReference data_eventos;
    private DatabaseReference data_badge_acess;
    private DatabaseReference data_badges;

    Badge badge;

    boolean ok_badge = true;

    private ArrayList<String> badges;

    //Usuario atual do app
    public String id_usuario = "";
    public Usuario user;

    //Locais de exibição das info
    private TextView nome_usuario;
    private ImageView desafio_semanal;
    private TextView level_usuario;
    private TextView score_usuario;

    //PROVISÓRIO
    public Button lista_materias;
    public Button leitor_QR;
    public Button edita_perfil;
    private Button botao_lista_atu;
    private Button botao_lista_eve;

    private ListView lista_atualizacoes;
    private ListView lista_eventos;

    private ArrayList<Amigo> amigos;
    private Atualizacoes ata = new Atualizacoes();
    private ArrayList<String> atualizacoes;
    private ArrayList<Evento> eventos;
    private ArrayList<String> nome_eventos;

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

    private void checa_badges(){

        data_badge_acess = FirebaseDatabase.getInstance().getReference("badge_acess");

        data_badge_acess.child(user.getID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                badges = new ArrayList<String>();
                preenche_array_badges(dataSnapshot);

                ok_badge = true;
                for(int i = 0; i < badges.size(); i++){
                    if(badges.get(i).equals("calouro")){
                        ok_badge = false;
                        break;
                    }
                }

                if(ok_badge){
                    badges.add("calouro");
                    badge_calouro();
                    //checa_badges();
                }
                else{
                    ok_badge = true;
                    Log.d(TAG, "badge_veterano: AQUI AHAHAHHAHAHAHAHAHAHAHAHAHHAAH " + badges.size());
                    for(int i = 0; i < badges.size(); i++){
                        Log.d(TAG, "b a d g e s v e t e r a n o " + badges.get(i));
                        if(badges.get(i).equals("veterano")){
                            Log.d(TAG, "badge_veterano: ");
                            ok_badge = false;
                            break;
                        }
                    }
                    Log.d(TAG, "badge_veterano: " + ok_badge);
                    if(ok_badge){
                        badge_veterano();
                    }
                    ok_badge = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void badge_calouro(){
        Logica_badges log_bad = new Logica_badges();
        if(log_bad.calouro(user.getLevel())){

            data_badge_acess.child(user.getID()).setValue(badges);

            //LIBERA BADGE DIALOG
            data_badges = FirebaseDatabase.getInstance().getReference("badges");
            data_badges.child("calouro").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    badge = dataSnapshot.getValue(Badge.class);
                    pop_up_badges(badge);
                    Modfica_usuario mod = new Modfica_usuario(user.getID(), user);
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

    private void badge_veterano(){
        Logica_badges log_bad_vet = new Logica_badges();

        if(log_bad_vet.veterano(user.getLevel())){

            badges.add("veterano");
            data_badge_acess.child(user.getID()).setValue(badges);

            //LIBERA BADGE DIALOG
            data_badges = FirebaseDatabase.getInstance().getReference("badges");
            data_badges.child("veterano").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    badge = dataSnapshot.getValue(Badge.class);
                    pop_up_badges(badge);
                    Modfica_usuario mod = new Modfica_usuario(user.getID(), user);
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

    private void pop_up_desafio_semanal(){

        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("BADGE");

        TextView nome_badge = settingsDialog.findViewById(R.id.nome_badge);
        nome_badge.setVisibility(View.GONE);

        TextView descricao_badge = settingsDialog.findViewById(R.id.descricao_badge);
        descricao_badge.setVisibility(View.GONE);

        TextView ponto_badge = settingsDialog.findViewById(R.id.ponto_badge);
        //String mensagem = badge.getPontuacao() + " " + getString(R.string.pontos);
        ponto_badge.setVisibility(View.GONE);

        ImageView imagem_badge = settingsDialog.findViewById(R.id.imagem_badge);
        String IdBadge = "gs://uplace-ff0b3.appspot.com/desafio/image.png";
        StorageReference Ref_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge);

        //Bagde
        Glide.with(this)
                .load(Ref_badge_1)
                .apply(new RequestOptions().override(2200,1650))
                .into(imagem_badge);

        settingsDialog.show();
    }

    private void preenche_array_badges(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            badges.add(snapshot.getValue(String.class));
        }
    }

    private void set_database_eventos(){
        data_eventos = FirebaseDatabase.getInstance().getReference("events");

        data_eventos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventos = new ArrayList<Evento>();
                preenche_array_eventos(dataSnapshot);
                set_lista_evento();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void set_lista_evento(){
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, nome_eventos);

        lista_eventos.setAdapter(adaptador);

        lista_eventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ok) {
                    String id_evento = (String) parent.getItemAtPosition(position);
                    for (int i = 0; i < eventos.size(); i++) {
                        if (eventos.get(i).getNome().equals(id_evento)) {
                            Intent intent = new Intent(MainActivity.this, View_evento.class);
                            intent.putExtra("evento", eventos.get(i));
                            intent.putExtra("ID_USUARIO", user.getID());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void preenche_array_eventos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            eventos.add(snapshot.getValue(Evento.class));
        }
        nome_eventos = new ArrayList<String>();
        for(int i = 0; i < eventos.size(); i++){
            nome_eventos.add(eventos.get(i).getNome());
            Log.d(TAG, nome_eventos.get(i));
        }
    }

    private void set_database_atualizacoes(){
        data_atualizacoes = FirebaseDatabase.getInstance().getReference("atualizacao");

        data_atualizacoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                atualizacoes = new ArrayList<String>();
                preenche_array_atualizacoes(dataSnapshot);
                Log.d("a t u a l i z a c a o", "AQUI NO VALUE");
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

        Log.d("a t u a l i z a c a o", "AQUI NA LISTA");

        lista_atualizacoes.setAdapter(adaptador);

        lista_atualizacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ok) {
                    String id_amigo = amigos.get(position).getId();

                    mDatabase.child(id_amigo).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Usuario amigo = dataSnapshot.getValue(Usuario.class);
                            Intent intent = new Intent(MainActivity.this, Perfil.class);
                            intent.putExtra("objeto", amigo);
                            intent.putExtra("caso", "0");
                            intent.putExtra("ID_USUARIO", user.getID());
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void preenche_array_atualizacoes(DataSnapshot dataSnapshot){
        ata = new Atualizacoes();
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for(int i = 0; i < amigos.size(); i++) {
                if (snapshot.getKey().equals(amigos.get(i).getId())) {
                    ata = snapshot.getValue(Atualizacoes.class);
                    atualizacoes.add(amigos.get(i).getNome() + " " + ata.get_last());
                    break;
                }
            }
        }
    }

    private void set_database_amigos(){

        data_amigos = FirebaseDatabase.getInstance().getReference("amigos");

        data_amigos.child(user.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                amigos = new ArrayList<Amigo>();
                preenche_array_amigos(dataSnapshot);
                set_database_atualizacoes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            amigos.add(snapshot.getValue(Amigo.class));
        }
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
                set_database_amigos();
                set_database_eventos();
                //checa_badges();
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

        nome_usuario = (TextView) findViewById(R.id.main_nome_usuario);

        lista_atualizacoes = (ListView) findViewById(R.id.main_lista_atualizacoes);
        lista_atualizacoes.setVisibility(View.VISIBLE);

        lista_eventos = (ListView) findViewById(R.id.main_lista_eventos);
        lista_eventos.setVisibility(View.GONE);

        botao_lista_atu = (Button) findViewById(R.id.botao_ver_atualizacoes);
        botao_lista_atu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_atualizacoes.setVisibility(View.VISIBLE);
                lista_eventos.setVisibility(View.GONE);
            }
        });

        botao_lista_eve = (Button) findViewById(R.id.botao_ver_eventos);
        botao_lista_eve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_eventos.setVisibility(View.VISIBLE);
                lista_atualizacoes.setVisibility(View.GONE);
            }
        });

        desafio_semanal = (ImageView) findViewById(R.id.iv_desafio_semanal);

        desafio_semanal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop_up_desafio_semanal();
            }
        });

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

    @Override
    public void onRestart() {
        super.onRestart();

        if(user != null) {
            Modfica_usuario mod = new Modfica_usuario(user.getID(), user);
            boolean level_up = mod.checa_lv();

            if (level_up) {
                pop_up_level_up(mod.getUser());
                checa_badges();
            }
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
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_perfil){
            //Vai para a tela de lista de amigos
            if(ok) {
                Intent intent = new Intent(MainActivity.this, Perfil.class);
                intent.putExtra("objeto", user);
                intent.putExtra("caso", "0");
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_busca){
            if(ok) {
                //Vai para a tela de lista de amigos
                Intent intent = new Intent(MainActivity.this, Buscar.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_lista_pedidos){
            if(ok) {
                //Vai para a tela de lista de amigos
                Intent intent = new Intent(MainActivity.this, Adiciona_amigo.class);
                intent.putExtra("ID_USUARIO", user.getID());
                intent.putExtra("objeto", user);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId() == R.id.menu_lista_materias) {
            if (ok) {
                Intent intent = new Intent(MainActivity.this, Lista_materias.class);
                intent.putExtra("ID_USUARIO", user.getID());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        }
        else if (item.getItemId() == R.id.menu_logout){

            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

}
