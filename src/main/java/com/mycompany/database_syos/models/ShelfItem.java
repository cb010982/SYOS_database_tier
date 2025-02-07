/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */
public class ShelfItem {
    private int shelfItemId;      
    private int shelfId;          
    private int batchId;          
    private String itemSerialNumber;
    private int productId;        

    public ShelfItem() {
    }

    public ShelfItem(int shelfItemId, int shelfId, int batchId, String itemSerialNumber, int productId) {
        this.shelfItemId = shelfItemId;
        this.shelfId = shelfId;
        this.batchId = batchId;
        this.itemSerialNumber = itemSerialNumber;
        this.productId = productId;
    }

    public ShelfItem(int shelfId, int batchId, String itemSerialNumber, int productId) {
        this.shelfId = shelfId;
        this.batchId = batchId;
        this.itemSerialNumber = itemSerialNumber;
        this.productId = productId;
    }

    public int getShelfItemId() {
        return shelfItemId;
    }

    public void setShelfItemId(int shelfItemId) {
        this.shelfItemId = shelfItemId;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ShelfItem{" +
                "shelfItemId=" + shelfItemId +
                ", shelfId=" + shelfId +
                ", batchId=" + batchId +
                ", itemSerialNumber='" + itemSerialNumber + '\'' +
                ", productId=" + productId +
                '}';
    }
}
