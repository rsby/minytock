package minytock.delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDelegationCache extends AbstractDelegationCache {
	
	private final Map<String, DelegationHandler<?>> handlerCache = new ConcurrentHashMap<String, DelegationHandler<?>>();
	
	@Override
	public void clear() {
		handlerCache.clear();
	}

	@Override
	protected Map<String, DelegationHandler<?>> getCache() {
		return handlerCache;
	}

}
