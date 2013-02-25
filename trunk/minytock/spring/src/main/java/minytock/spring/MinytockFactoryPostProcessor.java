package minytock.spring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
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
	
	private final List<MockConfigHandler> configHandlers;  

    public MinytockFactoryPostProcessor(List<MockConfigHandler> configHandlers) {
        this.configHandlers = configHandlers;
    }
	
	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (MockConfigHandler configHandler : this.configHandlers) {
            try {
                configHandler.init(beanFactory);
            } catch (Exception e) {
                LOG.error("Could not init handler.  Message:  " + e.getMessage());
            }
        }
    }

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		for (MockConfigHandler configHandler : this.configHandlers) {
            if (configHandler.isApplicable(beanName)) {
            	try {
					return configHandler.getMock(bean);
				} catch (Exception e) {
					return bean;
				}
            }
        }
		return bean;
	}

}
