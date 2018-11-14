package com.example.blgo94.uff_game;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Usuarios_proximos extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;

    //Database Realtime do firebase
    private DatabaseReference mDatabase;

    private String user_name;

    double latitude;
    double longitude;

    public Button teste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_proximos);

        //Checar se o GPS está ligado
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, "Ligue seu GPS para acessar este recurso.", Toast.LENGTH_SHORT).show();

            finish();
        }

        user_name = (String) getIntent().getStringExtra("ID_USUARIO");

        teste = (Button) findViewById(R.id.button_teste);
        teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recebe_posicao();
            }
        });

        //Checar se as permissoes do app foram concedidas
        //Caso tenham sido concedidas, inicia o serviço de traking do app
        //Caso contrário, pede permissão
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //recebe_posicao();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //Se a permissão for concedida, inicia o serviço
        //Caso contrário, fecha a activity
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //recebe_posicao();
        } else {
            Toast.makeText(this, "Por favor conceda a permissão para usar esse serviço", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //Start the TrackerService//

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void recebe_posicao() {

        //pega a localização atual
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //double longitude = location.getLongitude();
        //double latitude = location.getLatitude();

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        ArrayList<String> loc = new ArrayList<String>();
        loc.add(Double.toString(longitude));
        loc.add(Double.toString(latitude));

        Log.d("LOCALIZAÇÕES: ", loc.get(0) + "   " + loc.get(1));

        //salva no database
        mDatabase = FirebaseDatabase.getInstance().getReference("loc");
        mDatabase.child(user_name).setValue(loc);
        //checa as pessoas próximas
        //LE OS DADOS
        //PREENCHE A LISTA

    }
}
