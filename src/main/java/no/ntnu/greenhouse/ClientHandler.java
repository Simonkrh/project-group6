package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private GreenhouseSimulator greenhouseSimulator;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    public ClientHandler(Socket socket, GreenhouseSimulator greenhouseSimulator) throws IOException {
        this.greenhouseSimulator = greenhouseSimulator;
        this.clientSocket = socket;
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (!clientSocket.isClosed()) {
                String clientRequest = this.socketReader.readLine();
                if (clientRequest == null) {
                    break;
                }

                String response = processRequest(clientRequest);
                socketWriter.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.greenhouseSimulator.stop();
    }

    private String processRequest(String clientRequest) {
        // TODO - Implement method to process client's request
        return "Response to: " + clientRequest;
    }

    private void closeConnection() {
        try {
            if (this.socketReader != null) {
                this.socketReader.close();
            }
            if (this.socketWriter != null) {
                this.socketWriter.close();
            }
            if (this.clientSocket != null) {
                this.clientSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
