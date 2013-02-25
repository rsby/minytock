package minytock.spring;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public interface MockConfigHandler {
	
	void init(ConfigurableListableBeanFactory beanFactory) throws Exception;
	boolean isApplicable(String beanName);
	Object getMock(Object bean) throws Exception;

}
