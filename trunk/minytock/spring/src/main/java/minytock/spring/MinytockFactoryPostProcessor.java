package minytock.spring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * <p/>
 * 
 * @author reesbyars
 *
 */
public class MinytockFactoryPostProcessor implements BeanFactoryPostProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MinytockFactoryPostProcessor.class);
	
	private List<String> emptyMockClasses;

    public MinytockFactoryPostProcessor(List<String> emptyMockClasses) {
        this.emptyMockClasses = emptyMockClasses;
    }
	
	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String className : this.emptyMockClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                FactoryBean<?> factoryBean = MinytockFactoryBean.getFor(beanClass);
                String name = className + "$MINYTOCK_PROXY$";
                beanFactory.registerSingleton(name, factoryBean);
            } catch (ClassNotFoundException e) {
                LOG.error("Could not obtain class " + className + ".  Empty mocking cannot be performed.  Message:  " + e.getMessage());
            }
        }
    }

}
