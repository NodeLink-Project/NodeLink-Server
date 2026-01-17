package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.infra.RouteDefinition;
import io.nodelink.server.app.node.api.routes.v1.handler.GetAllNodesH;

public class GetAllNodesRoute implements RouteDefinition {
    private final GetAllNodesH handler = new GetAllNodesH();

    @Override public HandlerType method() { return HandlerType.GET; }
    @Override public String path() { return "/nodes"; }
    @Override public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) throws Exception {
        handler.handle(ctx);
    }
}
