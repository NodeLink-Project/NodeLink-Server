package io.nodelink.server.command;

import java.util.*;
import java.util.stream.Collectors;

public class TabCompleter {

    private final CommandRegistry registry;

    public TabCompleter(CommandRegistry registry) {
        this.registry = registry;
    }

    public List<String> complete(String buffer) {
        if (buffer == null) return List.of();
        String trimmed = buffer.trim();
        String[] raw = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
        List<String> tokens = new ArrayList<>(Arrays.asList(raw));

        boolean trailingSpace = buffer.endsWith(" ");

        if (tokens.isEmpty()) {
            return registry.getRoots().stream().map(CommandNode::getName).collect(Collectors.toList());
        }

        if (tokens.size() == 1 && !trailingSpace) {
            String prefix = tokens.get(0);
            return registry.getRoots().stream()
                    .map(CommandNode::getName)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }

        List<String> forSearch = new ArrayList<>(tokens);
        if (!trailingSpace) {
            forSearch.remove(forSearch.size() - 1);
        }

        Map.Entry<CommandNode, Integer> found = registry.findDeepest(forSearch.isEmpty() ? List.of() : forSearch);
        CommandNode node = found.getKey();
        if (node == null) return List.of();

        String prefix;
        if (!trailingSpace && !tokens.isEmpty()) {
            prefix = tokens.get(tokens.size() - 1);
        } else {
            prefix = "";
        }

        return node.getChildren().stream()
                .map(CommandNode::getName)
                .filter(n -> n.startsWith(prefix))
                .collect(Collectors.toList());
    }
}
