package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RoleType;
import org.hibernate.Session;

import java.util.List;

public class RoleTypeDAO extends GenericDAO<RoleType,Integer> {

	public RoleTypeDAO(final Session session) {
		super(session);
	}

	public List<RoleType> getRoleTypes(){
		return this.getAll();
	}

}
