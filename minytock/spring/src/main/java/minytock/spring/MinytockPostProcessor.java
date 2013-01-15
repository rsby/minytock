package minytock.spring;

import java.lang.reflect.Modifier;

import minytock.Minytock;
import minytock.delegate.DelegationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * The Minytock interface to the Spring IoC layer.  Packages and classes can be declared for automatic
 * preparing of Spring-managed beans (makes them ready for using {@link minytock.Minytock#delegate(Object)}
 * and auto-empty-mocking of Spring beans (useful when all you have available is an interface).
 * <p/>
 * Example usage:
 * <pre>
 * &lt;bean class=&quot;minytock.spring.MinytockPostProcessor&quot;&gt;
 *    &lt;property name=&quot;mockablePackages&quot; value=&quot;orb.byars&quot;/&gt;
 *    &lt;property name=&quot;emptyMockClasses&quot; value=&quot;org.byars.SomeService, org.byars.SomeOtherService&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p/>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 5:16 PM
 * <p/>
 * MinytockPostProcessor
 */
public class MinytockPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MinytockPostProcessor.class);

    String[] mockablePackages = {""};
    String[] emptyMockClasses = {};

    public void setMockablePackages(String mockablePackages) {
        this.mockablePackages = StringUtils.trimAllWhitespace(mockablePackages).split(",");
    }

    public void setEmptyMockClasses(String emptyMockClasses) {
        this.emptyMockClasses = StringUtils.trimAllWhitespace(emptyMockClasses).split(",");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    	return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    	
    	LOG.info("Minytock checking [" + beanName +  "] for delegation");
    	
    	Class<?> targetClass = this.getTargetClass(bean);
    	
    	if (Modifier.isFinal(targetClass.getModifiers())) {
    		LOG.info("Minytock skipping [" + beanName +  "] as not eligible delegation because its class is final and cannot be proxied");
    		return bean;
    	}
    	
    	boolean doProxy = false;
        for (String pack : mockablePackages) {
            if (targetClass.getName().startsWith(pack) && !beanName.endsWith("Test")) {
                doProxy = true;
                break;
            }
        }
        
        if (!doProxy) {
        	LOG.info("Minytock skipping [" + beanName +  "] as not eligible delegation");
        	return bean;
        }
        
        try {

        	LOG.info("Minytock preparing [" + beanName +  "] for delegation");
            return this.prepare(bean, targetClass);
 
        } catch (Exception e) {
        	
        	LOG.error("An exception occurred attempting to prepare the bean [" + beanName + "].  Forgoing preparation for this bean.  It will not be eligible for delegation.", e);
            return bean;
            
        }
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

    protected Class<?> getTargetClass(Object bean) {
    	return bean.getClass();
    }
    
    protected Object prepare(Object bean, Class<?> targetClass) throws DelegationException {
    	return Minytock.provider.getHandler(bean, targetClass, false).getProxy();
    }
    
}
