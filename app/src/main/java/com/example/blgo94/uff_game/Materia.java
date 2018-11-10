package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Materia implements Parcelable {

    private String nome;
    private String professor;
    private ArrayList<String> dias;
    private ArrayList<String> horarios ;
    private ArrayList<String> locais;
    private int total_dias;
    private int carga_horaria;
    private ArrayList<Falta> faltas;

    //DataSnapshot
    public Materia(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Materia(String nome, String professor, int carga_horaria){
        this.nome = nome;
        this.professor = professor;
        this.dias = new ArrayList<String>();
        this.horarios = new ArrayList<String>();
        this.locais = new ArrayList<String>();
        this.carga_horaria = carga_horaria;
        this.faltas = new ArrayList<Falta>();
    }

    //Construtor Parcel
    private Materia(Parcel in) {
        nome = in.readString();
        professor = in.readString();
        dias = in.readArrayList(null);
        horarios = in.readArrayList(null);
        locais = in.readArrayList(null);
        carga_horaria = in.readInt();
        faltas = in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(nome);
        dest.writeString(professor);
        dest.writeList(dias);
        dest.writeList(horarios);
        dest.writeList(locais);
        dest.writeInt(carga_horaria);
        dest.writeList(faltas);
    }

    public static final Parcelable.Creator<Materia> CREATOR = new Parcelable.Creator<Materia>() {

        public Materia createFromParcel(Parcel in) {
            return new Materia(in);
        }

        public Materia[] newArray(int size) {
            return new Materia[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public ArrayList<String> getDias() {
        return dias;
    }

    public void setDias(ArrayList<String> dias) {
        this.dias = dias;
    }

    public void add_dia(String dia){
        dias.add(dia);
    }

    public ArrayList<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(ArrayList<String> horarios) {
        this.horarios = horarios;
    }

    public void add_horario(String horario){
        horarios.add(horario);
    }

    public ArrayList<String> getLocais() {
        return locais;
    }

    public void setLocais(ArrayList<String> local) {
        this.locais = local;
    }

    public void add_local(String local){
        locais.add(local);
    }

    public int getCarga_horaria() {
        return carga_horaria;
    }

    public void setCarga_horaria(int carga_horaria) {
        this.carga_horaria = carga_horaria;
    }

    public ArrayList<Falta> getFaltas() {
        return faltas;
    }

    public void setFaltas(ArrayList<Falta> faltas) {
        this.faltas = faltas;
    }

    public float porcentagem_faltas(){

        if(faltas.size() == 0){
            return 0;
        }

        float porcentagem = (100*2*faltas.size())/carga_horaria;

        return porcentagem;
    }

    public int faltas_permitidas(){

        int faltas = carga_horaria/8;

        return faltas;
    }

    public boolean pode_faltar(){

        float porcentagem = this.porcentagem_faltas();

        if(porcentagem > 25) return false;

        return true;

    }

    public void reset_arrays(){
        dias.clear();
        horarios.clear();
        locais.clear();
    }

    public int total_faltas(){
        if(this.faltas == null){
            return 0;
        }
        return this.faltas.size();
    }

    public boolean checa_dia(String dia){
        for(int j = 0; j < dias.size(); j++){
            if(dias.get(j).equals(dia)){
                return true;
            }
        }
        return false;
    }

    /*
    public String toString(){

        String msg = new String();

        for(int i = 0; i < dias.size(); i++){

            msg = msg + dias.get(i) + " - " + horarios.get(i) + " - " + local.get(i);

            msg = msg + "\n";
        }

        return msg;
    }
    */

}
