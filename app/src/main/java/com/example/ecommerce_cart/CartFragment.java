package com.example.ecommerce_cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_cart.adapter.MyCartAdapter;
import com.example.ecommerce_cart.eventbus.MyUpdatecartEvent;
import com.example.ecommerce_cart.listener.ICardLoadListener;
import com.example.ecommerce_cart.model.CartModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartFragment extends Fragment implements ICardLoadListener {

    @BindView(R.id.recycler_Cart)
    RecyclerView recyclerViewCart;

    @BindView(R.id.cartlayout)
    RelativeLayout cartlayout;

    @BindView(R.id.txttotal)
    TextView txttotal;

    @BindView(R.id.cartbadge)
    NotificationBadge badge;

    @BindView(R.id.homebtncart)
    AppCompatImageView homeimageView;


    Unbinder unbinder;

    ICardLoadListener cardLoadListener;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdatecartEvent.class));
        EventBus.getDefault().removeStickyEvent(MyUpdatecartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdatecart(MyUpdatecartEvent event){

        LoadcartFromFirebase();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        unbinder = ButterKnife.bind(this, view);

        init();
        LoadcartFromFirebase();
        CountCartItem();


        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CountCartItem();
    }

    private void CountCartItem() {

        FirebaseDatabase.getInstance().getReference("Cart")
                .child("UNIQE_USER_ID")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists())
                        {
                            int counttotal = (int) snapshot.getChildrenCount();
                            badge.setNumber(counttotal);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void LoadcartFromFirebase() {

        List<CartModel> cartModels = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {


                            for (DataSnapshot cartSnapshot: snapshot.getChildren())
                            {
                                CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartSnapshot.getKey());
                                cartModels.add(cartModel);
                            }

                            cardLoadListener.onCartLoadSuccess(cartModels);
                        }
                        else
                        {
                            cardLoadListener.onCartLoadFailed("Cart Empty");



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        cardLoadListener.onCartLoadFailed(error.getMessage());

                    }
                });
    }

    private void init(){


        cardLoadListener = this;
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewCart.setLayoutManager(layoutManager);
        recyclerViewCart.addItemDecoration(new DividerItemDecoration(requireContext(), layoutManager.getOrientation()));

        homeimageView.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), MainActivity.class));
        });

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        double sum = 0;
        for (CartModel cartModel:cartModelList)
        {
            sum += cartModel.getTotalPrice();
        }
        txttotal.setText(new StringBuilder("Total  "+  "\u09F3").append(sum));

        MyCartAdapter adapter  = new MyCartAdapter(requireContext(), cartModelList);
        recyclerViewCart.setAdapter(adapter);
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(cartlayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
