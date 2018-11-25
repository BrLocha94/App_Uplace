package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Desafio {

    private String id_desafio;
    private String id_desafio_concluido;
    private ArrayList<String> vencedores;

    //DataSnapshot
    public Desafio(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public void setId_desafio(String id_desafio) {
        this.id_desafio = id_desafio;
    }

    public String getId_desafio() {
        return id_desafio;
    }

    public void setId_desafio_concluido(String id_desafio_concluido) {
        this.id_desafio_concluido = id_desafio_concluido;
    }

    public String getId_desafio_concluido() {
        return id_desafio_concluido;
    }

    public void setVencedores(ArrayList<String> vencedores) {
        this.vencedores = vencedores;
    }

    public ArrayList<String> getVencedores() {
        return vencedores;
    }
}
