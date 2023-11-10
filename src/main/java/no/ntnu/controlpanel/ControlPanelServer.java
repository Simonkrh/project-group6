package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlPanelServer {
    private static final int PORT_NUMBER = 10025;
    private ControlPanelLogic logic;
    private boolean isServerRunning;

    public ControlPanelServer(ControlPanelLogic logic) {
        this.logic = logic;
        this.isServerRunning = false;
    }

    public void startServer() {
        ServerSocket serverSocket = openListeningSocket();
        System.out.println("Server is listening on port " + PORT_NUMBER);

        if (serverSocket != null) {
            this.isServerRunning = true;
            while (this.isServerRunning) {
                ClientHandler clientHandler = this.acceptNextClientConnection(serverSocket);
                if (clientHandler != null) {
                    clientHandler.start();
                }
            }
        }
    }

    private ServerSocket openListeningSocket() {
        ServerSocket listeningSocket = null;
        try {
            listeningSocket = new ServerSocket(PORT_NUMBER);
        } catch (IOException err) {
            System.err.println("Could not open server socket: " + err.getMessage());
        }
        return listeningSocket;
    }

    public static int getPortNumber() {
        return PORT_NUMBER;
    }

    private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
        ClientHandler clientHandler = null;
        try {
            Socket clientSocket = listeningSocket.accept();
            System.out.println("New client connected from " + clientSocket.getRemoteSocketAddress());
            clientHandler = new ClientHandler(clientSocket, this);
        } catch (IOException err) {
            System.err.println("Could not accept client connection: " + err.getMessage());
        }
        return clientHandler;
    }
}
