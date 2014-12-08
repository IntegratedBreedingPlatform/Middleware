
package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Program;

public interface ProgramDataManager {
	
	public Program addProgram(final Program program) throws MiddlewareQueryException;
}
