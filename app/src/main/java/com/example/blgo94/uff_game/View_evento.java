package com.example.blgo94.uff_game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class View_evento extends AppCompatActivity {

    public TextView evento_nome;
    public TextView evento_data;
    public TextView evento_local;
    public TextView evento_org;
    public TextView evento_desc;

    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_evento);

        evento = (Evento) getIntent().getParcelableExtra("evento");

        Log.d("mimimimimimmimimimimim", "onCreate: "+ evento.getNome());

        evento_nome = (TextView) findViewById(R.id.tv_evento_nome);
        evento_data = (TextView) findViewById(R.id.tv_evento_data);
        evento_local = (TextView) findViewById(R.id.tv_evento_local);
        evento_org = (TextView) findViewById(R.id.tv_evento_org);
        evento_desc = (TextView) findViewById(R.id.tv_evento_desc);

        evento_nome.setText(evento.getNome());
        evento_data.setText(evento.getData());
        evento_local.setText(evento.getLocal());
        evento_org.setText(evento.getCriador());
        evento_desc.setText(evento.getDescricao());
    }
}
