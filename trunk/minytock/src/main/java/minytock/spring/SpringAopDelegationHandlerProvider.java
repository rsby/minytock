package minytock.spring;

import org.springframework.aop.framework.Advised;

import minytock.delegate.DelegationException;
import minytock.delegate.DelegationHandler;
import minytock.delegate.DelegationHandlerCache;
import minytock.delegate.DelegationHandlerProviderImpl;

public class SpringAopDelegationHandlerProvider extends DelegationHandlerProviderImpl {

	public SpringAopDelegationHandlerProvider(DelegationHandlerCache cache) {
		super(cache);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
    public <T> DelegationHandler<T> getHandler(T target, Class<?> targetInterface, boolean requireProxy) throws DelegationException {
		try {
			if (target instanceof Advised) {
				return (DelegationHandler<T>) super.getHandler(((Advised) target).getTargetSource().getTarget(), targetInterface, requireProxy);
			} else {
				return super.getHandler(target, targetInterface, requireProxy);
			}
		} catch (Exception e) {	
			throw new DelegationException("Exception obtaining handler", e);
		}
	}

}
