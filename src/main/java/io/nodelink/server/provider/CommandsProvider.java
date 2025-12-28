package io.nodelink.server.provider;

import io.nodelink.server.command.CommandNode;

public interface CommandsProvider {
    CommandNode getCommandNode();
}
