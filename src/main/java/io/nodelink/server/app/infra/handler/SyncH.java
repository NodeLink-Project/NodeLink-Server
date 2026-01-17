package io.nodelink.server.app.infra.handler;

import io.javalin.http.Context;
import io.nodelink.server.app.infra.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public class SyncH {
    private final ObjectMapper mapper = new ObjectMapper();

    public void handle(Context ctx) throws Exception {
        String table = ctx.queryParam("table"); // Ex: ?table=BoneTable

        if (table == null || (!table.equals("BoneTable") && !table.equals("ClusterTable"))) {
            ctx.status(400).result("Table invalide ou manquante.");
            return;
        }

        // --- SI GET : On renvoie les données locales pour que l'autre compare ---
        if (ctx.method().toString().equalsIgnoreCase("GET")) {
            Map<String, String> data = DatabaseService.getAllRows(table);
            ctx.json(data);
        }

        // --- SI POST : On reçoit une donnée plus récente et on l'écrase ---
        else if (ctx.method().toString().equalsIgnoreCase("POST")) {
            JsonNode body = mapper.readTree(ctx.body());

            // On s'attend à recevoir un objet avec id, content et timestamp
            String id = body.get("id").asText();
            String content = body.get("content").asText();
            String timestamp = body.get("updated_at").asText();

            DatabaseService.save(table, id, content, timestamp, true);
            ctx.status(200).result("Sync locale effectuée");
        }
    }
}