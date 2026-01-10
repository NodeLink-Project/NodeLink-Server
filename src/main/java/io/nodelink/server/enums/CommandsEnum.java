package io.nodelink.server.enums;

import io.nodelink.server.NodeLink;
import io.nodelink.server.app.data.BONE_LOCATION;
import io.nodelink.server.app.data.CLUSTER_LOCATION;
import io.nodelink.server.command.CommandNode;
import io.nodelink.server.provider.CommandsProvider;
import org.w3c.dom.Node;

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
            CommandNode dev = new CommandNode("dev");

            CommandNode api = new CommandNode("api");

            CommandNode cluster = new CommandNode("cluster");
            CommandNode bone = new CommandNode("bone");

            set.addChild(cluster);
            set.addChild(bone);

            service.addChild(set);
            service.addChild(mode);
            service.addChild(dev);


            dev.addChild(api);

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

    SERVICE_MODE_INFO {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode mode = root.child("mode");
            CommandNode info = mode.child("info");
            info.setOwner(this);
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
    },

    SERVICE_SET_CLUSTER_LOCATION {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode set = root.child("set");
            CommandNode cluster = set.child("cluster");
            CommandNode location = cluster.child("location");

            for (CLUSTER_LOCATION clusterLocation : CLUSTER_LOCATION.values()) {
                CommandNode locationNode = new CommandNode(clusterLocation.name());
                locationNode.setOwner(this);
                location.addChild(locationNode);
            }

            return root;
        }
    },

    SERVICE_SET_BONE_LOCATION {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode set = root.child("set");
            CommandNode bone = set.child("bone");
            CommandNode location = bone.child("location");

            for (BONE_LOCATION boneLocation : BONE_LOCATION.values()) {
                CommandNode locationNode = new CommandNode(boneLocation.name());
                locationNode.setOwner(this);
                location.addChild(locationNode);
            }

            return root;
        }
    },

    SERVICE_DEV_API_STOP {
        @Override
        public CommandNode getCommandNode() {
            CommandNode root = new CommandNode("service");
            CommandNode dev = root.child("dev");
            CommandNode api = dev.child("api");
            CommandNode stop = api.child("stop");
            stop.setOwner(this);
            return root;
        }
    };

    @Override
    public abstract CommandNode getCommandNode();
}