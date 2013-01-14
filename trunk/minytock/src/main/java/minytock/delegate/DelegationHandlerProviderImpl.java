package minytock.delegate;

import minytock.util.ProxyUtil;

/**
 * 
 * implementation of the handler provider
 * 
 * @author reesbyars
 *
 */
public class DelegationHandlerProviderImpl implements DelegationHandlerProvider {
	
	private DelegationHandlerCache cache = new DefaultDelegationHandlerCache(); 

	public DelegationHandlerProviderImpl(DelegationHandlerCache cache) {
		this.cache = cache;
	}
	
	/**
	 * {@inheritDoc}
	 */
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

        DelegationHandler<T> handler = cache.get(target);

        if (handler == null) {

            handler = DelegationHandler.Factory.create(target, targetInterface);

            cache.put(handler);

        }

        return handler;
    }
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getReal(T target) {
		
		DelegationHandler<T> handler = cache.get(target);

        if (handler == null) {
        	
        	return target;
        	
        } else {
        	
        	return handler.getRealObject();
        	
        }
        
	}
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public void removeAllDelegates() {
    	cache.clearDelegatesOnly();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeDelegates(Object... targets) {
		for (Object target : targets) {
			
			DelegationHandler<?> handler = cache.get(target);
			
	        if (handler != null) {
	        	handler.remove();
	        }
	        
    	}
	}

}
