package minytock.delegate;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * an interceptor that uses a cglib enhancer to create proxies.
 * <P>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:50 PM
 * <p/>
 * CgLibDelegationHandler
 */
public class CgLibDelegationHandler<T> extends AbstractDelegationHandler<T> implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(CgLibDelegationHandler.class);

    private CgLibDelegationHandler(T realObject) {
        super(realObject);
    }

    /**
     * intercepts calls and delegates them to any assigned delegate
     *
     * @param o
     * @param method
     * @param args
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    	if (fullDelegate) { //if the delegate is a full instance of the type, then we just use it and save some time
    		return methodProxy.invoke(delegate, args);
    	}
    	Object result = null;
    	Method delegateMethod = this.delegateMethodCache.get(method.hashCode());
        if (delegateMethod != null) {
        	try {
        		if (this.delegate instanceof InvocationAware) {
                    ((InvocationAware) this.delegate).notifyInvoked(delegateMethod);
                }
        		result = delegateMethod.invoke(delegate, args);
        	} catch (InvocationTargetException e) {
        		throw e.getTargetException();
        	}
        } else {
            result = methodProxy.invoke(realObject, args);
        }
        return result;
    }
    
    private static final Map<Class<?>, net.sf.cglib.proxy.Factory> FACTORIES = new HashMap<Class<?>, net.sf.cglib.proxy.Factory>();
    
    /**
     * used by the {@link DelegationInterceptor.Factory} to create instances of this interceptor
     *
     * @param target
     * @param targetInterface
     * @param <I>
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    protected static <T> DelegationHandler<T> create(T target, Class<?> targetInterface) {
        CgLibDelegationHandler<T> interceptor = new CgLibDelegationHandler<T>(target);
        if (targetInterface != null) {
        	net.sf.cglib.proxy.Factory factory = FACTORIES.get(targetInterface);
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(targetInterface, interceptor);
        		synchronized (FACTORIES) {
        			FACTORIES.put(target.getClass(), factory);
        		}
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor);
        } else if (!Modifier.isFinal(target.getClass().getModifiers())) {
        	net.sf.cglib.proxy.Factory factory = FACTORIES.get(target.getClass());
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(target.getClass(), interceptor);
        		synchronized (FACTORIES) {
        			FACTORIES.put(target.getClass(), factory);
        		}
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor); 
        } else {
            LOG.warn(target.getClass() + " is final, cannot proxy directly.  Proxying super class and implementing all interfaces.  Will work for some cases.  Why a final class?!?!");
            net.sf.cglib.proxy.Factory factory = FACTORIES.get(target.getClass().getSuperclass());
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(target.getClass().getSuperclass(), target.getClass().getInterfaces(), interceptor);
        		synchronized (FACTORIES) {
        			FACTORIES.put(target.getClass().getSuperclass(), factory);	
        		}
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor);
        }
        return interceptor;
    }

}
