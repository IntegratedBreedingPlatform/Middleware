package org.generationcp.middleware.auditory;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class AuditorDataManager extends DataManager {

	public AuditorDataManager(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public AuditorDataManager() {
		super();
	}

}
