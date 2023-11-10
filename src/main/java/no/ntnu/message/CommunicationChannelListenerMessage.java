package no.ntnu.message;

import no.ntnu.listeners.common.CommunicationChannelListener;

/**
 * A message containing the new communication channel listener.
 */
public class CommunicationChannelListenerMessage implements Message {
    private final CommunicationChannelListener communicationChannelListener;

    public CommunicationChannelListenerMessage(CommunicationChannelListener communicationChannelListener) {
        this.communicationChannelListener = communicationChannelListener;
    }

    public CommunicationChannelListener getCommunicationChannelListener() {
        return communicationChannelListener;
    }
}
