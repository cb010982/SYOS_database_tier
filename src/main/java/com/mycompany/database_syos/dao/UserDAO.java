/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.database_syos.dao;


import com.mycompany.database_syos.databaseconnection.DatabaseConnection;
import com.mycompany.database_syos.exceptions.DatabaseConnectionException;
import com.mycompany.database_syos.exceptions.InvalidUserException;
import com.mycompany.database_syos.models.UserModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserDAO() {   
    }
    
    public void createUser(UserModel userModel) throws DatabaseConnectionException {
    String query = "INSERT INTO users (username, password_hash, role, email, created_at) VALUES (?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(query)) {  

        pstmt.setString(1, userModel.getUsername());
        pstmt.setString(2, userModel.getPasswordHash());
        pstmt.setString(3, userModel.getRole());
        pstmt.setString(4, userModel.getEmail());
        pstmt.setTimestamp(5, Timestamp.valueOf(userModel.getCreatedAt()));

        pstmt.executeUpdate();
    } catch (SQLException e) {
        throw new DatabaseConnectionException("Error creating user in the database", e);
    }
}

   public List<UserModel> getAllUsers() throws DatabaseConnectionException {
    List<UserModel> users = new ArrayList<>();
    String query = "SELECT * FROM users";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) { 

        while (rs.next()) {
            users.add(new UserModel(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getTimestamp("created_at").toLocalDateTime()
            ));
        }

    } catch (SQLException e) {
        throw new DatabaseConnectionException("Error fetching all users from database", e);
    }
    return users;
}

   public void updateUser(UserModel userModel) throws DatabaseConnectionException, InvalidUserException {
    String query = "UPDATE users SET username = ?, password_hash = ?, role = ?, email = ? WHERE user_id = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query)) {  

        pstmt.setString(1, userModel.getUsername());
        pstmt.setString(2, userModel.getPasswordHash());
        pstmt.setString(3, userModel.getRole());
        pstmt.setString(4, userModel.getEmail());
        pstmt.setInt(5, userModel.getUserId());

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected == 0) {
            throw new InvalidUserException("User with ID " + userModel.getUserId() + " not found.");
        }

    } catch (SQLException e) {
        throw new DatabaseConnectionException("Error updating user in database", e);
    }
}

    public void deleteUser(int userId) throws DatabaseConnectionException, InvalidUserException {
      String query = "DELETE FROM users WHERE user_id = ?";

      try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
           PreparedStatement pstmt = connection.prepareStatement(query)) {  

          pstmt.setInt(1, userId);
          int rowsAffected = pstmt.executeUpdate();

          if (rowsAffected == 0) {
              throw new InvalidUserException("User with ID " + userId + " not found.");
          }

      } catch (SQLException e) {
          throw new DatabaseConnectionException("Error deleting user from database", e);
      }
  }

   public UserModel getUserByUsernameAndPassword(String username, String passwordHash) throws DatabaseConnectionException {
    UserModel userModel = null;
    String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";

    try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
         PreparedStatement pstmt = connection.prepareStatement(query)) {  

        pstmt.setString(1, username);
        pstmt.setString(2, passwordHash);

        try (ResultSet rs = pstmt.executeQuery()) { 
            if (rs.next()) {
                userModel = new UserModel(
                    rs.getInt("user_id"),
                    username,
                    passwordHash,
                    rs.getString("role"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        }

    } catch (SQLException e) {
        throw new DatabaseConnectionException("Error fetching user by username and password", e);
    }

    return userModel;
}

    public boolean verifyPassword(String inputPassword, String storedPasswordHash) {
        return inputPassword.equals(storedPasswordHash);
    }
        
    public UserModel getCurrentUser(String username) throws SQLException {
      String sql = "SELECT * FROM users WHERE username = ?"; 

      try (Connection connection = DatabaseConnection.getInstance().getConnection(); 
           PreparedStatement pstmt = connection.prepareStatement(sql)) { 

          pstmt.setString(1, username);  

          try (ResultSet rs = pstmt.executeQuery()) { 
              if (rs.next()) {
                  return new UserModel(
                      rs.getInt("user_id"),
                      rs.getString("username"),
                      rs.getString("password_hash"),
                      rs.getString("role"),
                      rs.getString("email"),
                      rs.getTimestamp("created_at").toLocalDateTime()
                  );
              }
          }

      } catch (SQLException e) {
          System.err.println("Error fetching current user: " + e.getMessage());
          throw e;
      }
      return null; 
  }
}
