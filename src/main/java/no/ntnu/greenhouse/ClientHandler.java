package no.ntnu.greenhouse;

import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private GreenhouseSimulator greenhouseSimulator;

    public ClientHandler(Socket socket, GreenhouseSimulator greenhouseSimulator) {
        this.greenhouseSimulator = greenhouseSimulator;
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // TODO - implement run method
    }
}
