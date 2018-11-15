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

public class List_adapter_provas extends ArrayAdapter<Prova> {

    List<Prova> provas;

    Context context;

    int resource;

    public List_adapter_provas(Context context, int resource, List<Prova> provas){
        super(context,resource,provas);

        this.provas = provas;
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
        TextView textViewMateria = view.findViewById(R.id.lista_provas_materia);
        TextView textViewData = view.findViewById(R.id.lista_provas_data);
        TextView textViewNota = view.findViewById(R.id.lista_provas_nota);


        //getting the user of the specified position
        Prova prova = provas.get(position);

        textViewMateria.setText(prova.getMateria());
        textViewData.setText(prova.getData());
        textViewNota.setText(prova.getNota());

        //finally returning the view
        return view;
    }
}
