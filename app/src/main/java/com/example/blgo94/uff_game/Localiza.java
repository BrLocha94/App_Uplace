package com.example.blgo94.uff_game;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;

public class Localiza extends AppCompatActivity implements LocationListener{

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1000 * 60 * 1

    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    private String user_name;

    DatabaseReference data;
    DatabaseReference data_users;
    DatabaseReference data_amigos;

    private int contador = 0;

    private ListView lista;

    private Localizacao loc_usuario = new Localizacao();
    private Localizacao loc_proximo = new Localizacao();

    private ArrayList<String> ids_amigos = new ArrayList<String>();

    private ArrayList<String> ids_proximos = new ArrayList<String>();

    private ArrayList<Usuario> users = new ArrayList<Usuario>();

    TextView texto_lista;

    private double raio = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_proximos);

        //Checar se o GPS está ligado
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, R.string.ligue_seu_gps , Toast.LENGTH_SHORT).show();

            finish();
        }

        user_name = (String) getIntent().getStringExtra("ID_USUARIO");

        texto_lista = (TextView)  findViewById(R.id.texto_lista_usuarios);

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsRejected.toArray(
                                                new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Localiza.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");

        //ATUALIZA DATABASE

        ArrayList<String> locations = new ArrayList<String>();
        locations.add(Double.toString(loc.getLatitude()));
        locations.add(Double.toString(loc.getLongitude()));

        Localizacao atual = new Localizacao(locations, DateFormat.getTimeInstance().format(loc.getTime()), user_name);

        data = FirebaseDatabase.getInstance().getReference("loc");
        data.child(user_name).setValue(atual);

        if(contador == 0){

            data_amigos = FirebaseDatabase.getInstance().getReference("amigos");

            data_amigos.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    preenche_array_amigos(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            data = FirebaseDatabase.getInstance().getReference("loc");

            data.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loc_usuario = dataSnapshot.getValue(Localizacao.class);
                    checa_usuarios_proximos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Inicia activity de lista
            /*
            Intent intent = new Intent(Localiza.this, Lista_usuarios_proximos.class);
            intent.putExtra("ID_USUARIO", user_name);
            startActivity(intent);
            */
            contador ++;
        }

    }

    private void preenche_array_amigos(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            ids_amigos.add(snapshot.getKey());
        }
    }

    private void checa_usuarios_proximos(){
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preenche_array(dataSnapshot);
                busca_users();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void busca_users(){

        data_users = FirebaseDatabase.getInstance().getReference("users");

        data_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                set_users(dataSnapshot);
                set_adaptador();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void set_adaptador(){
        lista = (ListView) findViewById(R.id.lista_usuarios_proximos);

        if(users.size() > 0) {

            lista.setVisibility(View.VISIBLE);
            texto_lista.setVisibility(View.GONE);

            List_adapter_usuarios adaptador = new List_adapter_usuarios(this, R.layout.estilo_amigos_lista, users);

            lista.setAdapter(adaptador);

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Usuario user_ref = (Usuario) parent.getItemAtPosition(position);

                    Intent intent = new Intent(Localiza.this, Perfil.class);
                    intent.putExtra("objeto", user_ref);
                    if (valida(user_ref.getID())) {
                        intent.putExtra("caso", "0");
                    } else {
                        intent.putExtra("caso", "1");
                    }
                    intent.putExtra("ID_USUARIO", user_name);
                    startActivity(intent);
                }
            });
        }
        else{
            texto_lista.setText(R.string.nenhum_usuario);
        }
    }

    private boolean valida(String alvo_id){
        for(int i = 0; i < ids_amigos.size(); i++){
            if (ids_amigos.get(i).equals(alvo_id)){
                return true;
            }
        }
        return false;
    }

    public void set_users(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for(int i = 0; i < ids_proximos.size(); i++){
                if(snapshot.getKey().equals(ids_proximos.get(i))){
                    users.add(snapshot.getValue(Usuario.class));
                }
            }
        }
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            loc_proximo = snapshot.getValue(Localizacao.class);
            if(!loc_proximo.getId().equals(loc_usuario.getId())) {
                if (distance(Double.parseDouble(loc_usuario.getLocalizacoes().get(0)), Double.parseDouble(loc_usuario.getLocalizacoes().get(1)),
                        Double.parseDouble(loc_proximo.getLocalizacoes().get(0)), Double.parseDouble(loc_proximo.getLocalizacoes().get(1)))
                        <= raio) {
                    ids_proximos.add(loc_proximo.getId());
                }
            }
        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
