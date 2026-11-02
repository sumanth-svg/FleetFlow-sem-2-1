package com.fleetflow.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Residual graph edge for network flow algorithms in FleetFlow.
 */
public class FlowEdge {
    private final int from;
    private final int to;
    private final double capacity;
    private double flow;
    private FlowEdge residual;
    private final String label;

    public FlowEdge(int from, int to, double capacity, String label) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0.0;
        this.label = label;
    }

    public int getFrom() { return from; }
    public int getTo() { return to; }
    public double getCapacity() { return capacity; }
    public double getFlow() { return flow; }
    public void setFlow(double flow) { this.flow = flow; }
    public FlowEdge getResidual() { return residual; }
    public void setResidual(FlowEdge residual) { this.residual = residual; }
    public String getLabel() { return label; }

    public double getResidualCapacity() {
        return capacity - flow;
    }

    public void addResidualFlow(double delta) {
        this.flow += delta;
        this.residual.flow -= delta;
    }

    @Override
    public String toString() {
        return String.format("%d -> %d [%s] (flow: %.1f / cap: %.1f)", from, to, label, flow, capacity);
    }
}
