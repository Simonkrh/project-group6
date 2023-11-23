package no.ntnu.message;


import no.ntnu.controlpanel.ControlPanelLogic;

public class TurnOnActuatorCommand extends Command {
    private final int nodeId;
    private final int actuatorId;

    public TurnOnActuatorCommand(int nodeId, int actuatorId) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
    }

    @Override
    public Message execute(ControlPanelLogic logic) {
        logic.onActuatorStateChanged(nodeId, actuatorId, true);
        return new ActuatorStateMessage(nodeId, actuatorId, logic.isActuatorOn(nodeId, actuatorId));
    }
}