package minytock.test;

import minytock.Minytock;
import minytock.delegate.DelegationException;
import minytock.test.EmptyMockFactory;
import minytock.test.Verifiable;

/**
 * A sort of "static interface" to the MinytockIntegrationTest delegation framework that can be imported into test classes
 * for simple use of the framework.
 * 
 * <p/>
 * 
 * User: reesbyars
 * Date: 9/11/12
 * Time: 6:54 PM
 * <p/>
 * MinytockIntegrationTest
 */
public class MinytockTest extends Minytock {

    /**
     * @param targets mock objects (ostensibly) to be verified according to their @Verify annotations
     */
    public static void verify(Object ... targets) {
    	for (Object proxy : targets) {
    		Object delegate = delegate(proxy).getDelegate();
            if (delegate instanceof Verifiable) {
                ((Verifiable) delegate).verify();
            }
    	}
    }

    /**
     * @param classToMock
     * @return a mock implementation of the given class that is ready for delegation, if desired
     */
    public static <T> T newEmptyMock(Class<T> classToMock) {
        return EmptyMockFactory.create(classToMock);
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