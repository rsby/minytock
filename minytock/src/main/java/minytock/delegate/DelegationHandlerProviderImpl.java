package minytock.delegate;

import minytock.util.ProxyUtil;

public class DelegationHandlerProviderImpl implements DelegationHandlerProvider {
	
	private DelegationHandlerCache cache = new DefaultDelegationCache(); 

	public DelegationHandlerProviderImpl(DelegationHandlerCache cache) {
		this.cache = cache;
	}
	
	@Override
    public <I, T extends I> DelegationHandler<T> getHandler(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException {

        if (requireProxy && !ProxyUtil.isProxyClass(target.getClass())) {
        	throw new DelegationException("The target is not a proxy.  A proxy must first be obtained using prepare(target) and then passed as the target.");
        }

        return getHandler(target, targetInterface);

    }

    private <I, T extends I> DelegationHandler<T> getHandler(T target, Class<I> targetInterface) throws DelegationException {

        if (target == null) {
            throw new DelegationException("Target cannot be null.  If trying to create an empty proxy of an interface, use the EmptyMockFactory or the Minytock.newEmptyMock() method.");
        }

        @SuppressWarnings("unchecked")
        DelegationHandler<T> handler = (DelegationHandler<T>) cache.get(target);

        if (handler == null) {

            handler = DelegationHandler.Factory.create(target, targetInterface);

            cache.put(target, handler);

        }

        return handler;
    }
    
    @Override
    public void clearCache() {
    	cache.clear();
    }

	@Override
	public void setCache(DelegationHandlerCache cache) {
		this.cache = cache;
	}

}