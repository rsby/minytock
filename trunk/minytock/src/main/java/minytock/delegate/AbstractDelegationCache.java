package minytock.delegate;

import java.util.Map;

public abstract class AbstractDelegationCache implements DelegationHandlerCache {
	
	@Override
	public void put(DelegationHandler<?> handler) {
		//associate the handler to work whether given the proxy or the real object
		getCache().put(getCacheKey(handler.getRealObject()), handler);
		getCache().put(getCacheKey(handler.getProxy()), handler);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> DelegationHandler<T> get(T key) {
		return (DelegationHandler<T>) getCache().get(getCacheKey(key));
	}
	
	@Override
	public <T> DelegationHandler<T> remove(T key) {
		DelegationHandler<T> handler = get(key);
		getCache().remove(getCacheKey(handler.getProxy()));
		getCache().remove(getCacheKey(handler.getRealObject()));
		return handler;
	}
	
	protected abstract Map<String, DelegationHandler<?>> getCache();
	
	//this is a bit of a bottleneck
	protected static String getCacheKey(Object target) {
    	return target.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(target));
    }

}
