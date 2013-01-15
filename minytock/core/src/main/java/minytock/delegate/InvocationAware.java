package minytock.delegate;

import java.lang.reflect.Method;

/**
 * 
 * @author reesbyars
 *
 */
public interface InvocationAware {
	
	/**
	 * 
	 * @param method
	 */
	void notifyInvoked(Method method);

}
