package io.nodelink.server.app.cluster.api.routes.v1.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

public class SyncHandler implements ApiHandler {
    @Override
    public void handle(Context ctx) throws Exception {
        JsonNode data = new ObjectMapper().readTree(ctx.body());
        String id = data.get("id").asText();
        String table = data.get("table").asText();
        String incomingTs = data.get("updated_at").asText();
        String content = data.get("content").toString();

        // Vérification du timestamp pour éviter d'écraser du plus récent
        String localTs = DatabaseService.getTimestamp(table, id);

        if (localTs == null || incomingTs.compareTo(localTs) > 0) {
            // Sauvegarde avec fromSync = true pour stopper la propagation ici
            DatabaseService.saveAndSync(table, id, content, incomingTs, true);
            ctx.status(200).result("Updated");
        } else {
            ctx.status(304); // Déjà à jour
        }
    }
}
