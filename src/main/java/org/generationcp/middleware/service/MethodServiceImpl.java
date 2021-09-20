package org.generationcp.middleware.service;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.MethodService;
import org.generationcp.middleware.util.FieldbookListUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class MethodServiceImpl extends Service implements MethodService {

	final DaoFactory daoFactory;

	public MethodServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Method> getAllBreedingMethods() {
		final List<Method> methodList = this.getGermplasmDataManager().getAllMethods();
		FieldbookListUtil.sortMethodNamesInAscendingOrder(methodList);
		return methodList;
	}
}
