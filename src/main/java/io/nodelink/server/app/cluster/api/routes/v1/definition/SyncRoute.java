package io.nodelink.server.app.cluster.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.cluster.api.routes.v1.handler.SyncHandler;
import io.nodelink.server.app.infra.RouteDefinition;

public class SyncRoute implements RouteDefinition {

    private final SyncHandler handler = new SyncHandler();

    @Override
    public HandlerType method() {
        // Le sync reçoit des données, donc on utilise POST
        return HandlerType.POST;
    }

    @Override
    public String path() {
        // L'URL finale sera /api/v1/sync
        return "/sync";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        handler.handle(ctx);
    }
}
