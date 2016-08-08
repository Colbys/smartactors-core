package info.smart_tools.smartactors.core.in_memory_database;

import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.idatabase.IDataBase;
import info.smart_tools.smartactors.core.idatabase.exception.IDataBaseException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of data base on list
 */
public class InMemoryDatabase implements IDataBase {

    public InMemoryDatabase() {
    }

    private List<DataBaseItem> list = new LinkedList<>();

    @Override
    public void upsert(final IObject document, final String collectionName) throws IDataBaseException {
        DataBaseItem item = null;
        try {
            item = new DataBaseItem(document, collectionName);
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new IDataBaseException("Failed to create DataBaseItem", e);
        }
        if (null == item.getId()) {
            insert(item);
        } else {
            update(item);
        }
    }

    @Override
    public void insert(final IObject document, final String collectionName) throws IDataBaseException {
        synchronized (this) {
            DataBaseItem item = null;
            try {
                item = new DataBaseItem(document, collectionName);
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new IDataBaseException("Failed to create DataBaseItem", e);
            }
            insert(item);
        }
    }

    @Override
    public void update(final IObject document, final String collectionName) throws IDataBaseException {
        DataBaseItem item = null;
        try {
            item = new DataBaseItem(document, collectionName);
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new IDataBaseException("Failed to create DataBaseItem", e);
        }
        update(item);
    }

    private void update(final DataBaseItem item) {
        for (int i = 0; i < list.size(); i++) {
            DataBaseItem inBaseElem = list.get(i);
            if (inBaseElem.getId().equals(item.getId()) && inBaseElem.getCollectionName().equals(item.getCollectionName())) {
                list.remove(i);
                list.add(i, item);
            }
        }
    }

    private void insert(final DataBaseItem item) throws IDataBaseException {
        try {
            item.setId(nextId());
        } catch (ChangeValueException | InvalidArgumentException e) {
            throw new IDataBaseException("Failed to set id to DataBaseItem", e);
        }
        list.add(item);
    }

    @Override
    public IObject getById(final Object id, final String collectionName) {
        for (DataBaseItem item : list) {
            if (item.getId().equals(id) && item.getCollectionName().equals(collectionName)) {
                return item.getDocument();
            }
        }
        return null;
    }

    private Object nextId() {
        return list.size() + 1;
    }

    @Override
    public List<IObject> select(final IObject condition, final String collectionName) throws IDataBaseException {
        List<IObject> outputList = new LinkedList<>();
        for (DataBaseItem item : list) {
            if (Objects.equals(item.getCollectionName(), collectionName)) {
                if (generalConditionParser(condition, item.getDocument())) {
                    outputList.add(clone(item.getDocument()));
                }
            }
        }
        return outputList;
    }

    private boolean generalConditionParser(final IObject condition, final IObject document) throws IDataBaseException {
        Iterator<Map.Entry<IFieldName, Object>> iterator = condition.iterator();
        String key = null;
        try {
            do {
                Map.Entry<IFieldName, Object> entry = iterator.next();
                key = entry.getKey().toString();
                if (entry.getValue() instanceof IObject) {
                    iterator = ((IObject) entry.getValue()).iterator();
                }
            }
            while (iterator.hasNext() && !(Boolean) IOC.resolve(Keys.getOrAdd("ContainsResolveDataBaseCondition"), key));
            return IOC.resolve(Keys.getOrAdd("ResolveDataBaseCondition"), key, condition, document);
        } catch (ResolutionException e) {
            throw new IDataBaseException("Failed to resolve \"ResolveDataBaseCondition\"", e);
        }

    }

    private IObject clone(final IObject iObject) throws IDataBaseException {
        try {
            String serializedIObject = iObject.serialize();
            return IOC.resolve(Keys.getOrAdd(DSObject.class.getCanonicalName()), serializedIObject);
        } catch (ResolutionException | SerializeException e) {
            throw new IDataBaseException("Failed to clone IObject", e);
        }
    }

    @Override
    public void delete(final IObject document, final String collectionName) {

    }
}
