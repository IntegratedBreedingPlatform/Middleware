package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.NamesDataManager;
import org.generationcp.middleware.pojos.Name;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NamesDataManagerImpl extends DataManager implements NamesDataManager {

	public NamesDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public NamesDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public List<Name> getNamesByNvalInTypeList(String name, List<Integer> typeList) {
		return getNameDao().getNamesByNvalInTypeList(name,typeList);
	}
}
