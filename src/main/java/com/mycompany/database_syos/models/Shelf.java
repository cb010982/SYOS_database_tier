/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */
import java.sql.Timestamp;

public class Shelf {
    private int shelfId;
    private int productId;
    private int maxCapacity;
    private int currentQuantity;
    private Timestamp lastRestockTimestamp;

    public Shelf(int productId, int maxCapacity, Timestamp lastRestockTimestamp) {
        this.productId = productId;
        this.maxCapacity = maxCapacity;
        this.currentQuantity = 0;  
        this.lastRestockTimestamp = lastRestockTimestamp;
    }

    public Shelf(int shelfId, int productId, int maxCapacity, int currentQuantity, Timestamp lastRestockTimestamp) {
        this.shelfId = shelfId;
        this.productId = productId;
        this.maxCapacity = maxCapacity;
        this.currentQuantity = currentQuantity;
        this.lastRestockTimestamp = lastRestockTimestamp;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
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

    public Timestamp getLastRestockTimestamp() {
        return lastRestockTimestamp;
    }

    public void setLastRestockTimestamp(Timestamp lastRestockTimestamp) {
        this.lastRestockTimestamp = lastRestockTimestamp;
    }

    @Override
    public String toString() {
        return "Shelf{" +
                "shelfId=" + shelfId +
                ", productId=" + productId +
                ", maxCapacity=" + maxCapacity +
                ", currentQuantity=" + currentQuantity +
                ", lastRestockTimestamp=" + lastRestockTimestamp +
                '}';
    }
}
