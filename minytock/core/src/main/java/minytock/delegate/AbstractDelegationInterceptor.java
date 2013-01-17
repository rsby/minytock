package minytock.delegate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * an abstract delegation interceptor that does everything but intercept
 * 
 * @author reesbyars
 *
 * @param <T> the type of the target object and the proxy
 */
public abstract class AbstractDelegationInterceptor<T> implements DelegationInterceptor<T> {

    protected Map<Method, Method> delegateMethodCache = Collections.emptyMap();
    protected Class<?> delegateClass;
    protected final Class<?> realObjectClass;
    protected final T realObject;
    protected Object delegate;
    protected T proxy;

    protected AbstractDelegationInterceptor(T realObject) {
        this.realObject = realObject;
        this.realObjectClass = realObject.getClass(); 
        this.setDelegate(realObject);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public void setDelegate(Object delegate) {
    	
    	if (delegate.equals(this.delegate)) {
        	this.delegate = this.realObject;  //here, if someone tries delegate(bean).to(bean) we set the real object as the delegate to avoid stackoverflow
        	return;
    	}
    	
        Class<?> newDelegateClass = delegate.getClass();
        
        if (newDelegateClass != this.delegateClass) {
            this.delegateClass = newDelegateClass;
            this.delegateMethodCache.clear();
            this.delegateMethodCache = new HashMap<Method, Method>();
        }
        
        if (delegate instanceof TargetAware) {
        	((TargetAware<T>) delegate).setTarget(realObject);
        }
        
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T removeDelegate() {
        this.delegate = this.realObject;
        this.delegateClass = realObjectClass;
        this.delegateMethodCache.clear();
        this.delegateMethodCache = Collections.emptyMap();
        return this.proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getProxy() {
        return this.proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getRealObject() {
        return this.realObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDelegate() {
        return this.delegate;
    }

    protected Method getDelegateMethod(Method realMethod) {

        if (this.delegateClass == this.realObjectClass) {
            return null;
        }

        Method method = this.delegateMethodCache.get(realMethod);
        if (!this.delegateMethodCache.containsKey(realMethod)) {
        	method = realMethod;
            String realMethodName = realMethod.getName();
            Class<?>[] realMethodParameterTypes = realMethod.getParameterTypes();
            try {
                method = this.delegateClass.getMethod(realMethodName, realMethodParameterTypes);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
            	try {
					method = this.delegateClass.getDeclaredMethod(realMethodName, realMethodParameterTypes);
					method.setAccessible(true);
				} catch (Exception ee) {
					//do nuthin
				} 
            }
            this.delegateMethodCache.put(realMethod, method);
        }
        if (this.delegate instanceof InvocationAware) {
            ((InvocationAware) this.delegate).notifyInvoked(method);
        }
        return method;
    }

}
