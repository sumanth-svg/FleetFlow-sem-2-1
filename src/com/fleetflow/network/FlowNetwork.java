package com.fleetflow.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Directed network flow graph with node labels and adjacency lists for FleetFlow.
 */
public class FlowNetwork {
    private final int vertexCount;
    private final List<List<FlowEdge>> adj;
    private final List<String> nodeNames;
    private final Map<String, Integer> nameToId;

    public FlowNetwork(int vertexCount) {
        this.vertexCount = vertexCount;
        this.adj = new ArrayList<>(vertexCount);
        this.nodeNames = new ArrayList<>(vertexCount);
        this.nameToId = new HashMap<>();

        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
            nodeNames.add("Node_" + i);
            nameToId.put("Node_" + i, i);
        }
    }

    public void setNodeName(int id, String name) {
        nodeNames.set(id, name);
        nameToId.put(name, id);
    }

    public String getNodeName(int id) {
        return (id >= 0 && id < nodeNames.size()) ? nodeNames.get(id) : ("Node_" + id);
    }

    public int getNodeId(String name) {
        return nameToId.getOrDefault(name, -1);
    }

    public void addEdge(int from, int to, double capacity, String label) {
        FlowEdge forward = new FlowEdge(from, to, capacity, label);
        FlowEdge backward = new FlowEdge(to, from, 0.0, "rev-" + label);
        forward.setResidual(backward);
        backward.setResidual(forward);
        adj.get(from).add(forward);
        adj.get(to).add(backward);
    }

    public void addEdge(String fromName, String toName, double capacity, String label) {
        int u = getNodeId(fromName);
        int v = getNodeId(toName);
        if (u != -1 && v != -1) {
            addEdge(u, v, capacity, label);
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public List<FlowEdge> getAdj(int v) {
        return adj.get(v);
    }

    public List<FlowEdge> getAllEdges() {
        List<FlowEdge> all = new ArrayList<>();
        for (List<FlowEdge> edges : adj) {
            for (FlowEdge e : edges) {
                if (e.getCapacity() > 0) {
                    all.add(e);
                }
            }
        }
        return all;
    }

    public void resetFlows() {
        for (List<FlowEdge> edges : adj) {
            for (FlowEdge e : edges) {
                e.setFlow(0.0);
            }
        }
    }
}
