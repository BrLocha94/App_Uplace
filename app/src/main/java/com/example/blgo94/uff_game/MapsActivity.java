package com.example.blgo94.uff_game;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<Lugar> lugares = new ArrayList<Lugar>();

    private ArrayList<Amigo> amigos = new ArrayList<Amigo>();

    private ArrayList<Localizacao> loc_amigos = new ArrayList<Localizacao>();

    private DatabaseReference data;
    private DatabaseReference data_amigos;
    private DatabaseReference data_loc_amigos;
    private DatabaseReference data_user;
    private DatabaseReference data_eventos;

    private String user_name;

    int contador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("UPLACE");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user_name = (String) getIntent().getStringExtra("ID_USUARIO");

        data = FirebaseDatabase.getInstance().getReference("lugares");

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array(dataSnapshot);
                lista_de_amigos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lista_de_amigos(){

        data_amigos = FirebaseDatabase.getInstance().getReference("amigos");

        data_amigos.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array_amigos(dataSnapshot);
                if(amigos.size() > 0){
                    localiza_amigos();
                }
                mapa_update();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void localiza_amigos(){
        data_loc_amigos = FirebaseDatabase.getInstance().getReference("loc");

        data_loc_amigos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array_loc(dataSnapshot);
                if(contador == 1){
                    mMap.clear();
                    mapa_amigos_update();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void preenche_array_loc(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for(int i = 0; i < amigos.size(); i++) {
                if (snapshot.getKey().equals(amigos.get(i).getId())) {
                    loc_amigos.add(snapshot.getValue(Localizacao.class));
                    break;
                }
            }
        }
    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            amigos.add(snapshot.getValue(Amigo.class));
        }
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            lugares.add(snapshot.getValue(Lugar.class));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng netos = new LatLng(-22.904680, -43.130028);
        //mMap.addMarker(new MarkerOptions().position(netos).title("Neto's PUB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(netos, 16));

    }

    public void mapa_amigos_update(){
        if(amigos.size() > 0) {
            Log.d("ta aqui esse mapa mald", loc_amigos.get(0).getId());
            for (int i = 0; i < loc_amigos.size(); i++) {
                double lat = Double.parseDouble(loc_amigos.get(i).getLocalizacoes().get(0));
                double lng = Double.parseDouble(loc_amigos.get(i).getLocalizacoes().get(1));
                String nome = amigos.get(i).getNome();
                String hora = loc_amigos.get(i).getHora();

                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(nome + " hora: " + hora));
            }
        }
    }

    public void mapa_update(){
        for(int i = 0; i < lugares.size(); i++){
            double lat = Double.parseDouble(lugares.get(i).getLoc().get(0));
            double lng = Double.parseDouble(lugares.get(i).getLoc().get(1));
            String local = lugares.get(i).getId();
            Log.d("TA AQUI IPOFOERWUJM", lugares.get(0).getId());
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(local));
        }
    }

    public void zoom_user(Localizacao loc){
        double lat = Double.parseDouble(loc.getLocalizacoes().get(0));
        double lng = Double.parseDouble(loc.getLocalizacoes().get(1));

        LatLng user = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(user).title("Voce esta aqui"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 18));
    }

    public void onRestart() {
        super.onRestart();
        mMap.clear();
        if(contador == 0){
            mapa_update();
        }
        else{
            mapa_amigos_update();
        }

        data_user = FirebaseDatabase.getInstance().getReference("loc");
        data_user.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Localizacao loc = dataSnapshot.getValue(Localizacao.class);
                zoom_user(loc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                //CARREGA LUGARES
                contador = 0;
                mMap.clear();
                mapa_update();
                return true;
            case R.id.amigos_map:
                //CARREGA AMGOS
                if(amigos.size() > 0) {
                    contador = 1;
                    mMap.clear();
                    mapa_amigos_update();
                }
                else{
                    Toast.makeText(this, "Voce não possui amigos adicionados...", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.check_in_map:
                Intent intent = new Intent(MapsActivity.this, Check_in.class);
                intent.putExtra("ID_USUARIO", user_name);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
