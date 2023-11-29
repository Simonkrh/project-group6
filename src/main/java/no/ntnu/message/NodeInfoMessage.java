package no.ntnu.message;

import no.ntnu.greenhouse.SensorActuatorNode;

import java.util.Map;

public class NodeInfoMessage implements Message {
    private final Map<Integer, SensorActuatorNode> nodesInfo;

    public NodeInfoMessage(Map<Integer, SensorActuatorNode> nodesInfo) {
        this.nodesInfo = nodesInfo;
    }

    public Map<Integer, SensorActuatorNode> getNodesInfo() {
        return nodesInfo;
    }
}
