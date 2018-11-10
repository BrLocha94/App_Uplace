package com.example.blgo94.uff_game;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable {

    private String ID;
    private String user_name;
    private String course;
    private String level;
    private String score;
    private String ProfilePic;
    private String IdBadge1;
    private String IdBadge2;
    private String IdBadge3;

    //DataSnapshot
    public Usuario(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    //Construtor normal
    public Usuario(String ID, String user_name, String course, String level, String score, String ProfilePic,
                   String IdBadge1, String IdBadge2, String IdBadge3){
        this.ID = ID;
        this.user_name = user_name;
        this.course = course;
        this.level = level;
        this.score = score;
        this.ProfilePic = ProfilePic;
        this.IdBadge1 = IdBadge1;
        this.IdBadge2 = IdBadge2;
        this.IdBadge3 = IdBadge3;
    }

    //Construtor Parcel
    private Usuario(Parcel in) {
        ID = in.readString();
        user_name = in.readString();
        course = in.readString();
        level = in.readString();
        score = in.readString();
        ProfilePic = in.readString();
        IdBadge1 = in.readString();
        IdBadge2 = in.readString();
        IdBadge3 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(ID);
        dest.writeString(user_name);
        dest.writeString(course);
        dest.writeString(level);
        dest.writeString(score);
        dest.writeString(ProfilePic);
        dest.writeString(IdBadge1);
        dest.writeString(IdBadge2);
        dest.writeString(IdBadge3);
    }

    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {

        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setUser_name(String user_name){
        this.user_name = user_name;
    }

    public String getUser_name(){
        return this.user_name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse(){
        return this.course;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel(){
        return this.level;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore(){
        return this.score;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getProfilePic(){
        return this.ProfilePic;
    }

    public void setIdBadge1(String idBadge1) {
        IdBadge1 = idBadge1;
    }

    public String getIdBadge1(){
        return this.IdBadge1;
    }

    public void setIdBadge2(String idBadge2) {
        IdBadge2 = idBadge2;
    }

    public String getIdBadge2(){
        return this.IdBadge2;
    }

    public void setIdBadge3(String idBadge3) {
        IdBadge3 = idBadge3;
    }

    public String getIdBadge3(){
        return this.IdBadge3;
    }

}
