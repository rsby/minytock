package minytock.spring;

import org.springframework.aop.framework.Advised;

import minytock.delegate.DelegationException;
import minytock.delegate.DelegationHandler;
import minytock.delegate.DelegationHandlerCache;
import minytock.delegate.DelegationHandlerProviderImpl;

public class SpringDelegationHandlerProvider extends DelegationHandlerProviderImpl {

	public SpringDelegationHandlerProvider(DelegationHandlerCache cache) {
		super(cache);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
    public <T> DelegationHandler<T> getHandler(T target, Class<?> targetInterface, boolean requireProxy) throws DelegationException {
		try {
			return (DelegationHandler<T>) super.getHandler(((Advised) target).getTargetSource().getTarget(), targetInterface, requireProxy);
		} catch (Exception e) {
			return super.getHandler(target, targetInterface, requireProxy);
		}
	}

}
