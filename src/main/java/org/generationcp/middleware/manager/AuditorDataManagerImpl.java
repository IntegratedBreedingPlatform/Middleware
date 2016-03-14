package org.generationcp.middleware.manager;

import org.generationcp.middleware.auditory.AuditoryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.AuditorDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AuditorDataManagerImpl extends DataManager implements AuditorDataManager {

	public static final String INVALID_BIBLIOGRAFIC_REFERENCE = "Invalid bibliografic reference";

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
	public UserDefinedField getBibrefType() {
		return this.getUserDefinedFieldDao().getByTableTypeAndCode("BIBREFS","PUBTYPE","BOOK");
	}
}
