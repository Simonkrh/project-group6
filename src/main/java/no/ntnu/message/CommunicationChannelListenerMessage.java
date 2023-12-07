package no.ntnu.message;

import no.ntnu.listeners.common.CommunicationChannelListener;

/**
 * A message containing the new communication channel listener.
 */
public class CommunicationChannelListenerMessage implements Message {
    private final CommunicationChannelListener communicationChannelListener;

    /**
     * Creates a new CommunicationChannelListenerMessage with the specified
     * communication channel listener.
     *
     * @param communicationChannelListener The communication channel listener to be
     *                                     associated with this message.
     */
    public CommunicationChannelListenerMessage(CommunicationChannelListener communicationChannelListener) {
        this.communicationChannelListener = communicationChannelListener;
    }

    /**
     * Returns the communication channel listener associated with this message.
     *
     * @return The CommunicationChannelListener object.
     */
    public CommunicationChannelListener getCommunicationChannelListener() {
        return communicationChannelListener;
    }
}
