package no.ntnu.controlpanel;

import no.ntnu.tools.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
        if(socket!= null && !socket.isClosed()){
            String change = "Actuator change " + nodeId + " " + actuatorId + (isOn ? "ON" : "OFF");
            try {
                OutputStream out = socket.getOutputStream();
                byte [] bytes = change.getBytes(StandardCharsets.UTF_8);
                out.write(bytes);
                Logger.info("The change is " + change);
            } catch (IOException e) {
               Logger.error("Could not send the change ");
            }
        }
        else {
            Logger.error("Could not connect the socket");
        }
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
