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
import java.util.Date;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ProjectBackup}.
 *
 */
public class ProjectBackupDAO extends GenericDAO<ProjectBackup, Long> {

	@SuppressWarnings("unchecked")
	public List<ProjectBackup> getAllProjectBackups() throws MiddlewareQueryException {
		try {
			SQLQuery query = this.getSession().createSQLQuery(ProjectBackup.GET_ALL_DISTINCT_PROJECT_BACKUP);
			List<ProjectBackup> projectBackupList = new ArrayList<ProjectBackup>();
			List<Object> results = query.list();

			for (Object o : results) {
				Object[] projectBackup = (Object[]) o;
				Long projectBackupId =
						Integer.class.isInstance(projectBackup[0]) ? ((Integer) projectBackup[0]).longValue() : (Long) projectBackup[0];
								Long projectId =
						Integer.class.isInstance(projectBackup[1]) ? ((Integer) projectBackup[1]).longValue() : (Long) projectBackup[1];
										Date backupTime = (Date) projectBackup[2];
										String backupPath = (String) projectBackup[3];

										ProjectBackup u = new ProjectBackup(projectBackupId, projectId, backupTime, backupPath);
										projectBackupList.add(u);
			}
			return projectBackupList;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in getAllProjectBackups() query from Project: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProjectBackup> getProjectBackups(long projectId) {
		return this.getSession().createCriteria(ProjectBackup.class).add(Restrictions.eq("projectId", projectId)).list();
	}

	@SuppressWarnings("unchecked")
	public List<ProjectBackup> getProjectBackupByBackupPath(String backupPath) {
		Query query = this.getSession().createQuery("FROM ProjectBackup pb WHERE pb.backupPath = ?");
		query.setParameter(0, backupPath);
		query.setMaxResults(1);
		return query.list();
	}
}
