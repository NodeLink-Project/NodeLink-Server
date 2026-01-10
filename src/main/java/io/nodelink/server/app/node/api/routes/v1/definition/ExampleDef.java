package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.config.RouteDefinition;
import io.nodelink.server.app.node.api.routes.v1.handler.ExampleHandler;

public class ExampleDef implements RouteDefinition {

    @Override
    public HandlerType method() { return HandlerType.GET; }

    @Override
    public String path() { return "/status"; }

    @Override
    public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) {
        new ExampleHandler().handle(ctx);
    }
}
