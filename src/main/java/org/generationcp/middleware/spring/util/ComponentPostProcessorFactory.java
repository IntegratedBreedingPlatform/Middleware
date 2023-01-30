package org.generationcp.middleware.spring.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Deprecated
public class ComponentPostProcessorFactory{

    public <T> BeanPostProcessor generatePostProcessorFactory(final Class<T> componentClass, final ComponentFactory<T> factoryInstance)  {
        BeanPostProcessor postProcessor = new BeanPostProcessor() {

            private ComponentFactory<T> factory = factoryInstance;

            @Override
            public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                if (componentClass.isInstance(o)) {
                    factory.addComponent((T) o);
                }

                return o;
            }

            @Override
            public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                return o;
            }

        };

        return postProcessor;
    }
}
