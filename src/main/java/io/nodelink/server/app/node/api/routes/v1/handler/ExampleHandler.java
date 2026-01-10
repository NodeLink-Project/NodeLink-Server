package io.nodelink.server.app.node.api.routes.v1.handler;

import io.javalin.http.Context;
import io.nodelink.server.app.config.ApiHandler;

import java.util.Map;

public class ExampleHandler implements ApiHandler {
    @Override
    public void handle(Context ctx) {
        ctx.json(Map.of("status", "Operational..."));
    }
}
