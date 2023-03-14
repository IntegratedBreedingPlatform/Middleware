package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.RoleTypePermission;
import org.generationcp.middleware.pojos.workbench.RoleTypePermissionId;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RoleTypePermissionDAO extends GenericDAO<RoleTypePermission, RoleTypePermissionId> {

	private static final Logger LOG = LoggerFactory.getLogger(RoleTypePermission.class);

	public RoleTypePermissionDAO(final Session session) {
		super(session);
	}

	public List<RoleTypePermission> getPermissionsByRoleTypeAndParent(final Integer roleTypeId, final Integer parentId) {
		final List<RoleTypePermission> toReturn;

		try {
			final Criteria criteria = this.getSession().createCriteria(RoleTypePermission.class);

			criteria.createAlias("roleType", "roleType");
			criteria.add(Restrictions.eq("roleType.id", roleTypeId));

			criteria.createAlias("permission", "permission");

			if (parentId == null) {
				criteria.add(Restrictions.isNull("permission.parent"));
			} else {
				criteria.createAlias("permission.parent", "parent");
				criteria.add(Restrictions.eq("parent.permissionId", parentId));
			}

			criteria.addOrder(Order.asc("id"));
			toReturn = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error with getPermissionsByRoleTypeAndParent query from RoleTypePermissionDAO: " + e.getMessage();
			RoleTypePermissionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

}
