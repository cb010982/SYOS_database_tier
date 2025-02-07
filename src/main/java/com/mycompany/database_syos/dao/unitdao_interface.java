/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.database_syos.dao;

import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import com.mycompany.database_syos.models.Unit;
import java.util.List;

/**
 *
 * @author User
 */
public interface unitdao_interface {
    
    List<Unit> getUnits() throws ProductDatabaseException;
    Unit getUnitById(int unitId) throws ProductDatabaseException;
    int createUnit(String name) throws ProductDatabaseException;
}

