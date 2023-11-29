package no.ntnu.message;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.listeners.common.CommunicationChannelListener;

/**
 * Represents a command for retrieving the current listener of the
 * communication channel in the control panel logic.
 */
public class CommunicationChannelListenerCommand extends Command {
    @Override
    public Message execute(ControlPanelLogic logic) {
        Message response;
        try {
            CommunicationChannelListener communicationChannelListener = logic.getCommunicationChannelListener();
            response = new CommunicationChannelListenerMessage(communicationChannelListener);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
