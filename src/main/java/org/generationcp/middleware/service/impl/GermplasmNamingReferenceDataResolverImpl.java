
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.GermplasmNamingReferenceDataResolver;
import org.generationcp.middleware.service.api.GermplasmType;

import com.google.common.collect.Sets;

/**
 * This is a CIMMYT (maize) specific implementation of resolving name types based on levels, resolving name components etc. Coding and
 * levels are both CIMMYT maize domain concepts. However, CIMMYT are the first and only client for level based naming requirement so this is
 * the default implementation as well for germplasm group naming service.
 * 
 */
public class GermplasmNamingReferenceDataResolverImpl implements GermplasmNamingReferenceDataResolver {

	static final String NAME_TYPE_LEVEL1 = "CODE1";
	static final String NAME_TYPE_LEVEL2 = "CODE2";
	static final String NAME_TYPE_LEVEL3 = "CODE3";

	private UserDefinedFieldDAO userDefinedFieldDAO;

	private OntologyVariableDataManager ontologyVariableDataManager;
	private CVTermDao cvTermDAO;

	public GermplasmNamingReferenceDataResolverImpl() {

	}

	public GermplasmNamingReferenceDataResolverImpl(final UserDefinedFieldDAO userDefinedFieldDAO,
			final OntologyVariableDataManager ontologyVariableDataManager, CVTermDao cvTermDao) {
		this.userDefinedFieldDAO = userDefinedFieldDAO;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
		this.cvTermDAO = cvTermDao;
	}

	public GermplasmNamingReferenceDataResolverImpl(final HibernateSessionProvider sessionProvider) {
		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
		this.cvTermDAO = new CVTermDao();
		this.cvTermDAO.setSession(sessionProvider.getSession());
	}

	// TODO remove hard coded ids/names
	@Override
	public UserDefinedField resolveNameType(final int level) {
		UserDefinedField nameTypeForLevel = null;
		String levelCode = null;

		if (level == 1) {
			levelCode = GermplasmNamingReferenceDataResolverImpl.NAME_TYPE_LEVEL1;
		} else if (level == 2) {
			levelCode = GermplasmNamingReferenceDataResolverImpl.NAME_TYPE_LEVEL2;
		} else if (level == 3) {
			levelCode = GermplasmNamingReferenceDataResolverImpl.NAME_TYPE_LEVEL3;
		}

		nameTypeForLevel = this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", levelCode);

		if (nameTypeForLevel == null) {
			String message = String.format(
					"Missing required reference data. Please ensure User defined field (UDFLD) record with fTable=NAMES, fType=NAME, fCode=%s is setup.",
					levelCode);
			throw new IllegalStateException(message);
		}
		return nameTypeForLevel;
	}

	@Override
	public List<String> getCategoryValues(final String variableName, final String programUUID) {

		CVTerm variableTerm = this.cvTermDAO.getByNameAndCvId(variableName, CvId.VARIABLES.getId());

		if (variableTerm == null) {
			throw new IllegalStateException(
					String.format("Missing required reference data. Please ensure an ontology variable with name: %s has been setup.",
							variableName));
		}

		Variable variable = this.ontologyVariableDataManager.getVariable(programUUID, variableTerm.getCvTermId(), true, false);

		if (variable.getScale() != null && variable.getScale().getDataType() != DataType.CATEGORICAL_VARIABLE) {
			throw new IllegalStateException(
					String.format(
							"Reference data setup incorrectly. Please ensure an ontology variable with name: %s has a categorical scale associated with it.",
							variableName));
		}

		List<String> categoryValues = new ArrayList<>();
		List<TermSummary> categories = variable.getScale().getCategories();
		for (TermSummary categoryTerm : categories) {
			categoryValues.add(categoryTerm.getName());
		}
		return categoryValues;
	}

	@Override
	public Set<GermplasmType> getGermplasmTypes() {
		return Sets.newHashSet(GermplasmType.values());
	}

}
