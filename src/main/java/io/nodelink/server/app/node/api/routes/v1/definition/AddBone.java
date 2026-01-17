package io.nodelink.server.app.node.api.routes.v1.definition;

import io.javalin.http.HandlerType;
import io.javalin.http.Context;
import io.nodelink.server.app.infra.RouteDefinition;
import io.nodelink.server.app.node.api.routes.v1.handler.AddBoneH;

public class AddBone implements RouteDefinition {
    @Override public HandlerType method() { return HandlerType.POST; }
    @Override public String path() { return "/addBone"; }
    @Override public boolean enabled() { return true; }

    @Override
    public void handle(Context ctx) throws Exception {
        new AddBoneH().handle(ctx);
    }
}
