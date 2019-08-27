package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.workbench.RoleType;

import java.util.List;

public class RoleTypeDAO extends GenericDAO<RoleType,Integer> {

	public List<RoleType> getRoleTypes(){
		return this.getAll();
	}

}
