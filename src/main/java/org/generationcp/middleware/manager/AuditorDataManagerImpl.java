package org.generationcp.middleware.manager;

import org.generationcp.middleware.auditory.Auditory;
import org.generationcp.middleware.auditory.AuditoryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.AuditorDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuditorDataManagerImpl extends DataManager implements AuditorDataManager {

	public static final String INVALID_BIBLIOGRAFIC_REFERENCE = "Invalid bibliografic reference";
	public static final String INVALID_BIBLIOGRAFIC_REFERENCE_TYPE = "Invalid bibliografic reference type";


	public AuditorDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public AuditorDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public Bibref save(Bibref ref) throws AuditoryException {
		if(ref==null){
			throw new AuditoryException(INVALID_BIBLIOGRAFIC_REFERENCE);
		}
		return this.getBibrefDao().save(ref);
	}

	@Override
	public UserDefinedField getBibrefType() throws AuditoryException{
		UserDefinedField bibrefType = getUserDefinedFieldDao().getByTableTypeAndCode("BIBREFS", "PUBTYPE", "BOOK");

		if(bibrefType == null){
			throw new AuditoryException(INVALID_BIBLIOGRAFIC_REFERENCE_TYPE);
		}

		return bibrefType;
	}

	@Override
	public Auditory getAuditory(int id) throws AuditoryException{
		Bibref auditory = this.getBibrefDao().getById(id);
		if(auditory == null){
			throw new AuditoryException(INVALID_BIBLIOGRAFIC_REFERENCE);
		}
		return auditory;
	}
}
