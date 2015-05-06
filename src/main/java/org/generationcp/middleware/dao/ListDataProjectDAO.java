package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class ListDataProjectDAO extends GenericDAO<ListDataProject, Integer> {

	public void deleteByListId(int listId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("delete ldp " +
	                   "from listdata_project ldp " + 
					   "where ldp.list_id = " + listId);
			statement.executeUpdate();	   
			
	        this.flush();
	        this.clear();
	
		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ListDataProject> getByListId(int listId) throws MiddlewareQueryException {
    	List<ListDataProject> list = new ArrayList<ListDataProject>();
        try {
            Criteria criteria = getSession().createCriteria(ListDataProject.class);
            criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
            criteria.addOrder(Order.asc("entryId"));
            return criteria.list();

        } catch (HibernateException e) {
            logAndThrowException("Error with getByListId(listId=" + listId + ") query from ListDataProjectDAO: "
                    + e.getMessage(), e);
        }
        return list;    	
	}

	@SuppressWarnings("unchecked")
	public ListDataProject getByListIdAndEntryNo(int listId,int entryNo) throws MiddlewareQueryException {
		ListDataProject result = null;

		try {
			Criteria criteria = getSession().createCriteria(ListDataProject.class);
			criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
			criteria.add(Restrictions.eq("entryId",entryNo));
			criteria.addOrder(Order.asc("entryId"));
			result = (ListDataProject) criteria.uniqueResult();

		} catch (HibernateException e) {
			logAndThrowException("Error with getByListIdAndEntryNo(listId=" + listId + ") query from ListDataProjectDAO: "
					+ e.getMessage(), e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public ListDataProject getByStudy(int studyId,GermplasmListType listType,int plotNo) throws MiddlewareQueryException {
		try {

			String queryStr =
					"select ldp.* FROM nd_experiment_project neproj,"
					+ " nd_experimentprop nd_ep, nd_experiment_stock nd_stock, stock,"
					+ " listdata_project ldp, project_relationship pr, projectprop pp"
					+ " WHERE nd_ep.type_id IN (:PLOT_NO_TERM_IDS)"
					+ " AND pp.project_id = pr.subject_project_id"
					+ " AND pr.object_project_id = :STUDY_ID"
					+ " AND pp.value = :DATASET_TYPE"
					+ " AND neproj.project_id = pr.subject_project_id"
					+ " AND neproj.nd_experiment_id = nd_ep.nd_experiment_id"
					+ " AND nd_stock.nd_experiment_id = nd_ep.nd_experiment_id"
					+ " AND stock.stock_id = nd_stock.stock_id"
					+ " AND ldp.germplasm_id = stock.dbxref_id"
					+ " AND nd_ep.value = :PLOT_NO"
					+ " AND ( EXISTS ("
					+ " SELECT 1"
					+ " FROM listnms cl"
					+ " WHERE cl.listid = ldp.list_id"
					+ " AND cl.listtype = 'CHECK'"
					+ " AND NOT EXISTS ("
					+ " SELECT 1 FROM listnms nl"
					+ " WHERE nl.listid = ldp.list_id"
					+ " AND nl.listtype = :LIST_TYPE"
					+ " )) OR EXISTS ("
					+ " SELECT 1 FROM listnms nl"
					+ " WHERE nl.listid = ldp.list_id"
					+ " AND nl.listtype = :LIST_TYPE"
					+ " ));";


			SQLQuery query = getSession().createSQLQuery(queryStr);
			query.addEntity("ldp",ListDataProject.class);
			query.setParameter("LIST_TYPE", listType.name());
			query.setParameter("STUDY_ID", studyId);
			query.setParameter("PLOT_NO",plotNo);
			query.setParameter("DATASET_TYPE", DataSetType.PLOT_DATA.getId());
			query.setParameterList("PLOT_NO_TERM_IDS",
					new Integer[] { TermId.PLOT_NO.getId(), TermId.PLOT_NNO.getId() });

			List resultList = query.list();
			if (!resultList.isEmpty()) {
				return (ListDataProject) resultList.get(0);
			}

		} catch(HibernateException e) {
			logAndThrowException("Error in getStudy=" + studyId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}

		return null;

	}


	public void deleteByListIdWithList(int listId) throws MiddlewareQueryException {
		try {
			this.flush();
			
			SQLQuery statement = getSession().createSQLQuery("delete ldp, lst " +
	                   "from listdata_project ldp, listnms lst " + 
					   "where ldp.list_id = lst.listid and ldp.list_id = " + listId);
			statement.executeUpdate();	   
			
	        this.flush();
	        this.clear();
	
		} catch(HibernateException e) {
			logAndThrowException("Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}
	
    public long countByListId(Integer id) throws MiddlewareQueryException {
        try {
        	if (id != null){
	            Criteria criteria = getSession().createCriteria(ListDataProject.class);
	            criteria.createAlias("list", "l");
	            criteria.add(Restrictions.eq("l.id", id));
	            criteria.setProjection(Projections.rowCount());
	            return ((Long) criteria.uniqueResult()).longValue(); //count
        	}
        } catch (HibernateException e) {
            logAndThrowException("Error with countByListId(id=" + id + ") query from ListDataProject " + e.getMessage(), e);
        }
        return 0;
    }

	public List<ListDataProject> getListDataProjectWithParents(Integer listID) throws MiddlewareQueryException {
		List<ListDataProject> listDataProjects = new ArrayList<ListDataProject>();
		try {

			String queryStr = "select "+ 
							" lp.listdata_project_id as listdata_project_id, "+
							" lp.entry_id as entry_id, "+ 
							" lp.designation as designation, "+ 
							" lp.group_name as group_name, "+
							" fn.nval as fnval, "+
							" fp.gid as fpgid, "+
							" mn.nval as mnval, "+
							" mp.gid as mpgid, "+
							" g.gid as gid, "+
							" lp.seed_source as seed_source, "+
							" lp.duplicate_notes as duplicate_notes "+
						" from listdata_project lp "+
						" inner join germplsm g on lp.germplasm_id = g.gid "+
						" inner join germplsm mp on g.gpid2 = mp.gid "+
						" inner join names mn on mp.gid = mn.gid and mn.nstat = 1 "+
						" inner join germplsm fp on g.gpid1 = fp.gid "+
						" inner join names fn on fp.gid = fn.gid and mn.nstat = 1 "+
						" where lp.list_id = :listId "+
						" group by entry_id";


			SQLQuery query = getSession().createSQLQuery(queryStr);
			query.setParameter("listId", listID);
			query.addScalar("listdata_project_id");
			query.addScalar("entry_id");
			query.addScalar("designation");
			query.addScalar("group_name");
			query.addScalar("fnval");
			query.addScalar("fpgid");
			query.addScalar("mnval");
			query.addScalar("mpgid");
			query.addScalar("gid");
			query.addScalar("seed_source");
			query.addScalar("duplicate_notes");
			
			createListDataProjectRows(listDataProjects,query);

		} catch(HibernateException e) {
			logAndThrowException("Error in getListDataProjectWithParents=" + listID + " in ListDataProjectDAO: " + e.getMessage(), e);
		}

		return listDataProjects;
	}

	private void createListDataProjectRows(
			List<ListDataProject> listDataProjects, SQLQuery query) {
		List<Object[]> result = query.list();
		
		for (Object[] row : result) {
			Integer listDataProjectId = (Integer) row[0];
			Integer entryId = (Integer) row[1];
			String designation = (String) row[2];
			String parentage = (String) row[3];
			String femaleParent = (String) row[4];
			Integer fgid = (Integer) row[5];
			String maleParent = (String) row[6];
			Integer mgid = (Integer) row[7];
			Integer gid = (Integer) row[8];
			String seedSource = (String) row[9];
			String duplicate = (String) row[10];
			
			ListDataProject listDataProject = new ListDataProject();
			listDataProject.setListDataProjectId(listDataProjectId);
			listDataProject.setEntryId(entryId);
			listDataProject.setDesignation(designation);
			listDataProject.setGroupName(parentage);
			listDataProject.setFemaleParent(femaleParent);
			listDataProject.setFgid(fgid);
			listDataProject.setMaleParent(maleParent);
			listDataProject.setMgid(mgid);
			listDataProject.setGermplasmId(gid);
			listDataProject.setSeedSource(seedSource);
			listDataProject.setDuplicate(duplicate);
			
			listDataProjects.add(listDataProject);
		}
		
	}
	
}
