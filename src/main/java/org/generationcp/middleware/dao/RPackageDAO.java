package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RPackage;
import org.hibernate.Session;

public class RPackageDAO extends GenericDAO<RPackage, Integer> {

	public RPackageDAO(final Session session) {
		super(session);
	}
}
