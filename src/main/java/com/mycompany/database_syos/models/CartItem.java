/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;



/**
 *
 * @author User
 */
import java.time.LocalDateTime;


public class CartItem {

    private int cartItemId;
    private int cartId;
    private int productId;
    private int batchId;
    private String itemSerialNumber;
    private int quantity;
    private LocalDateTime createdAt; 
    private double price;
     private String productName;

    public CartItem(int cartItemId, int cartId, int productId, int batchId, String itemSerialNumber, int quantity, LocalDateTime createdAt, double price) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.batchId = batchId;
        this.itemSerialNumber = itemSerialNumber;
        this.quantity = quantity;
        this.createdAt = createdAt;  
        this.price = price;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public String getItemSerialNumber() {
        return itemSerialNumber;
    }

    public void setItemSerialNumber(String itemSerialNumber) {
        this.itemSerialNumber = itemSerialNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {  
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {  
        this.createdAt = createdAt;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
  public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", batchId=" + batchId +
                ", itemSerialNumber='" + itemSerialNumber + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt + 
                ", price=" + price +
                '}';
    }
}
