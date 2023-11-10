package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A socket communication channel.
 */
public class SocketCommunicationChannel implements CommunicationChannel {
    private final int PORT_NUMBER;
    private final ControlPanelLogic logic;
    private ServerSocket socket;

    /**
     * Creates a new socket communication channel.
     * 
     * @param logic      The application logic of the control panel node.
     * @param portNumber The port number for the socket.
     */
    public SocketCommunicationChannel(ControlPanelLogic logic, int portNumber) {
        this.logic = logic;
        this.PORT_NUMBER = portNumber;
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        // TODO implement this method
        throw new UnsupportedOperationException("Unimplemented method 'sendActuatorChange'");
    }

    @Override
    public boolean open() {
        boolean isOpen = false;
        try {
            socket = new ServerSocket(PORT_NUMBER);
            isOpen = true;
        } catch (IOException err) {
            System.err.println("Could not open server socket: " + err.getMessage());
        }
        return isOpen;
    }
}
