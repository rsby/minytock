package minytock.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * <p/>
 * 
 * @author reesbyars
 *
 */
public class MinytockFactoryPostProcessor implements BeanFactoryPostProcessor, BeanPostProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MinytockFactoryPostProcessor.class);
	
	private final List<String> emptyMockClasses;
	private Map<String, FactoryBean<?>> mockTargets = new HashMap<String, FactoryBean<?>>();  

    public MinytockFactoryPostProcessor(List<String> emptyMockClasses) {
        this.emptyMockClasses = emptyMockClasses;
    }
	
	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String className : this.emptyMockClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                FactoryBean<?> factoryBean = MinytockFactoryBean.getFor(beanClass);
                String[] current = beanFactory.getBeanNamesForType(beanClass);
                if (current != null && current.length > 0) {
                	for (String name : current) {
                		mockTargets.put(name, factoryBean);
                	}
                } else {
                	String name = className + "$$MINYTOCK_MOCK$$";
                	beanFactory.registerSingleton(name, factoryBean);
                }
            } catch (ClassNotFoundException e) {
                LOG.error("Could not obtain class " + className + ".  Empty mocking cannot be performed.  Message:  " + e.getMessage());
            }
        }
    }

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		FactoryBean<?> factory = this.mockTargets.get(beanName);
		if (factory != null) {
			try {
				return factory.getObject();
			} catch (Exception e) {
				return bean;
			}
		}
		return bean;
	}

}
