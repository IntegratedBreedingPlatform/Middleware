package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.auditory.AuditoryException;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;

public interface AuditorDataManager {

	Bibref save(Bibref ref) throws AuditoryException;

	UserDefinedField getBibrefType();
}
