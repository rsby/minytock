package minytock.spring;

import minytock.Minytock;
import minytock.delegate.DelegationHandlerCache;
import minytock.spy.Spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.BeansException;

/**
 * Pretty the same as the {@link MinytockPostProcessor}, but made to work with Spring AOP proxies.
 * <p/>
 * MinytockAopPostProcessor
 */
public class MinytockAopPostProcessor extends MinytockPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MinytockAopPostProcessor.class);
    
    public MinytockAopPostProcessor() {
    	Minytock.provider = new SpringAopDelegationHandlerProvider(Spy.get(DelegationHandlerCache.class).from(Minytock.provider));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    	LOG.info("Minytock checking [" + beanName +  "] for delegation");
    	boolean doProxy = false;
    	Class<?> realClass = AopUtils.getTargetClass(bean);
        for (String pack : mockablePackages) {
            if (realClass.getName().startsWith(pack) && !beanName.endsWith("Test")) {
                doProxy = true;
                break;
            }
        }
        Object minytockProxy = bean;
        try {
            if (doProxy) {
            	LOG.info("Minytock preparing [" + beanName +  "] for delegation");
                minytockProxy = Minytock.provider.getHandler(minytockProxy, realClass, false).getProxy();
            } else {
            	LOG.info("Minytock skipping [" + beanName +  "] as not eligible delegation");
            }
        } catch (Exception e) {
            //nuthin
        }
        if (bean instanceof HotSwappableTargetSource ) {
        	LOG.info("Bean instance of HotSwappableTargetSource, setting Minytock proxy as the AOP proxy target.");
        	((HotSwappableTargetSource) bean).swap(minytockProxy);
        	return bean;
        } else {
        	LOG.info("Bean not an instance of HotSwappableTargetSource, returning the Minytock proxy as the bean.");
        	return minytockProxy;
        }
        
    }
    
}
