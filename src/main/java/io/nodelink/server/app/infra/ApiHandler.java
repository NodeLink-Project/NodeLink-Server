package io.nodelink.server.app.infra;

import io.javalin.http.Context;

@FunctionalInterface
public interface ApiHandler {
    void handle(Context ctx) throws Exception;
}
