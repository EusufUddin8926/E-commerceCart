package com.example.ecommerce_cart.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce_cart.R;
import com.example.ecommerce_cart.eventbus.MyUpdatecartEvent;
import com.example.ecommerce_cart.model.CartModel;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {

    private Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {

        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.imageView);

        holder.txtprice.setText(new StringBuilder("\u09F3").append(cartModelList.get(position).getPrice()));
        holder.txtname.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtquantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        // Event
        holder.btnminus.setOnClickListener(view ->
        {
            minuscartitem(holder, cartModelList.get(position));
        });

        holder.btnplus.setOnClickListener(view ->
        {
            pluscartitem(holder, cartModelList.get(position));
        });

        holder.btndelete.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Do you really want to delete item")
                    .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        // item remove
                        notifyItemRemoved(position);


                        deleteFromFirebase(cartModelList.get(position));
                        dialogInterface.dismiss();
                    }).create();
            alertDialog.show();
        });
    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQE_USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdatecartEvent()));

    }

    private void pluscartitem(MyCartViewHolder holder, CartModel cartModel) {

        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalPrice(cartModel.getQuantity()* Float.parseFloat(cartModel.getPrice()));

        holder.txtquantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updatefirebase(cartModel);

    }

    private void minuscartitem(MyCartViewHolder holder, CartModel cartModel) {

        if(cartModel.getQuantity()>1)
        {
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalPrice(cartModel.getQuantity()* Float.parseFloat(cartModel.getPrice()));

            // Update Quantity

            holder.txtquantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updatefirebase(cartModel);
        }
    }

    private void updatefirebase(CartModel cartModel) {

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdatecartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }


    public class MyCartViewHolder extends RecyclerView.ViewHolder{


        @BindView(R.id.btnminus)
        ImageView btnminus;

        @BindView(R.id.btnPlus)
        ImageView btnplus;

        @BindView(R.id.btndelete)
        ImageView btndelete;

        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.txtname)
        TextView txtname;

        @BindView(R.id.txtprice)
        TextView txtprice;

        @BindView(R.id.txtquantity)
        TextView txtquantity;

        Unbinder unbinder;

        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
