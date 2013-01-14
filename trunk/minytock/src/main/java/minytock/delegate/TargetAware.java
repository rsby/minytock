package minytock.delegate;

/**
 * 
 * @author reesbyars
 *
 * @param <T>
 */
public interface TargetAware<T> {
	
	/**
	 * 
	 * @param target
	 */
	void setTarget(T target);
}
