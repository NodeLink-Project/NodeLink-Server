package io.nodelink.server.app.cluster.api.routes.v1.handler;

import io.javalin.http.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

import java.util.Random;

public class AddClusterH implements ApiHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    @Override
    public void handle(Context ctx) throws Exception {
        ObjectNode requestBody = (ObjectNode) mapper.readTree(ctx.body());

        // 1. Validation de la localisation du Cluster (ex: "NBG_DE_EU_CLUSTER")
        String locParam = requestBody.get("location").asText();
        CLUSTER_LOCATION loc;
        try {
            loc = CLUSTER_LOCATION.valueOf(locParam);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : CLUSTER_LOCATION invalide.");
            return;
        }

        // 2. Validation du Bone Parent (ex: "EUROPE_WEST")
        String boneParam = requestBody.get("parentBone").asText();
        BONE_LOCATION boneLoc;
        try {
            boneLoc = BONE_LOCATION.valueOf(boneParam);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Erreur : BONE_LOCATION parent invalide.");
            return;
        }

        // 3. Génération ID "C" + random
        String clusterId = "C" + random.nextInt(10000);

        // 4. Construction de l'objet final
        ObjectNode clusterData = mapper.createObjectNode();
        clusterData.put("id", clusterId);
        clusterData.put("type", "Cluster");
        clusterData.put("name", loc.getNameCluster());
        clusterData.put("url", loc.getLocationCluster());

        // On récupère ici la propriété "locationBone" (ex: west.eu.bone)
        clusterData.put("parentBone", boneLoc.getLocation());

        // Coordonnées auto depuis l'Enum Cluster
        clusterData.putArray("coords")
                .add(loc.getLatitude())
                .add(loc.getLongitude());

        // Le lien technique (souvent l'ID du Bone ou d'un autre Cluster)
        clusterData.put("link", requestBody.get("link").asText());

        // 5. Sauvegarde
        DatabaseService.saveCluster(clusterId, clusterData.toString());

        ctx.status(201).json(clusterData);
    }
}