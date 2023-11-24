package no.ntnu.message;


import no.ntnu.controlpanel.ControlPanelLogic;

public class TurnOffActuatorCommand extends Command {
    private final int nodeId;
    private final int actuatorId;

    public TurnOffActuatorCommand(int nodeId, int actuatorId) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
    }

    @Override
    public Message execute(ControlPanelLogic logic) {
        logic.onActuatorStateChanged(nodeId, actuatorId, false);
        return new ActuatorStateMessage(nodeId, actuatorId, logic.isActuatorOn(nodeId, actuatorId));
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getActuatorId() {
        return actuatorId;
    }
}