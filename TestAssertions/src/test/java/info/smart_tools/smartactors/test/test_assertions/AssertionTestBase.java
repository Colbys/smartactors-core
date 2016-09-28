package info.smart_tools.smartactors.test.test_assertions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.test.iassertion.IAssertion;
import info.smart_tools.smartactors.testing.helpers.plugins_loading_test_base.PluginsLoadingTestBase;

/**
 * Base class for tests for {@link IAssertion} implementations.
 */
public class AssertionTestBase extends PluginsLoadingTestBase {
    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    protected void apply(Class<? extends IAssertion> clz, IObject desc, Object value)
            throws Exception {
        (clz.newInstance()).check(desc, value);
    }

    protected void apply(Class<? extends IAssertion> clz, String desc, Object value)
            throws Exception {
        apply(clz, IOC.<IObject>resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), desc), value);
    }
}
