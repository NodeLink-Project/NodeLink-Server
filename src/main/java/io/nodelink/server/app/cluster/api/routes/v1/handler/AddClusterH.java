package io.nodelink.server.app.cluster.api.routes.v1.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

import java.sql.SQLException;

public class AddClusterH implements ApiHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws JsonProcessingException, SQLException {
        JsonNode requestBody = objectMapper.readTree(ctx.body());

        // Check if in the request body we have "clusterType" and "boneType"
        if (requestBody == null || !requestBody.has("clusterType") || !requestBody.has("boneType")) {
            ctx.status(400).result("Invalid request body. 'boneType' and 'clusterType' are required.");
        }

        // Then i generate an ID
        int range = (1 - 9999) + 1;
        int id = (int) (Math.random() * range) + 9999;
        String clusterId = "C" + id;

        // I will put data in variables
        // Assert not null for requestBody
        assert requestBody != null;

        String clusterType = requestBody.get("clusterType").asText();
        String boneType = requestBody.get("boneType").asText();

        // Check SQLite
        if (DatabaseService.getTimestamp("ClusterTable", clusterId) != null) {
            ctx.status(409).result("Erreur : Le cluster " + clusterId + " existe déjà.");
        }

        // Check if location is valid
        CLUSTER_LOCATION clusterType_Enum;
        try {
            clusterType_Enum = CLUSTER_LOCATION.valueOf(clusterType.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : Location '" + clusterType + "' inconnue.");
            System.out.println("Location : " + clusterType + " inconnue");
            return;
        }

        Object getId = DatabaseService.getInfo("BoneTable", "boneType", boneType, "id").toString();



        String clusterLocation = clusterType_Enum.getLocationCluster();
        String finalUrl = String.format("https://%s.%s.nodelinkapp.xyz", clusterId, clusterLocation);
        // Response JSON
        ObjectNode clusterData = objectMapper.createObjectNode();
        clusterData.put("id", id);
        clusterData.put("clusterId", clusterId);
        clusterData.put("clusterType", clusterType);
        clusterData.put("url", finalUrl);

        clusterData.putArray("coords")
                .add(clusterType_Enum.getLatitude())
                .add(clusterType_Enum.getLongitude());

        // Save to Database
        try {
            DatabaseService.saveCluster(clusterId, clusterData.toString(), clusterType);

            System.out.println("[DB] Cluster " + clusterId + " saved to database.");
            ctx.status(201).json(clusterData);

        } catch (Exception e) {
            ctx.status(500).result("Error : " + e.getMessage());
        }
    }
}
