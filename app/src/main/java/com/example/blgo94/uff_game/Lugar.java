package com.example.blgo94.uff_game;

import java.util.ArrayList;

public class Lugar {

    private String id;
    private ArrayList<String> loc;

    //DataSnapshot
    public Lugar(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Lugar(String id, ArrayList<String> loc){
        this.id = id;
        this.loc = loc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getLoc() {
        return loc;
    }

    public void setLoc(ArrayList<String> loc) {
        this.loc = loc;
    }
}
