package io.nodelink.server.app.node.api.routes.v1.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

import java.util.Map;

public class GetAllNodesH implements ApiHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws Exception {
        // Récupération du filtre optionnel (ex: ?location=EUROPE_WEST)
        String filterLocation = ctx.queryParam("location");

        ObjectNode response = mapper.createObjectNode();

        // 1. Filtrage et ajout des Bones
        ArrayNode bonesArray = response.putArray("bones");
        processTable("BoneTable", bonesArray, filterLocation);

        // 2. Filtrage et ajout des Clusters
        ArrayNode clustersArray = response.putArray("clusters");
        processTable("ClusterTable", clustersArray, filterLocation);

        ctx.json(response);
    }

    private void processTable(String tableName, ArrayNode arrayNode, String filter) throws Exception {
        Map<String, String> data = DatabaseService.getAllRows(tableName);
        for (String content : data.values()) {
            JsonNode node = mapper.readTree(content);

            // Si pas de filtre, on ajoute tout.
            // Si filtre, on vérifie si le nom contient la localisation (ex: Bone-EUROPE_WEST)
            if (filter == null || node.get("name").asText().toUpperCase().contains(filter.toUpperCase())) {
                arrayNode.add(node);
            }
        }
    }
}