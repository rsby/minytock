package minytock.delegate;

public interface DelegationHandlerCache {
	
	void put(Object key, DelegationHandler<?> handler);
	DelegationHandler<?> get(Object key);
	void clear();

}
