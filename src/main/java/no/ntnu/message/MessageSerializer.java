package no.ntnu.message;

public class MessageSerializer {
    public static final String TURN_ON_ACTUATORS_COMMAND = "on";
    public static final String TURN_OFF_ACTUATORS_COMMAND = "off";

    private MessageSerializer() {
    }

    public static Message fromString(String string) {
        if (string == null) {
            return null;
        }
        Message message = null;
        switch (string) {
            case TURN_ON_ACTUATORS_COMMAND:
                // TODO - Implement
                // message = new TurnOnActuatorCommand();
                break;
            case TURN_OFF_ACTUATORS_COMMAND:
                // TODO - Implement
                // message = new TurnOffActuatorCommand();
                break;
        }
        return message;
    }


    private static Message parseParametrizedMessage(String string) {
        // TODO - implement
        return null;
    }

    public static String toString(Message message) {
        String string = null;
        if (message instanceof TurnOnActuatorCommand) {
            string = TURN_ON_ACTUATORS_COMMAND;
        } else if (message instanceof TurnOffActuatorCommand) {
            string = TURN_OFF_ACTUATORS_COMMAND;
        }
        return string;
    }
}