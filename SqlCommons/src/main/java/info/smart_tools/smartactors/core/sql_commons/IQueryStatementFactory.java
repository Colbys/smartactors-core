package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.sql_commons.exception.QueryStatementFactoryException;

@FunctionalInterface
public interface IQueryStatementFactory {

    QueryStatement create() throws QueryStatementFactoryException;
}