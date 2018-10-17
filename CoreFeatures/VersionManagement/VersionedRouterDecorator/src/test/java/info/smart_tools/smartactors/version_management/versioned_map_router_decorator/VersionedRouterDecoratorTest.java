package info.smart_tools.smartactors.version_management.versioned_map_router_decorator;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.message_processing.map_router.MapRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link VersionedRouterDecorator}.
 */
public class VersionedRouterDecoratorTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_mapIsNull()
            throws Exception {
        assertNull(new VersionedRouterDecorator(null, null));
    }

    @Test
    public void Should_storeAndRevertReceivers()
            throws Exception {
        Map<Object, Map<IModule, Object>> map = mock(Map.class);
        Map<Object, IMessageReceiver> innerMap = mock(Map.class);
        Object id = mock(Object.class);
        IMessageReceiver receiver0 = mock(IMessageReceiver.class);
        IMessageReceiver receiver1 = mock(IMessageReceiver.class);

        VersionedRouterDecorator router = new VersionedRouterDecorator(map, new MapRouter(innerMap));

        router.register(id, receiver0);
        verify(innerMap).put(same(id), same(receiver0));

        when(map.put(same(id), same(receiver1))).thenReturn(receiver0);
        router.register(id, receiver1);
        verify(map).put(same(id), same(receiver1));

        when(map.get(same(id))).thenReturn(receiver1);

        assertSame(receiver1, router.route(id));

        router.unregister(id);
        verify(map).remove(id);

        when(map.remove(same(id))).thenReturn(receiver1);
        router.unregister(id);
        verify(receiver1).dispose();
    }

    @Test(expected = RouteNotFoundException.class)
    public void Should_throwWhenNoRouteFound()
            throws Exception {
        new MapRouter(mock(Map.class)).route(mock(Object.class));
    }

    @Test
    public void Should_enumerate_returnListOfIdentifiersOfAllReceivers()
            throws Exception {
        Set<Object> keys = new HashSet<>(Arrays.asList(new Object(), new Object()));
        Map<Object, IMessageReceiver> mapMock = mock(Map.class);
        when(mapMock.keySet()).thenReturn(keys);

        IRouter router = new MapRouter(mapMock);

        assertEquals(new ArrayList<Object>(keys), router.enumerate());
    }
}
