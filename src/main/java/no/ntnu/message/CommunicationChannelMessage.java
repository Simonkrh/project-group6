package no.ntnu.message;

import no.ntnu.controlpanel.CommunicationChannel;

/**
 * A message containing the new communication channel.
 */
public class CommunicationChannelMessage implements Message {
    private final CommunicationChannel communicationChannel;

    /**
     * Creates a new CommunicationChannelMessage with the specified communication
     * channel.
     *
     * @param communicationChannel The communication channel to be associated with
     *                             this message.
     */
    public CommunicationChannelMessage(CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    /**
     * Returns the communication channel associated with this message.
     *
     * @return The communicationChannel object.
     */
    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }
}
