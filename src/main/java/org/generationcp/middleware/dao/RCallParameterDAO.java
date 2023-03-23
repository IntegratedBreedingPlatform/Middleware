package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RCallParameter;
import org.hibernate.Session;

public class RCallParameterDAO extends GenericDAO<RCallParameter, Integer> {

	public RCallParameterDAO(final Session session) {
		super(session);
	}
}
