package minytock.delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a high-performance cache that can be used with a deployed artifact, but not for parallel tests.
 * 
 * @author reesbyars
 *
 */
public class FastDelegationHandlerCache implements DelegationHandlerCache {
	
	private final Map<Class<?>, DelegationHandler<?>> handlerCache = new ConcurrentHashMap<Class<?>, DelegationHandler<?>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(DelegationHandler<?> handler) {
		//associate the handler to work whether given the proxy or the real object
		handlerCache.put(handler.getRealObject().getClass(), handler);
		handlerCache.put(handler.getProxy().getClass(), handler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> DelegationHandler<T> get(T key) {
		return (DelegationHandler<T>) handlerCache.get(key.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> DelegationHandler<T> remove(T key) {
		DelegationHandler<T> handler = get(key);
		handlerCache.remove(handler.getProxy().getClass());
		handlerCache.remove(handler.getRealObject().getClass());
		return handler;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearDelegatesOnly() {
		for (DelegationHandler<?> handler : this.handlerCache.values()) {
			handler.remove();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		handlerCache.clear();
	}

}
