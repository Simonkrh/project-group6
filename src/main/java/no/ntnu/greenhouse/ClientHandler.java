package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.message.Command;
import no.ntnu.message.Message;
import no.ntnu.message.MessageSerializer;
import no.ntnu.tools.Logger;

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
        Message response;
        try {
            while (!clientSocket.isClosed()) {
                String clientRequest = this.socketReader.readLine();
                if (clientRequest == null) {
                    break;
                }
                Command clientCommand = processRequest(clientRequest);
                response = clientCommand.execute(this.greenhouseSimulator.)

                this.socketWriter.println(clientCommand);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Logger.info("Client " + this.clientSocket.getRemoteSocketAddress() + " leaving");
        this.greenhouseSimulator.disconnectClient(this);
    }

    private Command processRequest(String clientRequest) {
        Message clientMessage = null;
        clientMessage = MessageSerializer.fromString(clientRequest);
        if (!(clientMessage instanceof Command)) {
            if (clientMessage != null) {
                System.err.println("Message from client is not valid: " + clientMessage);
            }
            clientMessage = null;
        }
        return (Command) clientMessage;
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
