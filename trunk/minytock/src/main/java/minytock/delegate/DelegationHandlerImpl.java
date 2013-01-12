package minytock.delegate;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:37 PM
 * <p/>
 * DelegationHandlerImpl
 */
public class DelegationHandlerImpl<T> implements DelegationHandler<T> {

    protected DelegationInterceptor<T> interceptor;

    protected DelegationHandlerImpl(DelegationInterceptor<T> interceptor) {
		this.interceptor = interceptor;
	}

	@Override
    public T to(Object delegate) {
        this.interceptor.setDelegate(delegate);
        return this.interceptor.getProxy();
    }

    @Override
    public T remove() {
        this.interceptor.removeDelegate();
        return this.interceptor.getProxy();
    }

    @Override
    public T getProxy() {
        return this.interceptor.getProxy();
    }

    @Override
    public T getRealObject() {
        return this.interceptor.getRealObject();
    }

    @Override
    public Object getDelegate() {
        return this.interceptor.getDelegate();
    }

}
