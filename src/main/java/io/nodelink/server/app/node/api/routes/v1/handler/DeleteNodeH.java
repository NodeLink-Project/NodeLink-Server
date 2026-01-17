package io.nodelink.server.app.node.api.routes.v1.handler;

import io.javalin.http.Context;
import io.nodelink.server.app.infra.ApiHandler;
import io.nodelink.server.app.infra.DatabaseService;

public class DeleteNodeH implements ApiHandler {

    @Override
    public void handle(Context ctx) throws Exception {
        // On récupère l'ID depuis l'URL (ex: /nodes/B3433)
        String id = ctx.pathParam("id");

        // Tentative de suppression dans les deux tables
        boolean deletedFromBones = DatabaseService.deleteRow("BoneTable", id);
        boolean deletedFromClusters = DatabaseService.deleteRow("ClusterTable", id);

        if (deletedFromBones || deletedFromClusters) {
            System.out.println("[P2P] Nœud supprimé du réseau : " + id);
            ctx.status(200).result("Nœud " + id + " supprimé avec succès.");
        } else {
            ctx.status(404).result("Erreur : Aucun nœud trouvé avec l'ID " + id);
        }
    }
}
