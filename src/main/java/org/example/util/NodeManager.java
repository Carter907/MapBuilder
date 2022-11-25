package org.example.util;

import javafx.scene.Node;

import java.util.HashMap;

public class NodeManager {

    private HashMap<String, Node> nodes;

    public NodeManager() {
        this.nodes = new HashMap<>();
    }

    public void give(String name,Node n) {
        if (nodes.containsKey(name))
            System.out.println(nodes.replace(name, n));
        else
        nodes.put(name,n);
    }
    public Node retrieve(String name) {
        return nodes.get(name);
    }

}
