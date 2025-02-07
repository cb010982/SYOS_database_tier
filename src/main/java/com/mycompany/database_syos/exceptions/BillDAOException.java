/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.exceptions;

/**
 *
 * @author User
 */
public class BillDAOException extends Exception {
    public BillDAOException(String message) {
        super(message);
    }

    public BillDAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
