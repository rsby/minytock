package minytock.delegate;

import java.lang.reflect.Method;

public interface InvocationAware {
	
	void notifyInvoked(Method method);

}
