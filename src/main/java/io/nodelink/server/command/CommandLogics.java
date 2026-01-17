package io.nodelink.server.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nodelink.server.NodeLink;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.app.infra.DatabaseService;
import io.nodelink.server.app.infra.SyncEngine;
import io.nodelink.server.enums.CommandsEnum;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_API_STOP, tokens -> {


            terminal.writer().println("Spring Boot arrêté");
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

                HttpRequest idRequest = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/bone/api/v1/getId"))
                                        .GET()
                                                .build();

                HttpResponse<String> idResponse = client.send(idRequest, HttpResponse.BodyHandlers.ofString());
                int generatedId = mapper.readTree(idResponse.body()).get("id").asInt();

                String location = boneLocation.name();
                String finalUrl = String.format("http://%d." + boneLocation.getLocation() + ".nodelinkapp.xyz:8080", generatedId);

                String registrationJson = String.format(
                        "{\"id\": \"%d\", \"location\": \"%s\", \"url\": \"%s\"}",
                        generatedId,
                        location,
                        finalUrl
                );

                HttpRequest registerReq = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/bone/api/v1/addBone"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(registrationJson))
                                .build();

                NodeLink.getHelper().fullClearAndRefresh(terminal);

                client.sendAsync(registerReq, HttpResponse.BodyHandlers.ofString())
                                .thenAccept(res -> System.out.println("Enregistrement du Bone : " + res.body()));

                NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().BONE_LOCATION, boneLocation.name());
            } catch (IllegalArgumentException e) {
                terminal.writer().println("Emplacement invalide. Veuillez choisir parmi les emplacements disponibles.");
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_SET_CLUSTER_LOCATION, tokens -> {
            List<String> cleanTokens = new ArrayList<>();
            for (String token : tokens) {
                if (!token.trim().isEmpty()) cleanTokens.add(token.trim());
            }
            String[] cleanedTokens = cleanTokens.toArray(new String[0]);

            Object value = NodeLink.getInstance().getStoreData().get(NodeLink.getInstance().getStoreData().WHICH_TYPE);


            if (value == null || !((boolean) value)) {
                terminal.writer().println("Erreur : Mode Cluster non activé (Actuellement en mode Bone)");
                return;
            }

            if (cleanedTokens.length < 6) {
                terminal.writer().println("Usage: service set cluster location <location> <parent_bone>");
                terminal.writer().println("Exemple: service set cluster location NBG_DE_EU_CLUSTER EUROPE_WEST");
                return;
            }

            String locationInput = cleanedTokens[4].toUpperCase();
            String parentBoneInput = cleanedTokens[5].toUpperCase();

            try {
                CLUSTER_LOCATION clusterLocation = CLUSTER_LOCATION.valueOf(locationInput);
                BONE_LOCATION boneLocation = BONE_LOCATION.valueOf(parentBoneInput);

                terminal.writer().println("Initialisation du cluster sur : " + clusterLocation.name());

                // --- PHASE 1 : Récupération de l'ID (SYNCHRONE) ---
                // Vérifie bien si l'URL est /bone/ ou /cluster/ dans ton RouteHandler
                HttpRequest idRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/cluster/api/v1/getId"))
                        .GET()
                        .build();

                terminal.writer().println("Demande d'ID unique au serveur...");
                HttpResponse<String> idResponse = client.send(idRequest, HttpResponse.BodyHandlers.ofString());

                if (idResponse.statusCode() != 200) {
                    terminal.writer().println("Erreur ID (" + idResponse.statusCode() + ") : " + idResponse.body());
                    return;
                }

                int genId = mapper.readTree(idResponse.body()).get("id").asInt();
                terminal.writer().println("ID reçu : " + genId);

                System.out.println("LOCATION Cluster : " + clusterLocation.name());
                System.out.println("Parent Bone : " + boneLocation);

                // --- PHASE 2 : Enregistrement (SYNCHRONE) ---
                String registrationJson = String.format(
                        "{\"id\": \"%d\", \"location\": \"%s\", \"parentBone\": \"%s\", \"link\": \"NONE\"}",
                        genId, clusterLocation.name(), boneLocation
                );

                HttpRequest registerReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/cluster/api/v1/addCluster"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(registrationJson))
                        .build();

                terminal.writer().println("Enregistrement du cluster en cours...");
                HttpResponse<String> res = client.send(registerReq, HttpResponse.BodyHandlers.ofString());

                if (res.statusCode() == 201) {
                    terminal.writer().println("Succès : Cluster enregistré et propagé !");
                    terminal.writer().println("Détails : " + res.body());
                    NodeLink.getInstance().getStoreData().put(NodeLink.getInstance().getStoreData().CLUSTER_LOCATION, clusterLocation.name());
                } else {
                    terminal.writer().println("Erreur d'enregistrement (" + res.statusCode() + ") : " + res.body());
                }

            } catch (IllegalArgumentException e) {
                terminal.writer().println("Erreur : Emplacement invalide (Cluster ou Parent Bone).");
            } catch (Exception e) {
                terminal.writer().println("Erreur critique : " + e.getMessage());
                e.printStackTrace();
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

            String type = cleanTokens.get(4).toUpperCase(); // BONE ou CLUSTER
            String url = cleanTokens.get(5); // L'URL saisie

            // Petite validation de l'URL
            if (!url.startsWith("http")) {
                terminal.writer().println("Erreur : L'URL doit commencer par http:// ou https://");
                return;
            }

            try {
                // Enregistrement dans la table PeerTable de DatabaseService
                DatabaseService.addPeer(url, type);

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

            // On lance la tâche en arrière-plan
            new Thread(() -> {
                try {
                    // Appel du moteur de synchronisation que nous avons créé
                    SyncEngine.syncWithAllPeers();

                    terminal.writer().println("\n[Sync] Terminé avec succès.");
                    terminal.writer().println("Les tables BoneTable et ClusterTable sont à jour.");
                } catch (Exception e) {
                    terminal.writer().println("\n[Erreur Sync] : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_PEER_LIST, tokens -> {
            try {
                // Récupération des données (URL -> TYPE)
                Map<String, String> peers = DatabaseService.getAllPeersWithType();

                terminal.writer().println("\n" + "=".repeat(60));
                terminal.writer().println(String.format(" %-30s | %-15s", "URL DU PEER", "TYPE"));
                terminal.writer().println("-".repeat(60));

                if (peers.isEmpty()) {
                    terminal.writer().println(" Aucun peer enregistré.");
                } else {
                    peers.forEach((url, type) -> {
                        // On met de la couleur ou du style pour le type si besoin
                        terminal.writer().println(String.format(" %-30s | %-15s", url, type));
                    });
                }

                terminal.writer().println("=".repeat(60));
                terminal.writer().println(" Total : " + peers.size() + " peer(s) configuré(s)");
                terminal.writer().println();

            } catch (Exception e) {
                terminal.writer().println("Erreur d'affichage : " + e.getMessage());
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_DEV_PEER_REMOVE, tokens -> {
            // Nettoyage des tokens pour récupérer l'URL
            List<String> cleanTokens = new ArrayList<>();
            for (String t : tokens) if (!t.trim().isEmpty()) cleanTokens.add(t.trim());

            // Structure : service dev peer remove <URL>
            // Index     : 0       1   2    3      4
            if (cleanTokens.size() < 5) {
                terminal.writer().println("Usage: service dev peer remove <URL>");
                terminal.writer().println("Exemple: service dev peer remove http://localhost:8081");
                return;
            }

            String urlToRemove = cleanTokens.get(4);

            try {
                boolean deleted = DatabaseService.removePeer(urlToRemove);

                if (deleted) {
                    terminal.writer().println("Succès : Le peer [" + urlToRemove + "] a été supprimé.");
                } else {
                    terminal.writer().println("Erreur : Aucun peer trouvé avec l'URL : " + urlToRemove);
                    terminal.writer().println("Utilisez 'service dev peer list' pour vérifier les URLs exactes.");
                }
            } catch (Exception e) {
                terminal.writer().println("Erreur SQL : " + e.getMessage());
            }
        });

        dispatcher.registerHandler(CommandsEnum.SERVICE_MODE_STATUS, tokens -> {
            String STATUS = NodeLink.getHelper().getStatus();
            String PRODUCT = NodeLink.getHelper().getPRODUCT();

            terminal.writer().println("Statut actuel : " + STATUS + " (" + PRODUCT + ")");
            terminal.writer().println("Opérationnel ? :" + " RED STATUS");
        });
    }
}
