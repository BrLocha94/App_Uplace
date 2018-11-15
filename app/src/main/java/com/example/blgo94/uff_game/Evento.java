package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Evento implements Parcelable {

    private String criador;
    private String nome;
    private String data;
    private String descricao;
    private String local;
    private ArrayList<String> membros;

    //DataSnapshot
    public Evento(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Evento(String criador, String nome, String data, String descricao, String local, String criador_id){
        this.criador = criador;
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
        this.membros = new ArrayList<String>();
        this.membros.add(criador_id);
    }

    //Construtor Parcel
    private Evento(Parcel in) {
        criador = in.readString();
        nome = in.readString();
        data = in.readString();
        descricao = in.readString();
        local = in.readString();
        membros = in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(criador);
        dest.writeString(nome);
        dest.writeString(data);
        dest.writeString(descricao);
        dest.writeString(local);
        dest.writeList(membros);
    }

    public static final Parcelable.Creator<Evento> CREATOR = new Parcelable.Creator<Evento>() {

        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        public Evento[] newArray(int size) {
            return new Evento[size];
        }
    };

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public String getCriador() {
        return criador;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getLocal() {
        return local;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public ArrayList<String> getMembros() {
        return membros;
    }

    public void setMembros(ArrayList<String> membros) {
        this.membros = membros;
    }
}
