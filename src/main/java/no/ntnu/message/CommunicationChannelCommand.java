package no.ntnu.message;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * Represents a command for retrieving the current state of the
 * communication channel in the control panel logic.
 */
public class CommunicationChannelCommand extends Command {
    @Override
    public Message execute(ControlPanelLogic logic) {
        Message response;
        try {
            CommunicationChannel communicationChannel = logic.getCommunicationChannel();
            response = new CommunicationChannelMessage(communicationChannel);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
