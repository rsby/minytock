package minytock.delegate;

import java.util.HashMap;
import java.util.Map;

public class LocalDelegationCache extends AbstractDelegationCache {
	
	private final ThreadLocal<Map<String, DelegationHandler<?>>> handlerCache = new ThreadLocal<Map<String, DelegationHandler<?>>>();
	
	@Override
	public void clear() {
		handlerCache.remove();
	}
	
	protected Map<String, DelegationHandler<?>> getCache() {
		Map<String, DelegationHandler<?>> localCache = handlerCache.get();
		if (localCache == null) {
			localCache = new HashMap<String, DelegationHandler<?>>();
			handlerCache.set(localCache);
		}
		return localCache;
	}

}
