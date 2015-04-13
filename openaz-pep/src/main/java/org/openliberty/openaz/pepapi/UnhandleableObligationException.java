package org.openliberty.openaz.pepapi;

/**
 * Runtime Exception thrown when the framework cannot find a registered handler to deal with the obligation.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 * 
 */
@SuppressWarnings("serial")
public class UnhandleableObligationException extends RuntimeException {
	
	public UnhandleableObligationException() {
		super();
	}

	public UnhandleableObligationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnhandleableObligationException(String message) {
		super(message);
	}

	public UnhandleableObligationException(Throwable cause) {
		super(cause);
	}
}
