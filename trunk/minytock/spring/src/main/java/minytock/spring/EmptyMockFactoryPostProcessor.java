package minytock.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * a utility for auto-empty-mocking of Spring beans (useful when all you have available is an interface).
 * <p/>
 * Example usage:
 * <pre>
 * &lt;bean class=&quot;minytock.spring.EmptyMockFactoryPostProcessor&quot;&gt;
 *    &lt;property name=&quot;emptyMockClasses&quot; value=&quot;org.byars.SomeService, org.byars.SomeOtherService&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p/>
 * 
 * @author reesbyars
 *
 */
public class EmptyMockFactoryPostProcessor implements BeanFactoryPostProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmptyMockFactoryPostProcessor.class);
	
	private String[] emptyMockClasses = {};

    public void setEmptyMockClasses(String emptyMockClasses) {
        this.emptyMockClasses = StringUtils.trimAllWhitespace(emptyMockClasses).split(",");
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
