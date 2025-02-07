/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */
public class ShelfException extends Exception {
    public ShelfException(String message) {
        super(message);
    }

    public ShelfException(String message, Throwable cause) {
        super(message, cause);
    }
}
