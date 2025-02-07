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

public class Batch {
    private int batchId;                
    private int productId;             
    private double purchasePrice;       
    private LocalDate expiryDate;       
    private LocalDate manufacturedDate;
    private int purchasedQuantity;      
    private int unitId;                
    private int supplierId;             
    private int brandId;                
    private String itemSerialNumber;    
    private LocalDate dateReceived;     

    public Batch(int batchId, int productId, double purchasePrice, LocalDate expiryDate, LocalDate manufacturedDate,
                 int purchasedQuantity, int unitId, int supplierId, int brandId, String itemSerialNumber, LocalDate dateReceived) {
        this.batchId = batchId;
        this.productId = productId;
        this.purchasePrice = purchasePrice;
        this.expiryDate = expiryDate;
        this.manufacturedDate = manufacturedDate;
        this.purchasedQuantity = purchasedQuantity;
        this.unitId = unitId;
        this.supplierId = supplierId;
        this.brandId = brandId;
        this.itemSerialNumber = itemSerialNumber;
        this.dateReceived = dateReceived;
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

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(LocalDate manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }

    public int getPurchasedQuantity() {
        return purchasedQuantity;
    }

    public void setPurchasedQuantity(int purchasedQuantity) {
        this.purchasedQuantity = purchasedQuantity;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getItemSerialNumber() {
        return itemSerialNumber;
    }

    public void setItemSerialNumber(String itemSerialNumber) {
        this.itemSerialNumber = itemSerialNumber;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "batchId=" + batchId +
                ", productId=" + productId +
                ", purchasePrice=" + purchasePrice +
                ", expiryDate=" + expiryDate +
                ", manufacturedDate=" + manufacturedDate +
                ", purchasedQuantity=" + purchasedQuantity +
                ", unitId=" + unitId +
                ", supplierId=" + supplierId +
                ", brandId=" + brandId +
                ", itemSerialNumber='" + itemSerialNumber + '\'' +
                ", dateReceived=" + dateReceived +
                '}';
    }
}
