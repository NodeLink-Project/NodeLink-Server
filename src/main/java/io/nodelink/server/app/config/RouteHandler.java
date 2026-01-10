package io.nodelink.server.app.config;

import io.javalin.Javalin;
import org.reflections.Reflections;
import java.util.Set;

public class RouteHandler {
    public static void registerAllRoutes(Javalin app, String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends RouteDefinition>> routeClasses = reflections.getSubTypesOf(RouteDefinition.class);

        for (Class<? extends RouteDefinition> routeClass : routeClasses) {
            try {
                RouteDefinition route = routeClass.getDeclaredConstructor().newInstance();

                if (route.enabled()) {
                    String pkg = routeClass.getPackageName();
                    String[] parts = pkg.split("\\.");
                    String version = "v1";

                    for (String part : parts) {
                        if (part.matches("v\\d+")) {
                            version = part;
                            break;
                        }
                    }

                    String fullPath = "/api/" + version + route.path();

                    app.addHttpHandler(route.method(), fullPath, route::handle);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Could not register route: " + routeClass.getName());
                e.printStackTrace();
            }
        }
    }
}