package no.ntnu.message;

/**
 * Represents a message for requesting the state of which the TV is powered.
 */
public class ActuatorStateMessage implements Message {
    private final int nodeId;
    private final int actuatorId;
    private final boolean isOn;

    public ActuatorStateMessage(int nodeId, int actuatorId, boolean isOn) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
        this.isOn = isOn;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getActuatorId() {
        return actuatorId;
    }

    public boolean isOn() {
        return isOn;
    }
}
