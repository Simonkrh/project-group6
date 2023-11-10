package no.ntnu.message;

import no.ntnu.controlpanel.ControlPanelLogic;

/**
 * A command sent from the client to the server.
 */
public abstract class Command implements Message {
    public abstract Message execute(ControlPanelLogic logic);
}
