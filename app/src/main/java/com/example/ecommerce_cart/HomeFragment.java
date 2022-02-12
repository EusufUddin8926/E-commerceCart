package com.example.ecommerce_cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce_cart.adapter.CategoryAdapter;
import com.example.ecommerce_cart.adapter.MyproductAdapter;
import com.example.ecommerce_cart.eventbus.MyUpdatecartEvent;
import com.example.ecommerce_cart.listener.ICardLoadListener;
import com.example.ecommerce_cart.listener.IProductLoadListener;
import com.example.ecommerce_cart.model.CartModel;
import com.example.ecommerce_cart.model.CategoryModel;
import com.example.ecommerce_cart.model.ProductModel;
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

public class HomeFragment extends Fragment implements IProductLoadListener, ICardLoadListener {

    @BindView(R.id.recyclerdrink)
    RecyclerView recyclerViewdrink;

    @BindView(R.id.mainlayout)
    RelativeLayout mainlayout;

    @BindView(R.id.badge)
    NotificationBadge badge;


    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    @BindView(R.id.homenavbar)
    AppCompatImageView homenavbar;

    IProductLoadListener drinkLoadListener;
    ICardLoadListener cardLoadListener;



    @BindView(R.id.recyclerViewcategory)
    RecyclerView recyclerViewcategory;
    Unbinder unbinder;

    ArrayList<CategoryModel> categoryModels;
    CategoryAdapter categoryAdapter;

    Context context;


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
        CountCartItem();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

     // recyclerViewcategory = view.findViewById(R.id.recyclerViewcategory);





        int[] productlogo = {R.drawable.tshirt,R.drawable.laptop,R.drawable.watch,R.drawable.headphones,R.drawable.fruit,R.drawable.oil };

        String[] productname = {"T-Shirt","Laptop", "Watch", "Headphones", "Fruit", "Oil"};

        categoryModels = new ArrayList<>();
        for(int i=0; i<productlogo.length; i++)
        {
            CategoryModel model = new CategoryModel(productlogo[i], productname[i]);
            categoryModels.add(model);
        }

        // Design Horizontal layout

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewcategory.setLayoutManager(layoutManager);
        recyclerViewcategory.setItemAnimator(new DefaultItemAnimator());


        categoryAdapter = new CategoryAdapter(categoryModels,getActivity());

        recyclerViewcategory.setAdapter(categoryAdapter);


       init();
       loadDrinkFromFirebase();
        CountCartItem();



        return view;
    }


    private void loadDrinkFromFirebase() {

        List<ProductModel> productModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("productlist")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            for(DataSnapshot drinkSnapshot:snapshot.getChildren()){
                                ProductModel productModel = drinkSnapshot.getValue(ProductModel.class);
                                productModel.setKey(drinkSnapshot.getKey());
                                productModels.add(productModel);
                            }
                            drinkLoadListener.onProductLoadSuccess(productModels);
                        }else {
                            drinkLoadListener.onProductLoadFailed("Can't load Drink");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        drinkLoadListener.onProductLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){

        drinkLoadListener = this;
        cardLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewdrink.setLayoutManager(gridLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewdrink.getContext(),
               gridLayoutManager .getOrientation());
        recyclerViewdrink.addItemDecoration(dividerItemDecoration);

        btnCart.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), CartActivity.class));
        });

        homenavbar.setOnClickListener(view -> {
            Toast.makeText(requireContext(),"Home Selected ",Toast.LENGTH_SHORT).show();
        });


    }


    @Override
    public void onProductLoadSuccess(List<ProductModel> productModelList) {

        MyproductAdapter myproductAdapter = new MyproductAdapter(requireContext(), productModelList,cardLoadListener);
        recyclerViewdrink.setAdapter(myproductAdapter);
    }

    @Override
    public void onProductLoadFailed(String message) {
        //Snackbar.make(context, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum= 0;
        for (CartModel cartModel: cartModelList)
        {
            cartSum += cartModel.getQuantity();
            badge.setNumber(cartSum);
        }
    }

    @Override
    public void onCartLoadFailed(String message) {

        Snackbar.make(mainlayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        CountCartItem();
    }

    private void CountCartItem() {

        List<CartModel> cartModels = new ArrayList<>();

        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot cartSnapshot: snapshot.getChildren())
                        {
                            CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cardLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        cardLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });

    }
}
