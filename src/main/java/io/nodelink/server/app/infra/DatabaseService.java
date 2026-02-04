package io.nodelink.server.app.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;

public class DatabaseService {
    private static final String URL = "jdbc:sqlite:nodelink_storage.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL)) {
            Statement stmt = conn.createStatement();

            // Tables existantes
            stmt.execute("CREATE TABLE IF NOT EXISTS ClusterTable (id TEXT PRIMARY KEY, content TEXT, clusterType TEXT, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);");
            stmt.execute("CREATE TABLE IF NOT EXISTS BoneTable (id TEXT PRIMARY KEY, content TEXT, boneType TEXT, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);");

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void save(String table, String id, String content, String timestamp, String Type) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "INSERT OR REPLACE INTO " + table + " (id, content, boneType, updated_at) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, content);
            pstmt.setString(3, Type);
            pstmt.setString(4, timestamp);
            pstmt.executeUpdate();
        }
    }

    // Raccourci pour sauvegarder un Bone
    public static void saveBone(String id, String jsonContent, String boneType) throws SQLException {
        String timestamp = java.time.Instant.now().toString();
        // On injecte le timestamp dans le JSON avant de sauvegarder
        String enrichedJson = injectTimestamp(jsonContent, timestamp);
        save("BoneTable", id, enrichedJson, timestamp, boneType);
    }

    public static Object getInfo(String table, String query, String value, String valueToReturn) throws SQLException {
        if (!table.equals("BoneTable") && !table.equals("ClusterTable")) {
            return false;
        }

        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "SELECT * FROM " + table + " WHERE " + query + " = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString(valueToReturn);
            }
        }
        return false;
    }


    // Raccourci pour sauvegarder un Cluster
    public static void saveCluster(String id, String jsonContent, String clusterType) throws SQLException {
        String timestamp = java.time.Instant.now().toString();
        // On injecte le timestamp dans le JSON avant de sauvegarder
        String enrichedJson = injectTimestamp(jsonContent, timestamp);
        save("ClusterTable", id, enrichedJson, timestamp, clusterType);
    }

    // Utilitaire pour ajouter le champ updated_at dans le JSON lui-même
    private static String injectTimestamp(String json, String ts) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);
            ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("updated_at", ts);
            return node.toString();
        } catch (Exception e) {
            return json;
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
