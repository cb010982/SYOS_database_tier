/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */
public class ShelfRestockException extends Exception {
    public ShelfRestockException(String message) {
        super(message);
    }

    public ShelfRestockException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShelfRestockException(Throwable cause) {
        super(cause);
    }
}
