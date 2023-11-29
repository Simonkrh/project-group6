package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static no.ntnu.tools.Parser.parseDoubleOrError;
import static no.ntnu.tools.Parser.parseIntegerOrError;

/**
 * Represents a serializer for messages to protocol-defined strings and vice
 * versa.
 */
public class MessageSerializer {
    public static final String TURN_ON_ACTUATORS_COMMAND = "on";
    public static final String TURN_OFF_ACTUATORS_COMMAND = "off";
    public static final String ACTUATOR_STATE_ON_MESSAGE = "ACTUATOR_ON";
    public static final String ACTUATOR_STATE_OFF_MESSAGE = "ACTUATOR_OFF";
    public static final String SENSOR_DATA_ADVERTISEMENT = "SENSOR_DATA";
    public static final String ERROR_MESSAGE = "e";

    /**
     * Creates a new instance of the MessageSerializer class.
     * It is left empty to avoid unwanted initialization of the class.
     */
    private MessageSerializer() {
    }

    /**
     * Creates a message from a specified string, according to the communication
     * protocol.
     *
     * @param string the string sent over the communication channel.
     * @return the message interpreted according to the protocol.
     */
    public static Message fromString(String string) {
        if (string == null) {
            return null;
        }

        Message message = null;

        if (string.startsWith(TURN_ON_ACTUATORS_COMMAND) ||
                string.startsWith(TURN_OFF_ACTUATORS_COMMAND) ||
                string.startsWith(ACTUATOR_STATE_ON_MESSAGE) ||
                string.startsWith(ACTUATOR_STATE_OFF_MESSAGE)) {
            message = parseCommandMessage(string);
        } else if (string.startsWith(SENSOR_DATA_ADVERTISEMENT)) {
            message = parseSensorDataAdvertisementMessage(string);
        } else if (string.startsWith(ERROR_MESSAGE)) {
            String errorMessage = string.substring(1);
            message = new ErrorMessage(errorMessage);
        }

        return message;
    }

    /**
     * Parses the parametrized message.
     *
     * @param string the string sent over the communication channel
     * @return the logical message, as interpreted according to the protocol
     */
    private static Message parseCommandMessage(String string) {
        String[] parts = string.split(":");
        if (parts.length < 3) {
            return null;
        }

        String commandType = parts[0];
        int nodeId;
        int actuatorId;

        try {
            nodeId = Integer.parseInt(parts[1]);
            actuatorId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        Message message = null;
        switch (commandType) {
            case TURN_ON_ACTUATORS_COMMAND:
                message = new TurnOnActuatorCommand(nodeId, actuatorId);
                break;
            case TURN_OFF_ACTUATORS_COMMAND:
                message = new TurnOffActuatorCommand(nodeId, actuatorId);
                break;
            case ACTUATOR_STATE_ON_MESSAGE:
                message = new ActuatorStateMessage(nodeId, actuatorId, true);
                break;
            case ACTUATOR_STATE_OFF_MESSAGE:
                message = new ActuatorStateMessage(nodeId, actuatorId, false);
                break;
            default:
                break;
        }
        return message;
    }

    /**
     * Parses a sensor data advertisement message from the given string.
     * The expected format is "SENSOR_DATA:nodeId;sensorType1=value1 unit1,sensorType2=value2 unit2,...".
     *
     * @param string the string containing the sensor data advertisement information.
     * @return a SensorDataAdvertisementMessage object representing the parsed data.
     * @throws IllegalArgumentException if the string format is incorrect or data is invalid.
     */
    private static SensorDataAdvertisementMessage parseSensorDataAdvertisementMessage(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Sensor specification can't be empty");
        }

        String[] parts = string.split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Incorrect specification format: " + string);
        }

        int nodeId = parseIntegerOrError(parts[0], "Invalid node ID:" + parts[0]);
        List<SensorReading> sensors = parseSensors(parts[1]);

        return new SensorDataAdvertisementMessage(nodeId, sensors);
    }

    private static List<SensorReading> parseSensors(String sensorInfo) {
        List<SensorReading> readings = new LinkedList<>();
        String[] readingInfo = sensorInfo.split(",");

        for (String reading : readingInfo) {
            readings.add(parseReading(reading));
        }
        return readings;
    }

    private static SensorReading parseReading(String reading) {
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
     * Converts a message to a serialized string.
     *
     * @param message the message to translate
     * @return string representation of the message
     */
    public static String toString(Message message) {
        String string = null;
        if (message instanceof TurnOnActuatorCommand turnOnActuatorCommand) {
            string = TURN_ON_ACTUATORS_COMMAND + ":" + turnOnActuatorCommand.getNodeId() + ":" + turnOnActuatorCommand.getActuatorId();
        } else if (message instanceof TurnOffActuatorCommand turnOffActuatorCommand) {
            string = TURN_OFF_ACTUATORS_COMMAND + ":" + turnOffActuatorCommand.getNodeId() + ":" + turnOffActuatorCommand.getActuatorId();
        } else if (message instanceof ActuatorStateMessage actuatorStateMessage) {
            string = actuatorStateMessage.isOn() ? ACTUATOR_STATE_ON_MESSAGE : ACTUATOR_STATE_OFF_MESSAGE;
        } else if (message instanceof SensorDataAdvertisementMessage sensorDataAdvertisementMessage) {
            return SENSOR_DATA_ADVERTISEMENT + ":" + sensorDataAdvertisementMessage.getNodeId() + ";" + sensorReadingsToString(sensorDataAdvertisementMessage.getSensorReadings());
        } else if (message instanceof ErrorMessage errorMessage) {
            string = ERROR_MESSAGE + errorMessage.getMessage();
        }
        return string;
    }

    private static String sensorReadingsToString(List<SensorReading> sensorReadings) {
        return sensorReadings.stream()
                .map(reading -> reading.getType() + "=" + reading.getValue() + " " + reading.getUnit())
                .collect(Collectors.joining(","));
    }
}