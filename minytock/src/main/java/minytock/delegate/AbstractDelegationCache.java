package minytock.delegate;

import java.util.Map;

/**
 * 
 * a base caching class that makes it simple to create new cache implementations without rewriting the common logic
 * 
 * @author reesbyars
 *
 */
public abstract class AbstractDelegationCache implements DelegationHandlerCache {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(DelegationHandler<?> handler) {
		//associate the handler to work whether given the proxy or the real object
		getCache().put(getCacheKey(handler.getRealObject()), handler);
		getCache().put(getCacheKey(handler.getProxy()), handler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> DelegationHandler<T> get(T key) {
		return (DelegationHandler<T>) getCache().get(getCacheKey(key));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> DelegationHandler<T> remove(T key) {
		DelegationHandler<T> handler = get(key);
		getCache().remove(getCacheKey(handler.getProxy()));
		getCache().remove(getCacheKey(handler.getRealObject()));
		return handler;
	}
	
	/**
	 * @return a map for storing the handlers
	 */
	protected abstract Map<String, DelegationHandler<?>> getCache();
	
	/**
	 * creates a unique key for safely storing and retrieving the handlers
	 * @param target the real or proxy object
	 * @return a unique string that can be reliably recreated for this object
	 */
	protected static String getCacheKey(Object target) {
    	return target.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(target));
    }

}
