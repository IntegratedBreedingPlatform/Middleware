/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;

public class ProjectBackupDAO extends GenericDAO<ProjectBackup, Long> {
    public List<ProjectBackup> getAllProjectBackups() throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(ProjectBackup.GET_ALL_DISTINCT_PROJECT_BACKUP);
            List<ProjectBackup> projectBackupList = new ArrayList<ProjectBackup>();
            List<Object> results = query.list();

            for (Object o : results) {
                Object[] projectBackup = (Object[]) o;
                Integer projectBackupId = (Integer) projectBackup[0];
                Integer projectId = (Integer) projectBackup[1];
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
}
