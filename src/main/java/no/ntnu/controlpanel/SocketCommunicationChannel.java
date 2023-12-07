package no.ntnu.controlpanel;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.message.ActuatorStateMessage;
import no.ntnu.message.Command;
import no.ntnu.message.Message;
import no.ntnu.message.MessageSerializer;
import no.ntnu.message.SensorDataAdvertisementMessage;
import no.ntnu.message.TurnOffActuatorCommand;
import no.ntnu.message.TurnOnActuatorCommand;
import no.ntnu.tools.Logger;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A socket communication channel.
 */
public class SocketCommunicationChannel implements CommunicationChannel {
    private final int PORT_NUMBER;
    private final String serverHost = "localhost";
    private final ControlPanelLogic logic;
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;

    /**
     * Creates a new socket communication channel.
     * 
     * @param logic      The application logic of the control panel node.
     * @param portNumber The port number for the socket.
     */
    public SocketCommunicationChannel(ControlPanelLogic logic, int portNumber) {
        this.logic = logic;
        this.PORT_NUMBER = portNumber;

    }

    /**
     * Sends the acturare state  over  the channel.
     * @param nodeId     ID of the node to which the actuator is attached
     * @param actuatorId Node-wide unique ID of the actuator
     * @param isOn       When true, actuator must be turned on; off when false.
     */
    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        if (socket != null && socket.isConnected()) {
            this.sendCommand(isOn ? new TurnOnActuatorCommand(nodeId, actuatorId)
                    : new TurnOffActuatorCommand(nodeId, actuatorId));
        } else {
            Logger.error("Could not connect the socket");
        }
    }

    /**
     * Send command to the clients
     * @param command the command to be sent
     * @return true if succesfully returned flase if not
     */
    public boolean sendCommand(Command command) {
        if (socketWriter == null || socketReader == null) {
            return false;
        }
        try {
            String serializedCommand = MessageSerializer.toString(command);
            socketWriter.println(serializedCommand);
            return true;
        } catch (Exception e) {
            Logger.error("Error while trying to send the command : " + e.getMessage());
            return false;
        }
    }

    private SensorActuatorNodeInfo createSensorNodeInfoFrom(String specification) {
        String[] parts = specification.split(":");
        int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
        SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(nodeId);
        if (parts.length == 3) {
            parseActuators(parts[2], info);
        }
        return info;
    }

    private void parseActuators(String actuatorSpecification, SensorActuatorNodeInfo info) {
        String[] parts = actuatorSpecification.split(",");
        for (String part : parts) {
            parseActuatorInfo(part, info);
        }
    }

    private void parseActuatorInfo(String s, SensorActuatorNodeInfo info) {
        String[] actuatorParts = s.split("=");
        if (actuatorParts.length != 2) {
            throw new IllegalArgumentException("Invalid actuator info format: " + s);
        }
        String[] actuatorInfo = actuatorParts[0].split("_");
        if (actuatorInfo.length != 2) {
            throw new IllegalArgumentException("Invalid actuator info format: " + actuatorParts[0]);
        }
        int actuatorId = parseIntegerOrError(actuatorInfo[0], "Invalid node ID:" + actuatorInfo[0]);
        String actuatorType = actuatorInfo[1];
        boolean activated = actuatorParts[1].equals("on");
        Actuator actuator = new Actuator(actuatorId, actuatorType, info.getId());
        actuator.set(activated);
        actuator.setListener(logic);
        info.addActuator(actuator);
    }

    private List<SensorReading> createSensorReadingsFrom(String specification) {
        String sensorInfo = specification.split(":")[1];
        return parseSensors(sensorInfo);
    }

    private List<SensorReading> parseSensors(String sensorInfo) {
        List<SensorReading> readings = new LinkedList<>();
        String[] readingInfo = sensorInfo.split(",");
        for (String reading : readingInfo) {
            readings.add(parseReading(reading));
        }
        return readings;
    }

    private SensorReading parseReading(String reading) {
        String[] assignmentParts = reading.split("=");
        if (assignmentParts.length != 2) {
            throw new IllegalArgumentException("Invalid sensor reading specified: " + reading);
        }
        String[] valueParts = assignmentParts[1].split(" ");
        if (valueParts.length != 2) {
            throw new IllegalArgumentException("Invalid sensor value/unit: " + reading);
        }
        String sensorType = assignmentParts[0];
        double value = parseDoubleOrError(valueParts[0], "Invalid sensor value: " + valueParts[0]);
        String unit = valueParts[1];
        return new SensorReading(sensorType, value, unit);
    }

    /**
     * Spawns new sensor/actuator nodes after a given delay.
     *
     * @param specification The specification in the specified format
     * @param delay         Delay in seconds
     */
    public void spawnNodes(String specification, int delay) {
        if (specification == null || specification.isEmpty()) {
            throw new IllegalArgumentException("Node specification can't be empty");
        }
        String[] parts = specification.split("\\|");
        if (!parts[0].equals("NODE_INFO")) {
            throw new IllegalArgumentException("Incorrect specification format");
        }
        for (int i = 1; i < parts.length; i++) {
            SensorActuatorNodeInfo nodeInfo = createSensorNodeInfoFrom(parts[i]);
            List<SensorReading> sensorReadings = createSensorReadingsFrom(parts[i]);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logic.onNodeAdded(nodeInfo);
                }
            }, delay * 1000L);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    logic.onSensorData(nodeInfo.getId(), sensorReadings);
                }
            }, delay * 1000L + 1000);
        }
    }

    private void fetchNodeData() {
        socketWriter.println("REQUEST_NODE_INFO");
        String response = null;
        try {
            response = this.socketReader.readLine();
        } catch (IOException e) {
            System.err.println("Did not receive any response: " + e.getMessage());
        }
        this.spawnNodes(response, 0);
    }

    private void listenForResponse() {
        while (this.socket != null && !this.socket.isClosed()) {
            String response = null;
            try {
                response = socketReader.readLine();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
            if (response != null && !response.isBlank()) {
                this.processResponse(response);
            }
        }
    }

    private void processResponse(String response) {
        Message serializedResponse = MessageSerializer.fromString(response);
        if (serializedResponse instanceof ActuatorStateMessage actuatorStateMessage) {
            this.logic.onActuatorStateChanged(actuatorStateMessage.getNodeId(), actuatorStateMessage.getActuatorId(),
                    actuatorStateMessage.isOn());
        } else if (serializedResponse instanceof SensorDataAdvertisementMessage sensorDataAdvertisementMessage) {
            this.logic.onSensorData(sensorDataAdvertisementMessage.getNodeId(),
                    sensorDataAdvertisementMessage.getSensorReadings());
        }
    }

    @Override
    public boolean open() {
        boolean isOpen = false;
        try {
            this.socket = new Socket(serverHost, PORT_NUMBER);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
            this.fetchNodeData();
            new Thread(() -> listenForResponse()).start();
            isOpen = true;
        } catch (IOException err) {
            System.err.println("Could not open server socket: " + err.getMessage());
        }
        return isOpen;
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException err) {
            System.err.println("Could not close server socket: " + err.getMessage());
        }
    }
}
