package info.smart_tools.smartactors.endpoint.actor.response_sender_actor.exceptions;

import info.smart_tools.smartactors.endpoint.actor.response_sender_actor.ResponseSenderActor;

/**
 * Exception for {@link ResponseSenderActor}
 */
public class ResponseSenderActorException extends Exception {
    /**
     * Constructor with specific error message
     * @param message specific error message
     */
    public ResponseSenderActorException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ResponseSenderActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */
    public ResponseSenderActorException(final Throwable cause) {
        super(cause);
    }
}
