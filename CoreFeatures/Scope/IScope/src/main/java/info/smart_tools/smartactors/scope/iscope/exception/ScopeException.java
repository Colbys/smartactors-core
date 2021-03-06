package info.smart_tools.smartactors.scope.iscope.exception;

import info.smart_tools.smartactors.scope.iscope.IScope;

/**
 * Exception for error in {@link IScope} methods
 */
public class ScopeException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ScopeException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ScopeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ScopeException(final Throwable cause) {
        super(cause);
    }
}
