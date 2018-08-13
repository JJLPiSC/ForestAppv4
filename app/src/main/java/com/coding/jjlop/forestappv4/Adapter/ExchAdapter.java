package com.coding.jjlop.forestappv4.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

import com.coding.jjlop.forestappv4.Model.Planted;
import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.coding.jjlop.forestappv4.Views.Plant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class ExchAdapter extends RecyclerView.Adapter<ExchAdapter.TreeViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;
    private String pk_t;
    private final String uid;
    //we are storing all the products in a list
    private List<Tree> treeList;

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
        holder.textViewTitle.setText(product.getName());
        holder.textViewDate.setText(product.getDp());
        holder.textViewOrder.setText(String.valueOf(product.getOrder()));
        holder.textViewSpecie.setText(String.valueOf(product.getSpecies()));
        holder.textViewValue.setText("Value: " + String.valueOf(product.getValue()));
        holder.textViewPK.setText(String.valueOf(product.getId_t()));
        holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.own));
        pk_t = String.valueOf(product.getId_t());
    }


    @Override
    public int getItemCount() {
        return treeList.size();
    }


    class TreeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewDate, textViewOrder, textViewSpecie, textViewValue, textViewPK;
        Button btn_wt, btn_exch;
        ImageView imageView;
        String nu_Id;
        boolean pass_conf = false;
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();

        private TreeViewHolder(View itemView, String uid) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.tv_T);
            textViewDate = itemView.findViewById(R.id.tv_D);
            textViewOrder = itemView.findViewById(R.id.tv_O);
            textViewSpecie = itemView.findViewById(R.id.tv_S);
            textViewValue = itemView.findViewById(R.id.tv_V);
            textViewPK = itemView.findViewById(R.id.tv_PK);
            btn_wt = itemView.findViewById(R.id.btn_W);
            btn_exch = itemView.findViewById(R.id.btn_E);
            btn_wt.setOnClickListener(this);
            btn_exch.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.imageView);
            uid = uid;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_W:
                    FancyToast.makeText(view.getContext(), "Water", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
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
                                Exc(mUser.getText().toString().trim(), mPassword.getText().toString().trim(), pk_t);
                                dialog.dismiss();
                            } else {
                                FancyToast.makeText(view.getContext(), "Empty Fields", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                            }
                        }
                    });

                    break;
            }
        }

        public void Exc(final String nu, final String p, final String k) {

            Query q = mData.child("Users").orderByChild("id_u").equalTo(uid);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                    if(areaSnapshot.child("id_u").getValue(String.class).equals(uid)){
                                        if (areaSnapshot.child("expassw").getValue(String.class).equals(p)) {
                                            val_Exc(nu, k);
                                        } else {
                                            FancyToast.makeText(mCtx, "Incorrect Password!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
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

        public void val_Exc(final String nu, final String k) {
            Query qu = mData.child("Users").orderByChild("nick").equalTo(nu);
            qu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        dataSnapshot.getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                    if (areaSnapshot.child("nick").getValue(String.class).equals(nu)) {
                                        nu_Id = areaSnapshot.child("id_u").getValue(String.class);
                                        Log.d("", "NU ID" + nu_Id);

                                        mData.child("Planted").child(k).child("id_at").setValue(nu_Id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FancyToast.makeText(mCtx, "Success Exchange", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                                } else {
                                                    FancyToast.makeText(mCtx, "Error!!!", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        FancyToast.makeText(mCtx, "User not Found!!!", Toast.LENGTH_SHORT, FancyToast.WARNING, true).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

