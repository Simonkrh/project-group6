package no.ntnu.controlpanel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands
 * and receive events. It supports listeners who will be notified on changes (for example, a new
 * node is added to the network, or a new sensor reading is received).
 * Note: this class may look like unnecessary forwarding of events to the GUI. In real projects
 * (read: "big projects") this logic class may do some "real processing" - such as storing events
 * in a database, doing some checks, sending emails, notifications, etc. Such things should never
 * be placed inside a GUI class (JavaFX classes). Therefore, we use proper structure here, even
 * though you may have no real control-panel logic in your projects.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
    CommunicationChannelListener {
  private final List<GreenhouseEventListener> listeners = new LinkedList<>();
  private Map<Integer, Map<Integer, Boolean>> actuatorStates = new HashMap<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;
  private GreenhouseSimulator greenhouseSimulator;

  /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Set the greenhouse simulator instance used by the control panel.
   *
   * @param greenhouseSimulator The GreenhouseSimulator instance to be used.
   */
  public void setGreenhouseSimulator(GreenhouseSimulator greenhouseSimulator) {
    this.greenhouseSimulator = greenhouseSimulator;
  }

  /**
   * Get the communication channel instance associated with this control panel.
   *
   * @return The getCommunicationChannel instance.
   */
  public CommunicationChannel getCommunicationChannel() {
    return this.communicationChannel;
  }

  /**
   * Get the communication channel listener instance associated with this control panel.
   *
   * @return The CommunicationChannelListener instance.
   */
  public CommunicationChannelListener getCommunicationChannelListener() {
    return this.communicationChannelListener;
  }

  /**
   * Get the greenhouse simulator instance associated with this control panel.
   *
   * @return The GreenhouseSimulator instance.
   */
  public GreenhouseSimulator getGreenhouseSimulator() {
    return this.greenhouseSimulator;
  }

  public boolean isActuatorOn(int nodeId, int actuatorId) {
    Map<Integer, Boolean> nodeActuators = actuatorStates.getOrDefault(nodeId, new HashMap<>());
    return nodeActuators.getOrDefault(actuatorId, false);
  }

  /**
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    listeners.forEach(listener -> listener.onNodeAdded(nodeInfo));
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    listeners.forEach(listener -> listener.onSensorData(nodeId, sensors));
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    actuatorStates.computeIfAbsent(nodeId, k -> new HashMap<>()).put(actuatorId, isOn);
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, isOn));
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn());
    }
    listeners.forEach(listener ->
        listener.onActuatorStateChanged(nodeId, actuator.getId(), actuator.isOn())
    );
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
  }

  /**
   * Removes the node
   * @param nodeId the node to be removed
   */
  public void removeNode(int nodeId) {
    actuatorStates.remove(nodeId);
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

}
