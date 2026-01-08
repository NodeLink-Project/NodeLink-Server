package io.nodelink.server.enums;

import io.nodelink.server.command.CommandNode;
import io.nodelink.server.provider.CommandsProvider;

public enum CommandsEnum implements CommandsProvider {
    CLEAR {
        @Override
        public CommandNode getCommandNode() {
            CommandNode clear = new CommandNode("clear");

            clear.setOwner(this);
            return clear;
        }
    },

    HELP {
        @Override
        public CommandNode getCommandNode() {
            CommandNode help = new CommandNode("help");

            help.setOwner(this);
            return help;
        }
    },

    SERVICE {
        @Override
        public CommandNode getCommandNode() {
            CommandNode service = new CommandNode("service");
            CommandNode set = new CommandNode("set");
            CommandNode mode = new CommandNode("mode");

            CommandNode info = new CommandNode("info");
            CommandNode status = new CommandNode("status");

            service.addChild(set);
            set.addChild(mode);

            mode.addChild(info);
            mode.addChild(status);

            return service;
        }
    },

    SERVICE_MODE_STATUS {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode mode = root.child("mode");
            CommandNode status = mode.child("status");
            status.setOwner(this);
            return root;
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