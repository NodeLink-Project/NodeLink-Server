package io.nodelink.server.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nodelink.server.NodeLink;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.app.infra.CONSTANT;
import io.nodelink.server.enums.CommandsEnum;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CommandLogics {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    private void blankSpace(Terminal terminal) {
        terminal.writer().println("\n");
    }

    public CommandLogics(CommandDispatcher dispatcher, LineReader reader, Terminal terminal) {


        dispatcher.registerHandler(CommandsEnum.CLEAR, tokens -> {
            NodeLink.getHelper().fullClearAndRefresh(terminal);
        });

        dispatcher.registerHandler(CommandsEnum.HELP, tokens -> {
            //NodeLink.getHelper().displayHelp(terminal);
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER, tokens -> {
            NodeLink.getHelper().updateStatus("Cluster");
            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().WHICH_TYPE, true);

            NodeLink.getHelper().fullClearAndRefresh(terminal);

            NodeLink.getInstance().getClusterStarter().startServer();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE, tokens -> {
            NodeLink.getHelper().updateStatus("Bone");
            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().WHICH_TYPE, false);

            NodeLink.getHelper().fullClearAndRefresh(terminal);

            NodeLink.getInstance().getNodeStarter().startServer();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_BONE_LOCATION, tokens -> {
            List<String> cleanTokens = new ArrayList<>();
            for (String token : tokens) {
                if (!token.trim().isEmpty()) {
                    cleanTokens.add(token.trim());
                }
            }
            String[] cleanedTokens = cleanTokens.toArray(new String[0]);

            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

            if (value == null || (boolean) value) {
                terminal.writer().println("Erreur : Mode Bone non activé");
                return;
            }

            if (cleanedTokens.length < 5) {
                terminal.writer().println("Usage: service set bone location <location>");
                return;
            }

            String locationInput = cleanedTokens[4].toUpperCase();
            try {
                BONE_LOCATION boneLocation = BONE_LOCATION.valueOf(locationInput);
                terminal.writer().println("Emplacement du bone défini sur : " + boneLocation.name());

                String registrationJson = String.format(
                        "{\"boneType\": \"%s\"}",
                        boneLocation.name()
                );

                HttpRequest registerReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + CONSTANT.PORT_BONE + "/bone/api/v1/addBone"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(registrationJson))
                        .build();

                client.sendAsync(registerReq, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(res -> {
                            JsonNode responseJson;

                            try {
                                responseJson = mapper.readTree(res.body());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().ID_BONE, responseJson.get("boneId").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().TYPE_BONE, responseJson.get("boneType").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().ID, responseJson.get("id").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().URL_BONE, responseJson.get("url").asText());
                        });

                NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().BONE_LOCATION, boneLocation.name());

                NodeLink.getHelper().fullClearAndRefresh(terminal);
            } catch (IllegalArgumentException e) {
                terminal.writer().println("Emplacement invalide. Veuillez choisir parmi les emplacements disponibles.");
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER_LOCATION, tokens -> {
            List<String> cleanTokens = new ArrayList<>();
            for (String token : tokens) {
                if (!token.trim().isEmpty()) {
                    cleanTokens.add(token.trim());
                }
            }
            String[] cleanedTokens = cleanTokens.toArray(new String[0]);

            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

            if (value == null || !(boolean) value) {
                terminal.writer().println("Erreur : Mode Cluster non activé");
                return;
            }

            if (cleanedTokens.length < 6) {
                terminal.writer().println("Usage: service set cluster location <locationCluster> <locationBone>");
                return;
            }

            String oneArgument = cleanedTokens[4].toUpperCase();
            String twoArgument = cleanedTokens[5].toUpperCase();
            System.out.println(oneArgument);
            System.out.println(twoArgument);
            try {
                CLUSTER_LOCATION clusterLocation = CLUSTER_LOCATION.valueOf(oneArgument);
                terminal.writer().println("Emplacement du bone défini sur : " + clusterLocation.name());

                BONE_LOCATION boneLocation = BONE_LOCATION.valueOf(twoArgument);


                String registrationJson = String.format(
                        "{\"clusterType\":\"%s\", \"boneType\":\"%s\"}",
                        clusterLocation.name(),
                        boneLocation.name()
                );

                System.out.println(registrationJson);

                HttpRequest registerReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + CONSTANT.PORT_CLUSTER + "/cluster/api/v1/addCluster"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(registrationJson))
                        .build();

                client.sendAsync(registerReq, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(res -> {
                            JsonNode responseJson;

                            try {
                                responseJson = mapper.readTree(res.body());
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                            System.out.println(res.body());

                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().ID_BONE, responseJson.get("boneId").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().TYPE_BONE, responseJson.get("boneType").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().ID, responseJson.get("id").asText());
                            NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().URL_BONE, responseJson.get("url").asText());
                        });

                NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().BONE_LOCATION, clusterLocation.name());

//                NodeLink.getHelper().fullClearAndRefresh(terminal);
            } catch (IllegalArgumentException e) {
                terminal.writer().println("Emplacement invalide. Veuillez choisir parmi les emplacements disponibles.");
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_PEER_ADD, tokens -> {
            // Nettoyage des tokens (on enlève les espaces vides)
            List<String> cleanTokens = new ArrayList<>();
            for (String t : tokens) if (!t.trim().isEmpty()) cleanTokens.add(t.trim());

            // Structure attendue : service dev peer add <TYPE> <URL>
            // Index :              0       1   2    3    4      5
            if (cleanTokens.size() < 6) {
                terminal.writer().println("Usage: service dev peer add <BONE|CLUSTER> <URL>");
                terminal.writer().println("Exemple: service dev peer add BONE http://127.0.0.1:8081");
                return;
            }

            String type = cleanTokens.get(4).toUpperCase();
            String url = cleanTokens.get(5);

            // Petite validation de l'URL
            if (!url.startsWith("http")) {
                terminal.writer().println("Erreur : L'URL doit commencer par http:// ou https://");
                return;
            }

            try {

                terminal.writer().println("------------------------------------------");
                terminal.writer().println("Succès : Nouveau Peer enregistré !");
                terminal.writer().println("Type : " + type);
                terminal.writer().println("URL  : " + url);
                terminal.writer().println("Ce nœud sera inclus dans la prochaine synchronisation.");
                terminal.writer().println("------------------------------------------");

            } catch (Exception e) {
                terminal.writer().println("Erreur lors de l'ajout en base de données : " + e.getMessage());
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_SYNC_START, tokens -> {
            terminal.writer().println("[Sync] Initialisation de la synchronisation globale...");

            new Thread(() -> {
                try {

                } catch (Exception e) {
                    terminal.writer().println("\n[Erreur Sync] : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_PEER_LIST, tokens -> {
            try {

                terminal.writer().println("\n" + "=".repeat(60));
                terminal.writer().println(String.format(" %-30s | %-15s", "URL DU PEER", "TYPE"));
                terminal.writer().println("-".repeat(60));

                terminal.writer().println();

            } catch (Exception e) {
                terminal.writer().println("Erreur d'affichage : " + e.getMessage());
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_PEER_REMOVE, tokens -> {
            // Nettoyage des tokens pour récupérer l'URL
            List<String> cleanTokens = new ArrayList<>();
            for (String t : tokens) if (!t.trim().isEmpty()) cleanTokens.add(t.trim());

            if (cleanTokens.size() < 5) {
                terminal.writer().println("Usage: service dev peer remove <URL>");
                terminal.writer().println("Exemple: service dev peer remove http://localhost:8081");
                return;
            }

            String urlToRemove = cleanTokens.get(4);


        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_INFO_STATS, tokens -> {
            terminal.writer().println("Affichage des stats...");

            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);

            if (value == null) {
                terminal.writer().println("Type de nœud : Non défini");
            } else if ((boolean) value) {
                terminal.writer().println("Type de nœud : Cluster");
            } else {
                terminal.writer().println("Type de nœud : Bone");
                terminal.writer().println("Bone ID    : " + NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().ID_BONE));
                terminal.writer().println("Bone Type  : " + NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().TYPE_BONE));
                terminal.writer().println("Bone URL   : " + NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().URL_BONE));
                terminal.writer().println("ID  : " + NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().ID));
            }
        });
    }
}
