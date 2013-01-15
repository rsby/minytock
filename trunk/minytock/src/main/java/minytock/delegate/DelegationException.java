package minytock.delegate;

/**
 * User: reesbyars
 * Date: 9/11/12
 * Time: 10:43 PM
 * <p/>
 * DelegationException
 */
public class DelegationException extends Exception {

	private static final long serialVersionUID = 1L;

	public DelegationException(String message) {
        super(message);
    }
	
	public DelegationException(String message, Exception e) {
        super(message, e);
    }
}
