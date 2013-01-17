package minytock.delegate;

import minytock.util.ProxyUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * an interceptor that uses a cglib enhancer to create proxies.  cglib can proxy just about anything, including concrete classes.
 * <P>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:50 PM
 * <p/>
 * CgLibDelegationInterceptor
 */
public class CgLibDelegationInterceptor<T> extends AbstractDelegationInterceptor<T> implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(CgLibDelegationInterceptor.class);

    private CgLibDelegationInterceptor(T realObject) {
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
    	Object result = null;
    	Method delegateMethod = this.getDelegateMethod(method);
        if (delegateMethod != null) {
        	try {
        		result = delegateMethod.invoke(delegate, args);
        	} catch (InvocationTargetException e) {
        		throw e.getTargetException();
        	}
        } else if (ProxyUtil.isProxyClass(delegateClass)) { //this is in order to support the edge case of delegating to another delegated bean
        	result = methodProxy.invoke(delegate, args);
        } else {
            result = methodProxy.invoke(realObject, args);
        }
        return result;
    }
    
    static Map<Class<?>, net.sf.cglib.proxy.Factory> factories = new ConcurrentHashMap<Class<?>, net.sf.cglib.proxy.Factory>();

    //net.sf.cglib.proxy.CallbackFilter
    
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
    protected static <T> DelegationInterceptor<T> create(T target, Class<?> targetInterface) {
        CgLibDelegationInterceptor<T> interceptor = new CgLibDelegationInterceptor<T>(target);
        if (targetInterface != null) {
        	net.sf.cglib.proxy.Factory factory = factories.get(targetInterface);
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(targetInterface, interceptor);
        		factories.put(target.getClass(), factory);
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor);
        } else if (!Modifier.isFinal(target.getClass().getModifiers())) {
        	net.sf.cglib.proxy.Factory factory = factories.get(target.getClass());
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(target.getClass(), interceptor);
        		factories.put(target.getClass(), factory);
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor);
        } else {
            LOG.warn(target.getClass() + " is final, cannot proxy directly.  Proxying super class and implementing all interfaces.  Will work for some cases.  Why a final class?!?!");
            net.sf.cglib.proxy.Factory factory = factories.get(target.getClass().getSuperclass());
        	if (factory == null) {
        		factory = (net.sf.cglib.proxy.Factory) Enhancer.create(target.getClass().getSuperclass(), target.getClass().getInterfaces(), interceptor);
        		factories.put(target.getClass().getSuperclass(), factory);
        	}
            interceptor.proxy = (T) factory.newInstance(interceptor);
        }
        return interceptor;
    }

}
