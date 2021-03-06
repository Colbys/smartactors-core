package info.smart_tools.smartactors.testing.test_runner;

import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.exception.EnvironmentHandleException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ChainTestRunner}.
 */
public class TestRunnerChainTest {

    private IStrategyContainer container = new StrategyContainer();
    private Object chainId = mock(Object.class);
    private IChainStorage chainStorage = mock(IChainStorage.class);
    private IEnvironmentHandler testHandler = mock(IEnvironmentHandler.class);
    private IReceiverChain receiverChain = mock(IReceiverChain.class);

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), this.container);
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ApplyFunctionToArgumentsStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "chain_id_from_map_name"),
                new ApplyFunctionToArgumentsStrategy((a) -> {
                    return this.chainId;
                })
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), IChainStorage.class.getCanonicalName()),
                new SingletonStrategy(this.chainStorage)
        );
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "test environment handler"),
                new SingletonStrategy(testHandler)
        );
        when(this.chainStorage.resolve(this.chainId)).thenReturn(this.receiverChain);
    }

    @Test
    public void checkCreation()
            throws Exception {
        ITestRunner runner = new ChainTestRunner();
        assertNotNull(runner);
    }

    @Test
    public void checkRunnerHandle()
            throws Exception {
        ITestRunner runner = new ChainTestRunner();
        IObject desc = mock(IObject.class);
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecuteException, InvalidArgumentException {
            }
        };
        when(desc.getValue(new FieldName("chainName"))).thenReturn("chain");
        runner.runTest(desc, callback);
        verify(this.testHandler, times(1)).handle(desc, this.receiverChain, callback);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullFirstArgument()
            throws Exception {
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecuteException, InvalidArgumentException {
            }
        };
        ITestRunner runner = new ChainTestRunner();
        runner.runTest(null, callback);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullSecondArgument()
            throws Exception {
        ITestRunner runner = new ChainTestRunner();
        runner.runTest(mock(IObject.class), null);
        fail();
    }

    @Test (expected = InitializationException.class)
    public void checkInitializationExceptionOnCreation()
            throws Exception {
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        doThrow(ResolutionException.class).when(strategy).resolve(any());
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                strategy
        );
        new ChainTestRunner();
        fail();
    }

    @Test (expected = TestExecutionException.class)
    public void checkTestExecutionExceptionOnHandle()
            throws Exception {
        ITestRunner runner = new ChainTestRunner();
        IObject desc = mock(IObject.class);
        IAction<Throwable> callback = new IAction<Throwable>() {
            @Override
            public void execute(Throwable actingObject)
                    throws ActionExecuteException, InvalidArgumentException {
            }
        };
        when(desc.getValue(new FieldName("chainName"))).thenReturn("chain");
        doThrow(EnvironmentHandleException.class).when(this.testHandler).handle(
                desc, this.receiverChain, callback
        );
        runner.runTest(desc, callback);
        fail();
    }
}
