package com.example.ecommerce_cart.listener;

import com.example.ecommerce_cart.model.ProductModel;


import java.util.List;

public interface IProductLoadListener {

    void onProductLoadSuccess(List<ProductModel> productModelList);
    void onProductLoadFailed(String message);
}
