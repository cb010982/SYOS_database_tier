/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;

/**
 *
 * @author User
 */


import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import com.mycompany.database_syos.models.Unit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO implements unitdao_interface {
   
    public UnitDAO() {      
    }
    
    @Override
    public List<Unit> getUnits() throws ProductDatabaseException {
        String sql = "SELECT unit_id, unit_name FROM units";
        List<Unit> units = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("unit_id");
                String name = rs.getString("unit_name");
                units.add(new Unit(id, name));
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to fetch units from the database", e);
        }

        return units;
    }

    @Override
    public Unit getUnitById(int unitId) throws ProductDatabaseException {
        String sql = "SELECT unit_id, unit_name FROM units WHERE unit_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, unitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Unit(
                        rs.getInt("unit_id"),
                        rs.getString("unit_name")
                    );
                } else {
                    throw new ProductDatabaseException("Unit not found for ID: " + unitId);
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to fetch unit by ID: " + unitId, e);
        }
    }

  @Override
    public int createUnit(String name) throws ProductDatabaseException {
        String sql = "INSERT INTO units (unit_name) VALUES (?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new ProductDatabaseException("Failed to create a new unit.");
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Error creating new unit", e);
        }
    }
}

