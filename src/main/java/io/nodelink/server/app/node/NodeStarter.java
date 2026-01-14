package io.nodelink.server.app.node;

import io.javalin.Javalin;
import io.nodelink.server.NodeLink;
import io.nodelink.server.app.infra.RouteHandler;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class NodeStarter {

    private static final NodeStarter INSTANCE = new NodeStarter();
    private Javalin app;

    static {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("io.javalin").setLevel(Level.OFF);
        loggerContext.getLogger("org.eclipse.jetty").setLevel(Level.OFF);
        loggerContext.getLogger("reactor").setLevel(Level.OFF);
    }

    public void startServer() {
        Mono.fromRunnable(() -> {
                    app = Javalin.create(config -> {
                        config.showJavalinBanner = false;
                        config.router.contextPath = "/bone";
                    }).start(8080);

                    RouteHandler.registerAllRoutes(app, "io.nodelink.server.app.node.api.routes");

                    NodeLink.getInstance().getLogger().SUCCESS("Node API Server started");
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    public Javalin getApp() {
        return app;
    }

    public static NodeStarter getNodeStarterSingleton() {
        return INSTANCE;
    }
}
