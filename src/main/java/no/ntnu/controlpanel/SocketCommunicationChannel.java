package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.Socket;

/**
 * A socket communication channel.
 */
public class SocketCommunicationChannel implements CommunicationChannel {
    private final int PORT_NUMBER;
    private String serverHost = "localhost";
    private final ControlPanelLogic logic;
    private Socket socket;

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
            this.socket = new Socket(serverHost, PORT_NUMBER);
            isOpen = true;
        } catch (IOException err) {
            System.err.println("Could not open server socket: " + err.getMessage());
        }
        return isOpen;
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException err) {
            System.err.println("Could not close server socket: " + err.getMessage());
        }
    }
}
