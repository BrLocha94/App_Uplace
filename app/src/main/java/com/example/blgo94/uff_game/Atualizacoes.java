package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Atualizacoes {

    private ArrayList<String> atualizacao;

    //DataSnapshot
    public Atualizacoes(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Atualizacoes(ArrayList<String> atualizacao){
        this.atualizacao = atualizacao;
    }

    public void setAtualizacao(ArrayList<String> atualizacao) {
        this.atualizacao = atualizacao;
    }

    public ArrayList<String> getAtualizacao() {
        return atualizacao;
    }

    public String get_last(){
        return atualizacao.get(0);
    }

    public void add_info(String info){
        atualizacao.add(0, info);
    }
}
