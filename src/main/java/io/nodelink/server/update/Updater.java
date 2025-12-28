package io.nodelink.server.update;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.nodelink.server.NodeLink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class Updater {

    private static final Updater INSTANCE = new Updater();
    private final String PREFIX = "\u001B[94m[NodeLink] \u001B[0m";

    public static Updater getUpdaterSingleton() {
        return INSTANCE;
    }

    public void handleArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--delete-old") && (i + 1) < args.length) {
                try {
                    Path oldPath = Paths.get(args[i + 1]);
                    // On attend un peu que l'ancien OS libère le verrou sur le fichier
                    Thread.sleep(1500);
                    if (Files.exists(oldPath)) {
                        Files.delete(oldPath);
                        System.out.println(PREFIX + "Cleanup: Old version deleted.");
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    public Mono<Void> checkForUpdates() {
        return fetchVersionMono()
                .flatMap(latest -> {
                    if (latest.equals(Version.VERSION)) {
                        System.out.println(PREFIX + "Latest version: " + Version.VERSION);
                        NodeLink.getHelper().INITIALIZE();
                        return Mono.empty();
                    }
                    return prepareAndDownload(latest).flatMap(this::launchNewProcess);
                })
                .doOnError(e -> {
                    System.err.println("Update failed: " + e.getMessage());
                    NodeLink.getHelper().INITIALIZE();
                });
    }

    private Mono<String> fetchVersionMono() {
        return Mono.fromCallable(() -> {
            System.out.println(PREFIX + "Checking updates...");
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/NodeLink-Project/NodeLink-Server/tags"))
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray data = new Gson().fromJson(response.body(), JsonArray.class);
            return (data != null && !data.isEmpty()) ? data.get(0).getAsJsonObject().get("name").getAsString() : Version.VERSION;
        }).subscribeOn(Schedulers.boundedElastic()).onErrorReturn(Version.VERSION);
    }

    private Mono<UpdateContext> prepareAndDownload(String latestVersion) {
        return Mono.fromCallable(() -> {
            System.out.println(PREFIX + "Downloading: " + latestVersion);
            URL url = Updater.class.getProtectionDomain().getCodeSource().getLocation();
            Path currentJarPath = Paths.get(url.toURI());
            Path folder = currentJarPath.getParent();
            Path newJarPath = folder.resolve("NodeLink-Server-" + latestVersion + ".jar");

            String downloadUrl = "https://raw.githubusercontent.com/NodeLink-Project/nodelink-project.github.io/main/nodelink-server/jar/io/nodelink/server/NodeLink-Server/"
                    + latestVersion + "/NodeLink-Server-" + latestVersion + "-fat.jar";

            try (ReadableByteChannel rbc = Channels.newChannel(new URL(downloadUrl).openStream());
                 FileOutputStream fos = new FileOutputStream(newJarPath.toFile())) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return new UpdateContext(currentJarPath.toAbsolutePath().toString(), newJarPath.toAbsolutePath().toString());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Void> launchNewProcess(UpdateContext ctx) {
        return Mono.fromRunnable(() -> {
            try {
                System.out.println(PREFIX + "Starting new version...");
                String javaBin = ProcessHandle.current().info().command().orElse("java");

                ProcessBuilder pb = new ProcessBuilder(javaBin, "-jar", ctx.newJar, "--delete-old", ctx.oldJar);
                pb.inheritIO();

                // On démarre le processus
                pb.start();

                // On laisse un délai pour que le nouveau processus s'imprime à l'écran
                // avant de tuer brutalement celui-ci.
                Thread.sleep(1000);
                System.out.println(PREFIX + "Update complete. Goodbye!");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                NodeLink.getHelper().INITIALIZE();
            }
        });
    }

    private record UpdateContext(String oldJar, String newJar) {}
}
