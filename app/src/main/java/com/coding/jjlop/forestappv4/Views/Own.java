package com.coding.jjlop.forestappv4.Views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coding.jjlop.forestappv4.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Own extends AppCompatActivity {

    private ListView ListV;
    private DatabaseReference mDataBase;
    private ArrayList<String> tree_List = new ArrayList<>();
    private ArrayList<String> keys_List= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own);
        mDataBase= FirebaseDatabase.getInstance().getReference().child("Trees");
        ListV= findViewById(R.id.T_List);

        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,tree_List);
        ListV.setAdapter(arrayAdapter);

        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value= dataSnapshot.getValue(String.class);
                tree_List.add(value);
                arrayAdapter.notifyDataSetChanged();
                String key = dataSnapshot.getKey();
                keys_List.add(key);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value= dataSnapshot.getValue(String.class);
                String key= dataSnapshot.getKey();
                int index = keys_List.indexOf(key);
                tree_List.set(index,value);
                arrayAdapter.notifyDataSetChanged();
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
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
