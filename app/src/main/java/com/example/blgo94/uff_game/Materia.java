package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Materia implements Parcelable {

    private String nome;
    private String professor;
    private ArrayList<String> dias;
    private ArrayList<String> horarios ;
    private String local;
    private int carga_horaria;
    //private ArrayList<Falta> faltas;
    private ArrayList<String> faltas;

    //DataSnapshot
    public Materia(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Materia(String nome, String professor, String local, int carga_horaria){
        this.nome = nome;
        this.professor = professor;
        this.dias = new ArrayList<String>();
        this.horarios = new ArrayList<String>();
        this.local = local;
        this.carga_horaria = carga_horaria;
        //this.faltas = new ArrayList<Falta>();
        this.faltas = new ArrayList<String>();
    }

    //Construtor Parcel
    private Materia(Parcel in) {
        nome = in.readString();
        professor = in.readString();
        dias = in.readArrayList(null);
        horarios = in.readArrayList(null);
        local = in.readString();
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
        dest.writeString(local);
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

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getCarga_horaria() {
        return carga_horaria;
    }

    public void setCarga_horaria(int carga_horaria) {
        this.carga_horaria = carga_horaria;
    }


    public ArrayList<String> getFaltas() {
        return faltas;
    }

    public void setFaltas(ArrayList<String> faltas) {
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
    }

    public int total_faltas(){
        if(this.faltas == null){
            return 0;
        }
        return this.faltas.size();
    }

    public String get_format_local_hora(){
        String retorna = local;

        for(int i = 0; i < dias.size(); i++){
            retorna = retorna + " " + dias.get(i) + ":" + horarios.get(i);
        }

        return retorna;
    }

    public String get_format_carga(){
        String retorna = "Carga: " + Integer.toString(this.carga_horaria);

        return retorna;
    }

    public String get_horario_local(String dia){
        for(int i = 0; i < dias.size(); i++){
            if(dias.get(i).equals(dia)){
                String string = local + " " + horarios.get(i);
                return string;
            }
        }
        return "Não informado";
    }

    public void add_falta(String falta){
        this.faltas.add(falta);
    }


}
