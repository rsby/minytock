package minytock.test;

import minytock.Minytock;
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

}