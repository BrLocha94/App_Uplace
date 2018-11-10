package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Falta implements Parcelable {

    private String data;
    private String motivo;
    private String materia_perdida;

    //DataSnapshot
    public Falta(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Falta(String data, String motivo, String materia_perdida){

        this.data = data;
        this.motivo = motivo;
        this.materia_perdida = materia_perdida;
    }

    //Construtor Parcel
    private Falta(Parcel in) {
        data = in.readString();
        motivo = in.readString();
        materia_perdida = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(data);
        dest.writeString(motivo);
        dest.writeString(materia_perdida);
    }

    public static final Parcelable.Creator<Falta> CREATOR = new Parcelable.Creator<Falta>() {

        public Falta createFromParcel(Parcel in) {
            return new Falta(in);
        }

        public Falta[] newArray(int size) {
            return new Falta[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMateria_perdida() {
        return materia_perdida;
    }

    public void setMateria_perdida(String materia_perdida) {
        this.materia_perdida = materia_perdida;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

}
