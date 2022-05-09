package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.CropParameter;
import org.hibernate.Session;

public class CropParameterDAO extends GenericDAO<CropParameter, String> {

	public CropParameterDAO(final Session session) {
		super(session);
	}
}
