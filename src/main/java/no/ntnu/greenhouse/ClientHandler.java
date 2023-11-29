package no.ntnu.greenhouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import no.ntnu.message.ActuatorStateMessage;
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
        do {
            String clientRequest = null;
            try {
                clientRequest = this.socketReader.readLine();
            } catch (IOException e) {
                System.err.println("Client request was not received: " + e.getMessage());
            }
            Command clientCommand = processRequest(clientRequest);
            if (clientCommand != null) {
                System.out.println("Client request was successfully received: " + clientCommand.getClass().getSimpleName());
                response = clientCommand.execute(this.greenhouseSimulator.getLogic());
                if (response != null) {
                    this.sendResponse(response);
                }
            } else {
                response = null;
            }
        } while (response != null);
        Logger.info("Client " + this.clientSocket.getRemoteSocketAddress() + " leaving");
        this.closeConnection();
    }

    private Command processRequest(String clientRequest) {
        Message clientMessage;
        clientMessage = MessageSerializer.fromString(clientRequest);
        if (!(clientMessage instanceof Command)) {
            if (clientMessage != null) {
                System.err.println("Message from client is not valid: " + clientMessage);
            }
            clientMessage = null;
        }
        return (Command) clientMessage;
    }

    public void sendResponseToClient(Message message) {
        this.socketWriter.println(MessageSerializer.toString(message));
    }

    public void broadCastMessage(Message message) {
        this.greenhouseSimulator.sendResponseToAllClients(message);
    }

    private boolean isBroadcastMessage(Message message) {
        return message instanceof ActuatorStateMessage;
    }

    public void sendResponse(Message message) {
        if (isBroadcastMessage(message)) {
            broadCastMessage(message);
        } else {
            sendResponseToClient(message);
        }
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
            if (this.greenhouseSimulator != null) {
                this.greenhouseSimulator.disconnectClient(this);
            }
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
