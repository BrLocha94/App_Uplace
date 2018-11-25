package com.example.blgo94.uff_game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Editar_perfil extends AppCompatActivity {

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    //Storage do firebase
    private StorageReference mStorageRef_avatar;

    //Locais de carregamento das imagens
    private ImageView avatar;

    //Usuário logado
    public Usuario usuario;

    private TextView perfil_nome;

    //Edittexts da edição do perfil
    private EditText perfil_nome_informado;
    private EditText perfil_curso;

    //Botao
    private Button edita_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        usuario = (Usuario) getIntent().getParcelableExtra("objeto");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        avatar = (ImageView) findViewById(R.id.editar_perfil_avatar);

        carrega_imagens(usuario.getProfilePic());

        perfil_nome = (TextView) findViewById(R.id.perfil_nome);

        perfil_nome_informado = (EditText) findViewById(R.id.perfil_edita_nome);
        perfil_curso = (EditText) findViewById(R.id.perfil_edita_curso);

        edita_perfil = (Button) findViewById(R.id.botao_edita_perfil);

        edita_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean ok = checa_problemas();

                if(ok) {
                    usuario.setUser_name(perfil_nome.getText().toString());
                    usuario.setCourse(perfil_curso.getText().toString());

                    mDatabase.child(usuario.getID()).setValue(usuario);

                    finish();
                }
            }
        });

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

        /*
        //reseta os erros
        perfil_nome.setError(null);
        perfil_curso.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = perfil_nome.getText().toString();
        String curso_string = perfil_curso.getText().toString();

        //primeiro check: se não foi informado o nome
        if (TextUtils.isEmpty(nome_string)) {
            perfil_nome.setError(getString(R.string.erro_vazio));
            foco = perfil_nome;
            prossegue = false;
        }
        //Chave de segurança do email
        else if (!chave_tamanho(nome_string)) {
            perfil_nome.setError(getString(R.string.erro_tamanho));
            foco = perfil_nome;
            prossegue = false;
        }


        //primeiro check: se não foi informado o curso
        if (TextUtils.isEmpty(curso_string)) {
            perfil_curso.setError(getString(R.string.erro_vazio));
            foco = perfil_curso;
            prossegue = false;
        }
        //se o tamanho é maior que o permitido
        else if (!chave_tamanho(curso_string)) {
            perfil_curso.setError(getString(R.string.erro_tamanho));
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

    public boolean chave_tamanho(String nome){
        if(nome.length() > 3){
            return true;
        }
        return false;

        */
        return true;
    }
}
