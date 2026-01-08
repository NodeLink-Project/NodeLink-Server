package io.nodelink.server.app.node.api;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class NodeStarter implements WebFluxConfigurer {

    private static final NodeStarter INSTANCE = new NodeStarter();

    public static NodeStarter getNodeStarter() {
        return INSTANCE;
    }

    public Mono<Void> startNode() {
        //connectLib.StoreAndRetrieve().put(connectLib.StoreAndRetrieve().IS_APP_RUNNING, true);
        return Mono.fromRunnable(() -> {
            SpringApplication app = new SpringApplication(NodeStarter.class);
            app.setBannerMode(Banner.Mode.OFF);
            app.setLogStartupInfo(false);
            Map<String, Object> props = new HashMap<>();
            props.put("server.port", 8080);
            props.put("logging.level.root", "OFF");
            app.setDefaultProperties(props);
            app.run();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
