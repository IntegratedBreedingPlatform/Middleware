package org.generationcp.middleware.auditory;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.generationcp.middleware.manager.api.AuditorDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.springframework.beans.factory.annotation.Autowired;
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
	public static final String EMPTY_STRING = " ";

	AuditorDataManager manager;

	Bibref ref;

	@Autowired
	public Auditor(AuditorDataManager manager) {
		this.manager = manager;
	}


	public Auditory startAuditory(String username, String filename) throws AuditoryException {
		if (username==null || username.isEmpty() || filename==null || filename.isEmpty()){
			throw new AuditoryException(INVALID_INPUT_DATA);
		}

		try{

			ref = new Bibref();
			ref.setAuthors(username);
			ref.setVolume(EMPTY_STRING);
			ref.setRefid(0);
			ref.setType(manager.getBibrefType());
			ref.setEditors(EMPTY_STRING);
			ref.setAnalyt(EMPTY_STRING);
			ref.setMonogr(filename);
			ref.setSeries(EMPTY_STRING);
			ref.setIssue(EMPTY_STRING);
			ref.setPagecol(EMPTY_STRING);
			ref.setPublish(EMPTY_STRING);
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
			String date = formatter.format(new Date());
			ref.setPubdate(Integer.parseInt(date));
			ref.setPucity(EMPTY_STRING);
			ref.setPucntry(EMPTY_STRING);
			ref = manager.save(ref);
			return this.ref;
		}catch (Exception ex){
			throw new AuditoryException(COULD_NOT_START_AUDITORY);
		}
	}

	public Auditable audit(Auditable auditable) throws AuditoryException {
		if(ref == null || ref.getId()==0 || auditable==null){
			throw new AuditoryException(MISSUSE_OF_AUDITOR_COMPONENT);
		}
		auditable.audit(ref);
		return auditable;
	}

	public void closeAuditory() {
		ref=null;
	}
}
