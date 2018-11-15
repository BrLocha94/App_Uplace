package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Prova implements Parcelable {

    private String id;
    private String materia;
    private String data;
    private String nota;

    //DataSnapshot
    public Prova(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Prova(String id, String materia, String data, String nota){
        this.id = id;
        this.materia = materia;
        this.data = data;
        this.nota = nota;
    }

    //Construtor Parcel
    private Prova(Parcel in) {
        id = in.readString();
        materia = in.readString();
        data = in.readString();
        nota = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(id);
        dest.writeString(materia);
        dest.writeString(data);
        dest.writeString(nota);
    }

    public static final Parcelable.Creator<Prova> CREATOR = new Parcelable.Creator<Prova>() {

        public Prova createFromParcel(Parcel in) {
            return new Prova(in);
        }

        public Prova[] newArray(int size) {
            return new Prova[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }
}
