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
import com.mycompany.database_syos.models.Subcategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubcategoryDAO {
    public SubcategoryDAO() {
       
    }

    public List<Subcategory> getCategoriesByMainCategory(int mainCategoryId) throws ProductDatabaseException {
    String sql = "SELECT sub_category_id, sub_category_name, sub_category_code FROM sub_category WHERE main_category_id = ?";
    List<Subcategory> subcategories = new ArrayList<>();

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, mainCategoryId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("sub_category_id");
                String name = rs.getString("sub_category_name");
                String code = rs.getString("sub_category_code");
                subcategories.add(new Subcategory(id, name, code));
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch subcategories", e);
    }

    return subcategories;
}
    
    public Subcategory getSubcategoryById(int subcategoryId) throws ProductDatabaseException {
    String sql = "SELECT sub_category_id, sub_category_name, sub_category_code FROM sub_category WHERE sub_category_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql)) {

        pstmt.setInt(1, subcategoryId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Subcategory(
                    rs.getInt("sub_category_id"),
                    rs.getString("sub_category_name"),
                    rs.getString("sub_category_code")
                );
            } else {
                throw new ProductDatabaseException("Subcategory not found for ID: " + subcategoryId);
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Failed to fetch subcategory by ID: " + subcategoryId, e);
    }
}

    public int createSubcategory(String name, String code, int mainCategoryId) throws ProductDatabaseException {
    String sql = "INSERT INTO sub_category (sub_category_name, sub_category_code, main_category_id) VALUES (?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, name);
        pstmt.setString(2, code);
        pstmt.setInt(3, mainCategoryId);
        pstmt.executeUpdate();

        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new ProductDatabaseException("Failed to create a new subcategory.");
            }
        }
    } catch (SQLException e) {
        throw new ProductDatabaseException("Error creating new subcategory", e);
    }
}

}
