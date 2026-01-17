package io.nodelink.server.update;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.nodelink.server.NodeLink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Updater {

    private static final Updater INSTANCE = new Updater();

    public void checkForUpdates() {
        try {
            NodeLink.getInstance().getLogger().INFO("\u001B[94m[NodeLink] \u001B[0mChecking for updates...");
            NodeLink.getInstance().getLogger().INFO("\u001B[94m[NodeLink] \u001B[0mEn rainson de coût financier lié aux requêtes API GitHub, la vérification des mises à jour sont interrrompues jusqu'à nouvel ordre.");

            Thread.sleep(2000);

            NodeLink.getHelper().INITIALIZE();

//            Class<?> classReference = Updater.class;
//
//            URL url = classReference.getProtectionDomain().getCodeSource().getLocation();
//
//            Path filePath = Paths.get(url.toURI());
//            Path FolderParent = filePath.getParent();
//
//            if (isLatestVersion()) {
//                downloadFile("https://raw.githubusercontent.com/NodeLink-Project/nodelink-project.github.io/main/nodelink-server/jar/io/nodelink/server/NodeLink-Server/" + fetchVersion() + "/NodeLink-Server-" + fetchVersion() + "-fat.jar", FolderParent.toString() + "/NodeLink-Server-" + fetchVersion() + ".jar");
//                removeAndStartNewVersion();
//            }

        } catch (Exception exception) {
            NodeLink.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    private String fetchVersion() {
        try {
            final String API_URL = "https://api.github.com/repos/NodeLink-Project/NodeLink-Server/tags";
            final String TOKEN = "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";

            final String authorizationHeader = "Bearer " + TOKEN;
            Thread.sleep(1000);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", authorizationHeader)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonArray data = gson.fromJson(response.body(), JsonArray.class);

            if (data != null && !data.isEmpty()) {
                return data.get(0).getAsJsonObject().get("name").getAsString();
            } else {
                return "No tags found";
            }

        } catch (Exception exception) {
            NodeLink.getInstance().getLogger().ERROR(exception.getMessage());
            return "Error";
        }
    }

    private boolean isLatestVersion() {
        try {
            String NODELINK = "\u001B[94m[NodeLink] \u001B[0m";
            String fetchedVersion = fetchVersion();

            if (!fetchedVersion.equals(Version.VERSION)) {
                NodeLink.getInstance().getLogger().INFO(NODELINK + "A new version is available: " + fetchedVersion + " (You are using " + Version.VERSION + ")");
                Thread.sleep(1000);

                return true;
            } else {
                NodeLink.getInstance().getLogger().INFO(NODELINK + "You are using the latest version: " + Version.VERSION);

                NodeLink.getHelper().INITIALIZE();
            }

        } catch (Exception exception) {
            NodeLink.getInstance().getLogger().ERROR(exception.getMessage());
        }
        return false;
    }

    private void downloadFile(String urlStr, String filePath) {
        try {
            Thread.sleep(1000);

            URL url = new URL(urlStr);

            try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(filePath)) {

                long bytesTransferred = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.flush();
                fos.getFD().sync();

                System.out.println("Downloaded " + bytesTransferred + " bytes to " + filePath);
            }

            Thread.sleep(500);

            File downloadedFile = new File(filePath);
            if (!downloadedFile.exists() || downloadedFile.length() == 0) {
                throw new IOException("Download failed: file is missing or empty");
            }

            System.out.println("File downloaded successfully: " + downloadedFile.length() + " bytes");

        } catch (Exception exception) {
            exception.printStackTrace();
            NodeLink.getInstance().getLogger().ERROR("Download error: " + exception.getMessage());

            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void removeAndStartNewVersion() {
        try {
            Thread.sleep(1000);

            Class<?> classReference = Updater.class;

            URL url = classReference.getProtectionDomain().getCodeSource().getLocation();

            Path filePath = Paths.get(url.toURI());
            Path FolderParent = filePath.getParent();

            File LOCAL_JAR = new File(FolderParent.toString() + "/NodeLink-Server-" + Version.VERSION + ".jar");
            String newJarPath = FolderParent + "/NodeLink-Server-" + fetchVersion() + ".jar";

            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-jar",
                    newJarPath,
                    "--delete-old",
                    LOCAL_JAR.getAbsolutePath()
            );

            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();

            Thread.sleep(2000);

            if (!process.isAlive()) {
                System.err.println("ERROR: New process exited with code: " + process.exitValue());
                System.err.println("The new version failed to start. Check the output above.");
            } else {
                System.exit(1);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            NodeLink.getInstance().getLogger().ERROR(exception.getMessage());
        }
    }

    public static Updater getUpdaterSingleton() {
        return INSTANCE;
    }
}
