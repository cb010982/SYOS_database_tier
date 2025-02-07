/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */
public class OnlineInventoryRestockException extends Exception {
    public OnlineInventoryRestockException(String message) {
        super(message);
    }

    public OnlineInventoryRestockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OnlineInventoryRestockException(Throwable cause) {
        super(cause);
    }
}
