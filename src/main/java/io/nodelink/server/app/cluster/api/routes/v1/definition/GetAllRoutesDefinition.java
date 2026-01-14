package io.nodelink.server.app.cluster.api.routes.v1.definition;

import io.nodelink.server.app.infra.RouteDefinition;
import io.javalin.http.HandlerType;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.RouteHandler;

public class GetAllRoutesDefinition implements RouteDefinition {

    @Override
    public HandlerType method() {
        return HandlerType.GET;
    }

    @Override
    public String path() {
        // L'URL finale sera /api/v1/routes
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
