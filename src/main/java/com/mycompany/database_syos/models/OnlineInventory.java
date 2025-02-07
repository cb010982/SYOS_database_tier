/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

import java.sql.Timestamp;

/**
 *
 * @author User
 */
public class OnlineInventory {
    
    private int onlineInventoryId;
    private int productId;
    private int maxCapacity;
    private int currentQuantity;
    private int restockThreshold;
    private Timestamp timestamp;

    public OnlineInventory(int onlineInventoryId, int productId, int maxCapacity, int currentQuantity, int restockThreshold, Timestamp timestamp) {
        this.onlineInventoryId = onlineInventoryId;
        this.productId = productId;
        this.maxCapacity = maxCapacity;
        this.currentQuantity = currentQuantity;
        this.restockThreshold = restockThreshold;
        this.timestamp = timestamp;
    }

    public int getOnlineInventoryId() {
        return onlineInventoryId;
    }

    public void setOnlineInventoryId(int onlineInventoryId) {
        this.onlineInventoryId = onlineInventoryId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getRestockThreshold() {
        return restockThreshold;
    }

    public void setRestockThreshold(int restockThreshold) {
        this.restockThreshold = restockThreshold;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OnlineInventory{" +
                "onlineInventoryId=" + onlineInventoryId +
                ", productId=" + productId +
                ", maxCapacity=" + maxCapacity +
                ", currentQuantity=" + currentQuantity +
                ", restockThreshold=" + restockThreshold +
                ", timestamp=" + timestamp +
                '}';
    }
}