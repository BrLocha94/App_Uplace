package com.example.blgo94.uff_game;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Modfica_usuario {

    private Usuario user;

    private DatabaseReference data;

    public Modfica_usuario(String id, Usuario user){
        this.data = FirebaseDatabase.getInstance().getReference("users");
        this.user = user;
    }

    public boolean add_score(int score){
        user.setScore(Integer.toString(Integer.parseInt(user.getScore()) + score));

        data.child(user.getID()).setValue(user);

        return checa_lv();
    }

    public boolean checa_lv(){
        int acumulador = 0;
        int count = 0;
        int score = Integer.parseInt(user.getScore());
        boolean loop = true;

        while (loop){
            acumulador = acumulador + count*10;

            if(score < acumulador){
                if(Integer.parseInt(user.getLevel()) < (count-1)){
                    user.setLevel(Integer.toString(count - 1));
                    data.child(user.getID()).setValue(user);
                    return true;
                }
                return false;
            }

            count ++;
        }

        return false;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public Usuario getUser() {
        return user;
    }
}
