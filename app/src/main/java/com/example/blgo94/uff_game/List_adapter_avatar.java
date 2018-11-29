package com.example.blgo94.uff_game;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class List_adapter_avatar extends ArrayAdapter<String> {

    List<String> avatares;

    Context context;

    int resource;

    //Storage do firebase
    private StorageReference mStorageRef_avatar;

    public List_adapter_avatar(Context context, int resource, List<String> avatares){
        super(context,resource,avatares);

        this.avatares = avatares;
        this.context = context;
        this.resource = resource;
    }


    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        ImageView imageView = view.findViewById(R.id.iv_style_avatar);

        //getting the user of the specified position
        String avatar = avatares.get(position);

        String ProfilePic = "gs://uplace-ff0b3.appspot.com/avatar/" + avatar;

        //adding values to the list item
        mStorageRef_avatar = FirebaseStorage.getInstance().getReferenceFromUrl(ProfilePic);

        //Avatar
        Glide.with(context)
                .load(mStorageRef_avatar)
                .apply(new RequestOptions().override(64,64))
                .into(imageView);

        //finally returning the view
        return view;
    }
}
