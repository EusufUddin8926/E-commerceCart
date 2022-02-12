package com.example.ecommerce_cart.adapter;

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
import com.example.ecommerce_cart.listener.ICardLoadListener;
import com.example.ecommerce_cart.listener.IRecyclerViewCartClickListner;
import com.example.ecommerce_cart.model.CartModel;
import com.example.ecommerce_cart.model.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyproductAdapter extends RecyclerView.Adapter<MyproductAdapter.MyDrinkViewHolder> {

    private Context context;
    private List<ProductModel> productModelList;
    private ICardLoadListener cardLoadListener;


    public MyproductAdapter(Context context, List<ProductModel> productModelList, ICardLoadListener cardLoadListener) {
        this.context = context;
        this.productModelList = productModelList;
        this.cardLoadListener = cardLoadListener;
    }

    @NonNull
    @Override
    public MyDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyDrinkViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_product_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyDrinkViewHolder holder, int position) {
        Glide.with(context)
                .load(productModelList.get(position).getImage())
                .into(holder.imageView);

        holder.txtprice.setText(new StringBuilder("\u09F3").append(productModelList.get(position).getPrice()));
        holder.txtname.setText(new StringBuilder().append(productModelList.get(position).getName()));

        holder.cartview.setOnClickListener((View) -> {

            addToCart(productModelList.get(position));
        });
    }

    private void addToCart(ProductModel productModel) {

        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQE_USER_ID");


        userCart.child(productModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())  // if user have already in cart
                        {

                            // just Update Quantity and toatal price

                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String, Object> updatedata = new HashMap<>();

                            updatedata.put("quantity", cartModel.getQuantity());
                            updatedata.put("totalprice", cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()) );

                            userCart.child(productModel.getKey())
                                    .updateChildren(updatedata)
                                    .addOnSuccessListener(aVoid -> {
                                        cardLoadListener.onCartLoadFailed("Add to cart Success");
                                    })
                                    .addOnFailureListener(e -> {
                                        cardLoadListener.onCartLoadFailed(e.getMessage());
                                    });

                        }

                        else  // if item not have in cart, add new
                        {
                                CartModel cartModel = new CartModel();
                                cartModel.setName(productModel.getName());
                                cartModel.setImage(productModel.getImage());
                                cartModel.setKey(productModel.getKey());
                                cartModel.setPrice(productModel.getPrice());
                                cartModel.setQuantity(1);
                                cartModel.setTotalPrice(Float.parseFloat(productModel.getPrice()));


                                userCart.child(productModel.getKey())
                                        .setValue(cartModel)
                                        .addOnSuccessListener(aVoid -> {
                                           // cardLoadListener.onCartLoadFailed("Add to cart Success");
                                        })
                                        .addOnFailureListener(e -> {
                                            cardLoadListener.onCartLoadFailed(e.getMessage());
                                        });
                            }


                            EventBus.getDefault().postSticky(new MyUpdatecartEvent());


                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cardLoadListener.onCartLoadFailed(error.getMessage() );
                    }
                });

    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class MyDrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.txtname)
        TextView txtname;
        @BindView(R.id.txtprice)
        TextView txtprice;

        @BindView(R.id.cartimage)
        ImageView cartview;

        IRecyclerViewCartClickListner listener;

        public void setListener(IRecyclerViewCartClickListner listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;

        public MyDrinkViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);

            cartview.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            listener.onRecyclerCartClick(view, getAdapterPosition());
        }
    }
}
