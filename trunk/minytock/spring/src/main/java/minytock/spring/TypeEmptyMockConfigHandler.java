package minytock.spring;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TypeEmptyMockConfigHandler implements MockConfigHandler {
	
	private Class<?> beanClass;
	private FactoryBean<?> factoryBean;
	private Set<String> targets = new HashSet<String>();
	
	public TypeEmptyMockConfigHandler(String type) {
		try {
			beanClass = Class.forName(type);
			factoryBean = MinytockFactoryBean.getFor(beanClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init(ConfigurableListableBeanFactory beanFactory) {
		String[] current = beanFactory.getBeanNamesForType(beanClass);
        if (current != null && current.length > 0) {
        	for (String name : current) {
        		targets.add(name);
        	}
        } else {
        	String name = beanClass.getName() + "$$MINYTOCK_MOCK$$";
        	beanFactory.registerSingleton(name, factoryBean);
        }
	}

	@Override
	public boolean isApplicable(String beanName) {
		return targets.contains(beanName);
	}

	@Override
	public Object getMock(Object bean) throws Exception {
		return factoryBean.getObject();
	}

}
