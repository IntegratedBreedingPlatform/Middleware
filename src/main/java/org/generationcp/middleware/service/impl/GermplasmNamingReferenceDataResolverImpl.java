
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmNamingReferenceDataResolver;

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

	public GermplasmNamingReferenceDataResolverImpl() {

	}

	public GermplasmNamingReferenceDataResolverImpl(UserDefinedFieldDAO userDefinedFieldDAO) {
		this.userDefinedFieldDAO = userDefinedFieldDAO;
	}

	public GermplasmNamingReferenceDataResolverImpl(final HibernateSessionProvider sessionProvider) {
		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
	}

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

}
