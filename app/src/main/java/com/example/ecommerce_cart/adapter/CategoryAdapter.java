package com.example.ecommerce_cart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_cart.R;
import com.example.ecommerce_cart.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewholder> {

    ArrayList<CategoryModel> categoryModels;
    Context context;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModels, Context context) {
        this.categoryModels = categoryModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryproductlayout, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {

        holder.imageView.setImageResource(categoryModels.get(position).getProductlogo());
        holder.textView.setText(categoryModels.get(position).getProductname());
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageview);
            textView = itemView.findViewById(R.id.txtview);



        }
    }
}
