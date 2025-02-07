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
import com.mycompany.database_syos.models.MainCategory;
import com.mycompany.database_syos.exceptions.ProductDatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainCategoryDAO {
   
     public MainCategoryDAO() {}

    public List<MainCategory> getMainCategories() throws ProductDatabaseException {
        String sql = "SELECT main_category_id, main_category_name, main_category_code FROM main_category";
        List<MainCategory> mainCategories = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("main_category_id");
                String name = rs.getString("main_category_name");
                String code = rs.getString("main_category_code");
                mainCategories.add(new MainCategory(id, name, code));
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to fetch main categories from the database", e);
        }

        return mainCategories;
    }

    public MainCategory getMainCategoryById(int mainCategoryId) throws ProductDatabaseException {
        String sql = "SELECT main_category_id, main_category_name, main_category_code FROM main_category WHERE main_category_id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, mainCategoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MainCategory(
                        rs.getInt("main_category_id"),
                        rs.getString("main_category_name"),
                        rs.getString("main_category_code")
                    );
                } else {
                    throw new ProductDatabaseException("Main category not found for ID: " + mainCategoryId);
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Failed to fetch main category by ID: " + mainCategoryId, e);
        }
    }

    public int createMainCategory(String name, String code) throws ProductDatabaseException {
        String sql = "INSERT INTO main_category (main_category_name, main_category_code) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new ProductDatabaseException("Failed to create a new main category.");
                }
            }
        } catch (SQLException e) {
            throw new ProductDatabaseException("Error creating new main category", e);
        }
    }

}
