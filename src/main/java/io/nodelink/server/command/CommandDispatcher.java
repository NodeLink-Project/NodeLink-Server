package io.nodelink.server.command;

import java.util.*;
import java.util.function.Consumer;

/**
 * Dispatcher simple : associe un Enum (handler key) à une action (Consumer<String[]>).
 * Lors de l'exécution, on trouve le noeud le plus profond et on invoque le handler enregistré pour son owner.
 */
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
        action.accept(tokens.toArray(new String[0]));
        return true;
    }
}
