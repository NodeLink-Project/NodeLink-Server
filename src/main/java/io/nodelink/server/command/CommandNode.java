package io.nodelink.server.command;

import java.util.*;

public class CommandNode {
    private final String name;
    private final LinkedHashMap<String, CommandNode> children = new LinkedHashMap<>();
    private Enum<?> owner;

    public CommandNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CommandNode addChild(CommandNode child) {
        children.put(child.name, child);
        return this;
    }

    public CommandNode addChild(String childName, CommandNode child) {
        children.put(childName, child);
        return this;
    }

    public CommandNode child(String childName) {
        return children.computeIfAbsent(childName, CommandNode::new);
    }

    public Collection<CommandNode> getChildren() {
        return children.values();
    }

    public Set<String> getChildNames() {
        return children.keySet();
    }

    public CommandNode getChild(String name) {
        return children.get(name);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setOwner(Enum<?> owner) {
        this.owner = owner;
    }

    public Enum<?> getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "CommandNode{" + name + ", children=" + children.keySet() + "}";
    }
}

