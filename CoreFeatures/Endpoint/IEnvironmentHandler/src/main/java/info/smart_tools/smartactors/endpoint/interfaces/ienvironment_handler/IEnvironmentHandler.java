package info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

/**
 * Interface of environment handler, that should create MessageProcessor and process it
 */
public interface IEnvironmentHandler {
    /**
     * Method for handle environment
     * @param environment Environment of the {@link IMessageProcessor}

     * @param receiverChain Chain that should receive environment
     * @param callback      the callback for processing exception
     * @throws EnvironmentHandleException exception for case if there are some problem on handle
     */
    void handle(final IObject environment, final IReceiverChain receiverChain, final IAction<Throwable> callback)
            throws EnvironmentHandleException, InvalidArgumentException;
}
