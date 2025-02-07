/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.models;

/**
 *
 * @author User
 */

public class Subcategory {
    private int id;
    private String name;
    private String code; 

    public Subcategory(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code; 
    }
    public Subcategory() {
       }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code; 
    }

    public void setCode(String code) {
        this.code = code;
    }
     public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
}
