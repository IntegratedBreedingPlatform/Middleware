package org.generationcp.middleware.v2.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.Folder;
import org.hibernate.HibernateException;
import org.hibernate.Query;

public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {

	@SuppressWarnings("unchecked")
	public List<Folder> getRootFolders() throws MiddlewareQueryException{
		
		List<Folder> folderList = new ArrayList<Folder>();
		
		try {
			Query query = getSession().createSQLQuery(DmsProject.GET_ROOT_FOLDERS);
			List<Object[]> list =  query.list();
			for (Object[] row : list){
				Long id = new Long((Integer)row[0]); //project.id
				String name = (String) row [1]; //project.name
				folderList.add(new Folder(id, name));
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getRootFolders query from Project: " + e.getMessage(), e);
		}
		
		return folderList;
		
	}
}
