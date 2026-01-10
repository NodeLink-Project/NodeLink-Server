package io.nodelink.server.app.config;

import io.javalin.http.Context;

@FunctionalInterface
public interface ApiHandler {
    void handle(Context ctx) throws Exception;
}
