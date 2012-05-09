package org.generationcp.middleware.dao;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Study;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

public class StudyDAO extends GenericDAO<Study, Integer> {
	
	@SuppressWarnings("unchecked")
	public List<Study> getTopLevelStudies(int start, int numOfRows) throws QueryException {
		try {
			Criteria crit = getSession().createCriteria(Study.class);
			//top level studies are studies without parent folders (shierarchy = 0)
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
	public List<Study> getStudiesByParentFolderID(Integer parentFolderId, int start, int numOfRows) throws QueryException {
		try {
			Criteria crit = getSession().createCriteria(Study.class);
			//top level studies are studies without parent folders (shierarchy = 0)
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
