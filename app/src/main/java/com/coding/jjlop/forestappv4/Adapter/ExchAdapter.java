package com.coding.jjlop.forestappv4.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExchAdapter extends RecyclerView.Adapter<ExchAdapter.TreeViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
    private String pk_t, ty, dp, ip;
    private final String uid;
    //we are storing all the products in a list
    private List<Tree> treeList;
    LocationManager locationManager;

    //getting the context and product list with constructor
    public ExchAdapter(Context mCtx, List<Tree> productList, String uid) {
        this.mCtx = mCtx;
        this.treeList = productList;
        this.uid = uid;
    }

    @Override
    public TreeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.exc_layout, null);
        return new TreeViewHolder(view, uid);
    }

    @Override
    public void onBindViewHolder(TreeViewHolder holder, int position) {
        //getting the product of the specified position
        Tree product = treeList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getAlias());
        holder.textViewName.setText(product.getName());
        holder.textViewDate.setText(product.getDp());
        holder.textViewOrder.setText(String.valueOf(product.getOrder()));
        holder.textViewSpecie.setText(String.valueOf(product.getSpecies()));
        holder.textViewValue.setText("Valor: " + String.valueOf(product.getValue()));
        //holder.textViewIP.setText("Dias para Riego: " + String.valueOf(product.getValue()));
        holder.textViewPK.setText(String.valueOf(product.getId_t()));
        holder.textViewLat.setText(String.valueOf(product.getLat()));
        holder.textViewLng.setText(String.valueOf(product.getLng()));
        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.own));
        locationManager = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
        pk_t = String.valueOf(product.getId_t());
        ty = String.valueOf(product.getName());
        dp = String.valueOf(product.getDp());
        ip = String.valueOf(product.getI_perd());
    }

    @Override
    public int getItemCount() {
        return treeList.size();
    }


    class TreeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewName, textViewDate, textViewOrder, textViewIP, textViewSpecie, textViewValue, textViewPK, textViewLat, textViewLng;
        Button btn_wt, btn_exch, btn_view;
        ImageView imageView;
        String nu_Id;
        LocationManager locationManager;
        double longitudeNetwork, latitudeNetwork;
        boolean pass_conf = false;
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        View nview;

        private TreeViewHolder(View itemView, String uid) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.tv_T);
            textViewName = itemView.findViewById(R.id.tv_nom);
            textViewDate = itemView.findViewById(R.id.tv_D);
            textViewOrder = itemView.findViewById(R.id.tv_O);
            textViewSpecie = itemView.findViewById(R.id.tv_S);
            textViewValue = itemView.findViewById(R.id.tv_V);
            textViewIP = itemView.findViewById(R.id.tv_IP);
            textViewPK = itemView.findViewById(R.id.tv_PK);
            textViewLat = itemView.findViewById(R.id.tv_lat);
            textViewLng = itemView.findViewById(R.id.tv_lng);
            btn_wt = itemView.findViewById(R.id.btn_W);
            btn_exch = itemView.findViewById(R.id.btn_E);
            btn_view = itemView.findViewById(R.id.btn_V);
            btn_wt.setOnClickListener(this);
            btn_exch.setOnClickListener(this);
            btn_view.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.imageView);
            nview = itemView;
            locationManager = (LocationManager) mCtx.getSystemService(Context.LOCATION_SERVICE);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_W:
                    NetworkUpdates(view);
                    break;
                case R.id.btn_E:
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                    View mNView = view.getRootView().inflate(view.getContext(), R.layout.n_exch, null);
                    final EditText mUser = mNView.findViewById(R.id.et1);
                    final EditText mPassword = mNView.findViewById(R.id.et22);
                    //final EditText mPK = mNView.findViewById(R.id.tv_PK);
                    Button mLogin = mNView.findViewById(R.id.btn_na);
                    mBuilder.setView(mNView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    mLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!mUser.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
                                Log.d("", "" + mUser.getText().toString());
                                Log.d("", "" + mPassword.getText().toString());
                                Log.d("", "" + pk_t);
                                Exc(mUser.getText().toString().trim(), mPassword.getText().toString().trim(), pk_t, ty);
                                dialog.dismiss();
                            } else {
                                FancyToast.makeText(view.getContext(), "Campos Vacios!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                            }
                        }
                    });
                    break;
                case R.id.btn_V:
                    /*AlertDialog.Builder mBuild = new AlertDialog.Builder(view.getContext());
                    View mNVie = view.getRootView().inflate(view.getContext(), R.layout.singlem, null);
                    Button mView = mNView.findViewById(R.id.btnV);
                    mBuild.setView(mNView);
                    final AlertDialog dialo = mBuild.create();
                    dialo.show();
                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialo.dismiss();
                        }
                    });*/
                    FancyToast.makeText(mCtx, "No Disponible :(", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                    break;
            }
        }

        private boolean checkLocation() {
            if (!isLocationEnabled())
                showAlert();
            return isLocationEnabled();
        }

        private void showAlert() {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
            dialog.setTitle("Enable Location")
                    .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                            "usa esta app")
                    .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mCtx.startActivity(myIntent);
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

        private final LocationListener locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(android.location.Location location) {
                longitudeNetwork = location.getLongitude();
                latitudeNetwork = location.getLatitude();
                if (latitudeNetwork != 0.0 && longitudeNetwork != 0.0) {
                    getCoord(latitudeNetwork, longitudeNetwork, textViewPK.getText().toString(), textViewTitle.getText().toString());
                    FancyToast.makeText(mCtx, "Ubicado", Toast.LENGTH_SHORT, FancyToast.CONFUSING, true).show();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                FancyToast.makeText(mCtx, "Actualizando", Toast.LENGTH_SHORT, FancyToast.CONFUSING, true).show();
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        public void NetworkUpdates(View view) {
            if (!checkLocation())
                return;
            if (btn_wt.getText().equals(R.string.wait)) {
                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                locationManager.removeUpdates(locationListenerNetwork);
                btn_wt.setText(R.string.water);
            } else {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 20 * 1000, 10, locationListenerNetwork);
                FancyToast.makeText(mCtx, "Comprobando Ubicacion", Toast.LENGTH_SHORT, FancyToast.CONFUSING, true).show();
                btn_wt.setText(R.string.wait);
            }
        }

        @SuppressLint("ResourceAsColor")
        public void sFecha() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = new Date();
            String f1 = dateFormat.format(date.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date); // Configuramos la fecha que se recibe (Actual)
            calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(ip));  // numero de horas a añadir, o restar en caso de horas<0
            String f2 = dateFormat.format(calendar.getTime());
            Integer dias = (int) ((Integer.parseInt(f1) - Integer.parseInt(f2)) / 86400000);
            if (dias >= 0) {
                textViewIP.setText("Dias para Riego: " + String.valueOf(dias));
                textViewIP.setTextColor(R.color.colorP);
            } else {
                textViewIP.setText("Dias para Riego: " + String.valueOf(dias));
                textViewIP.setTextColor(R.color.colorN);
            }
        }

        public void getCoord(final Double lat, final Double lng, final String key, final String alias) {
            Log.d("", "Latitud Actual: " + lat.toString());
            Log.d("", "Lon Actual: " + lng.toString());
            Query q = mData.child("Planted").orderByKey().equalTo(key);
            Log.d("", "Tree Key: " + key);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                    Log.d("", "ALIASBD: " + areaSnapshot.child("alias").getValue(String.class));
                                    Log.d("", "ALIAS: " + alias);
                                    if (areaSnapshot.child("alias").getValue(String.class).equals(alias)) {
                                        Log.d("", "Latitud: " + lat.toString());
                                        Log.d("", "Longitud: " + lng.toString());
                                        Log.d("", "LatDB: " + areaSnapshot.child("lat").getValue(String.class));
                                        Log.d("", "LngDB: " + areaSnapshot.child("lng").getValue(String.class));
                                        if (Objects.equals(areaSnapshot.child("lat").getValue(String.class), lat.toString()) && Objects.equals(areaSnapshot.child("lng").getValue(String.class), lng.toString())) {
                                            Log.d("", "ST " + areaSnapshot.child("type").getValue(String.class));
                                            Log.d("", "SLAT " + areaSnapshot.child("lat").getValue(String.class));
                                            Log.d("", "SLONG " + areaSnapshot.child("lng").getValue(String.class));
                                            // sFecha();
                                            FancyToast.makeText(mCtx, "Regado", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                                        } else {
                                            Log.d("", "NO " + areaSnapshot.child("type").getValue(String.class));
                                            Log.d("", "NO " + areaSnapshot.child("lat").getValue(String.class));
                                            Log.d("", "NO " + areaSnapshot.child("lng").getValue(String.class));
                                            FancyToast.makeText(mCtx, "Ubicacion Invalida", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } else {
                        FancyToast.makeText(mCtx, "Sin coincidencias", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            locationManager.removeUpdates(locationListenerNetwork);
            btn_wt.setText(R.string.water);
        }

        public void Exc(final String nu, final String p, final String k, final String ty) {

            Query q = mData.child("Users").orderByChild("id_u").equalTo(uid);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                    if (areaSnapshot.child("id_u").getValue(String.class).equals(uid)) {
                                        if (areaSnapshot.child("expassw").getValue(String.class).equals(p)) {
                                            val_Exc(nu, k, ty);
                                        } else {
                                            FancyToast.makeText(mCtx, "Contraseña Incorrecta!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void val_Exc(final String nu, final String k, final String ty) {
            Query qu = mData.child("Users").orderByChild("nick").equalTo(nu);
            qu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                    Log.d("", "NU ID1: " + nu);
                                    if (areaSnapshot.child("nick").getValue(String.class).equals(nu)) {
                                        nu_Id = areaSnapshot.child("id_u").getValue(String.class);
                                        Log.d("", "NU ID" + nu_Id);
                                        /*Planted p = new Planted(nu_Id);
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("id_at", p);*/

                                        mData.child("Planted").child(k).child("id_at").setValue(nu_Id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FancyToast.makeText(mCtx, "Intercambio Exitoso", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                                } else {
                                                    FancyToast.makeText(mCtx, "Error!!!", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                                }
                                            }
                                        });
                                    } else {
                                        //FancyToast.makeText(mCtx, "Vale Verga!!!", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        FancyToast.makeText(mCtx, "Usuario Incorrecto!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}

