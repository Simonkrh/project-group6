package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A socket communication channel.
 */
public class SocketCommunicationChannel implements CommunicationChannel {
    private int PORT_NUMBER ;
    private final ControlPanelLogic logic;
    private ServerSocket socket;
    private Map<Integer, Socket> connectedNodes = new HashMap<>();


    /**
     * Creates a new socket communication channel.
     * 
     * @param logic      The application logic of the control panel node.
     * @param portNumber The port number for the socket.
     */
    public SocketCommunicationChannel(ControlPanelLogic logic ,int portNumber) {
        this.logic = logic;
        this.PORT_NUMBER = 10025;
    }

    private ServerSocket openforListeningSocket() {
        ServerSocket listeningSocket = null;
        try {
            listeningSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException e) {
            System.err.println("Couldnt open a server socket" + e.getMessage());
        }
        return listeningSocket;
    }
    public int getPortNUmber(){
        return this.PORT_NUMBER;
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
