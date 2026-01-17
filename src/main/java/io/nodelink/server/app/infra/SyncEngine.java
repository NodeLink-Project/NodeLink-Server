package io.nodelink.server.app.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

public class SyncEngine {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void syncWithAllPeers() {
        // Récupère les URLs de ta table Peers (ex: http://34.12.x.x:8080)
        List<String> peers = DatabaseService.getAllPeerUrls();

        for (String peerUrl : peers) {
            System.out.println("[Sync] Tentative de synchronisation avec : " + peerUrl);
            syncTable(peerUrl, "BoneTable");
            syncTable(peerUrl, "ClusterTable");
        }
    }

    private static void syncTable(String peerUrl, String tableName) {
        try {
            // 1. Demander les données au Peer
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(peerUrl + "/api/v1/sync?table=" + tableName))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return;

            // 2. Analyser la réponse (Map<ID, JSON_CONTENT>)
            JsonNode remoteData = mapper.readTree(response.body());

            remoteData.fields().forEachRemaining(entry -> {
                String id = entry.getKey();
                String remoteJson = entry.getValue().asText();

                try {
                    JsonNode remoteObj = mapper.readTree(remoteJson);
                    String remoteTsStr = remoteObj.has("updated_at") ? remoteObj.get("updated_at").asText() : null;

                    // 3. Comparaison des dates
                    String localTsStr = DatabaseService.getTimestamp(tableName, id);

                    if (isRemoteNewer(localTsStr, remoteTsStr)) {
                        System.out.println("[Sync] Mise à jour détectée pour " + id + " (" + tableName + ")");
                        // On sauvegarde localement la version plus récente
                        DatabaseService.save(tableName, id, remoteJson, remoteTsStr, true);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur traitement ID " + id + " : " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Échec de synchronisation avec " + peerUrl + " : " + e.getMessage());
        }
    }

    private static boolean isRemoteNewer(String localTs, String remoteTs) {
        if (remoteTs == null) return false;
        if (localTs == null) return true; // On ne l'a pas, donc c'est forcément "plus récent"

        try {
            Instant local = Instant.parse(localTs);
            Instant remote = Instant.parse(remoteTs);
            return remote.isAfter(local);
        } catch (Exception e) {
            // En cas d'erreur de format, on compare les chaînes de caractères par défaut
            return remoteTs.compareTo(localTs) > 0;
        }
    }
}