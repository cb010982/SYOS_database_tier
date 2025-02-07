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
import com.mycompany.database_syos.models.Brand;
import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {
     public BrandDAO() {}

    public List<Brand> getBrands() throws ProductDatabaseException {
    String sql = "SELECT brand_id, brand_name, brand_code FROM brand";
    List<Brand> brands = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("brand_id");
            String name = rs.getString("brand_name");
            String code = rs.getString("brand_code");
            brands.add(new Brand(id, name, code));
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch brands from the database", e);
    }

    return brands;
}
 
   public Brand getBrandById(int brandId) throws ProductDatabaseException {
    String sql = "SELECT brand_id, brand_name, brand_code FROM brand WHERE brand_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, brandId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Brand(
                    rs.getInt("brand_id"),
                    rs.getString("brand_name"),
                    rs.getString("brand_code")
                );
            } else {
                throw new ProductDatabaseException("Brand not found for ID: " + brandId);
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch brand by ID: " + brandId, e);
    }
}

   public int createBrand(String name, String code) throws ProductDatabaseException {
    String sql = "INSERT INTO brand (brand_name, brand_code) VALUES (?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, name);
        pstmt.setString(2, code);
        pstmt.executeUpdate();

        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new ProductDatabaseException("Failed to create a new brand.");
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Error creating new brand", e);
    }
}

}