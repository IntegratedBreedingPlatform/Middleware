package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserDefinedFieldsDataManagerImpl extends DataManager implements UserDefinedFieldsDataManager {



	public UserDefinedFieldsDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public UserDefinedFieldsDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public List<UserDefinedField> getNotCodeNamesFactor(List<Integer> codedIds) {
		List<UserDefinedField> namesFactors = getUserDefinedFieldDao().getByTableAndTypeWithoutList("NAMES", "NAME",codedIds);

		return namesFactors;
	}
}
