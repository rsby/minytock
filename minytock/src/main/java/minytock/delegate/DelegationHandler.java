package minytock.delegate;

/**
 * The primary interface around which the delegation framework is built.  Exposes useful methods
 * while allowing implementations to hide their details.
 *
 * User: reesbyars
 * Date: 9/10/12
 * Time: 10:55 PM
 * <p/>
 * DelegationHandler
 */
public interface DelegationHandler<T> {

    /**
     * used to assign the delegate
     * @param delegate the object that will take over execution of the real object's methods
     * @return the proxy that will delegate methods calls to the delegate
     */
    public T to(Object delegate);

    /**
     * removes any current delegate
     * @return the proxy
     */
    public T remove();

    /**
     *
     * @return a delegating proxy that can be used to delegate method calls
     */
    public T getProxy();

    /**
     *
     * @return the real object that the proxy is delegating on behalf of
     */
    public T getRealObject();

    /**
     * 
     * @return
     */
    public Object getDelegate();
    
    /**
     * 
     * @author reesbyars
     *
     * @param <T>
     */
    static class Factory<T> {
    
    	/**
    	 * 
    	 * @param target
    	 * @param targetInterface
    	 * @return
    	 * @throws DelegationException
    	 */
    	public static <I, T extends I> DelegationHandler<T> create(T target, Class<I> targetInterface) throws DelegationException {
    		return new DelegationHandlerImpl<T>(DelegationInterceptor.Factory.create(target, targetInterface));
    	}
    }


}
