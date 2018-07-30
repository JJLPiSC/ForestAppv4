package com.coding.jjlop.forestappv4.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.coding.jjlop.forestappv4.Adapter.ExchAdapter;
import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Exchange extends AppCompatActivity {

    List<Tree> tList;
    RecyclerView recyclerView;
    private DatabaseReference mDataBase;
    private String uid;

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

        //adding some items to our list
        //fillList();

        //creating recyclerview adapter
        ExchAdapter adapter = new ExchAdapter(this, fillList());

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }

    public List<Tree> fillList(){
        tList.clear();
        recyclerView.removeAllViews();
        Query query = mDataBase.child("Planted").orderByChild("id_at").equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                String lat = areaSnapshot.child("lat").getValue(String.class);
                                String lng = areaSnapshot.child("lng").getValue(String.class);
                                String t = areaSnapshot.child("type").getValue(String.class);
                                String q = areaSnapshot.child("type").getValue(String.class);
                                String b = areaSnapshot.child("type").getValue(String.class);
                                String c = areaSnapshot.child("type").getValue(String.class);

                                Tree tree = new Tree(q,lat,lng,t,b,c);
                                //Tree tree = dataSnapshot.getValue(Tree.class);
                                //t = String.valueOf(dataSnapshot.getValue());
                                //t=tree.getName()
                                tList.add(tree);
                                Log.d("0",""+tree.getSpecies());
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
        return tList;
    }
}
