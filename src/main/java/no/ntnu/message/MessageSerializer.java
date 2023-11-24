package no.ntnu.message;

/**
 * Represents a serializer for messages to protocol-defined strings and vice
 * versa.
 */
public class MessageSerializer {
    public static final String TURN_ON_ACTUATORS_COMMAND = "on";
    public static final String TURN_OFF_ACTUATORS_COMMAND = "off";

    /**
     * Creates a new instance of the MessageSerializer class.
     *
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

        if (string.startsWith(TURN_ON_ACTUATORS_COMMAND) || string.startsWith(TURN_OFF_ACTUATORS_COMMAND)) {
            message = parseParametrizedMessage(string);
        }

        return message;
    }

    /**
     * Parses the parametrized message.
     *
     * @param string the string sent over the communication channel
     * @return the logical message, as interpreted according to the protocol
     */
    private static Message parseParametrizedMessage(String string) {
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
            default:
                break;
        }
        return message;
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
        }
        return string;
    }
}