package org.generationcp.middleware.api.ontology;

import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Replaces {@link OntologyVariableDataManagerImpl}
 */
@Transactional
@Service
public class OntologyVariableServiceImpl implements OntologyVariableService {
	private final DaoFactory daoFactory;

	public OntologyVariableServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public Map<Integer, Variable> getVariablesWithFilterById(final VariableFilter variableFilter) {
		return this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);
	}

}
