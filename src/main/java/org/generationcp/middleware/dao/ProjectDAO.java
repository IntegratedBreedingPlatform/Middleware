package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.pojos.workbench.Project;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.QueryException;

public class ProjectDAO extends GenericDAO<Project, Long> {

    /**
     * Get the list of {@link Project}s.
     * 
     * @return
     */
    public List<Project> findAll() {
	return findAll(null, null);
    }

    /**
     * Get the list of {@link Project}s.
     * 
     * @param start
     *            the index of the first result to return. This parameter is
     *            ignored if null.
     * @param numOfRows
     *            the number of rows to return. This parameter is ignored if
     *            null.
     * @return
     */
    public List<Project> findAll(Integer start, Integer numOfRows) {
	try {
	    Criteria criteria = getSession().createCriteria(Project.class);
	    if (start != null) {
		criteria.setFirstResult(start);
	    }
	    if (numOfRows != null) {
		criteria.setMaxResults(numOfRows);
	    }
	    @SuppressWarnings("unchecked")
	    List<Project> projects = criteria.list();

	    return projects;
	} catch (HibernateException ex) {
	    throw new QueryException(ex);
	}
    }
}
