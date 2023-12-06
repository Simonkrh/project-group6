package no.ntnu.message;

/**
 * Represents a message for requesting the state of the actuator.
 */
public class ActuatorStateMessage implements Message {
    private final int nodeId;
    private final int actuatorId;
    private final boolean isOn;

    /**
     * Creates a new instance of the ActuatorStateMessage class with the specified node ID, actuator ID,
     * and actuator state.
     *
     * @param nodeId     The ID of the node.
     * @param actuatorId The ID of the actuator.
     * @param isOn       The state of the actuator; true if the actuator is on, false otherwise.
     */
    public ActuatorStateMessage(int nodeId, int actuatorId, boolean isOn) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
        this.isOn = isOn;
    }

    /**
     * Returns the node ID associated with this message.
     *
     * @return The node ID.
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Returns the actuator ID associated with this message.
     *
     * @return The actuator ID.
     */
    public int getActuatorId() {
        return actuatorId;
    }

    /**
     * Returns the state of the actuator.
     *
     * @return true if the actuator is on, false otherwise.
     */
    public boolean isOn() {
        return isOn;
    }
}
