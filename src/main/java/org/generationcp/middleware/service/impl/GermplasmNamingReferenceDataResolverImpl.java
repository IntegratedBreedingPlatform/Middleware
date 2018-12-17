
package org.generationcp.middleware.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
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

	public GermplasmNamingReferenceDataResolverImpl() {

	}

	public GermplasmNamingReferenceDataResolverImpl(final UserDefinedFieldDAO userDefinedFieldDAO,
			final OntologyVariableDataManager ontologyVariableDataManager) {
		this.userDefinedFieldDAO = userDefinedFieldDAO;
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}

	public GermplasmNamingReferenceDataResolverImpl(final HibernateSessionProvider sessionProvider) {
		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
		this.ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
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

	// TODO remove hard coded ids/names
	@Override
	public List<String> getProgramIdentifiers(final Integer levelCode, String programUUID) {
		Variable variable = null;
		List<String> programIdentifiers = new ArrayList<>();
		if (levelCode == 1) {
			variable = this.ontologyVariableDataManager.getVariable(programUUID, 3001, true);
			if (variable == null || !variable.getName().equals("Project_Prefix")) {
				throw new IllegalStateException(
						"Missing required reference data. Please ensure an ontology variable with name Project_Prefix (id 3001) has been setup. It is required for Level 1 coding.");
			}
		} else if (levelCode == 2) {
			variable = this.ontologyVariableDataManager.getVariable(programUUID, 3002, true);
			if (variable == null || !variable.getName().equals("CIMMYT_Target_Region")) {
				throw new IllegalStateException(
						"Missing required reference data. Please ensure an ontology variable with name CIMMYT_Target_Region (id 3002) has been setup. It is required for Level 2 coding.");
			}
		}

		if (variable != null) {
			final List<TermSummary> categories = variable.getScale().getCategories();
			for (TermSummary categoryTerm : categories) {
				programIdentifiers.add(categoryTerm.getName());
			}
		}
		return programIdentifiers;
	}

	@Override
	public Set<GermplasmType> getGermplasmTypes() {
		return Sets.newHashSet(GermplasmType.values());
	}

}
