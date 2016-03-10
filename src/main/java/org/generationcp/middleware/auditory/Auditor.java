package org.generationcp.middleware.auditory;

import org.generationcp.middleware.pojos.Bibref;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 * This component behaves as an Auditor.
 * Each instance of the object takes care of one and only one Auditory that might audit many elements in the domain model.
* */
@Component
@Scope("prototype")
public class Auditor {

	public static final String MISSUSE_OF_AUDITOR_COMPONENT = "Misuse of auditor component";
	public static final String COULD_NOT_START_AUDITORY = "Could not start auditory";
	public static final String INVALID_INPUT_DATA = "Invalid input data";

	AuditorManager manager;

	Bibref ref;

	public Auditor(AuditorManager manager) {
		this.manager = manager;
	}


	public Auditory startAuditory(String username, String filename) throws AuditoryException {
		if (username==null || username.isEmpty() || filename==null || filename.isEmpty()){
			throw new AuditoryException(INVALID_INPUT_DATA);
		}

		try{

			ref = new Bibref();
			ref.setAuthors(username);
			ref.setVolume(filename);
			ref = manager.getBibrefDao().save(ref);
			return this.ref;
		}catch (Exception ex){
			throw new AuditoryException(COULD_NOT_START_AUDITORY);
		}
	}

	public Auditable audit(Auditable auditable) throws AuditoryException {
		if(ref == null || ref.getId()==0){
			throw new AuditoryException(MISSUSE_OF_AUDITOR_COMPONENT);
		}
		auditable.attachToAuditory(ref);
		return auditable;
	}
}
