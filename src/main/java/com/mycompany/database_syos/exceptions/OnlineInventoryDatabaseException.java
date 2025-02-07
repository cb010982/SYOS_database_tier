/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */

public class OnlineInventoryDatabaseException extends Exception {

    public OnlineInventoryDatabaseException(String message) {
        super(message);
    }

    public OnlineInventoryDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
