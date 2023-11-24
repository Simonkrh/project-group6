package no.ntnu.message;


import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Represents a command to turn off a specific actuator.
 */
public class TurnOffActuatorCommand extends Command {
    private final int nodeId;
    private final int actuatorId;

    /**
     * Creates a new instance of the TurnOffActuatorCommand class.
     *
     * @param nodeId The ID of the node where the actuator is located.
     * @param actuatorId The ID of the actuator to be turned on.
     */
    public TurnOffActuatorCommand(int nodeId, int actuatorId) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
    }

    @Override
    public Message execute(ControlPanelLogic logic) {
        try {
            logic.onActuatorStateChanged(nodeId, actuatorId, false);
            return new ActuatorStateMessage(nodeId, actuatorId, logic.isActuatorOn(nodeId, actuatorId));
        } catch (Exception err) {
            return new ErrorMessage(err.getMessage());
        }
    }

    /**
     * Gets the node ID for this command.
     *
     * @return The node ID.
     */
    public int getNodeId() {
        return nodeId;
    }

    /**
     * Gets the actuator ID for this command.
     *
     * @return The actuator ID.
     */
    public int getActuatorId() {
        return actuatorId;
    }
}