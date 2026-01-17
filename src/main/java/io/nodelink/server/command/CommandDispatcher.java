package io.nodelink.server.command;

import java.util.*;
import java.util.function.Consumer;

public class CommandDispatcher {

    private final CommandRegistry registry;
    private final Map<Enum<?>, Consumer<String[]>> handlers = new HashMap<>();

    public CommandDispatcher(CommandRegistry registry) {
        this.registry = registry;
    }

    public void registerHandler(Enum<?> key, Consumer<String[]> action) {
        handlers.put(key, action);
    }

    public boolean dispatch(String inputLine) {
        if (inputLine == null || inputLine.isBlank()) return false;
        List<String> tokens = Arrays.asList(inputLine.trim().split("\\s+"));
        Map.Entry<CommandNode, Integer> found = registry.findDeepest(tokens);
        CommandNode node = found.getKey();
        if (node == null) return false;
        Enum<?> owner = node.getOwner();
        if (owner == null) return false;
        Consumer<String[]> action = handlers.get(owner);
        if (action == null) return false;
        try {
            action.accept(tokens.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exécution de la commande", e);
        }
        return true;
    }
}
