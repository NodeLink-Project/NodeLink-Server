package io.nodelink.server.command;

import io.nodelink.server.provider.CommandsProvider;

import java.util.*;
public class CommandRegistry {

    private final Map<String, CommandNode> roots = new LinkedHashMap<>();

    public void registerParentEnum(Class<? extends Enum<?>> enumClass) {
        if (enumClass == null) return;
        Enum<?>[] consts = enumClass.getEnumConstants();
        if (consts == null) return;
        for (Enum<?> e : consts) {
            if (e instanceof CommandsProvider cpp) {
                CommandNode node = cpp.getCommandNode();
                if (node != null) {
                    CommandNode root = roots.computeIfAbsent(node.getName(), CommandNode::new);
                    merge(root, node);
                    if (root.getOwner() == null && node.getOwner() != null) {
                        root.setOwner(node.getOwner());
                    }
                }
            }
        }
    }

    private void merge(CommandNode target, CommandNode src) {
        if (target.getOwner() == null && src.getOwner() != null) {
            target.setOwner(src.getOwner());
        }
        for (CommandNode child : src.getChildren()) {
            CommandNode existing = target.getChild(child.getName());
            if (existing == null) {
                target.addChild(child);
            } else {
                merge(existing, child);
            }
        }
    }

    public Optional<CommandNode> getRoot(String name) {
        return Optional.ofNullable(roots.get(name));
    }

    public Collection<CommandNode> getRoots() {
        return roots.values();
    }

    public Map.Entry<CommandNode, Integer> findDeepest(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return Map.entry(null, -1);
        CommandNode current = roots.get(tokens.get(0));
        if (current == null) return Map.entry(null, -1);
        int idx = 0;
        while (idx + 1 < tokens.size()) {
            CommandNode next = current.getChild(tokens.get(idx + 1));
            if (next == null) break;
            current = next;
            idx++;
        }
        return Map.entry(current, idx);
    }
}