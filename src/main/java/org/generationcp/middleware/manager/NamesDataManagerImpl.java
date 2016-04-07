package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.NamesDataManager;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

@Transactional
public class NamesDataManagerImpl extends DataManager implements NamesDataManager {

	public NamesDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public NamesDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
	}

	@Override
	public List<Name> getNamesByNvalInFCodeList(String name, List<String> typeList) {
		return getNameDao().getNamesByNvalInTypeList(name,typeList);
	}

	@Override
	public List<Name> getNameByGIDAndCodedName(Integer gid, List<String> fCodecodedNames) {
		return getNameDao().getNamesByGIDAndCodedName(gid,fCodecodedNames);
	}
}
