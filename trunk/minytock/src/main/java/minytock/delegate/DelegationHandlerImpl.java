package minytock.delegate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import minytock.util.ProxyUtil;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 9:37 PM
 * <p/>
 * DelegationHandlerImpl
 */
public class DelegationHandlerImpl<T> implements DelegationHandler<T> {

    protected static final Map<String, DelegationHandler<?>> handlerCache = new ConcurrentHashMap<String, DelegationHandler<?>>();

    protected DelegationInterceptor<T> interceptor;

    private DelegationHandlerImpl() {}

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

    public static <I, T extends I> T getProxy(T target, Class<I> targetInterface) throws DelegationException {
        return resolveHandler(target, targetInterface).getProxy();
    }

    public static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface, boolean requireProxy) throws DelegationException {

        if (requireProxy && !ProxyUtil.isProxyClass(target.getClass())) {
        	throw new DelegationException("The target is not a proxy.  A proxy must first be obtained using prepare(target) and then passed as the target.");
        }

        return resolveHandler(target, targetInterface);

    }

    public static void cleanup() {
        handlerCache.clear();
    }

    private static <I, T extends I> DelegationHandler<T> resolveHandler(T target, Class<I> targetInterface) throws DelegationException {

        if (target == null) {
            throw new DelegationException("Target cannot be null.  If trying to create an empty proxy of an interface, use the EmptyMockFactory or the Minytock.newEmptyMock() method.");
        }

        @SuppressWarnings("unchecked")
        DelegationHandlerImpl<T> handler = (DelegationHandlerImpl<T>) handlerCache.get(getCacheKey(target));

        if (handler == null) {

            handler = new DelegationHandlerImpl<T>();

            handler.interceptor = DelegationInterceptor.Factory.create(target, targetInterface);

            //associate the handler to work whether given the proxy or the real object
            handlerCache.put(getCacheKey(handler.getProxy()), handler);
            handlerCache.put(getCacheKey(target), handler);

        }

        return handler;
    }
    
    private static String getCacheKey(Object target) {
    	return target.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(target));
    }

}
