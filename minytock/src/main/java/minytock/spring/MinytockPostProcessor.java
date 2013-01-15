package minytock.spring;

import minytock.Minytock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
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
public class MinytockPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, PriorityOrdered {

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
    	LOG.debug("Minytock checking [" + beanName +  "] for delegation");
    	boolean doProxy = false;
    	Class<?> realClass = AopUtils.getTargetClass(bean);
        for (String pack : mockablePackages) {
            if (realClass.getName().startsWith(pack) && !beanName.endsWith("Test")) {
                doProxy = true;
                break;
            }
        }
        Object proxy = bean;
        try {
            if (doProxy) {
            	LOG.debug("Minytock preparing [" + beanName +  "] for delegation");
                proxy = Minytock.provider.getHandler(proxy, realClass, false).getProxy();
            } else {
            	LOG.debug("Minytock skipping [" + beanName +  "] as not eligible delegation");
            }
        } catch (Exception e) {
            //nuthin
        }
        return proxy;
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

    /**
     * we want to be an early post-processor so that our proxy gets included in down-stream dynamic configurations
     */
	@Override
	public int getOrder() {
		return 0;
	}
    
}
