package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoleTypeDAO extends GenericDAO<RoleType,Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(RoleTypeDAO.class);

	public List<RoleType> getRoleTypes(){
		return this.getAll();
	}
}
