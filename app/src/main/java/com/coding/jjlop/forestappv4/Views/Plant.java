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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private DatabaseReference mDatabase, t_LRef, p_LRef;
    private ArrayList<String> tree_List = new ArrayList<>();
    private ArrayList<String> keys_List = new ArrayList();
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
        p_LRef = FirebaseDatabase.getInstance().getReference();
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
        //////
        //String Key=mDatabase.child("Users/"+uid+"/Trees").getKey();
        //Log.d(l,"Key1"+Key);
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
        mMap.addMarker(new MarkerOptions().position(t1).title("My Tree!!!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        float zoom = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t1, zoom));
    }


    public void Save() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        String Lat = latitudeValueNetwork.getText().toString().trim();
        String Lng = longitudeValueNetwork.getText().toString().trim();
        //mDatabase.child("T_Ctlg");
        //Query q= mDatabase.child("T_Ctlg").orderByChild("name").equalTo((String) tSpinner.getSelectedItem());
        Query q = mDatabase.child("T_Ctlg");
        Log.d("Type", "" + tSpinner.getSelectedItem());

        //Query q = mDatabase.child("T_Ctlg");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // tree = dataSnapshot.getValue(Tree.class);
                //t = String.valueOf(dataSnapshot.getValue());
                // t=tree.getName();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String tName = areaSnapshot.child("name").getValue(String.class);
                    //String tId = areaSnapshot.child("id_t").getValue(String.class);
                    if (tName == tSpinner.getSelectedItem()) {
                        k = areaSnapshot.getKey();
                        Log.d("PKey: ", k);
                        t = areaSnapshot.child("name").getValue(String.class);
                    }
                }
                //Log.d("FY: "t);
                //Log.d("Type tree",""+tree.toString());
                // Log.d("Type",""+tree.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Planted p = new Planted(uid, Lat, Lng, fecha, t);
       /* Map dataMap= new HashMap();
        dataMap.put("Id_P",uid);
        dataMap.put("Lat",Lat);
        dataMap.put("Lng",Lng);*/
        mDatabase.child("Planted").push().setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Plant.this, "Stored...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Plant.this, "Error..!!!" + task.getException(), Toast.LENGTH_SHORT).show();
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
                String value = dataSnapshot.child("name").getValue(String.class);
                String ke = dataSnapshot.child("name").getKey();
                trees.remove(value);
                tAdapter.remove(String.valueOf(tAdapter.getPosition(value)));
                trees.add(value);
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
        Query q = p_LRef.child("Planted");
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String idu = areaSnapshot.child("id_enc").getValue(String.class);
                    Log.d("DB1 idu: ", "" + idu);
                    Log.d("Local1 idu: ", "" + uid);
                    if (uid == idu) {
                        String lat = areaSnapshot.child("lat").getValue(String.class);
                        String lng = areaSnapshot.child("lng").getValue(String.class);
                        Log.d("Lat 1: ", "" + lat);
                        Log.d("Lng 1: ", "" + lng);
                        //String title = areaSnapshot.child("type").getValue(String.class);
                        LatLng t2 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        marker.setPosition(t2);
                        marker.setVisible(true);
                        mMap.addMarker(new MarkerOptions().position(t2).title("Tree").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        float zoom = 16;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t2, zoom));
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String idu = areaSnapshot.child("id_enc").getValue(String.class);
                    Log.d("DB2 idu: ", "" + idu);
                    Log.d("Local2 idu: ", "" + uid);
                    if (uid == idu) {
                        String lat = areaSnapshot.child("lat").getValue(String.class);
                        String lng = areaSnapshot.child("lng").getValue(String.class);
                        String title = areaSnapshot.child("type").getValue(String.class);
                        Log.d("Lat 2: ", "" + lat);
                        Log.d("Lng 2: ", "" + lng);

                        LatLng t2 = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        mMap.addMarker(new MarkerOptions().position(t2).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        float zoom = 16;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t2, zoom));
                    }
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
                fillMap();
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
        String d = "", n = "", v = "";
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                d = "1";
                n = "Pino";
                v = "1";
            } else if (i == 1) {
                d = "2";
                n = "Durazno";
                v = "2";
            } else if (i == 2) {
                d = "3";
                n = "Ciruelo";
                v = "3";
            }
            Tree t1 = new Tree(d, n, "O", "S", v);
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
