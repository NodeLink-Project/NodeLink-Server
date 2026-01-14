package io.nodelink.server.app.node.api.routes.v1.handler;

import io.javalin.http.Context;
import io.nodelink.server.app.data.BONE_LOCATION;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

import java.util.Random;

public class AddBoneH implements ApiHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    @Override
    public void handle(Context ctx) throws Exception {
        // 1. On lit le JSON (ex: { "location": "EUROPE_WEST", "url": "..." })
        ObjectNode requestBody = (ObjectNode) mapper.readTree(ctx.body());

        // 2. Validation de la location via l'Enum
        String locationName = requestBody.get("location").asText();
        BONE_LOCATION boneLoc;
        try {
            boneLoc = BONE_LOCATION.valueOf(locationName);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : La location '" + locationName + "' n'existe pas dans BONE_LOCATION.");
            return;
        }

        // 3. Génération de l'ID (B + random)
        String boneId = "B" + random.nextInt(10000);

        // 4. Construction de l'objet Bone (Sans links)
        ObjectNode boneData = mapper.createObjectNode();
        boneData.put("id", boneId);
        boneData.put("type", "Bone");
        boneData.put("name", "Bone-" + boneLoc.getNameLocationBone());
        boneData.put("url", requestBody.get("url").asText());

        // Coordonnées automatiques récupérées depuis l'Enum
        boneData.putArray("coords")
                .add(boneLoc.getLatitude())
                .add(boneLoc.getLongitude());

        // 5. Sauvegarde dans la table BoneTable
        DatabaseService.saveBone(boneId, boneData.toString());

        // 6. Réponse
        ctx.status(201).json(boneData);
    }
}
