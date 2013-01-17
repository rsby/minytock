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
            this.delegateMethodCache = new HashMap<Method, Method>();
            this.cacheDelegateMethods();
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
    
    protected void cacheDelegateMethods() {
		for (Method realMethod : realObjectClass.getMethods()) {
			Method method = null;
            String realMethodName = realMethod.getName();
            Class<?>[] realMethodParameterTypes = realMethod.getParameterTypes();
            try {
                method = delegateClass.getMethod(realMethodName, realMethodParameterTypes);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
            	try {
					method = delegateClass.getDeclaredMethod(realMethodName, realMethodParameterTypes);
					method.setAccessible(true);
				} catch (Exception ee) {
					//do nuthin
				} 
            }
            delegateMethodCache.put(realMethod, method);
		}
    }

}
