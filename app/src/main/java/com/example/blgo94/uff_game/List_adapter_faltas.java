package com.example.blgo94.uff_game;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class List_adapter_faltas extends ArrayAdapter<String> {

    List<String> faltas;

    Context context;

    int resource;

    int count = 0;

    public List_adapter_faltas(Context context, int resource, List<String> faltas){
        super(context,resource,faltas);

        this.faltas = faltas;
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
        TextView post_it_1 = view.findViewById(R.id.post_it_1);
        TextView post_it_2 = view.findViewById(R.id.post_it_2);

        post_it_1.setText(faltas.get(position));

        //finally returning the view
        return view;
    }
}
