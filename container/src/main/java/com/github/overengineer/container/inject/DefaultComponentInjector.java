package com.github.overengineer.container.inject;

import com.github.overengineer.container.Provider;

import java.util.Set;

/**
 * @author rees.byars
 */
public class DefaultComponentInjector<T> implements ComponentInjector<T> {

    private final Set<MethodInjector<T>> injectors;

    public DefaultComponentInjector(Set<MethodInjector<T>> injectors) {
         this.injectors = injectors;
    }

    @Override
    public void inject(T component, Provider provider) {
        for (MethodInjector<T> injector : injectors) {
            injector.inject(component, provider);
        }
    }
}