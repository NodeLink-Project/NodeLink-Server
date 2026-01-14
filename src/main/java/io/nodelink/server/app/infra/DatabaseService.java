package io.nodelink.server.app.infra;

import java.sql.*;

public class DatabaseService {
    private static final String URL = "jdbc:sqlite:nodelink_storage.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL)) {
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS ClusterTable (" +
                    "id TEXT PRIMARY KEY, " +
                    "content TEXT, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS BoneTable (" +
                    "id TEXT PRIMARY KEY, " +
                    "content TEXT, " +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            // Trigger pour le timestamp automatique
            stmt.execute("CREATE TRIGGER IF NOT EXISTS tr_update_cluster_time " +
                    "AFTER UPDATE ON ClusterTable BEGIN " +
                    "UPDATE ClusterTable SET updated_at = CURRENT_TIMESTAMP WHERE id = old.id; " +
                    "END;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveBone(String id, String jsonContent) throws SQLException {
        String sql = "INSERT OR REPLACE INTO BoneTable(id, content, updated_at) VALUES(?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, jsonContent);
            pstmt.executeUpdate();
        }
    }

    public static void saveCluster(String id, String jsonContent) throws SQLException {
        String sql = "INSERT OR REPLACE INTO ClusterTable(id, content, updated_at) VALUES(?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, jsonContent);
            pstmt.executeUpdate();
        }
    }
}
