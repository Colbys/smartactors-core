package info.smart_tools.smartactors.core.cached_collection.task;

import info.smart_tools.smartactors.core.cached_collection.exception.CreateCachedCollectionTaskException;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.idatabase_task.exception.TaskSetConnectionException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.wrapper_generator.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Makes add or update operation for cached collection
 */
public class UpsertIntoCachedCollectionTask implements IDatabaseTask {

    private IDatabaseTask upsertTask;

    private Field<String> startDateTimeField;
    //TODO:: this format should be setted for whole project?
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * @param upsertTask Target update task
     */
    public UpsertIntoCachedCollectionTask(final IDatabaseTask upsertTask) throws CreateCachedCollectionTaskException {
        this.upsertTask = upsertTask;
        try {
            this.startDateTimeField = IOC.resolve(Keys.getOrAdd("Field"), "document/startDateTime");
        } catch (ResolutionException e) {
            throw new CreateCachedCollectionTaskException("Can't create UpsertIntoCachedCollectionTask.", e);
        }
    }

    /**
     * Prepares database query
     * @param query query object
     * @throws TaskPrepareException if error occurs in process of query preparing
     */
    @Override
    public void prepare(final IObject query) throws TaskPrepareException {

        try {
            if (startDateTimeField.out(query) == null) {
                startDateTimeField.in(query, LocalDateTime.now().format(FORMATTER));
            }
            upsertTask.prepare(query);
        } catch (InvalidArgumentException | ReadValueException | ChangeValueException e) {
            throw new TaskPrepareException("Can't prepare query for upsert into cached collection", e);
        }
    }

    @Override
    public void setConnection(final StorageConnection connection) throws TaskSetConnectionException {
        upsertTask.setConnection(connection);
    }

    /**
     * Execute the task.
     *
     * @throws TaskExecutionException if error occurs in process of task execution
     */
    @Override
    public void execute() throws TaskExecutionException {
        upsertTask.execute();
    }
}
