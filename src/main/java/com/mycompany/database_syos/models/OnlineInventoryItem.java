/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */
public class OnlineInventoryItem {

    private int onlineInventoryItemId;
    private int onlineInventoryId;
    private String itemSerialNumber;
    private int batchId;
    private int productId;

    public OnlineInventoryItem(int onlineInventoryItemId, int onlineInventoryId, String itemSerialNumber, int batchId, int productId) {
        this.onlineInventoryItemId = onlineInventoryItemId;
        this.onlineInventoryId = onlineInventoryId;
        this.itemSerialNumber = itemSerialNumber;
        this.batchId = batchId;
        this.productId = productId;
    }

    public int getOnlineInventoryItemId() {
        return onlineInventoryItemId;
    }

    public void setOnlineInventoryItemId(int onlineInventoryItemId) {
        this.onlineInventoryItemId = onlineInventoryItemId;
    }

    public int getOnlineInventoryId() {
        return onlineInventoryId;
    }

    public void setOnlineInventoryId(int onlineInventoryId) {
        this.onlineInventoryId = onlineInventoryId;
    }

    public String getItemSerialNumber() {
        return itemSerialNumber;
    }

    public void setItemSerialNumber(String itemSerialNumber) {
        this.itemSerialNumber = itemSerialNumber;
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

    @Override
    public String toString() {
        return "OnlineInventoryItem{" +
                "onlineInventoryItemId=" + onlineInventoryItemId +
                ", onlineInventoryId=" + onlineInventoryId +
                ", itemSerialNumber='" + itemSerialNumber + '\'' +
                ", batchId=" + batchId +
                ", productId=" + productId +
                '}';
    }
}
