package no.ntnu.greenhouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.message.Message;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
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
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
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

  private void initiateRealCommunication() {
  // TODO - here you can set up the TCP or UDP communication
    int port = 10025;
    try {
      serverSocket = new ServerSocket(port);
      new Thread(() -> handleNewClients()).start();
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
   * @return A map where the key is the node ID and the value is the SensorActuatorNode instance.
   */
  public Map<Integer, SensorActuatorNode> getNodesInfo() {
    return new HashMap<>(nodes);
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      // TODO - here you stop the TCP/UDP communication
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

  public void sendResponseToAllClients(Message message) {
    for (ClientHandler clientHandler : this.connectedClients) {
      clientHandler.sendResponseToClient(message);
    }
  }

  public boolean disconnectClient(ClientHandler clientHandler) {
    return this.connectedClients.remove(clientHandler);
  }
}
