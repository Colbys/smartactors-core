package info.smart_tools.smartactors.das.utilities.exception;

public class ProjectResolutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ProjectResolutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ProjectResolutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ProjectResolutionException(final Throwable cause) {
        super(cause);
    }
}
