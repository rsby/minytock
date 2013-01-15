package minytock.spring;

import java.lang.reflect.Modifier;

import minytock.Minytock;
import minytock.delegate.DelegationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

/**
 * a utility for automatically preparing of Spring-managed beans for delegation.
 * <p/>
 * Example usage:
 * <pre>
 * &lt;bean class=&quot;minytock.spring.DelegationPostProcessor&quot;&gt;
 *    &lt;property name=&quot;mockablePackages&quot; value=&quot;orb.byars&quot;/&gt;
 * &lt;/bean&gt;
 * </pre>
 * <p/>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 5:16 PM
 * <p/>
 * DelegationPostProcessor
 */
public class DelegationPostProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DelegationPostProcessor.class);

    private String[] mockablePackages = {""};

    public void setMockablePackages(String mockablePackages) {
        this.mockablePackages = StringUtils.trimAllWhitespace(mockablePackages).split(",");
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

    protected Class<?> getTargetClass(Object bean) {
    	return bean.getClass();
    }
    
    protected Object prepare(Object bean, Class<?> targetClass) throws DelegationException {
    	return Minytock.provider.getHandler(bean, targetClass, false).getProxy();
    }
    
}
