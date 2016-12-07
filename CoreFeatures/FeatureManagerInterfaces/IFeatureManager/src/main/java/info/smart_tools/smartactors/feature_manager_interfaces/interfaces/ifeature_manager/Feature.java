package info.smart_tools.smartactors.feature_manager_interfaces.interfaces.ifeature_manager;

import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.feature_manager_interfaces.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.feature_manager_interfaces.interfaces.ifeature.IFeatureState;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.Set;

/**
 * Created by sevenbits on 11/14/16.
 */
public class Feature implements IFeature {

    private String name;
    private Set<String> dependencies;
    private IPath featureLocation;
    private IFeatureState status;
    private String groupId;
    private String version;

    public Feature(String name, Set<String> dependencies, IPath location)
            throws ResolutionException {
        this.name = name;
        this.dependencies = dependencies;
        this.featureLocation = location;
        this.status = IOC.resolve(Keys.getOrAdd("feature-state:initial-state"));
    }

    public Feature(String name, String groupId, String version, IPath featureLocation)
            throws ResolutionException {
        this.name = name;
        this.groupId = groupId;
        this.version = version;
        this.featureLocation = featureLocation;
        this.status = IOC.resolve(Keys.getOrAdd("feature-state:initial-state"));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getDependencies() {
        return this.dependencies;
    }

    @Override
    public IPath getFeatureLocation() {
        return this.featureLocation;
    }

    @Override
    public IFeatureState getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(IFeatureState status) {
        this.status = status;
    }

    @Override
    public void setName(String featureName) {
        this.name = featureName;
    }

    @Override
    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public void setFeatureLocation(IPath location) {
        this.featureLocation = (IPath) location;
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = (String) groupId;
    }

    @Override
    public void setVersion(String version) {
        this.version = (String) version;
    }
}