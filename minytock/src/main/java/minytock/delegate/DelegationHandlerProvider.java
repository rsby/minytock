package minytock.delegate;

public interface DelegationHandlerProvider {
	<I, T extends I> DelegationHandler<T> getHandler(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException;
	void setCache(DelegationHandlerCache cache);
	void clearCache();
}
