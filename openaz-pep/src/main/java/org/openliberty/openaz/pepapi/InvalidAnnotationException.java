package org.openliberty.openaz.pepapi;

/**
 * RuntimeException thrown when a registered handler class does not contain one of the 
 * required annotations - <code>@MatchAnyObligation</code>, <code>@MatchAllObligationAttributes</code>.
 *   
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
@SuppressWarnings("serial")
public class InvalidAnnotationException extends RuntimeException {

	public InvalidAnnotationException() {
		super();
	}

	public InvalidAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAnnotationException(String message) {
		super(message);
	}

	public InvalidAnnotationException(Throwable cause) {
		super(cause);
	}

}
