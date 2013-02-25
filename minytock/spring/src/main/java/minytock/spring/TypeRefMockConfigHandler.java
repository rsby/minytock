package minytock.spring;

import java.util.HashSet;
import java.util.Set;

import minytock.Minytock;
import minytock.util.EmptyMockFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TypeRefMockConfigHandler implements MockConfigHandler {
	
	private Class<?> type;
	private String mockRef;
	private ConfigurableListableBeanFactory beanFactory;
	private Set<String> targets = new HashSet<String>();
	
	public TypeRefMockConfigHandler(String type, String ref) {
		try {
			this.type = Class.forName(type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void init(final ConfigurableListableBeanFactory beanFactory) {
		String[] current = beanFactory.getBeanNamesForType(type);
        if (current != null && current.length > 0) {
        	for (String name : current) {
        		targets.add(name);
        	}
        } else {
        	String name = type.getName() + "$$MINYTOCK_MOCK$$";
        	beanFactory.registerSingleton(name, new FactoryBean() {

				@Override
				public Object getObject() throws Exception {
					return Minytock.delegate(EmptyMockFactory.create(type)).to(beanFactory.getBean(mockRef));
				}

				@Override
				public Class<?> getObjectType() {
					return type;
				}

				@Override
				public boolean isSingleton() {
					return true;
				}
        		
        	});
        }
        this.beanFactory = beanFactory;
	}

	@Override
	public boolean isApplicable(String beanName) {
		return this.targets.contains(beanName);
	}

	@Override
	public Object getMock(Object bean) {
		return Minytock.delegate(Minytock.prepare(bean)).to(beanFactory.getBean(mockRef));
	}

}
