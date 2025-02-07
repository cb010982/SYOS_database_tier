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

public class Bill {
    private int billId;
    private int userId;
    private String billNo;
    private LocalDateTime date;
    private double grossTotal;
    private double discount;
    private double loyaltyDeduction;
    private double netTotal;
    private double total;
    private double cashReceived;
    private double changeGiven;
    private String paymentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Bill(int userId, String billNo, LocalDateTime date, double grossTotal, double total, double discount, double loyaltyDeduction, double netTotal, double cashReceived, double changeGiven, String paymentType) {
        this.userId = userId;
        this.billNo = billNo;
        this.date = date;
        this.grossTotal = grossTotal;
        this.discount = discount;
        this.loyaltyDeduction = loyaltyDeduction;
        this.netTotal = netTotal;
        this.cashReceived = cashReceived;
        this.changeGiven = changeGiven;
        this.paymentType = paymentType;
        this.total = total;
        this.createdAt = LocalDateTime.now();  
        this.updatedAt = LocalDateTime.now();  
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getLoyaltyDeduction() {
        return loyaltyDeduction;
    }

    public void setLoyaltyDeduction(double loyaltyDeduction) {
        this.loyaltyDeduction = loyaltyDeduction;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(double changeGiven) {
        this.changeGiven = changeGiven;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", userId=" + userId +
                ", billNo='" + billNo + '\'' +
                ", date=" + date +
                ", grossTotal=" + grossTotal +
                ", discount=" + discount +
                ", loyaltyDeduction=" + loyaltyDeduction +
                ", netTotal=" + netTotal +
                ", cashReceived=" + cashReceived +
                ", changeGiven=" + changeGiven +
                ", paymentType='" + paymentType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
