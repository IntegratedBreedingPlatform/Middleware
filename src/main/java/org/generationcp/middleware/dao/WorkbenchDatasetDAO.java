/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link WorkbenchDataset}.
 *
 */
public class WorkbenchDatasetDAO extends GenericDAO<WorkbenchDataset, Long> {

	@Override
	public WorkbenchDataset getById(Long datasetId) throws MiddlewareQueryException {
		try {
			if (datasetId != null) {
				Criteria criteria =
						this.getSession().createCriteria(WorkbenchDataset.class).add(Restrictions.eq("datasetId", datasetId))
								.setMaxResults(1);
				return (WorkbenchDataset) criteria.uniqueResult();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error getById(datasetId=" + datasetId + ") query from WorkbenchDataset: " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Returns a list of {@link WorkbenchDataset} records by project id.
	 *
	 * @param projectId the project id
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the list of {@link WorkbenchDataset}s
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<WorkbenchDataset> getByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {

		try {
			if (projectId != null) {
				Criteria criteria = this.getSession().createCriteria(WorkbenchDataset.class);
				Project p = new Project();
				p.setProjectId(projectId);

				criteria.add(Restrictions.eq("project", p));
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with getByWorkbenchProjectId(projectId=" + projectId + ") query from WorkbenchDataset: " + e.getMessage(), e);
		}
		return new ArrayList<WorkbenchDataset>();
	}

	/**
	 * Returns the number of {@link WorkbenchDataset} records by project id.
	 *
	 * @param projectId the project id
	 * @return the number of {@link WorkbenchDataset} records
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countByProjectId(Long projectId) throws MiddlewareQueryException {
		try {
			if (projectId != null) {
				Criteria criteria = this.getSession().createCriteria(WorkbenchDataset.class);
				Project p = new Project();
				p.setProjectId(projectId);
				criteria.add(Restrictions.eq("project", p));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error with countByWorkbenchProjectId(projectId=" + projectId + ") query from WorkbenchDataset: " + e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * Returns a list of {@link WorkbenchDataset} by name.
	 *
	 * @param name - the {@link WorkbenchDataset} name
	 * @param op - the operator; EQUAL, LIKE
	 * @param start - the start
	 * @param numOfRows - the num of rows
	 * @return the list of {@link WorkbenchDataset}
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<WorkbenchDataset> getByName(String name, Operation op, int start, int numOfRows) throws MiddlewareQueryException {

		try {
			if (name != null) {
				Criteria criteria = this.getSession().createCriteria(WorkbenchDataset.class);

				if (Operation.EQUAL.equals(op)) {
					criteria.add(Restrictions.eq("name", name));
				} else if (Operation.LIKE.equals(op)) {
					criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
				} else {
					this.logAndThrowException("Error in getByName(name=" + name + "): Operation " + op.toString() + " not supported.",
							new Throwable());
				}

				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getByName(name=" + name + ") query from WorkbenchDataset: " + e.getMessage(), e);
		}
		return new ArrayList<WorkbenchDataset>();
	}

	/**
	 * Returns the number of {@link WorkbenchDataset} by name.
	 *
	 * @param name - the {@link WorkbenchDataset} name
	 * @param op - the operator; EQUAL, LIKE
	 * @return the number of {@link WorkbenchDataset}
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	public long countByName(String name, Operation op) throws MiddlewareQueryException {
		try {
			if (name != null) {
				Criteria criteria = this.getSession().createCriteria(WorkbenchDataset.class);

				if (Operation.EQUAL.equals(op)) {
					criteria.add(Restrictions.eq("name", name));
				} else if (Operation.LIKE.equals(op)) {
					criteria.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
				} else {
					this.logAndThrowException("Error in countByName(name=" + name + "): Operation " + op.toString() + " not supported.",
							new Throwable());
				}

				criteria.setProjection(Projections.rowCount());

				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with countByName(name=" + name + ") query from WorkbenchDataset: " + e.getMessage(), e);
		}
		return 0;
	}
}
