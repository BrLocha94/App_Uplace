package com.example.blgo94.uff_game;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Service;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Check_in extends AppCompatActivity implements LocationListener {

    final String TAG = "Check_in";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
    private static final long MIN_TIME_BW_UPDATES = 100; // 1000 * 60 * 1

    TextView mensagem;
    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    DatabaseReference data;
    DatabaseReference data_lugares;
    DatabaseReference data_atualizacao;

    private String user_name;

    private ArrayList<Lugar> lugares = new ArrayList<Lugar>();

    private ArrayList<String> ids_proximos = new ArrayList<String>();

    private double raio = 0.03;

    private ListView lista_lugares;

    DatabaseReference data_badges;
    DatabaseReference data_badge_acess;

    Badge badge;

    Usuario user;

    boolean ok = true;

    private ArrayList<String> badges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        //Checar se o GPS está ligado
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Toast.makeText(this, R.string.ligue_seu_gps , Toast.LENGTH_SHORT).show();

            finish();
        }

        mensagem = (TextView) findViewById(R.id.text_view_check_in);

        user_name = (String) getIntent().getStringExtra("ID_USUARIO");

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

            data_lugares = FirebaseDatabase.getInstance().getReference("lugares");

            data_lugares.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lugares = new ArrayList<Lugar>();
                    preenche_array(dataSnapshot);
                    // get location
                    getLocation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void preenche_array(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
            lugares.add(snapshot.getValue(Lugar.class));
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
        new AlertDialog.Builder(Check_in.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");

        mensagem.setText(Double.toString(loc.getLatitude()));

        //ATUALIZA DATABASE

        ArrayList<String> locations = new ArrayList<String>();
        locations.add(Double.toString(loc.getLatitude()));
        locations.add(Double.toString(loc.getLongitude()));

        Localizacao atual = new Localizacao(locations, DateFormat.getTimeInstance().format(loc.getTime()), user_name);

        data = FirebaseDatabase.getInstance().getReference("loc");
        data.child(user_name).setValue(atual);


        checa_proximidades(locations);
    }

    private void checa_proximidades(ArrayList<String> loc_usuario){
        if(lugares.size() != 0) {
            ids_proximos = new ArrayList<String>();
            for (int i = 0; i < lugares.size(); i++) {

                if (distance(Double.parseDouble(loc_usuario.get(0)), Double.parseDouble(loc_usuario.get(1)),
                        Double.parseDouble(lugares.get(i).getLoc().get(0)), Double.parseDouble(lugares.get(i).getLoc().get(1)))
                        <= raio) {
                    ids_proximos.add(lugares.get(i).getId());
                }
            }
            if(ids_proximos.size() == 0){
                mensagem.setText( R.string.nao_consegue_checkin );
            }
            else{
                mensagem.setText("");
                set_adaptador();
            }
        }
    }

    public void set_adaptador(){
        lista_lugares = (ListView) findViewById(R.id.lista_lugares_checkin);

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.estilo_remove,
                R.id.nome_remove, ids_proximos);

        lista_lugares.setAdapter(adaptador);

        lista_lugares.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String id_selecionado = (String) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(Check_in.this);
                builder.setTitle(R.string.voce_esta_em + id_selecionado + "?");

                //if the response is positive in the alert
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        data_atualizacao = FirebaseDatabase.getInstance().getReference("atualizacao");

                        data_atualizacao.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Atualizacoes ata = dataSnapshot.getValue(Atualizacoes.class);
                                ata.add_info("Check in " + id_selecionado + ", " + get_data_atual() + " - " + get_hora_atual());
                                data_atualizacao.child(user_name).setValue(ata);

                                checa_badges(id_selecionado, true);

                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                //if response is negative nothing is being done
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //creating and displaying the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void checa_badges(final String log, final boolean controle){

        data_badge_acess = FirebaseDatabase.getInstance().getReference("badge_acess");

        data_badge_acess.child(user_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                badges = new ArrayList<String>();
                preenche_array_badges(dataSnapshot);
                ok = true;
                for(int i = 0; i < badges.size(); i++){
                    if(badges.get(i).equals("bandejao")){
                        ok = false;
                        break;
                    }
                }

                if(ok && controle){
                    badge_bandejao(log);
                    checa_badges(log, false);
                }
                else {
                    ok = true;

                    for (int i = 0; i < badges.size(); i++) {
                        if (badges.get(i).equals("campi")) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        badge_campi(log);
                        //checa_badges();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void badge_campi(String log){

        if(log.equals("Bandejão PV") || log.equals("Bandejão grags")) {

            Logica_badges log_bad = new Logica_badges();
            if (log_bad.campi(log, user_name)) {

                badges.add("campi");
                data_badge_acess.child(user_name).setValue(badges);

                get_usuario(user_name);

                //LIBERA BADGE DIALOG
                data_badges = FirebaseDatabase.getInstance().getReference("badges");
                data_badges.child("campi").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        badge = dataSnapshot.getValue(Badge.class);
                        pop_up_badges(badge);
                        Modfica_usuario mod = new Modfica_usuario(user_name, user);
                        boolean level_up = mod.add_score(Integer.parseInt(badge.getPontuacao()));

                        if (level_up) {
                            pop_up_level_up(mod.getUser());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private void badge_bandejao(String log){

        Log.d(TAG, "onDataChange: ENTROU AQUI MESMO AHAHAHAHAAHAHHA");

        Logica_badges log_bad = new Logica_badges();
        if (log_bad.bandejao(user_name)) {

            Log.d(TAG, "onDataChange: ENTROU AQUI MESMO AHAHAHAHAAHAHHA");

            badges.add("bandejao");
            data_badge_acess.child(user_name).setValue(badges);

            get_usuario(user_name);

            //LIBERA BADGE DIALOG
            data_badges = FirebaseDatabase.getInstance().getReference("badges");
            data_badges.child("bandejao").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    badge = dataSnapshot.getValue(Badge.class);
                    pop_up_badges(badge);
                    Modfica_usuario mod = new Modfica_usuario(user_name, user);
                    boolean level_up = mod.add_score(Integer.parseInt(badge.getPontuacao()));

                    if (level_up) {
                        pop_up_level_up(mod.getUser());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void get_usuario(String id){
        DatabaseReference data = FirebaseDatabase.getInstance().getReference("users");

        data.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = (dataSnapshot.getValue(Usuario.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pop_up_level_up(Usuario usuario){
        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("LEVEL_UP");

        TextView descricao_badge = settingsDialog.findViewById(R.id.descricao_badge);
        descricao_badge.setText("PARABENS POR SUBIR DE LV!!!");

        TextView ponto_badge = settingsDialog.findViewById(R.id.ponto_badge);
        String mensagem = "Level: " + usuario.getLevel() + " Score: " + usuario.getScore();
        ponto_badge.setText(mensagem);

        settingsDialog.show();
    }

    private void pop_up_badges(Badge badge){
        Dialog settingsDialog = new Dialog(this);

        settingsDialog.setContentView(R.layout.mostra_badge);
        settingsDialog.setTitle("BADGE");

        TextView descricao_badge = settingsDialog.findViewById(R.id.descricao_badge);
        descricao_badge.setText(badge.getDescricao());

        TextView ponto_badge = settingsDialog.findViewById(R.id.ponto_badge);
        String mensagem = badge.getPontuacao() + " " + getString(R.string.pontos);
        ponto_badge.setText(mensagem);

        ImageView imagem_badge = settingsDialog.findViewById(R.id.imagem_badge);
        String IdBadge = "gs://uplace-ff0b3.appspot.com/badge/" + badge.getId() +".gif";
        StorageReference Ref_badge_1 = FirebaseStorage.getInstance().getReferenceFromUrl(IdBadge);

        //Bagde
        Glide.with(this)
                .load(Ref_badge_1)
                .apply(new RequestOptions().override(350,350))
                .into(imagem_badge);

        settingsDialog.show();
    }

    private void preenche_array_badges(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            badges.add(snapshot.getValue(String.class));
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

    public String get_hora_atual(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");

        return formato.format(calendar.getTime());
    }

    public String get_data_atual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("dd / MM / yyyy");

        return formato.format(calendar.getTime());
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
