package io.nodelink.server.app.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ReplicationService {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void broadcast(String table, String id, String content, String timestamp) {
        List<String> peers = DatabaseService.getAllPeerUrls();

        // Préparation du message de synchronisation
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("table", table);
        payload.put("id", id);
        payload.set("content", parseJson(content)); // On remet le String en objet JSON
        payload.put("updated_at", timestamp);

        String jsonPayload = payload.toString();

        for (String url : peers) {
            // On n'envoie pas à soi-même (optionnel : vérifier si url != maPropreUrl)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/api/v1/sync"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    private static JsonNode parseJson(String s) {
        try { return new ObjectMapper().readTree(s); } catch (Exception e) { return null; }
    }
}
