package minytock.delegate;

/**
 * 
 * The primary <i>internal</i> interface to the delegation framework
 * 
 * @author reesbyars
 *
 */
public interface DelegationHandlerProvider {
	
	/**
	 * 
	 * @param target
	 * @param targetInterface
	 * @param requireProxy
	 * @return
	 * @throws DelegationException
	 */
	<I, T extends I> DelegationHandler<T> getHandler(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException;
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	<T> T getReal(T target);
	
	/**
	 * 
	 * @param targets
	 */
	void removeDelegates(Object ... targets);
	
	/**
	 * 
	 */
	void removeAllDelegates();
}
