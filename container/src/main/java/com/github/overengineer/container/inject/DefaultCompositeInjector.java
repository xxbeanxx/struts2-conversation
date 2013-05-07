package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;

import java.util.Set;

/**
 * @author rees.byars
 */
public class DefaultCompositeInjector<T> implements CompositeInjector<T> {

    private final Set<Injector<T>> injectors;

    public DefaultCompositeInjector(Set<Injector<T>> injectors) {
         this.injectors = injectors;
    }

    @Override
    public void inject(T component, Provider provider) {
        for (Injector<T> injector : injectors) {
            injector.inject(component, provider);
        }
    }
}