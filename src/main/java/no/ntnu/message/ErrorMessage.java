package no.ntnu.message;

/**
 * Represents an error message for a failed command execution.
 */
public class ErrorMessage implements Message {
    private final String message;

    /**
     * Creates a new instance of the {@code ErrorMessage} class.
     *
     * @param message the error message of the failed command execution.
     */
    public ErrorMessage(String message) {
        this.message = message;
    }

    /**
     * Get the error message.
     *
     * @return the error message of the failed command execution.
     */
    public String getMessage() {
        return message;
    }
}
