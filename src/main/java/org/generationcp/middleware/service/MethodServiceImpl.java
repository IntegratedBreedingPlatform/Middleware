package org.generationcp.middleware.service;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.MethodService;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class MethodServiceImpl extends Service implements MethodService {

	public MethodServiceImpl(final HibernateSessionProvider sessionProvider, final String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public List<Method> getAllBreedingMethods() {
		final List<Method> methodList = this.getGermplasmDataManager().getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}
}
