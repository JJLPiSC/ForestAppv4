package com.coding.jjlop.forestappv4.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.coding.jjlop.forestappv4.Adapter.ExchAdapter;
import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Exchange extends AppCompatActivity {

    private List<Tree> tList;
    private RecyclerView recyclerView;
    private DatabaseReference mDataBase;
    private String uid;
    private String d_p, type,id_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        uid = getIntent().getStringExtra("Uid");
        mDataBase = FirebaseDatabase.getInstance().getReference();
        //getting the recyclerview from xml
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the productlist
        tList = new ArrayList<>();

        //creating recyclerview adapter
        final ExchAdapter adapter = new ExchAdapter(this, fillList(),uid);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }

    public List<Tree> fillList() {
        tList.clear();
        recyclerView.removeAllViews();

        Query q = mDataBase.child("Planted");

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Query query = mDataBase.child("Planted");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.child("id_at").getValue(String.class);
                if (uid.equals(id)) {
                    d_p = dataSnapshot.child("d_plant").getValue(String.class);
                    type = dataSnapshot.child("type").getValue(String.class);
                    id_t = dataSnapshot.getKey();
                    c_list(d_p, type,id_t);
                }
                //////
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //tList.clear();
                //fillList();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //tList.clear();
                //fillList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return tList;
    }

    public List<Tree> c_list(final String d, final String t,final String it ) {
        final String d_p=d;
        final String type=t;
        final String idt=it;
        Query quer = mDataBase.child("T_Ctlg");
        quer.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String n = dataSnapshot.child("name").getValue(String.class);
                if (type.equals(n)) {
                    String o = dataSnapshot.child("order").getValue(String.class);
                    String e = dataSnapshot.child("species").getValue(String.class);
                    String v = dataSnapshot.child("value").getValue(String.class);
                    String i = dataSnapshot.child("i_perd").getValue(String.class);
                    Tree tree = new Tree(idt, t, o, e, v, i,d_p);
                    tList.add(tree);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                tList.clear();
                fillList();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                tList.clear();
                fillList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return tList;
    }
}
