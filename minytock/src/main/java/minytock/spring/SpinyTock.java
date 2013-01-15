package minytock.spring;

import minytock.Minytock;
import minytock.delegate.DelegationException;
import minytock.delegate.DelegationHandler;
import minytock.delegate.DelegationHandlerCache;
import minytock.spy.Spy;

public class SpinyTock extends Minytock {
	
	public static SpringDelegationHandlerProvider springProvider = new SpringDelegationHandlerProvider(Spy.get(DelegationHandlerCache.class).from(provider)); 
	
	public static <T> DelegationHandler<T> delegateSpringProxy(T target) {
    	try {
    		return springProvider.getHandler(target, null, true);
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

}
