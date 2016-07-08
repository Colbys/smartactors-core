package info.smart_tools.smartactors.actors.authentication.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * Wrapper for message for authentication actor
 */
public interface AuthenticationMessage {
    /**
     * @return auth information from http request
     * @throws ReadValueException
     */
    String getRequestUserAgent() throws ReadValueException;

    /**
     * Set error to message if validation is failed
     * @return auth information from user session
     * @throws ReadValueException
     */
    String getSessionUserAgent() throws ReadValueException;
}