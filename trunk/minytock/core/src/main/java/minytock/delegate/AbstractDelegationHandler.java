package minytock.delegate;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * an abstract delegation handler
 * 
 * @author reesbyars
 *
 * @param <T> the type of the target object and the proxy
 */
public abstract class AbstractDelegationHandler<T> implements DelegationHandler<T> {

    protected Map<Integer, Method> delegateMethodCache = Collections.emptyMap();
    protected Class<?> delegateClass;
    protected final Class<?> realObjectClass;
    protected final T realObject;
    protected Object delegate;
    protected T proxy;
    protected boolean fullDelegate;

    protected AbstractDelegationHandler(T realObject) {
        this.realObject = realObject;
        this.realObjectClass = realObject.getClass(); 
        this.setDelegate(realObject);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public T to(Object delegate) {
        setDelegate(delegate);
        return proxy;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public T remove() {
    	this.delegate = this.realObject;
        this.delegateClass = realObjectClass;
        this.delegateMethodCache = Collections.emptyMap();
        return this.proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getProxy() {
        return proxy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getRealObject() {
        return realObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDelegate() {
        return delegate;
    }
    
    @SuppressWarnings("unchecked")
    protected final void setDelegate(Object delegate) {
    	
    	if (delegate.equals(this.delegate)) {
        	this.delegate = this.realObject;  //here, if someone tries delegate(bean).to(bean) we set the real object as the delegate to avoid stackoverflow
        	return;
    	}
    	
        Class<?> newDelegateClass = delegate.getClass();
        
        if (newDelegateClass != this.delegateClass) {
            this.delegateClass = newDelegateClass;
            fullDelegate = realObjectClass.isAssignableFrom(delegateClass);
            if (!fullDelegate) {
            	this.cacheDelegateMethods(); //no need to cash them if we can cast the delegate to the real object type
            }
        }
        
        if (delegate instanceof TargetAware) {
        	((TargetAware<T>) delegate).setTarget(realObject);
        }
        
        this.delegate = delegate;
    }
    
    protected final void cacheDelegateMethods() {
    	delegateMethodCache = new HashMap<Integer, Method>();
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
					method = null;
				} 
            }
            delegateMethodCache.put(realMethod.hashCode(), method);
		}
    }

}
