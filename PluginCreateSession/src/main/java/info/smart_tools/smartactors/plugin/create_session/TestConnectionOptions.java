package info.smart_tools.smartactors.plugin.create_session;

import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.postgres_connection.wrapper.ConnectionOptions;

//TODO:: remove this class. It has been added only for testing purposes
public class TestConnectionOptions implements ConnectionOptions {

    @Override
    public String getUrl() throws ReadValueException {
        return "jdbc:postgresql://localhost:5432/vp";
    }

    @Override
    public String getUsername() throws ReadValueException {
        return "test_user";
    }

    @Override
    public String getPassword() throws ReadValueException {
        return "qwerty";
    }

    @Override
    public Integer getMaxConnections() throws ReadValueException {
        return 10;
    }

    @Override
    public void setUrl(final String url) throws ChangeValueException {

    }

    @Override
    public void setUsername(final String username) throws ChangeValueException {

    }

    @Override
    public void setPassword(final String password) throws ChangeValueException {

    }

    @Override
    public void setMaxConnections(final Integer maxConnections) throws ChangeValueException {

    }
}