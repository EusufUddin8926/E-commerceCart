package com.example.ecommerce_cart.listener;

import com.example.ecommerce_cart.model.CartModel;


import java.util.List;

public interface ICardLoadListener {

    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}
