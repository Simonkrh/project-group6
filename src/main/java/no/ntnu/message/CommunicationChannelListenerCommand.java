package no.ntnu.message;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.listeners.common.CommunicationChannelListener;

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
