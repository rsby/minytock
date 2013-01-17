package minytock.spring;

import minytock.Minytock;
import minytock.delegate.DelegationException;
import minytock.delegate.DelegationHandlerCache;
import minytock.spy.Spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.HotSwappableTargetSource;

/**
 * Pretty the same as the {@link DelegationPostProcessor}, but made to work with Spring AOP proxies.
 * <p/>
 * MinytockAopPostProcessor
 */
public class MinytockAopPostProcessor extends DelegationPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MinytockAopPostProcessor.class);
    
    public MinytockAopPostProcessor() {
    	Minytock.provider = new SpringAopDelegationHandlerProvider(Spy.get(DelegationHandlerCache.class).from(Minytock.provider));
    }
    
    @Override
    protected Class<?> getTargetClass(Object bean) {
    	return AopUtils.getTargetClass(bean);
    }
    
    @Override
    protected Object prepare(Object bean, Class<?> targetClass) throws DelegationException {
    	
    	Object minytockProxy = Minytock.provider.getHandler(bean, targetClass, false).getProxy();
        
        //simply swap if bean is hot-swappable
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