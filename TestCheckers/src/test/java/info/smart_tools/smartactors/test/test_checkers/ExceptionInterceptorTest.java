package info.smart_tools.smartactors.test.test_checkers;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.core.irouter.IRouter;
import info.smart_tools.smartactors.core.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.test.iassertion.exception.AssertionFailureException;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Test for {@link ExceptionInterceptor}.
 */
public class ExceptionInterceptorTest extends PluginsLoadingTestBase {
    private IResolveDependencyStrategy receiverIdStrategyMock;
    private IRouter routerMock;
    private IMessageReceiver expectedReceiverMock;
    private IMessageReceiver unexpectedReceiverMock;
    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence sequenceMock;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Override
    protected void registerMocks()
            throws Exception {
        receiverIdStrategyMock = Mockito.mock(IResolveDependencyStrategy.class);
        routerMock = Mockito.mock(IRouter.class);
        expectedReceiverMock = Mockito.mock(IMessageReceiver.class);
        unexpectedReceiverMock = Mockito.mock(IMessageReceiver.class);
        messageProcessorMock = Mockito.mock(IMessageProcessor.class);
        sequenceMock = Mockito.mock(IMessageProcessingSequence.class);

        Mockito.when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        IOC.register(Keys.getOrAdd("receiver_id_from_iobject"), receiverIdStrategyMock);
        IOC.register(Keys.getOrAdd(IRouter.class.getCanonicalName()), new IResolveDependencyStrategy() {
            @Override
            public <T> T resolve(Object... args) throws ResolveDependencyStrategyException {
                return (T)routerMock;
            }
        });
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenInvalidClassNameGivenInDescriptionObject()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'org.hell.Unexist'}".replace('\'', '"'));
        Object receiverId = new Object();
        Mockito.when(receiverIdStrategyMock.resolve(Matchers.same(desc))).thenReturn(receiverId);
        Mockito.when(routerMock.route(Matchers.same(receiverId))).thenReturn(expectedReceiverMock);

        new ExceptionInterceptor(desc);
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenInvalidReceiverIdGivenAsArgument()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.NullPointerException'}".replace('\'', '"'));
        Object receiverId = new Object();
        Mockito.when(receiverIdStrategyMock.resolve(Matchers.same(desc))).thenReturn(receiverId);
        Mockito.when(routerMock.route(Matchers.same(receiverId))).thenThrow(RouteNotFoundException.class);

        new ExceptionInterceptor(desc);
    }

    @Test(expected = InitializationException.class)
    public void Should_constructorThrowWhenSomethingGoesWrong()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.NullPointerException'}".replace('\'', '"'));

        IOC.remove(Keys.getOrAdd("receiver_id_from_iobject"));

        new ExceptionInterceptor(desc);
    }

    private ExceptionInterceptor createCorrect()
            throws Exception {
        IObject desc = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'class': 'java.lang.IllegalStateException'}".replace('\'', '"'));
        Object receiverId = new Object();
        Mockito.when(receiverIdStrategyMock.resolve(Matchers.same(desc))).thenReturn(receiverId);
        Mockito.when(routerMock.route(Matchers.same(receiverId))).thenReturn(expectedReceiverMock);

        return new ExceptionInterceptor(desc);
    }

    @Test
    public void Should_createNoArgumentsObjectForSuccessReceiver()
            throws Exception {
        Assert.assertNull(createCorrect().getSuccessfulReceiverArguments());
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenNoExceptionOccurred()
            throws Exception {
        createCorrect().check(messageProcessorMock, null);
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenUnexpectedExceptionOccurs()
            throws Exception {
        createCorrect().check(messageProcessorMock, new Exception(new IllegalArgumentException(new NullPointerException())));
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccursInUnexpectedPlace()
            throws Exception {
        Mockito.when(sequenceMock.getCurrentReceiver()).thenReturn(unexpectedReceiverMock);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenExceptionOccursInUnexpectedPlaceAndStrategyCannotResolveUnexpectedReceiverId()
            throws Exception {
        Mockito.when(sequenceMock.getCurrentReceiver()).thenReturn(unexpectedReceiverMock);
        IObject unexpectedId = Mockito.mock(IObject.class);
        Mockito.when(sequenceMock.getCurrentReceiverArguments()).thenReturn(unexpectedId);
        Mockito.when(receiverIdStrategyMock.resolve(Matchers.same(unexpectedId))).thenThrow(ResolveDependencyStrategyException.class);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }

    @Test
    public void Should_notThrowWhenEverythingIsOk()
            throws Exception {
        Mockito.when(sequenceMock.getCurrentReceiver()).thenReturn(expectedReceiverMock);

        createCorrect().check(messageProcessorMock, new Exception(new IllegalStateException(new NullPointerException())));
    }
}
