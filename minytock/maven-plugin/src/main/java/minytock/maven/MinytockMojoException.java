package minytock.maven;

public class MinytockMojoException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public MinytockMojoException(String message) {
		super(message);
	}
	
	public MinytockMojoException(String message, Exception root) {
		super(message, root);
	}

}
