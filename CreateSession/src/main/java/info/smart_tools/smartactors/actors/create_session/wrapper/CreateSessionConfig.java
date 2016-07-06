package info.smart_tools.smartactors.actors.create_session.wrapper;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ipool.IPool;

/**
 * Contains params for CreateSessionActor
 */
public interface CreateSessionConfig {

    /**
     *
     * @return Return collection name
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when change read value of variable
     */
    String getCollectionName() throws ReadValueException, ChangeValueException;

    /**
     * @return Return connection pool
     * @throws ReadValueException Calling when try read value of variable
     * @throws ChangeValueException Calling when try change value of variable
     */
    IPool getConnectionPool() throws ReadValueException, ChangeValueException;
}
