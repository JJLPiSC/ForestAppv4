package com.coding.jjlop.forestappv4.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coding.jjlop.forestappv4.Login;
import com.coding.jjlop.forestappv4.MainActivity;
import com.coding.jjlop.forestappv4.Model.User;
import com.coding.jjlop.forestappv4.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

public class CProfile extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private ImageView photoImageView;
    private TextView txt_nick, txt_email;
    private EditText txt_d, txt_q,txt_psw;
    private Button b1;
    private DatabaseReference mDatabase;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser user;
    String uid, d, qu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cprofile);
        uid = getIntent().getStringExtra("Uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        photoImageView = findViewById(R.id.photoImageView);
        txt_nick = findViewById(R.id.txt_nick);
        txt_email = findViewById(R.id.txt_email);
        txt_d = findViewById(R.id.txt_de);
        txt_q = findViewById(R.id.txt_qu);
        txt_psw = findViewById(R.id.txt_psw);
        b1 = findViewById(R.id.btn_inf);
        b1.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                setUserData(user);
                if (user != null) {
                    Check();
                } else {
                    goLogInScreen();
                }
            }
        };
    }

    private void setUserData(FirebaseUser user) {
        txt_nick.setText(user.getDisplayName());
        txt_email.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);
        uid = user.getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void Save() {
        Query query = mDatabase.child("Users").orderByChild("id_u").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getValue(String.class);
                if (dataSnapshot.getChildrenCount() == 0) {
                    User u = new User(uid, getIntent().getStringExtra("Name"), getIntent().getStringExtra("Email"), txt_d.getText().toString(), txt_q.getText().toString(), "0",txt_psw.getText().toString());
                    mDatabase.child("Users").push().setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FancyToast.makeText(getApplicationContext(), "Cuenta Completada!!!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Uid", uid);
                                startActivity(intent);
                            } else {
                                FancyToast.makeText(getApplicationContext(), "Operacion Fallida!!!", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   public void Check() {
        Query query = mDatabase.child("Users").orderByChild("id_u").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                            } else {
                                goLogInScreen();
                            }
                        }
                    };
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Uid", uid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_inf:
                if (!txt_d.getText().equals("") && !txt_q.getText().equals("")){
                    Save();
                }else{
                    FancyToast.makeText(getApplicationContext(), "Por favor llene los campos!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                }
                break;
        }
    }
}
