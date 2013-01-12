package minytock.delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentDelegationCache implements DelegationHandlerCache {
	
	private final Map<String, DelegationHandler<?>> handlerCache = new ConcurrentHashMap<String, DelegationHandler<?>>();
	
	@Override
	public void put(Object key, DelegationHandler<?> handler) {
		//associate the handler to work whether given the proxy or the real object
		handlerCache.put(getCacheKey(key), handler);
		handlerCache.put(getCacheKey(handler.getProxy()), handler);
	}
	
	@Override
	public DelegationHandler<?> get(Object key) {
		return handlerCache.get(getCacheKey(key));
	}
	
	@Override
	public void clear() {
		handlerCache.clear();
	}
	
	//this is a bit of a bottleneck
	private static String getCacheKey(Object target) {
    	return target.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(target));
    }

}
