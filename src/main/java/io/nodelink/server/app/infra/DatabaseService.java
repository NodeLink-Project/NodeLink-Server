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

            // Tables existantes
            stmt.execute("CREATE TABLE IF NOT EXISTS ClusterTable (id TEXT PRIMARY KEY, content TEXT, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);");
            stmt.execute("CREATE TABLE IF NOT EXISTS BoneTable (id TEXT PRIMARY KEY, content TEXT, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);");

            // NOUVELLE TABLE : PeerTable pour stocker les URLs distantes
            stmt.execute("CREATE TABLE IF NOT EXISTS PeerTable (url TEXT PRIMARY KEY, type TEXT, added_at DATETIME DEFAULT CURRENT_TIMESTAMP);");

            stmt.execute("CREATE TRIGGER IF NOT EXISTS tr_update_cluster_time AFTER UPDATE ON ClusterTable BEGIN UPDATE ClusterTable SET updated_at = CURRENT_TIMESTAMP WHERE id = old.id; END;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter un Peer (Bone ou Cluster distant)
    public static void addPeer(String url, String type) throws SQLException {
        String sql = "INSERT OR REPLACE INTO PeerTable(url, type) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, url);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        }
    }

    // Récupère toutes les URLs à synchroniser
    public static List<String> getAllPeerUrls() {
        List<String> urls = new ArrayList<>();
        String sql = "SELECT url FROM PeerTable";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                urls.add(rs.getString("url"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static Map<String, String> getAllPeersWithType() {
        Map<String, String> peers = new java.util.HashMap<>();
        String sql = "SELECT url, type FROM PeerTable";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                peers.put(rs.getString("url"), rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return peers;
    }

    public static boolean removePeer(String url) throws SQLException {
        String sql = "DELETE FROM PeerTable WHERE url = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, url);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public static void save(String table, String id, String content, String timestamp, boolean fromSync) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "INSERT OR REPLACE INTO " + table + " (id, content, updated_at) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, content);
            pstmt.setString(3, timestamp);
            pstmt.executeUpdate();
        }
    }

    // Raccourci pour sauvegarder un Bone
    public static void saveBone(String id, String jsonContent) throws SQLException {
        String timestamp = java.time.Instant.now().toString();
        // On injecte le timestamp dans le JSON avant de sauvegarder
        String enrichedJson = injectTimestamp(jsonContent, timestamp);
        save("BoneTable", id, enrichedJson, timestamp, false);
    }

    // Raccourci pour sauvegarder un Cluster
    public static void saveCluster(String id, String jsonContent) throws SQLException {
        String timestamp = java.time.Instant.now().toString();
        // On injecte le timestamp dans le JSON avant de sauvegarder
        String enrichedJson = injectTimestamp(jsonContent, timestamp);
        save("ClusterTable", id, enrichedJson, timestamp, false);
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

    public static Map<String, String> getAllRows(String table) {
        // Sécurité : on vérifie que la table demandée est autorisée
        if (!table.equals("BoneTable") && !table.equals("ClusterTable")) {
            return new java.util.HashMap<>();
        }

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
        // Sécurité : on vérifie que la table est autorisée pour éviter l'injection SQL
        if (!table.equals("BoneTable") && !table.equals("ClusterTable") && !table.equals("PeerTable")) {
            return false;
        }

        String sql = "DELETE FROM " + table + " WHERE id = ?";
        // Note : Pour PeerTable, le champ est 'url', adaptons la requête si c'est cette table
        if (table.equals("PeerTable")) {
            sql = "DELETE FROM PeerTable WHERE url = ?";
        }

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Retourne vrai si une ligne a bien été supprimée

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression (" + table + ") : " + e.getMessage());
            return false;
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
