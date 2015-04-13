/**
 * Copyright 2009-2011 Oracle, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 * Authors:
 * 	  1.1 (2011): Rich Levinson, Prateek Mishra (Oracle)
 * 	  1.0 (2009): Josh Bregman, Rich Levinson, Prateek Mishra (Oracle)
 * Contributor:
 * 	  Rich Levinson (Oracle)
 */
package org.openliberty.openaz.pepapi;

/**
 * The PepException is used to provide additional
 * information to callers of the PepApi when
 * exception conditions occur.
 * <p>
 * PepApi 1.1: now extends RuntimeException in order
 *  that users do not require try/catch blocks
 *  when using PepApi 1.1.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class PepException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 
	 * Create a PepException containing a Throwable that
	 * specifies the cause of this PepException.
	 * @param cause
	 */
	public PepException(Throwable cause) {
        super(cause);
    }

	/**
	 * Create a PepException containing the message provided
	 * and a Throwable containing further information as to
	 * the cause of the PepException.
	 * @param message
	 * @param cause
	 */
    public PepException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a PepException containing the message provided.
     * @param message
     */
    public PepException(String message) {
        super(message);
    }

	public PepException() {super();}
}

