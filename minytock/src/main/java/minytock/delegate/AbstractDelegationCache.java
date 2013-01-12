package minytock.delegate;

import java.util.Map;

public abstract class AbstractDelegationCache implements DelegationHandlerCache {
	
	@Override
	public void put(Object key, DelegationHandler<?> handler) {
		//associate the handler to work whether given the proxy or the real object
		getCache().put(getCacheKey(key), handler);
		getCache().put(getCacheKey(handler.getProxy()), handler);
	}
	
	@Override
	public DelegationHandler<?> get(Object key) {
		return getCache().get(getCacheKey(key));
	}
	
	protected abstract Map<String, DelegationHandler<?>> getCache();
	
	//this is a bit of a bottleneck
	protected static String getCacheKey(Object target) {
    	return target.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(target));
    }

}
