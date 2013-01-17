package minytock.delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * a cache implementation that uses thread-local caching to support parallel testing
 * 
 * @author reesbyars
 *
 */
public class ThreadLocalDelegationHandlerCache extends AbstractDelegationHandlerCache {
	
	private final ThreadLocal<Map<String, DelegationHandler<?>>> handlerCache = new ThreadLocal<Map<String, DelegationHandler<?>>>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		handlerCache.remove();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, DelegationHandler<?>> getCache() {
		Map<String, DelegationHandler<?>> localCache = handlerCache.get();
		if (localCache == null) {
			localCache = new HashMap<String, DelegationHandler<?>>();
			handlerCache.set(localCache);
		}
		return localCache;
	}

}
