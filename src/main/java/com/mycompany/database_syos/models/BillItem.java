/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */
import java.util.Date;
import java.time.LocalDateTime;

public class BillItem {
    private int billItemId;
    private int billId;
    private int productId;
    private int batchId;
    private String itemSerialNumber;
    private int quantity;
    private double price;
    private LocalDateTime createdAt;


    public BillItem(int billId, int productId, int batchId, String itemSerialNumber, int quantity, double price) {
        this.billId = billId;
        this.productId = productId;
        this.batchId = batchId;
        this.itemSerialNumber = itemSerialNumber;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = LocalDateTime.now();
    }

    public int getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(int billItemId) {
        this.billItemId = billItemId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotal() {
        return this.price * this.quantity;
    }
}
