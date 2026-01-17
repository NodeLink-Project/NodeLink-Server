package io.nodelink.server.app.cluster.api.routes.v1.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

public class AddClusterH implements ApiHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws Exception {
        try {
            // 1. Lecture du JSON entrant
            JsonNode requestBody = mapper.readTree(ctx.body());

            // 2. Vérification des champs obligatoires
            if (requestBody == null || !requestBody.has("id") || !requestBody.has("location") || !requestBody.has("parentBone")) {
                ctx.status(400).result("Erreur : Les champs 'id', 'location' et 'parentBone' sont requis.");
                return;
            }

            // 3. Extraction et formatage
            String rawId = requestBody.get("id").asText();
            String clusterId = "C" + rawId;

            // Validation des enums (lance une IllegalArgumentException si invalide)
            CLUSTER_LOCATION loc = CLUSTER_LOCATION.valueOf(requestBody.get("location").asText().toUpperCase());
            BONE_LOCATION boneLoc = BONE_LOCATION.valueOf(requestBody.get("parentBone").asText().toUpperCase());

            // 4. Préparation de l'objet final à stocker
            String finalUrl = String.format("http://%s.%s.nodelinkapp.xyz", rawId, loc.getLocationCluster());

            ObjectNode clusterData = mapper.createObjectNode();
            clusterData.put("id", clusterId);
            clusterData.put("type", "Cluster");
            clusterData.put("name", loc.getNameCluster());
            clusterData.put("url", finalUrl);
            clusterData.put("parentBone", boneLoc.getLocation());

            clusterData.putArray("coords")
                    .add(loc.getLatitude())
                    .add(loc.getLongitude());

            String link = requestBody.has("link") ? requestBody.get("link").asText() : "NONE";
            clusterData.put("link", link);

            // 5. Appel du SAVE LOCAL (Simple SQLite)
            // On utilise saveCluster qui est déjà défini dans ton DatabaseService
            DatabaseService.saveCluster(clusterId, clusterData.toString());

            System.out.println("[INFO] Cluster " + clusterId + " sauvegardé localement.");

            // 6. Réponse au client
            ctx.status(201).json(clusterData);

        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : Localisation invalide.");
        } catch (Exception e) {
            System.err.println("--- CRASH SERVEUR ---");
            System.err.println("Cause : " + e.getMessage());
            e.printStackTrace(); // <--- C'est cette ligne qui va te dire la ligne exacte du bug
            ctx.status(500).result("Erreur : " + e.getMessage());
        }
    }
}