package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.infra.RouteDefinition;
import io.nodelink.server.app.node.api.routes.v1.handler.GetIDH;

public class GetID implements RouteDefinition {
    @Override public HandlerType method() { return HandlerType.GET; }
    @Override public String path() { return "/getId"; }
    @Override public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) throws Exception {
        new GetIDH().handle(ctx);
    }
}
