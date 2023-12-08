package no.ntnu.message;

import no.ntnu.controlpanel.ControlPanelLogic;

public class RemoveNodeCommand extends Command {
    private int nodeId;

    public RemoveNodeCommand(int nodeId) {
        this.nodeId = nodeId;
    }

    public Message execute(ControlPanelLogic logic) {
        //logic.removeNode(nodeId);
        return new NodeRemovedMessage(nodeId);
    }
}
