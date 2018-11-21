package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Amigo {

    private String id;
    private String nome;
    private String curso;

    //DataSnapshot
    public Amigo(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Amigo(String id, String nome, String curso){
        this.id = id;
        this.nome = nome;
        this.curso = curso;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCurso() {
        return curso;
    }
}
