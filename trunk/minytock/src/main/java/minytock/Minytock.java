package minytock;

import minytock.delegate.*;
import minytock.spy.Spy;
import minytock.test.EmptyMockFactory;
import minytock.test.Ready;
import minytock.test.Verifiable;

/**
 * A sort of "static interface" to the Minytock delegation framework that can be imported into test classes
 * for simple use of the framework.
 * 
 * <p/>
 * 
 * User: reesbyars
 * Date: 9/11/12
 * Time: 6:54 PM
 * <p/>
 * Minytock
 */
public class Minytock {
	
	private static final DelegationHandlerProvider PROVIDER = new DelegationHandlerProviderImpl(new DefaultDelegationHandlerCache());

    /**
     * get a delegatable proxy of the given object.  this can be done automatically for all autowired beans
     * by using the {@link minytock.spring.MinytockPostProcessor MinytockPostProcessor} and for class-level
     * fields in unit tests using the {@link Ready @Ready} annotation.
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T prepare(T target) {
    	return prepare(target, null);
    }

    /**
     * get a delegatable proxy of the given object.
     *
     * @param target
     * @param targetInterface
     * @param <I>
     * @param <T>
     * @return
     */
    private static <I, T extends I> T prepare(T target, Class<I> targetInterface) {
    	try {
    		return PROVIDER.getHandler(target, targetInterface, false).getProxy();
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * returns the delegation handler for the given proxy which can then be used to assign a delegate in the form:
     * <pre>
     *
     *     delegate(myObject).to(myMock);
     *
     * </pre>
     * after which calls to <i>myObject</i> will be delegated to <i>myMock</i>.  it is probably most useful for using anonymous classes
     * in the form:
     * <pre>
     *
     *      delegate(this.metricService).to(new Object() {
     *          void saveMetric(BusinessMetrics metric) {
     *              System.out.println("hello proxied minytock world!");
     *          }
     *      });
     *
     *      this.metricService.saveMetric(mockMetric);
     *
     * </pre>
     * which would just print "hello proxied minytock world!".
     * 
     * @param target
     * @param <T>
     * @return
     */
    public static <T> DelegationHandler<T> delegate(T target) {
    	return delegate(target, null);
    }

    /**
     * remove the current delegate for the given proxies
     *
     */
    public static void remove(Object ... targets) {
    	getProvider().remove(targets);
    }

    /**
     * gets the real object from behind a proxy
     *
     * @param target
     * @param <T>
     * @return the real object behind the given proxy.  if the given object is not a proxy, it just returns that object
     */
    public static <T> T real(T target) {
    	return getProvider().getReal(target);
    }

    private static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface) {
    	return delegate(target, targetInterface, true); //true to enforce good practice
    }

    private static <I, T extends I> DelegationHandler<T> delegate(T target, Class<I> targetInterface, boolean requireProxy) {
    	try {
    		return PROVIDER.getHandler(target, targetInterface, requireProxy);
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * cleans up delegation resources to reduce memory imprint.  automatically called from the minytock test runners.
     */
    public static void clearAll() {
    	PROVIDER.clearCache();
    }

    public static void verify(Object ... proxies) {
    	for (Object proxy : proxies) {
    		Object delegate = delegate(proxy).getDelegate();
            if (delegate instanceof Verifiable) {
                ((Verifiable) delegate).verify();
            }
    	}
    }

    public static <T> T newEmptyMock(Class<T> classToMock) {
        return EmptyMockFactory.create(classToMock);
    }

    public static <T> Spy.Infiltrator set(T value) {
    	try {
    		return Spy.set(value);
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

    public static <T> Spy.Hijacker<T> get(Class<T> classToGet) {
        return Spy.get(classToGet);
    }
    
    public static DelegationHandlerProvider getProvider() {
    	return PROVIDER;
    }

}