package io.nodelink.server.app.infra;

import io.javalin.Javalin;
import io.javalin.http.HandlerType;
import org.reflections.Reflections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RouteHandler {

    // Liste pour stocker les infos des routes pour l'export
    public static final List<Map<String, String>> registeredRoutesInfos = new ArrayList<>();

    public static void registerAllRoutes(Javalin app, String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends RouteDefinition>> routeClasses = reflections.getSubTypesOf(RouteDefinition.class);

        for (Class<? extends RouteDefinition> routeClass : routeClasses) {
            try {
                RouteDefinition route = routeClass.getDeclaredConstructor().newInstance();

                if (route.enabled()) {
                    String pkg = routeClass.getPackageName();
                    String version = "v1";
                    if (pkg.contains(".v2.")) version = "v2";

                    String fullPath = "/api/" + version + route.path();
                    HandlerType method = route.method();

                    // Enregistrement dans Javalin
                    app.addHttpHandler(method, fullPath, route::handle);

                    // Sauvegarde des infos pour la route de listing
                    registeredRoutesInfos.add(Map.of(
                            "name", routeClass.getSimpleName(),
                            "type", method.toString(),
                            "url", fullPath
                    ));

                    System.out.println("[ROUTE] " + method + " " + fullPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}