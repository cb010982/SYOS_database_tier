/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author User
 */
public class Cart {

    private int cartId;
    private int userId;
    private String cartNo;
    private Date date;
    private double total;
    private double grossTotal;
    private double discount;
    private double netTotal;
    private Timestamp createdAt;
    private String telephone;
    private String address;
    private List<CartItem> items;
    
   public Cart() {
        this.total = 0.0;
        this.grossTotal = 0.0;
        this.discount = 0.0;
        this.netTotal = 0.0;
    }

    public Cart(int cartId, int userId, String cartNo, Date date, double total, double grossTotal, double discount, double netTotal, Timestamp createdAt, String telephone, String address) {
        this.cartId = cartId;
        this.userId = userId;
        this.cartNo = cartNo;
        this.date = date;
        this.total = total;
        this.grossTotal = grossTotal;
        this.discount = discount;
        this.netTotal = netTotal;
        this.createdAt = createdAt;
        this.telephone = telephone;
        this.address = address;
    }


    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCartNo() {
        return cartNo;
    }

    public void setCartNo(String cartNo) {
        this.cartNo = cartNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

 public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
        calculateNetTotal(); 
    }

    public double getDiscount() {
        return discount;
    }
public void setDiscount(double discount) {
        this.discount = discount;
        calculateNetTotal();
    }
   private void calculateNetTotal() {
        this.netTotal = this.grossTotal - this.discount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        if (this.items == null || this.items.isEmpty()) {
            return 0.0;
        }
        return this.items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", cartNo='" + cartNo + '\'' +
                ", date=" + date +
                ", total=" + total +
                ", grossTotal=" + grossTotal +
                ", discount=" + discount +
                ", netTotal=" + netTotal +
                ", createdAt=" + createdAt +
                ", telephone='" + telephone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
