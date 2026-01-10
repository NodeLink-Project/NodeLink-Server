package io.nodelink.server.app.config;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public interface RouteDefinition {
    HandlerType method();    // GET, POST, etc.
    String path();          // /example
    boolean enabled();      // Activé ou non
    void handle(Context ctx); // La logique (le handler)
}
