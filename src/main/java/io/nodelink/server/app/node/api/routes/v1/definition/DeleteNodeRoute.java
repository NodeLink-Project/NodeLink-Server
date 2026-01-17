package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.infra.RouteDefinition;
import io.nodelink.server.app.node.api.routes.v1.handler.DeleteNodeH;

public class DeleteNodeRoute implements RouteDefinition {
    private final DeleteNodeH handler = new DeleteNodeH();

    @Override public HandlerType method() { return HandlerType.DELETE; }

    // Le {id} permet de passer l'identifiant directement dans l'URL
    @Override public String path() { return "/nodes/{id}"; }

    @Override public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) throws Exception {
        handler.handle(ctx);
    }
}