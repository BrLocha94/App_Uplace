package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class Logica_badges {

    private ArrayList<String> array_ids_materias = new ArrayList<String>();
    private ArrayList<Prova> array_provas = new ArrayList<Prova>();

    private ArrayList<String> array_campi = new ArrayList<String>();


    private String count;

    public Logica_badges(){

    }

    //badge calouro ---- lv >= 1
    public boolean calouro(String level_usuario){
        if(Integer.parseInt(level_usuario) >= 1){
            return true;
        }
        return false;
    }

    //badge veterano ---- lv >= 8
    public boolean veterano(String level_usuario){
        if(Integer.parseInt(level_usuario) >= 8){
            return true;
        }
        return false;
    }

    //2 provas: listar por data e verificar se há duas provas ou mais no mesmo dia
    public boolean duas_provas(final String id_usuario){

        DatabaseReference data = FirebaseDatabase.getInstance().getReference("provas").child(id_usuario);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_ids_materias = new ArrayList<String>();
                set_array(dataSnapshot);
                set_provas(id_usuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return checa_logica_duas_provas();
    }

    //- 5 provas: listar por semana e verificar se há cinco provas ou mais na mesma semana
    public boolean cinco_provas(final String id_usuario){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("provas").child(id_usuario);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_ids_materias = new ArrayList<String>();
                set_array(dataSnapshot);
                set_provas(id_usuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return checa_logica_cinco_provas();
    }

    //badge de bandeco - mais de 15 check-in no bandeco do grags ou pv
    public boolean bandejao(final String id_usuario){

        final DatabaseReference data = FirebaseDatabase.getInstance().getReference("badge_req");

        final boolean ok = false;

        data.child("bandejao").child(id_usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count = dataSnapshot.getValue(String.class);
                if(count.isEmpty()){
                    count = "1";
                    data.child("bandejao").child(id_usuario).setValue(count);
                }
                else{
                    count = Integer.toString(Integer.parseInt(count) + 1);
                    data.child("bandejao").child(id_usuario).setValue(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(Integer.parseInt(count) >= 15){
            return true;
        }
        else {
            return false;
        }

    }

    //badge de campi - pelo menos um check in em todos os campus da uff
    public boolean campi(final String log, final String id_usuario){

        final DatabaseReference data = FirebaseDatabase.getInstance().getReference("badge_req");

        data.child("campi").child(id_usuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array_campi(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return checa_logica_campi(id_usuario, log);
    }

    private boolean checa_logica_campi(final String id_usuario, String log){
        String array[] = {"UFF PV", "UFF Gragoatá", "Valonguinho", "Vet UFF", "Farmácia UFF",
                "Direito", "IACS", "IACS II", "Biomédico"};

        if(array_campi.size() == 0){
            for(int i = 0; i < array.length; i++){
                array_campi.add("0");
            }
            salva_array_campi(id_usuario);
        }

        for(int i = 0; i < array.length; i++){
            if(array[i].equals(log)){
                array_campi.set(i, "1");
                salva_array_campi(id_usuario);
                break;
            }
        }

        for (int i = 0; i < array.length; i++){
            if(array_campi.get(i).equals("0")){
                return false;
            }
        }
        return true;
    }

    private void salva_array_campi(String id_usuario){
        final DatabaseReference data = FirebaseDatabase.getInstance().getReference("badge_req");

        data.child(id_usuario).setValue(array_campi);
    }

    private void preenche_array_campi(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            array_campi.add(snapshot.getValue(String.class));
        }
    }

    private boolean checa_logica_cinco_provas(){
        if(array_provas.size() <= 4) return false;

        int dia;
        int mes;
        int cout;

        for(int i = 0; i < array_provas.size(); i ++){
            cout = 0;
            dia = array_provas.get(i).get_dia();
            mes = array_provas.get(i).get_mes();
            for(int j = 0; j < array_provas.size(); j ++){
                if(i != j){
                    int teste_mes = array_provas.get(j).get_mes() - mes;
                    if((teste_mes >= -1) && (teste_mes <= 1)){
                        int teste_dia = array_provas.get(j).get_dia() - dia;
                        if((teste_dia >= -1) && (teste_dia <= 1)){
                           cout ++;
                        }
                    }
                }
            }
            if (cout >= 5) return true;
        }

        return false;
    }

    private boolean checa_logica_duas_provas(){
        if( array_provas.size() <= 0) return false;

        String data;

        for(int i = 0; i < array_provas.size(); i++){
            data = array_provas.get(i).getData();
            for (int j = 0; j < array_provas.size(); j++){
                if(i != j){
                    if(data.equals(array_provas.get(j).getData())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void set_provas(String id_usuario){
        DatabaseReference data_02 = FirebaseDatabase.getInstance().getReference("provas").child(id_usuario);

        array_provas = new ArrayList<Prova>();

        for(int i = 0; i < array_ids_materias.size(); i++){
            data_02.child(array_ids_materias.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    set_array_provas(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void set_array_provas(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            array_provas.add(snapshot.getValue(Prova.class));
            Log.d("mimimmimimimimimimimim", "set_array_provas: " + array_provas.get(0).getData());
        }
    }

    private void set_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            array_ids_materias.add(snapshot.getKey());
            Log.d("mimimimimimimimimimimi", "set_array: " + snapshot.getKey());
        }
    }


    //- 5 provas: listar por semana e verificar se há cinco provas ou mais na mesma semana

    //badge de bandeco - mais de 15 check-in no bandeco do grags ou pv

    //badge de campi - pelo menos um check in em todos os campus da uff

    //badge de reprovado -  analisar a nota das provas, fazer a média e ver se o aluno reprovou

    //badge de aula sábado - ver a lista de matérias e ver se há aula no sábado

}
