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

public class ShelfRestockLog {
    private int restockLogId;
    private int shelfId;
    private int quantity;
    private Timestamp restockTimestamp;

    public ShelfRestockLog() {
    }

    public ShelfRestockLog(int restockLogId, int shelfId, int quantity, Timestamp restockTimestamp) {
        this.restockLogId = restockLogId;
        this.shelfId = shelfId;
        this.quantity = quantity;
        this.restockTimestamp = restockTimestamp;
    }

    public int getRestockLogId() {
        return restockLogId;
    }

    public void setRestockLogId(int restockLogId) {
        this.restockLogId = restockLogId;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getRestockTimestamp() {
        return restockTimestamp;
    }

    public void setRestockTimestamp(Timestamp restockTimestamp) {
        this.restockTimestamp = restockTimestamp;
    }
}


