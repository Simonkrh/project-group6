package no.ntnu.controlpanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Represents the handler for a specific TCP client connection.
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private ControlPanelServer server;

    public ClientHandler(Socket socket, ControlPanelServer server) throws IOException {
        this.server = server;
        this.socket = socket;
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {

    }
}
