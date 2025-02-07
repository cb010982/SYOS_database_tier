/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */
public class ProductDatabaseException extends Exception {
    public ProductDatabaseException(String message) {
        super(message);
    }

    public ProductDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
