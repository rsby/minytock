package minytock.test;

import minytock.Minytock;
import minytock.delegate.DelegationException;

import java.lang.reflect.Field;

/**
 * 
 * this little guy makes breaking encapsulation fun again.  does not use any caching (so he's a little bit slower than some people)
 * and he only let's you get and set by type, not name (that's by design - he's intended for setting and getting collaborators
 * and services, not a bunch of strings and ints - which is also why he doesn't worry about caching anything in memory).
 * 
 * @author reesbyars
 *
 * @param <T> the class of the field to get or set
 */
public class Spy<T> {

	
    public static <T> Infiltrator set(T value) throws DelegationException {
        return new Infiltrator(value);
    }

    public static <T> Hijacker<T> get(Class<T> classToGet) {
        return new Hijacker<T>(classToGet);
    }

    /**
     * 
     * an infiltrator excels at placing new values inside of its targets
     * 
     * @author reesbyars
     *
     */
    public static class Infiltrator {
    	
        Object value;
        
        Infiltrator(Object value) {
            this.value = value;
        }
        
        /**
         * 
         * @param target the target on which to set the value
         */
        public void on(Object target) {
            Object real = Minytock.real(target);
            for (Field field : real.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(value.getClass())) {
                    field.setAccessible(true);
                    try {
                        field.set(real, value);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException("Could not set field of " + value.getClass() + ":  ", e);
                    }
                }
            }
            throw new RuntimeException("Could not set field:  ", new NoSuchFieldException(real.getClass() + " does not have a field of " + value.getClass()));
        }
    }

    /**
     * 
     * a hijacker excels at stealing field values from its targets
     * 
     * @author reesbyars
     *
     * @param <T>
     */
    public static class Hijacker<T> {
    	
        Class<T> type;
        
        Hijacker(Class<T> type) {
            this.type = type;
        }
        
        /**
         * 
         * @param target the object from which to get the value
         * @return
         */
        @SuppressWarnings("unchecked")
		public T from(Object target) {
            Object real = Minytock.real(target);
            for (Field field : real.getClass().getDeclaredFields()) {
                if (field.getType().equals(type)) {
                    field.setAccessible(true);
                    try {
                        return (T) field.get(real);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not get field of " + type + ":  ", e);
                    }
                }
            }
            throw new RuntimeException("Could not get field:  ", new NoSuchFieldException(real.getClass() + " does not have a field of " + type));
        }
        
    }

}
