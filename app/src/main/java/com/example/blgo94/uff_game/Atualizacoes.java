package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Atualizacoes {

    private ArrayList<String> atualiz;

    //DataSnapshot
    public Atualizacoes(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Atualizacoes(ArrayList<String> atualiz){
        this.atualiz = atualiz;
    }

    public void setAtualiz(ArrayList<String> atualiz) {
        this.atualiz = atualiz;
    }

    public ArrayList<String> getAtualiz() {
        return atualiz;
    }

    public String get_last(){
        return this.atualiz.get(0);
    }

    public void add_info(String info){
        this.atualiz.add(0, info);
        if(atualiz.size() > 8){
            atualiz.remove(atualiz.size() - 1);
        }
    }
}
