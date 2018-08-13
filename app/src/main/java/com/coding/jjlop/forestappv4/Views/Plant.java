package com.coding.jjlop.forestappv4.Views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coding.jjlop.forestappv4.MainActivity;
import com.coding.jjlop.forestappv4.Model.Planted;
import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class Plant extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    LocationManager locationManager;
    double longitudeNetwork, latitudeNetwork;
    TextView longitudeValueNetwork, latitudeValueNetwork;
    Button btn1, btn2, btn3;
    GoogleMap mMap;
    Marker marker;
    Spinner tSpinner;
    private DatabaseReference mDatabase, t_LRef;
    //private ArrayList<String> tree_List = new ArrayList<>();
    //private ArrayList<String> keys_List = new ArrayList();
    final List<String> trees = new ArrayList<>();
    final List<String> itrees = new ArrayList<>();
    String l, t, k;
    String uid;
    Tree tree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        t_LRef = FirebaseDatabase.getInstance().getReference();
        //p_LRef = FirebaseDatabase.getInstance().getReference();
        uid = getIntent().getStringExtra("Uid");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btn1 = findViewById(R.id.btn_Add);
        btn2 = findViewById(R.id.btn_Save);
        btn3 = findViewById(R.id.btn_t);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        tSpinner = findViewById(R.id.snp_tr);
        longitudeValueNetwork = findViewById(R.id.longitudeValueNetwork);
        latitudeValueNetwork = findViewById(R.id.latitudeValueNetwork);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog d = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            d.show();
        }
        fillSnp();
        fillMap();

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void toggleNetworkUpdates(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals(getResources().getString(R.string.pause))) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(locationListenerNetwork);
            button.setText(R.string.resume);
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 20 * 1000, 10, locationListenerNetwork);
            Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
            button.setText(R.string.pause);
        }
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(android.location.Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    longitudeValueNetwork.setText(longitudeNetwork + "");
                    latitudeValueNetwork.setText(latitudeNetwork + "");
                    //Toast.makeText(MainActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public void AddMarker() {
        LatLng t1 = new LatLng(Double.parseDouble((String) latitudeValueNetwork.getText()), Double.parseDouble((String) longitudeValueNetwork.getText()));
        mMap.addMarker(new MarkerOptions().position(t1).title("My New Tree!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        float zoom = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t1, zoom));
    }

    public void AddmMarker(String lat, String lng, String t) {
        LatLng t1 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.addMarker(new MarkerOptions().position(t1).title(t).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }


    public void Save() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        String Lat = latitudeValueNetwork.getText().toString().trim();
        String Lng = longitudeValueNetwork.getText().toString().trim();
        Query q = mDatabase.child("T_Ctlg");
        Log.d("Type", "" + tSpinner.getSelectedItem());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String tName = areaSnapshot.child("name").getValue(String.class);
                    if (tName == tSpinner.getSelectedItem()) {
                        k = areaSnapshot.getKey();
                        t = areaSnapshot.child("name").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Planted p = new Planted(uid, Lat, Lng, fecha, t);
        mDatabase.child("Planted").push().setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Plant.this, "Stored...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Plant.this, "Try Again!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void fillSnp() {
        Query q = t_LRef.child("T_Ctlg");
        final ArrayAdapter<String> tAdapter = new ArrayAdapter<>(Plant.this, android.R.layout.simple_spinner_item, trees);
        tAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tSpinner.setAdapter(tAdapter);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String tName = dataSnapshot.child("name").getValue(String.class);
                String ke = dataSnapshot.child("name").getKey();
                if (!trees.contains(tName)) {
                    trees.add(tName);
                }
                tAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                trees.clear();
                tAdapter.clear();
                fillSnp();
                tAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String tName = dataSnapshot.child("name").getValue(String.class);
                if (trees.contains(tName)) {
                    trees.remove(tName);
                }
                tAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fillMap() {
        Query query = mDatabase.child("Planted").orderByChild("id_at").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                String id = areaSnapshot.child("id_at").getValue(String.class);
                                if (id.equals(uid)) {
                                    String lat = areaSnapshot.child("lat").getValue(String.class);
                                    String lng = areaSnapshot.child("lng").getValue(String.class);
                                    String t = areaSnapshot.child("type").getValue(String.class);
                                    AddmMarker(lat, lng, t);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings ui = mMap.getUiSettings();
        ui.setZoomControlsEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Add:
                AddMarker();
                break;
            case R.id.btn_Save:
                Save();
                break;
            case R.id.btn_t:
                addT();
                break;
        }
    }

    private void addT() {
        String d = "", n = "", v = "", ip = "";
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                d = "1";
                n = "Pino";
                v = "1";
                ip = "6";
            } else if (i == 1) {
                d = "2";
                n = "Durazno";
                v = "2";
                ip = "4";
            } else if (i == 2) {
                d = "3";
                n = "Ciruelo";
                v = "3";
                ip = "3";
            }
            Tree t1 = new Tree(d, n, "O", "S", v, ip);
            mDatabase.child("T_Ctlg").push().setValue(t1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Plant.this, "Stored...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Plant.this, "Error..!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
