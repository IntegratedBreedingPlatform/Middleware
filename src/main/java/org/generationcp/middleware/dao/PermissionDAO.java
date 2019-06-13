package org.generationcp.middleware.dao;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PermissionDAO extends GenericDAO<Permission, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(PermissionDAO.class);

	private static final String SQL_FILTERED_PERMISSIONS = "select "
		+ "p.permission_id as id, "
		+ "p.name as name, "
		+ "p.description as description, "
		+ "p.parent_id as parentId,"
		+ "p.workbench_sidebar_category_link_id as workbenchCategoryLinkId "
		+ "from permission p "
		+ "inner join role_permission rp on p.permission_id = rp.permission_id "
		+ "inner join role r on rp.role_id = r.id "
		+ "inner join users_roles ur on r.id = ur.role_id "
		+ "where  (r.role_type_id = " + RoleType.INSTANCE.getId()
		+ "  or (r.role_type_id = "+ RoleType.CROP.getId() +" and ur.crop_name = :cropName) "
		+ "  or (r.role_type_id = "+ RoleType.PROGRAM.getId() +" and ur.crop_name = :cropName and ur.workbench_project_id = :projectId)) "
		+ "and ur.userid = :userId and r.active = 1";

	private static final String PERMISSION_CHILDREN = "select "
		+ "p.permission_id as id, "
		+ "p.name as name, "
		+ "p.description as description, "
		+ "p.parent_id as parentId,"
		+ "p.workbench_sidebar_category_link_id as workbenchCategoryLinkId "
		+ "from workbench.permission p "
		+ "where  p.parent_id = :parentId ";

	public List<PermissionDto> getPermissions(final Integer userId, final String cropName, final Integer programId) {

		try {
			final SQLQuery query = this.getSession().createSQLQuery(PermissionDAO.SQL_FILTERED_PERMISSIONS);
			query.setParameter("userId", userId);
			query.setParameter("cropName", cropName);
			query.setParameter("projectId", programId);
			query.addScalar("id").addScalar("name").addScalar("parentId")
				.addScalar("description").addScalar("workbenchCategoryLinkId");
			query.setResultTransformer(Transformers.aliasToBean(PermissionDto.class));
			final List<PermissionDto> results = query.list();
			final List<PermissionDto> copy = new ArrayList<>();
			copy.addAll(results);
			for (final PermissionDto permission : results) {
				for (final PermissionDto permission1 : results) {
					if (permission1.getId().equals((permission.getParentId()))) {
						copy.remove(permission);
					}
				}
			}
			return copy;
		} catch (final HibernateException e) {
			final String message = "Error with getPermissions query from RoleDAO: " + e.getMessage();
			PermissionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<PermissionDto> getChildrenOfPermission(final PermissionDto permissionDto) {
		final SQLQuery query = this.getSession().createSQLQuery(PermissionDAO.PERMISSION_CHILDREN);
		query.setParameter("parentId", permissionDto.getId());
		query.addScalar("id").addScalar("name").addScalar("description")
			.addScalar("parentId").addScalar("workbenchCategoryLinkId");
		query.setResultTransformer(Transformers.aliasToBean(PermissionDto.class));
		final List<PermissionDto> results = query.list();
		return results;
	}
}
