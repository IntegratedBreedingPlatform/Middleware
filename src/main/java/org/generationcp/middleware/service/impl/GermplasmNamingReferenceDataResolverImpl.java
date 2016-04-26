
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	@Override
	public UserDefinedField resolveNameType(final int level) {
		final List<UserDefinedField> allCodedNameTypes = this.userDefinedFieldDAO.getByFieldTableNameAndType("NAMES", "CODE");

		Collections.sort(allCodedNameTypes, new Comparator<UserDefinedField>() {
			@Override
			public int compare(UserDefinedField udfld1, UserDefinedField udfld2) {
				return udfld1.getFcode().compareTo(udfld2.getFcode());
			}
		});

		UserDefinedField nameTypeForLevel = null;

		if (allCodedNameTypes.size() >= level) {
			// FIXME using index as a level. 0 = level 1, 1 = level 2 etc from the sorted list.
			nameTypeForLevel = allCodedNameTypes.get(level - 1);
		}

		if (nameTypeForLevel == null) {
			String message = String.format(
					"Missing required reference data. Please ensure User defined field (UDFLD) records with fTable=NAMES, fType=CODE are appropriately setup.");
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
