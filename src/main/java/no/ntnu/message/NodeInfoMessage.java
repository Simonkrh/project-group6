package no.ntnu.message;

import no.ntnu.greenhouse.SensorActuatorNode;

import java.util.Map;

/**
 * Represents a message containing information about all
 * sensor and actuator nodes in the greenhouse.
 */

public class NodeInfoMessage implements Message {
    private final Map<Integer, SensorActuatorNode> nodesInfo;

    public NodeInfoMessage(Map<Integer, SensorActuatorNode> nodesInfo) {
        this.nodesInfo = nodesInfo;
    }

    public Map<Integer, SensorActuatorNode> getNodesInfo() {
        return nodesInfo;
    }
}
