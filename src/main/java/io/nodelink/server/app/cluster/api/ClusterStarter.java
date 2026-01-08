package io.nodelink.server.app.cluster.api;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ClusterStarter implements WebFluxConfigurer {

    private static final ClusterStarter INSTANCE = new ClusterStarter();

    public static ClusterStarter getClusterStarter() {
        return INSTANCE;
    }

    public Mono<Void> startCluster() {
        //connectLib.StoreAndRetrieve().put(connectLib.StoreAndRetrieve().IS_APP_RUNNING, true);
        return Mono.fromRunnable(() -> {
            SpringApplication app = new SpringApplication(ClusterStarter.class);
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
