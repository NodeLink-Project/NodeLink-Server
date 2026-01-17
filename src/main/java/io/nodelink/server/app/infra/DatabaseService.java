package io.nodelink.server.app.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static Map<String, String> getAllRows(String table) {
        Map<String, String> rows = new java.util.HashMap<>();
        String sql = "SELECT id, content FROM " + table;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rows.put(rs.getString("id"), rs.getString("content"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
        }
        return rows;
    }

    public static boolean deleteRow(String table, String id) {
        String sql = "DELETE FROM " + table + " WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Retourne vrai si une ligne a été supprimée

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression : " + e.getMessage());
            return false;
        }
    }

    public static List<String> getAllPeerUrls() {
        List<String> urls = new ArrayList<>();
        String[] tables = {"BoneTable", "ClusterTable"};

        try (Connection conn = DriverManager.getConnection(URL)) {
            for (String table : tables) {
                String sql = "SELECT content FROM " + table;
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    // On extrait l'URL du JSON stocké dans la colonne content
                    JsonNode node = new ObjectMapper().readTree(rs.getString("content"));
                    if (node.has("url")) {
                        urls.add(node.get("url").asText());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static void saveAndSync(String table, String id, String content, String timestamp, boolean fromSync) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "INSERT OR REPLACE INTO " + table + " (id, content, updated_at) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, content);
            pstmt.setString(3, timestamp);
            pstmt.executeUpdate();
        }

        if (!fromSync) {
            ReplicationService.broadcast(table, id, content, timestamp);
        }
    }

    public static String getTimestamp(String table, String id) {
        // On valide le nom de la table pour éviter des injections SQL
        if (!table.equals("BoneTable") && !table.equals("ClusterTable")) {
            return null;
        }

        String sql = "SELECT updated_at FROM " + table + " WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("updated_at");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
