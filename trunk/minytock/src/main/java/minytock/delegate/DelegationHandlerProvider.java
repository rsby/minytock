package minytock.delegate;

public interface DelegationHandlerProvider {
	<I, T extends I> DelegationHandler<T> getHandler(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException;
	<T> T getReal(T target);
	void remove(Object ... targets);
	void setCache(DelegationHandlerCache cache);
	void clearCache();
}
