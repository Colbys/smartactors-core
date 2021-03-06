package info.smart_tools.smartactors.http_endpoint_plugins.http_endpoint_plugin;


import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.ioc_strategy_pack.resolve_by_type_and_name_strategy.ResolveByTypeAndNameStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.http_endpoint_plugins.endpoint_plugin.EndpointPlugin;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

public class HttpEndpointPluginTest {
    private IBootstrap bootstrap;
    private HttpEndpointPlugin plugin;
    private ResolveByTypeAndNameStrategy deserializationStrategyChooser;
    private ResolveByTypeAndNameStrategy resolveByTypeAndNameStrategy;
    private ResolveByTypeAndNameStrategy resolveCookies;
    private ResolveByTypeAndNameStrategy resolveHeaders;
    private ResolveByTypeAndNameStrategy resolveStatusSetter;

    @Before
    public void setUp() throws Exception {
        deserializationStrategyChooser = mock(ResolveByTypeAndNameStrategy.class);
        resolveByTypeAndNameStrategy = mock(ResolveByTypeAndNameStrategy.class);
        resolveCookies = mock(ResolveByTypeAndNameStrategy.class);
        resolveHeaders = mock(ResolveByTypeAndNameStrategy.class);
        resolveStatusSetter = mock(ResolveByTypeAndNameStrategy.class);
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), new StrategyContainer());
        ScopeProvider.setCurrentScope(scope);
        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );

        Bootstrap bootstrap = new Bootstrap();
        new EndpointPlugin(bootstrap);
        bootstrap.start();

        IOC.register(
                Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                )
        );

        IOC.register(Keys.getOrAdd("DeserializationStrategyChooser"), new SingletonStrategy(
                        deserializationStrategyChooser
                )
        );


        IOC.register(Keys.getOrAdd("ResponseSenderChooser"), new SingletonStrategy(
                        resolveByTypeAndNameStrategy
                )
        );
        IOC.register(Keys.getOrAdd("CookiesSetterChooser"), new SingletonStrategy(
                        resolveCookies
                )
        );
        IOC.register(Keys.getOrAdd("HeadersExtractorChooser"), new SingletonStrategy(
                        resolveHeaders
                )
        );
        IOC.register(Keys.getOrAdd("ResponseStatusSetter"), new SingletonStrategy(
                        resolveStatusSetter
                )
        );
    }


    @Test
    public void checkPluginCreation()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new HttpEndpointPlugin(bootstrap);
        assertNotNull(plugin);
        reset(bootstrap);
    }

    @Test
    public void checkLoadExecution()
            throws Exception {
        Checker checker = new Checker();
        checker.item = new BootstrapItem("test");
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        List<IBootstrapItem<String>> itemList = new ArrayList<>();
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                checker.item = (IBootstrapItem<String>) args[0];
                itemList.add(checker.item);
                return null;
            }
        })
                .when(bootstrap)
                .add(any(IBootstrapItem.class));
        IPlugin plugin = new HttpEndpointPlugin(bootstrap);
        plugin.load();
        assertEquals(itemList.size(), 1);
        IBootstrapItem<String> item = itemList.get(0);
        item.executeProcess();
        reset(bootstrap);
    }

    @Test(expected = PluginException.class)
    public void checkPluginExceptionOnPluginLoad()
            throws Exception {
        IBootstrap<IBootstrapItem<String>> bootstrap = mock(IBootstrap.class);
        IPlugin plugin = new HttpEndpointPlugin(bootstrap);
        doThrow(Exception.class).when(bootstrap).add(any(IBootstrapItem.class));
        plugin.load();
        fail();
    }

}

class Checker {
    public IBootstrapItem<String> item;
}
