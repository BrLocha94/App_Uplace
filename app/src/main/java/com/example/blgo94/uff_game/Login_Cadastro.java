package com.example.blgo94.uff_game;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Login_Cadastro extends AppCompatActivity {

    public final String TAG = "LOGIN/CADASTRO ACTIVTY";

    //Autenticação do firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    //Botoes da activity
    Button login;
    Button cadastrar;

    //Recebem os inputs do usuário e checam se é válido
    public EditText email;
    public EditText senha;

    private boolean ok = true;

    //Usado para controlar o UI
    int controle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cadastro);

        FirebaseApp.initializeApp(this);

        //Instancias de identificação do usuário
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        login = (Button) findViewById(R.id.botao_sign_in);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //PEGA O ID+SENHA INFORMADOS PELO USUÁRIO E CHAMA A FUNÇÃO SIGN IN
                //ANTES, O ID E SENHA PASSAM PELA CHECAGEM DE SEGURANÇA

                //preenche as variaveis EditText com as informações fornecidas
                email = (EditText) findViewById(R.id.email_informado);
                senha = (EditText) findViewById(R.id.senha_informada);

                //checa as chaves de segurança
                ok = checa_problemas();

                if(ok){
                    sign_in(email.getText().toString().trim(), senha.getText().toString().trim());
                }
                else{
                    Toast.makeText(Login_Cadastro.this, "Informe o email e senha corretamente.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cadastrar = (Button) findViewById(R.id.botao_sign_up);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //PEGA O ID+SENHA INFORMADOS PELO USUÁRIO E CHAMA A FUNÇÃO SIGN UP
                //ANTES, O ID E SENHA PASSAM PELA CHECAGEM DE SEGURANÇA

                //preenche as variaveis EditText com as informações fornecidas
                email = (EditText) findViewById(R.id.email_informado);
                senha = (EditText) findViewById(R.id.senha_informada);

                //checa as chaves de segurança
                ok = checa_problemas();

                if(ok){
                    sign_up(email.getText().toString().trim(), senha.getText().toString().trim());
                }
                else{
                    Toast.makeText(Login_Cadastro.this, "Informe o email e senha corretamente.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checa_problemas() {

        //reseta os erros
        email.setError(null);
        senha.setError(null);

        //boleano que checa as chaves
        boolean prossegue = true;

        //view que foca no local do erro
        View foco = null;

        //recebe as Strings das informações para serem checadas
        String nome_string = email.getText().toString();
        String valor_string = senha.getText().toString();

        //primeiro check: se não foi informado o nome
        if (TextUtils.isEmpty(nome_string)) {
            email.setError(getString(R.string.erro_vazio));
            foco = email;
            prossegue = false;
        }
        //Chave de segurança do email
        else if (!chave_email(nome_string)) {
            email.setError(getString(R.string.erro_email));
            foco = email;
            prossegue = false;
        }


        if (TextUtils.isEmpty(valor_string)) {
            senha.setError(getString(R.string.erro_vazio));
            foco = senha;
            prossegue = false;
        }
        //Deve ter uma chave para checar o tamanho da senha
        else if (!chave_senha(valor_string)) {
            senha.setError(getString(R.string.erro_senha));
            foco = senha;
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

    private void sign_in(String email, String senha){

        //controle de login = 1
        this.controle = 1;

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(Login_Cadastro.this, "Login feito com sucesso", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login_Cadastro.this, "Authentication falhou, verifique seu email e senha.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void sign_up(String email, String senha){

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Login_Cadastro.this, "Verifique seu email para confirmação...", Toast.LENGTH_SHORT).show();

                            set_database();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login_Cadastro.this, "Ocorreu um erro no cadastro...",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void set_database() {

        String array [] = new String[2];
        array = email.getText().toString().split("@");

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        Usuario user = new Usuario(array[0], array[0], "nenhum", "0", "0", "default.jpeg", "default_b.jpeg", "", "");

        mDatabase.child(user.getID()).setValue(user);
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            if(controle == 1){

                //SALVAR DADOS DE EMAIL E SENHA EM UM BANCO
                //RETORNAR PARA A ACTIVITY MAIN
                //NO CASO A MAIN SERÁ ABERTA
                Intent intent = new Intent(Login_Cadastro.this, MainActivity.class);
                startActivity(intent);
                this.controle = 0;
            }
        } else {
            this.controle = 0;
        }

    }

    private boolean chave_email(String valor){
        //Checa se o email é o id.uff.br
        if(valor.contains("@id.uff.br")){
            return true;
        }
        return false;
    }

    private boolean chave_senha(String valor){
        //Checa se tem entre 6 e 12 caracteres
        if((valor.length() >= 6) && (valor.length() <= 12)){
            return true;
        }
        return false;
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
        finish();
    }



}
