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

    DatabaseReference data;

    private String user_name;

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
                mapa_update();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        mMap.addMarker(new MarkerOptions().position(netos).title("Neto's PUB"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(netos, 20));

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

                return true;
            case R.id.amigos_map:
                //CARREGA AMGOS
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
