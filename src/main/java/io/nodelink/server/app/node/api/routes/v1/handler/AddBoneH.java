package io.nodelink.server.app.node.api.routes.v1.handler;

import io.javalin.http.Context;
import io.nodelink.server.app.data.BONE_LOCATION;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;
import java.time.Instant;

public class AddBoneH implements ApiHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(Context ctx) throws Exception {
        // 1. Lecture sécurisée du JSON
        JsonNode requestBody = mapper.readTree(ctx.body());

        // Vérification si les champs indispensables sont présents
        if (requestBody == null || !requestBody.has("id") || !requestBody.has("location")) {
            ctx.status(400).result("Erreur : Les champs 'id' et 'location' sont obligatoires.");
            return;
        }

        // 2. Récupération des données
        String rawId = requestBody.get("id").asText();
        String boneId = "B" + rawId;

        // 3. Vérification des doublons dans SQLite
        try {
            if (DatabaseService.getTimestamp("BoneTable", boneId) != null) {
                ctx.status(409).result("Erreur : Le Bone " + boneId + " existe déjà.");
                return;
            }
        } catch (Exception e) {
            // Si la table n'existe pas encore ou erreur SQL
            System.err.println("Erreur SQL lors de la vérification : " + e.getMessage());
        }

        // 4. Validation de la location via l'Enum
        String locationName = requestBody.get("location").asText();
        BONE_LOCATION boneLoc;
        try {
            boneLoc = BONE_LOCATION.valueOf(locationName.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : Location '" + locationName + "' inconnue.");
            return;
        }

        // 5. Construction de l'URL et des données
        String subDomain = boneLoc.getLocation();
        String finalUrl = String.format("http://%s.%s.nodelinkapp.xyz", rawId, subDomain);

        ObjectNode boneData = mapper.createObjectNode();
        boneData.put("id", boneId);
        boneData.put("type", "Bone");
        boneData.put("name", "Bone-" + boneLoc.getNameLocationBone());
        boneData.put("url", finalUrl);

        boneData.putArray("coords")
                .add(boneLoc.getLatitude())
                .add(boneLoc.getLongitude());

        // 6. SAUVEGARDE DANS SQLITE
        try {
            // On convertit l'ObjectNode en String JSON pour le stockage
            String jsonString = boneData.toString();

            // On appelle la méthode que nous avons créée dans DatabaseService
            DatabaseService.saveBone(boneId, jsonString);

            // 7. RÉPONSE AU CLIENT
            System.out.println("[DB] Bone " + boneId + " enregistré avec succès.");
            ctx.status(201).json(boneData);

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}