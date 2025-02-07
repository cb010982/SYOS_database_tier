/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */
import java.time.LocalDate;

public class StoreInventory {
    private int inventoryId;          
    private int batchId;              
    private int productId;           
    private int quantity;             
    private LocalDate expiryDate;    
    private boolean isExpirable;      
    private String itemSerialNumber;  

 
    public StoreInventory(int inventoryId, int batchId, int productId, int quantity, LocalDate expiryDate, boolean isExpirable, String itemSerialNumber) {
        this.inventoryId = inventoryId;
        this.batchId = batchId;
        this.productId = productId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.isExpirable = isExpirable;
        this.itemSerialNumber = itemSerialNumber;
    }


    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpirable() {
        return isExpirable;
    }

    public void setExpirable(boolean expirable) {
        isExpirable = expirable;
    }

    public String getItemSerialNumber() {
        return itemSerialNumber;
    }

    public void setItemSerialNumber(String itemSerialNumber) {
        this.itemSerialNumber = itemSerialNumber;
    }

    @Override
    public String toString() {
        return "StoreInventory{" +
                "inventoryId=" + inventoryId +
                ", batchId=" + batchId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", isExpirable=" + isExpirable +
                ", itemSerialNumber='" + itemSerialNumber + '\'' +
                '}';
    }
}
