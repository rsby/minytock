package minytock.delegate;

public interface DelegationHandlerCache {
	
	void put(DelegationHandler<?> handler);
	<T> DelegationHandler<T> get(T key);
	<T> DelegationHandler<T> remove(T key);
	void clear();

}
