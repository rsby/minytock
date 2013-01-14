package minytock.delegate;

/**
 * a cache for storing {@link DelegationHandler DelegationHandlers}.
 * 
 * @author reesbyars
 *
 */
public interface DelegationHandlerCache {
	
	/**
	 * @param handler a handler that will be cached and can be retrieved via {@link #get(Object)}
	 */
	void put(DelegationHandler<?> handler);
	
	/**
	 * @param key either the "real" object or its proxy
	 * @return the handler for the given key
	 */
	<T> DelegationHandler<T> get(T key);
	
	/**
	 * removes the handler for the given key.  this effectively "unprepares" the object for delegation.
	 * compare to the similarly named but very different {@link DelegationHandler#remove()} which just
	 * removes the current delegate, but leaves the handler in place so that the object can still be 
	 * delegated.
	 * 
	 * @param key either the "real" object or its proxy
	 * @return the handler being removed
	 */
	<T> DelegationHandler<T> remove(T key);
	
	/**
	 * clears all handlers from the cache
	 */
	void clear();

}
