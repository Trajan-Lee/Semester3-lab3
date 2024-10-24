package com.algonquin.loggy.dao;

import com.algonquin.loggy.model.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    private Connection connection;

    public LogDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new log entry
    public void createLog(Log log) throws SQLException {
        String sql = "INSERT INTO logs (title, content, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, log.getTitle());
            stmt.setString(2, log.getContent());
            System.out.println("Pre Timestamp");
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(log.getTimestamp()));
            System.out.println("post Timestamp");
            stmt.executeUpdate(); // Execute the insertion
        } catch (SQLException e) {
        	System.out.println(e);
        	connection.rollback();
        }
    }
    
    public void updateLog(Log log) throws SQLException {
        String sql = "UPDATE logs SET title = ?, content = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, log.getTitle());
            stmt.setString(2, log.getContent());
            stmt.setInt(3, log.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    
    public void deleteLog(int logId) throws SQLException {
        String sql = "DELETE FROM logs WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, logId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
    
    
    // Read all log entries
    public List<Log> getAllLogs() throws SQLException {
        List<Log> logs = new ArrayList<>();
        String sql = "SELECT * FROM Logs ORDER BY id ASC"; // Order by ID in ascending order
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Log log = new Log(rs.getInt("id"), rs.getString("title"), rs.getString("content"));
                logs.add(log);
            }
        }
        return logs; // Return the ordered list of logs
    }

}
