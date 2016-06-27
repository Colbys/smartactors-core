package info.smart_tools.smartactors.core.db_task.search_by_id.psql;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.PreparedQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.db_task.search_by_id.psql.wrapper.ISearchByIdQuery;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.exception.PoolTakeException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.sql_commons.FieldPath;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
import info.smart_tools.smartactors.core.sql_commons.QueryStatementFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;

@PrepareForTest(IOC.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class PSQLSearchByIdTaskTest {

    private PSQLSearchByIdTask task;
    private JDBCCompiledQuery compiledQuery;
    private StorageConnection connection;

    @Before
    public void setUp() throws StorageException, IllegalAccessException {
        compiledQuery = mock(JDBCCompiledQuery.class);
        task = new PSQLSearchByIdTask();
    }

    @Test
    public void ShouldPrepareQuery()
            throws TaskPrepareException, ResolutionException, ReadValueException, ChangeValueException, StorageException, PoolTakeException, TaskSetConnectionException {

        IObject createCollectionMessage = mock(IObject.class);
        ISearchByIdQuery message = mock(ISearchByIdQuery.class);
        PreparedQuery preparedQuery = new QueryStatement();
        initDataForPrepare(preparedQuery, message, createCollectionMessage);
        Map<String, String> indexes = new HashMap<>();
        indexes.put("meta.tags", "tags");
        when(message.getId()).thenReturn("123");
        StorageConnection connection = mock(StorageConnection.class);
        when(connection.compileQuery(any(PreparedQuery.class))).thenReturn(compiledQuery);
        when(connection.getId()).thenReturn("testConnectionId");

        IKey compiledQueryKey = mock(IKey.class);
        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenReturn(compiledQueryKey);
        when(IOC.resolve(eq(compiledQueryKey), eq(connection), anyObject())).thenReturn(compiledQuery);

        task.setConnection(connection);
        task.prepare(createCollectionMessage);

        PowerMockito.verifyStatic();
        IOC.resolve(any(IKey.class), eq(connection), any(QueryStatementFactory.class));
    }

    @Test(expected = TaskExecutionException.class)
    public void ShouldExecuteQueryAndThrowTaskException() throws Exception {

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        field(PSQLSearchByIdTask.class, "query").set(task, compiledQuery);
        task.execute();

        verify(compiledQuery).executeQuery();
        verify(preparedStatement).execute();
    }

    @Test
    public void ShouldSetConnection() throws Exception {

        StorageConnection storageConnectionBefore = (StorageConnection) MemberModifier.field(PSQLSearchByIdTask.class, "connection").get(task);
        connection = mock(StorageConnection.class);
        when(connection.getId()).thenReturn("testConnectionId");
        task.setConnection(connection);
        StorageConnection storageConnectionAfter = (StorageConnection) MemberModifier.field(PSQLSearchByIdTask.class, "connection").get(task);

        assertNull(storageConnectionBefore);
        assertNotNull(storageConnectionAfter);
        assertEquals(connection, storageConnectionAfter);
    }

    private void initDataForPrepare(PreparedQuery preparedQuery, ISearchByIdQuery message, IObject createCollectionMessage)
            throws ResolutionException, ReadValueException, ChangeValueException {

        mockStatic(IOC.class);

        IKey key1 = mock(IKey.class);
        IKey keyQuery = mock(IKey.class);
        IKey keyMessage = mock(IKey.class);
        IKey keyFieldPath = mock(IKey.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(key1);
        when(IOC.resolve(eq(key1), eq(QueryStatement.class.toString()))).thenReturn(keyQuery);
        when(IOC.resolve(eq(key1), eq(ISearchByIdQuery.class.toString()))).thenReturn(keyMessage);
        when(IOC.resolve(eq(key1), eq(FieldPath.class.toString()))).thenReturn(keyFieldPath);


        FieldPath fieldPath = mock(FieldPath.class);
        when(IOC.resolve(eq(keyQuery))).thenReturn(preparedQuery);
        when(IOC.resolve(eq(keyMessage), eq(createCollectionMessage))).thenReturn(message);
        when(IOC.resolve(eq(keyFieldPath), anyString())).thenReturn(fieldPath);
        when(fieldPath.getSQLRepresentation()).thenReturn("");

        CollectionName collectionName = mock(CollectionName.class);
        when(collectionName.toString()).thenReturn("collection");
        when(message.getCollectionName()).thenReturn(collectionName);
    }
}