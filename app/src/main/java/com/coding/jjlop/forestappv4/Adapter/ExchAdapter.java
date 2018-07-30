package com.coding.jjlop.forestappv4.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coding.jjlop.forestappv4.Model.Tree;
import com.coding.jjlop.forestappv4.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ExchAdapter extends RecyclerView.Adapter<ExchAdapter.TreeViewHolder> {
        //this context we will use to inflate the layout
        private Context mCtx;

        //we are storing all the products in a list
        private List<Tree> treeList;

        //getting the context and product list with constructor
        public ExchAdapter(Context mCtx, List<Tree> productList) {
            this.mCtx = mCtx;
            this.treeList = productList;
        }

        @Override
        public TreeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.exc_layout, null);
            return new TreeViewHolder(view);
        }


    @Override
        public void onBindViewHolder(TreeViewHolder holder, int position) {
            //getting the product of the specified position
            Tree product = treeList.get(position);

            //binding the data with the viewholder views
            holder.textViewTitle.setText(product.getId_t());
            holder.textViewShortDesc.setText(product.getName());
            holder.textViewRating.setText(String.valueOf(product.getOrder()));
            holder.textViewPrice.setText(String.valueOf(product.getSpecies()));

            holder.imageView.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.own));

        }


        @Override
        public int getItemCount() {
            return treeList.size();
        }


        class TreeViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
            ImageView imageView;

            public TreeViewHolder(View itemView) {
                super(itemView);

                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                textViewShortDesc = itemView.findViewById(R.id.textViewShortDesc);
                textViewRating = itemView.findViewById(R.id.textViewRating);
                textViewPrice = itemView.findViewById(R.id.textViewPrice);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

