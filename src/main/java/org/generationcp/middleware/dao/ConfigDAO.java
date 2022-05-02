package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.Config;
import org.hibernate.Session;

public class ConfigDAO extends GenericDAO<Config, String> {

	public ConfigDAO(final Session session) {
		super(session);
	}
}
