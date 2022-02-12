package com.example.ecommerce_cart.model;

public class CategoryModel {


    Integer productlogo;
    String productname;

    public CategoryModel(Integer productlogo, String productname) {
        this.productlogo = productlogo;
        this.productname = productname;
    }

    public Integer getProductlogo() {
        return productlogo;
    }

    public void setProductlogo(Integer productlogo) {
        this.productlogo = productlogo;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }
}
