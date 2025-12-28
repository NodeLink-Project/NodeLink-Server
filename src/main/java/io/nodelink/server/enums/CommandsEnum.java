package io.nodelink.server.enums;

import io.nodelink.server.command.CommandNode;
import io.nodelink.server.provider.CommandsProvider;

public enum CommandsEnum implements CommandsProvider {
    SERVICE {
        @Override
        public CommandNode getCommandNode() {
            CommandNode service = new CommandNode("service");
            CommandNode set = new CommandNode("set");

            set.addChild(new CommandNode("status"));
            set.addChild(new CommandNode("info"));


            service.addChild(set);

            return service;
        }
    },

    SERVICE_SET_BONE {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode set = root.child("set");
            CommandNode bone = set.child("bone");
            bone.setOwner(this);
            return root;
        }
    },

    SERVICE_SET_CLUSTER {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode set = root.child("set");
            CommandNode cluster = set.child("cluster");
            cluster.setOwner(this);
            return root;
        }
    }
    ;

    @Override
    public abstract CommandNode getCommandNode();
}