package no.ntnu.controlpanel;

import no.ntnu.message.Command;
import no.ntnu.message.MessageSerializer;
import no.ntnu.tools.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A socket communication channel.
 */
public class SocketCommunicationChannel implements CommunicationChannel {
    private final int PORT_NUMBER;
    private final String serverHost = "localhost";
    private final ControlPanelLogic logic;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
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
        if(socket!= null && socket.isConnected()){
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

    public boolean sendCommand(Command command) {
        if (socketWriter == null || socketReader == null) {
            return false;
        }
        try {
            String serializedCommand = MessageSerializer.toString(command);
            socketWriter.println(serializedCommand);
            return true;
        } catch (Exception e) {
           Logger.error("Error while trying to send the command : " + e.getMessage());
           return false;
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
