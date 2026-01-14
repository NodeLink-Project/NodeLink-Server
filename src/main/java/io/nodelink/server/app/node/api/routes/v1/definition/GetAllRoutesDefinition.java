package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.nodelink.server.app.infra.RouteDefinition;
import io.nodelink.server.app.infra.RouteHandler;

public class GetAllRoutesDefinition implements RouteDefinition {

    @Override
    public HandlerType method() {
        return HandlerType.GET;
    }

    @Override
    public String path() {
        return "/routes";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public void handle(Context ctx) {
        ctx.json(RouteHandler.registeredRoutesInfos);
    }
}
