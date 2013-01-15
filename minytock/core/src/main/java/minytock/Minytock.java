package minytock;

import minytock.delegate.*;
import minytock.spy.Spy;

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
	
	public static DelegationHandlerProvider provider = new DelegationHandlerProviderImpl(new DefaultDelegationHandlerCache());

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
    	try {
    		return provider.getHandler(target, null, false).getProxy();
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
    	try {
    		return provider.getHandler(target, null, true);
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * gets the real object from behind a proxy
     *
     * @param target
     * @param <T>
     * @return the real object behind the given proxy.  if the given object is not a proxy, it just returns that object
     */
    public static <T> T real(T target) {
    	return provider.getReal(target);
    }
    
    /**
     * remove the current delegate for the given proxies
     *
     */
    public static void remove(Object ... targets) {
    	provider.removeDelegates(targets);
    }

    /**
     * cleans up delegation resources to reduce memory imprint.  automatically called from the minytock test runners.
     */
    public static void clearDelegates() {
    	provider.removeAllDelegates();
    }

    /**
     * @param value a value to set
     * @return a {@link Spy.Infiltrator} that can set the given value on a target
     */
    public static <T> Spy.Infiltrator set(T value) {
    	try {
    		return Spy.set(value);
    	} catch (DelegationException e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * @param classToGet the class of the field to retrieve
     * @return a {@link Spy.Hijacker} that can retrieve the field from a target
     */
    public static <T> Spy.Hijacker<T> get(Class<T> classToGet) {
        return Spy.get(classToGet);
    }

}