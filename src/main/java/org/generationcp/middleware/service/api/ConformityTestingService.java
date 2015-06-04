
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.exceptions.ConformityException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
public interface ConformityTestingService {

	/**
	 * This method is used to check the given input for conformity.
	 *
	 * @param input the object representing the input to be tested
	 * @return a map containing GIDs as a key, and a map of marker names with their values as the value. These represents the GID / marker
	 *         entries that did not pass the conformity testing
	 * @throws MiddlewareQueryException
	 * @throws ConformityException
	 */
	public java.util.Map<Integer, java.util.Map<String, String>> testConformity(UploadInput input) throws MiddlewareQueryException,
			ConformityException;
}
