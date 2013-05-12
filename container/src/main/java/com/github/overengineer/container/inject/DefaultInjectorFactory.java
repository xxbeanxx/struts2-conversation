package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;
import com.github.overengineer.container.metadata.MetadataAdapter;
import com.github.overengineer.container.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author rees.byars
 */
public class DefaultInjectorFactory implements InjectorFactory {

    private final MetadataAdapter metadataAdapter;

    public DefaultInjectorFactory(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    public <T> CompositeInjector<T> create(Class<T> implementationType) {
        Set<Injector<T>> injectors = new HashSet<Injector<T>>();
        for (Method method : implementationType.getMethods()) {
            if (ReflectionUtil.isPublicSetter(method)) {
                Type type = method.getGenericParameterTypes()[0];
                String propertyName = metadataAdapter.getPropertyName(method);
                if (propertyName != null) {
                    injectors.add(new PropertyInjector<T>(method, propertyName, type));
                } else if (metadataAdapter.isComponentSetter(method)) {
                    injectors.add(new ComponentInjector<T>(method, type));
                }
            }
        }
        if (injectors.size() == 0) {
            return new EmptyInjector<T>();
        } else {
            return new DefaultCompositeInjector<T>(injectors);
        }
    }

    static class EmptyInjector<T> implements CompositeInjector<T> {
        @Override
        public void inject(T component, Provider provider) {}
    }
}