package minytock.spring;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import minytock.Minytock;
import minytock.delegate.DelegationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * a utility for automatically preparing Spring-managed beans for delegation.
 * <p/>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 5:16 PM
 * <p/>
 * DelegationPostProcessor
 */
public class DelegationPostProcessor implements BeanPostProcessor, SpringDelegationRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DelegationPostProcessor.class);

    private SortedSet<String> beanNames = new TreeSet<String>();
    private SortedSet<String> delegateNames = new TreeSet<String>();
    
    private String[] includeFilters;
    private String[] excludeFilters;
    
    public DelegationPostProcessor(List<String> includeFilters, List<String> excludeFilters) {
	     this.includeFilters = includeFilters.toArray(new String[includeFilters.size()]);
	     this.excludeFilters = excludeFilters.toArray(new String[excludeFilters.size()]);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    	if (AnnotationUtils.findAnnotation(bean.getClass(), MinytockDelegate.class) != null) {
    		delegateNames.add(beanName);
    	}
    	return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    	
    	LOG.debug("Minytock checking [" + beanName +  "] for delegation");
    	
    	Class<?> targetClass = this.getTargetClass(bean);
    	
    	boolean doProxy = PatternMatchUtils.simpleMatch(includeFilters, targetClass.getName()) && !PatternMatchUtils.simpleMatch(excludeFilters, targetClass.getName());
        
        if (!doProxy) {
        	LOG.debug("Minytock skipping [" + beanName + "] as not eligible for delegation");
        	return bean;
        }
        
        if (Modifier.isFinal(targetClass.getModifiers())) {
    		LOG.warn("Minytock skipping [" + beanName +  "] as not eligible for delegation because its class is final and cannot be proxied");
    		return bean;
    	}
        
        try {

        	LOG.info("Minytock preparing [" + beanName +  "] for delegation");
        	Object proxy = this.prepare(bean, targetClass); 
        	beanNames.add(beanName);
            return proxy;
 
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

	@Override
	public SortedSet<String> getBeanNames() {
		return beanNames;
	}

	@Override
	public SortedSet<String> getDelegateNames() {
		return delegateNames;
	}
    
}
