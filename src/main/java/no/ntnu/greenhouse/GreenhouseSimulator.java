package no.ntnu.greenhouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.message.Message;
import no.ntnu.message.NodeRemovedMessage;
import no.ntnu.message.SensorDataAdvertisementMessage;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
    private final int PORT_NUMBER = 10025;

    private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

    private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
    private List<ClientHandler> connectedClients = new ArrayList<>();
    private final boolean fake;
    private ServerSocket serverSocket;
    private ControlPanelLogic logic;

    /**
     * Create a greenhouse simulator.
     *
     * @param fake When true, simulate a fake periodic events instead of creating
     *             socket communication
     */
    public GreenhouseSimulator(boolean fake, ControlPanelLogic logic) {
        this.fake = fake;
        this.logic = logic;
    }

    /**
     * Get the control panel's logic.
     *
     * @return The instance of ControlPanelLogic.
     */
    public ControlPanelLogic getLogic() {
        return this.logic;
    }

    /**
     * Initialize the greenhouse but don't start the simulation just yet.
     */
    public void initialize() {
        createNode(1, 2, 1, 1, 0, 0);
        createNode(1, 0, 1, 0, 2, 1);
        createNode(2, 0, 0, 0, 0, 0);
        Logger.info("Greenhouse initialized");
    }

    private void createNode(int temperature, int humidity, int lightLevel, int windows, int fans, int heaters) {
        SensorActuatorNode node = DeviceFactory.createNode(
                temperature, humidity, lightLevel, windows, fans, heaters);
        nodes.put(node.getId(), node);
    }

    /**
     * Start a simulation of a greenhouse - all the sensor and actuator nodes inside
     * it.
     */
    public void start() {
        initiateCommunication();
        for (SensorActuatorNode node : nodes.values()) {
            node.start();
        }
        for (PeriodicSwitch periodicSwitch : periodicSwitches) {
            periodicSwitch.start();
        }

        Logger.info("Simulator started");
    }

    private void initiateCommunication() {
        if (fake) {
            initiateFakePeriodicSwitches();
        } else {
            initiateRealCommunication();
        }
    }

    private void handleNewClients() {
        while (this.serverSocket != null && !this.serverSocket.isClosed()) {
            ClientHandler clientHandler = this.acceptNextClientConnection(serverSocket);
            if (clientHandler != null) {
                this.connectedClients.add(clientHandler);
                clientHandler.start();
            }
        }
    }

    /**
     * Accepts the next client connection.
     * This code was implemented from Girts Strazdins smart-tv example from:
     * <a href="https://github.com/strazdinsg/datakomm-tools.git">...</a>
     *
     * @param listeningSocket the {@code ServerSocket} listening for the next
     *                        client.
     * @return the handler for the client.
     */
    private ClientHandler acceptNextClientConnection(ServerSocket listeningSocket) {
        ClientHandler clientHandler = null;
        try {
            Socket clientSocket = listeningSocket.accept();
            Logger.info("New client connected from " + clientSocket.getRemoteSocketAddress());
            clientHandler = new ClientHandler(clientSocket, this);
        } catch (IOException err) {
            Logger.error("Could not accept client connection: " + err.getMessage());
        }
        return clientHandler;
    }

    private void sendSensorDataPeriodically() {
        if (this.serverSocket == null || this.serverSocket.isClosed()) {
            return;
        }
        this.sendSensorDataAdvertisementMessages();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendSensorDataPeriodically();
            }
        }, 1 * 1000L);
    }

    private void sendSensorDataAdvertisementMessages() {
        List<SensorDataAdvertisementMessage> messages = new ArrayList<>();
        for (Map.Entry<Integer, SensorActuatorNode> node : this.getNodesInfo().entrySet()) {
            int nodeId = node.getKey();
            SensorActuatorNode sensors = node.getValue();
            SensorDataAdvertisementMessage message = new SensorDataAdvertisementMessage(nodeId, sensors.getReadings());
            messages.add(message);
        }
        for (SensorDataAdvertisementMessage message : messages) {
            this.sendResponseToAllClients(message);
        }
    }

    private void initiateRealCommunication() {
        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            new Thread(() -> handleNewClients()).start();
            new Thread(() -> sendSensorDataPeriodically()).start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void initiateFakePeriodicSwitches() {
        periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
        periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
    }

    /**
     * Returns a map of all sensor/actuator nodes within the greenhouse simulator.
     *
     * @return A map where the key is the node ID and the value is the
     *         SensorActuatorNode instance.
     */
    public Map<Integer, SensorActuatorNode> getNodesInfo() {
        return new HashMap<>(nodes);
    }

    /**
     * Stop the simulation of the greenhouse - all the nodes in it.
     */
    public void stop() {
        stopCommunication();
        List<SensorActuatorNode> tempNodeList = new ArrayList<>(nodes.values());
        for (SensorActuatorNode node : tempNodeList) {
            node.stop();
        }
    }


    private void stopCommunication() {
        if (fake) {
            for (PeriodicSwitch periodicSwitch : periodicSwitches) {
                periodicSwitch.stop();
            }
        } else {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Add a listener for notification of node staring and stopping.
     *
     * @param listener The listener which will receive notifications
     */
    public void subscribeToLifecycleUpdates(NodeStateListener listener) {
        for (SensorActuatorNode node : nodes.values()) {
            node.addStateListener(listener);
        }
    }

    /**
     * Sends response to all connected clients. This method was implemented from
     * Girts Strazdins smart-tv example from:
     * <a href="https://github.com/strazdinsg/datakomm-tools.git">...</a>
     *
     * @param message The message to be sent to all clients.
     */
    public void sendResponseToAllClients(Message message) {
        for (ClientHandler clientHandler : this.connectedClients) {
            clientHandler.sendResponseToClient(message);
        }
    }

    /**
     * Disconnect a client from the server. This method was implemented from Girts
     * Strazdins smart-tv example from:
     * <a href="https://github.com/strazdinsg/datakomm-tools.git">...</a>
     *
     * @param clientHandler the handler for the client to be removed.
     * @return {@code true} if the client was removed.
     */
    public boolean disconnectClient(ClientHandler clientHandler) {
        return this.connectedClients.remove(clientHandler);
    }

    /**
     * Changes the state of an actuator to a specific state.
     * 
     * @param nodeId     the id of the node that contains the actuator.
     * @param actuatorId the id of the actuator.
     * @param state      the state to change the actuator to.
     */
    public void changeActuatorState(int nodeId, int actuatorId, boolean state) {
        this.getNodesInfo().get(nodeId).getActuators().get(actuatorId).set(state);
    }

    /**
     * Removes a node from the simulator.
     * 
     * @param node the node to remove.
     */
    public void removeNode(SensorActuatorNode node) {
        int nodeId = node.getId();
        this.nodes.remove(nodeId);
        NodeRemovedMessage message = new NodeRemovedMessage(nodeId);
        this.sendResponseToAllClients(message);
    }
}
