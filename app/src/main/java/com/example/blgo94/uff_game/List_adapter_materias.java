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

public class List_adapter_materias extends ArrayAdapter<Materia> {

    List<Materia> materias;

    Context context;

    int resource;

    String dia;

    public List_adapter_materias(Context context, int resource, List<Materia> materias, String dia){
        super(context,resource,materias);

        this.materias = materias;
        this.context = context;
        this.resource = resource;
        this.dia = dia;
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
        TextView textViewMateria = view.findViewById(R.id.lista_materias_nome);
        TextView textViewhorario = view.findViewById(R.id.lista_materias_horarios);
        TextView textViewfaltas = view.findViewById(R.id.lista_materias_faltas);


        //getting the user of the specified position
        Materia materia = materias.get(position);

        textViewMateria.setText(materia.getNome());
        textViewhorario.setText(materia.get_horario_local(dia));
        textViewfaltas.setText(Integer.toString(materia.total_faltas()));

        //finally returning the view
        return view;
    }
}
