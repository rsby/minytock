package minytock.delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a high-performance cache that can be used with a deployed artifact, but not for parallel tests.
 * 
 * @author reesbyars
 *
 */
public class DefaultDelegationHandlerCache extends AbstractDelegationHandlerCache {
	
	private final Map<String, DelegationHandler<?>> handlerCache = new ConcurrentHashMap<String, DelegationHandler<?>>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		handlerCache.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, DelegationHandler<?>> getCache() {
		return handlerCache;
	}

}
