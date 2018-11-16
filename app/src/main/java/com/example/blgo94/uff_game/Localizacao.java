package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Localizacao {

    private ArrayList<String> localizacoes;
    private String hora;
    private String id;

    //DataSnapshot
    public Localizacao(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Localizacao(ArrayList<String> loc, String hora, String id){
        this.localizacoes = loc;
        this.hora = hora;
        this.id = id;
    }

    public ArrayList<String> getLocalizacoes() {
        return localizacoes;
    }

    public void setLocalizacoes(ArrayList<String> localizacoes) {
        this.localizacoes = localizacoes;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
