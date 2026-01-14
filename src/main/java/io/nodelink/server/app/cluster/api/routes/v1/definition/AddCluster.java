package io.nodelink.server.app.cluster.api.routes.v1.definition;

import io.javalin.http.HandlerType;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.RouteDefinition;

public class AddCluster implements RouteDefinition {
    @Override
    public HandlerType method() { return HandlerType.POST; }

    @Override
    public String path() { return "/addCluster"; }

    @Override
    public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) throws Exception {
        new AddCluster().handle(ctx);
    }
}
