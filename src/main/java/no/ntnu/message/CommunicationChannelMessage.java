package no.ntnu.message;

import no.ntnu.controlpanel.CommunicationChannel;

/**
 * A message containing the new communication channel.
 */
public class CommunicationChannelMessage implements Message {
    private final CommunicationChannel communicationChannel;

    public CommunicationChannelMessage(CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }
}
