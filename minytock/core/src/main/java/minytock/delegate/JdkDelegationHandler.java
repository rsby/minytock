package minytock.delegate;

import minytock.util.ProxyUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * an interceptor that uses the JDK Proxy class to create proxies.  cannot proxy concrete classes.
 * <P>
 * User: reesbyars
 * Date: 9/11/12
 * Time: 10:32 PM
 * <p/>
 * JdkDelegationHandler
 */
public class JdkDelegationHandler<T> extends AbstractDelegationHandler<T> implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JdkDelegationHandler.class);

    private JdkDelegationHandler(T realObject) {
        super(realObject);
    }

    /**
     * intercepts calls and delegates them to any assigned delegate
     *
     * @param o
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
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
        } else if (ProxyUtil.isProxyClass(delegateClass)) { //this is in order to support the edge case of delegating to another delegated bean
        	result = method.invoke(delegate, args);
        } else {
            result = method.invoke(realObject, args);
        }
        return result;
    }

    /**
     * used by the {@link DelegationInterceptor.Factory} to create instances of this interceptor
     *
     * @param target
     * @param targetInterface
     * @param <I>
     * @param <T>
     * @return
     * @throws DelegationException
     */
    @SuppressWarnings("unchecked")
    protected static <T> DelegationHandler<T> create(T target, Class<?> targetInterface) throws DelegationException {
        JdkDelegationHandler<T> interceptor = new JdkDelegationHandler<T>(target);
        if (targetInterface == null || !targetInterface.isInterface()) {
            Class<?> targetClass = target.getClass();
            try {
                LOG.warn("JDK Proxies must rely on interfaces, if you reference the " + targetClass + " concretely instead of by it's interface, then you probably see a bunch of Spring NoSuchBeanDefinitionExceptions below.  In that case, either add CgLib to the classpath or change references to interfaces");
                interceptor.proxy = (T) Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), interceptor);
            } catch (Exception e) {
                if (targetClass.getInterfaces() == null || targetClass.getInterfaces().length == 0) {
                    throw new DelegationException("The target [" + targetClass + "] does not implement an interface that can be proxied.  CgLib must be included in the pom.xml with a test scope in order to proxy this object.");
                }
                throw new DelegationException("Target object could not be proxied.  Message:  " + e.getMessage());
            }
        }  else {
            interceptor.proxy = (T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class<?>[]{targetInterface}, interceptor);
        }
        return interceptor;
    }
}
