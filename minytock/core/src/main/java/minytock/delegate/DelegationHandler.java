package minytock.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sort of bridge interface that hides the interceptor details from users and the 
 * rest of the framework.
 *
 * User: reesbyars
 * Date: 9/10/12
 * Time: 10:55 PM
 * <p/>
 * DelegationHandler
 */
public interface DelegationHandler<T> {

    /**
     * used to assign the delegate
     * @param delegate the object that will take over execution of the real object's methods
     * @return the proxy that will delegate methods calls to the delegate
     */
    public T to(Object delegate);

    /**
     * removes any current delegate
     * @return the proxy
     */
    public T remove();

    /**
     *
     * @return a delegating proxy that can be used to delegate method calls
     */
    public T getProxy();

    /**
     *
     * @return the real object that the proxy is delegating on behalf of
     */
    public T getRealObject();

    /**
     * 
     * @return the current delegate, or the target/real object if there is no current delegate
     */
    public Object getDelegate();
    
    /**
     * a static factory that can be used to create handlers
     * 
     * @author reesbyars
     *
     * @param <T>
     */
    static class Factory {

        static final Logger LOG = LoggerFactory.getLogger(Factory.class);

        static boolean useCgLib;

        static {
            try {
                Class.forName("net.sf.cglib.proxy.Enhancer", false, DelegationHandler.class.getClassLoader());
                LOG.info("CgLib was found on the classpath.  CgLib will be used for proxy creation.");
                useCgLib = true;
            } catch (ClassNotFoundException e) {
                LOG.warn("Did not find CgLib on the classpath.  Using native JDK Proxy for proxy creation.  Will work fine for most cases, but will not be able to proxy concrete classes that do not implement interfaces.");
                useCgLib = false;
            }
        }

        protected static <T> DelegationHandler<T> create(T target, Class<?> targetInterface) throws DelegationException {
            if (useCgLib) {
            	return CgLibDelegationHandler.create(target, targetInterface);
            } else {
                return JdkDelegationHandler.create(target, targetInterface);
            }
        }
    }


}
