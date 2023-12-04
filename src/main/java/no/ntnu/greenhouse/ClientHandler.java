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

/**
 * Represents the handler for a specific TCP client connection in a TCP-based greenhouse simulator.
 * This class extends {@link Thread} and handles communication with a single client,
 * processing incoming requests and sending responses accordingly.
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private GreenhouseSimulator greenhouseSimulator;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    /**
     * Creates a new instance for the ClientHandler call.
     *
     * @param socket The socket associated with the client connection.
     * @param greenhouseSimulator The TCP server class which this handler is part of.
     * @throws IOException If an error occurs while trying to establish the input or the output streams.
     */
    public ClientHandler(Socket socket, GreenhouseSimulator greenhouseSimulator) throws IOException {
        this.greenhouseSimulator = greenhouseSimulator;
        this.clientSocket = socket;
        this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Runs the logic for client handling. Continuously listens for client requests,
     * processes them, and sends back responses until the connection is terminated.
     */
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

    /**
     * Processes a client request by deserializing the message and casting it to a command if valid.
     *
     * @param clientRequest The request received from the client before it is processed.
     * @return a Command object if the request is valid, null if not.
     */
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

    /**
     * Sends response (message) to client.
     *
     * @param message The message to be sent.
     */
    public void sendResponseToClient(Message message) {
        this.socketWriter.println(MessageSerializer.toString(message));
    }

    /**
     * Broadcasts message to all connected clients.
     *
     * @param message The message to be broadcast.
     */
    public void broadCastResponse(Message message) {
        this.greenhouseSimulator.sendResponseToAllClients(message);
    }

    /**
     * Determines if the given message is of a type that should be broadcast.
     *
     * @param message The message to check if it should be broadcast.
     * @return true if the message should be broadcast, false if not
     */
    private boolean isBroadcastMessage(Message message) {
        return message instanceof ActuatorStateMessage;
    }

    /**
     * Sends a response to either a single client or broadcast it to all clients,
     * based on the message type.
     *
     * @param message The message to be sent as a response.
     */
    public void sendResponse(Message message) {
        if (isBroadcastMessage(message)) {
            broadCastResponse(message);
        } else {
            sendResponseToClient(message);
        }
    }

    /**
     * Closes the connection as well as used resources and notifies the server.
     */
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
