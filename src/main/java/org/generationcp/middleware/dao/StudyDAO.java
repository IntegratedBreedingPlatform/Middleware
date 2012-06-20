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

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Study;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

public class StudyDAO extends GenericDAO<Study, Integer>{

    @SuppressWarnings("unchecked")
    public List<Study> findByNameUsingEqual(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Study.FIND_BY_NAME_USING_EQUAL);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<Study> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by  name query using equal for Study: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> findByNameUsingLike(String name, int start, int numOfRows) throws QueryException {
        try {
            Query query = getSession().getNamedQuery(Study.FIND_BY_NAME_USING_LIKE);
            query.setParameter("name", name);
            query.setFirstResult(start);
            query.setMaxResults(numOfRows);

            List<Study> results = query.list();
            return results;
        } catch (HibernateException ex) {
            throw new QueryException("Error with find by  name query using like for Study: " + ex.getMessage());
        }
    }

    public int countByName(String name, Operation operation) throws QueryException {

        try {
            // if operation == null or operation = Operation.EQUAL
            Query query = getSession().getNamedQuery(Study.COUNT_BY_NAME_USING_EQUAL);
            if (operation == Operation.LIKE) {
                query = getSession().getNamedQuery(Study.COUNT_BY_NAME_USING_LIKE);
            }
            query.setParameter("name", name);
            return ((Long) query.uniqueResult()).intValue();

        } catch (HibernateException ex) {
            throw new QueryException("Error with count by name for Study: " + ex.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    public List<Study> getTopLevelStudies(int start, int numOfRows) throws QueryException {
        try {
            Criteria crit = getSession().createCriteria(Study.class);
            // top level studies are studies without parent folders (shierarchy
            // = 0)
            crit.add(Restrictions.eq("hierarchy", new Integer(0)));
            crit.setFirstResult(start);
            crit.setMaxResults(numOfRows);
            List<Study> topLevelStudies = crit.list();
            return topLevelStudies;
        } catch (HibernateException ex) {
            throw new QueryException("Error with retrieving top level Studies: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Study> getByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws QueryException {
        try {
            Criteria crit = getSession().createCriteria(Study.class);
            // studies with parent folder = parentFolderId
            crit.add(Restrictions.eq("hierarchy", parentFolderId));
            crit.setFirstResult(start);
            crit.setMaxResults(numOfRows);
            List<Study> studies = crit.list();
            return studies;
        } catch (HibernateException ex) {
            throw new QueryException("Error with retrieving Studies by parent folder id: " + ex.getMessage());
        }
    }

}
