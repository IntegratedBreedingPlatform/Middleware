
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmNameTypeResolver;

/**
 * This is a CIMMYT (maize) specific implementation of resolving name types based on levels. Coding and levels are both CIMMYT maize domain
 * concepts. However, CIMMYT are the first and only client for level based naming requirement so this is the default implementation as well
 * for germplasm group naming service.
 * 
 */
public class CodedNameTypeResolver implements GermplasmNameTypeResolver {

	static final String NAME_TYPE_LEVEL1 = "CODE1";
	static final String NAME_TYPE_LEVEL2 = "CODE2";
	static final String NAME_TYPE_LEVEL3 = "CODE3";

	private UserDefinedFieldDAO userDefinedFieldDAO;

	public CodedNameTypeResolver() {

	}

	public CodedNameTypeResolver(UserDefinedFieldDAO userDefinedFieldDAO) {
		this.userDefinedFieldDAO = userDefinedFieldDAO;
	}

	public CodedNameTypeResolver(final HibernateSessionProvider sessionProvider) {
		this.userDefinedFieldDAO = new UserDefinedFieldDAO();
		this.userDefinedFieldDAO.setSession(sessionProvider.getSession());
	}

	@Override
	public UserDefinedField resolve(final int level) {
		UserDefinedField nameTypeForLevel = null;
		String levelCode = null;

		if (level == 1) {
			levelCode = CodedNameTypeResolver.NAME_TYPE_LEVEL1;
		} else if (level == 2) {
			levelCode = CodedNameTypeResolver.NAME_TYPE_LEVEL2;
		} else if (level == 3) {
			levelCode = CodedNameTypeResolver.NAME_TYPE_LEVEL3;
		}

		nameTypeForLevel = this.userDefinedFieldDAO.getByTableTypeAndCode("NAMES", "NAME", levelCode);

		if (nameTypeForLevel == null) {
			throw new IllegalStateException(
					"Missing required reference data: Please ensure User defined field (UDFLD) record for name type '" + levelCode
							+ "' has been setup.");
		}
		return nameTypeForLevel;
	}

}
