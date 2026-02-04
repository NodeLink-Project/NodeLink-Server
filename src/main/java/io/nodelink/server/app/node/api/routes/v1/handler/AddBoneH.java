package io.nodelink.server.app.node.api.routes.v1.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

public class AddBoneH implements ApiHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws JsonProcessingException {
            JsonNode requestBody = objectMapper.readTree(ctx.body());

            // Check if in the request body we have "location" and "boneType"
            if (requestBody == null || !requestBody.has("boneType")) {
                ctx.status(400).result("Invalid request body. 'boneType' are required.");
            }

            // Then i generate an ID
            int range = (1 - 9999) + 1;
            int id = (int) (Math.random() * range) + 9999;
            String boneId = "B" + id;

            // I will put data in variables
            // Assert not null for requestBody
            assert requestBody != null;

            String boneType = requestBody.get("boneType").asText();

            // Check SQLite
            if (DatabaseService.getTimestamp("BoneTable", boneId) != null) {
                ctx.status(409).result("Erreur : Le Bone " + boneId + " existe déjà.");
            }

            // Check if location is valid
            BONE_LOCATION boneType_Enum;
            try {
                boneType_Enum = BONE_LOCATION.valueOf(boneType.toUpperCase());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result("Erreur : Location '" + boneType + "' inconnue.");
                e.getStackTrace();
                return;
            }

            String boneLocation = boneType_Enum.getLocation();
            String finalUrl = String.format("https://%s.%s.nodelinkapp.xyz", boneId, boneLocation);

            // Response JSON
            ObjectNode boneData = objectMapper.createObjectNode();
            boneData.put("id", id);
            boneData.put("boneId", boneId);
            boneData.put("boneType", boneType);
            boneData.put("url", finalUrl);

            boneData.putArray("coords")
                    .add(boneType_Enum.getLatitude())
                    .add(boneType_Enum.getLongitude());

            // Save to Database
        try {
            DatabaseService.saveBone(boneId, boneData.toString(), boneType);

            System.out.println("[DB] Bone " + boneId + " saved to database.");
            ctx.status(201).json(boneData);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Error : " + e.getMessage());
        }
    }
}
