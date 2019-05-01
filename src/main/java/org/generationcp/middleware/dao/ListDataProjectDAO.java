
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class ListDataProjectDAO extends GenericDAO<ListDataProject, Integer> {

	private static final String ENTRY_ID = "entryId";

	static final String GERMPLASM_LIST_DATA_LIST_ID_COLUMN = "listId";

	static final String GERMPLASM_TABLE = "germplasm";

	static final String GERMPLASM_TABLE_ALIAS = "g";

	static final String GERMPLASM_LIST_NAME_TABLE = "list";

	static final String GERMPLASM_LIST_NAME_TABLE_ALIAS = "l";

	public static final String GET_GERMPLASM_USED_IN_ENTRY_LIST = " SELECT \n" + "   ldp.germplasm_id, \n"
			+ "   group_concat(p.name) \n" + " FROM listnms l \n"
			+ "   INNER JOIN listdata_project ldp ON l.listid = ldp.list_id \n"
			+ "   INNER JOIN project p ON l.projectid = p.project_id \n" + " WHERE ldp.germplasm_id IN (:gids) \n"
			+ "       AND l.liststatus != " + GermplasmListDAO.STATUS_DELETED + " \n" + "       AND l.listtype IN ('"
			+ GermplasmListType.STUDY.name() + "', '"
			+ GermplasmListType.CHECK.name() + "') \n" + " GROUP BY ldp.germplasm_id";

	public void deleteByListId(final int listId) {
		try {
			// Please note we are manually flushing because non hibernate based
			// deletes and updates causes the Hibernate session to get out of
			// synch with
			// underlying database. Thus flushing to force Hibernate to
			// synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final SQLQuery statement = this.getSession()
					.createSQLQuery("delete ldp " + "from listdata_project ldp " + "where ldp.list_id = " + listId);
			statement.executeUpdate();
		} catch (final HibernateException e) {

			throw new MiddlewareQueryException(
					"Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ListDataProject> getByListId(final int listId) {
		try {

			final DetachedCriteria listDataProjectCriteria = DetachedCriteria.forClass(ListDataProject.class);
			listDataProjectCriteria.setProjection(Property.forName("germplasmId"));
			listDataProjectCriteria.add(Restrictions.eq("list", new GermplasmList(listId)));

			final Criteria germplasmCriteria = this.getSession().createCriteria(Germplasm.class);
			germplasmCriteria.add(Property.forName("gid").in(listDataProjectCriteria));

			final List<Germplasm> germplasmList = germplasmCriteria.list();

			final Criteria criteria = this.getSession().createCriteria(ListDataProject.class, "listDataProject");
			criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
			criteria.addOrder(Order.asc(ListDataProjectDAO.ENTRY_ID));

			final List<ListDataProject> listDataProjects = criteria.list();

			final Map<Integer, Integer> mgidMap = new HashMap<>();

			for (final Germplasm germplasm : germplasmList) {
				mgidMap.put(germplasm.getGid(), germplasm.getMgid());
			}

			for (final ListDataProject ldp : listDataProjects) {
				if (mgidMap.containsKey(ldp.getGermplasmId())) {
					ldp.setGroupId(mgidMap.get(ldp.getGermplasmId()));
				}
			}

			return listDataProjects;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getByListId(listId=" + listId + ") query from ListDataProjectDAO: " + e.getMessage(),
					e);
		}

	}

	public ListDataProject getByListIdAndEntryNo(final int listId, final int entryNo) {
		ListDataProject result = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(ListDataProject.class);
			criteria.add(Restrictions.eq("list", new GermplasmList(listId)));
			criteria.add(Restrictions.eq(ListDataProjectDAO.ENTRY_ID, entryNo));
			criteria.addOrder(Order.asc(ListDataProjectDAO.ENTRY_ID));
			result = (ListDataProject) criteria.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByListIdAndEntryNo(listId=" + listId
					+ ") query from ListDataProjectDAO: " + e.getMessage(), e);
		}
		return result;
	}

	public List<ListDataProject> getByStudy(final int studyId, final GermplasmListType listType, final List<Integer> plotNumbers, final String instanceNumber) {
		try {

			final String queryStr = "select ldp.* FROM nd_experiment e,"
				+ " nd_experimentprop nd_ep, stock,"
				+ " listdata_project ldp, project_relationship pr, projectprop pp, listnms nms, nd_geolocation geo"
				+ " WHERE nd_ep.type_id IN (:PLOT_NO_TERM_IDS)" + " AND nms.projectid = pr.object_project_id"
				+ " AND nms.listid = ldp.list_id" + " AND pp.project_id = pr.subject_project_id"
				+ " AND nms.projectid = :STUDY_ID" + " AND pp.value = :DATASET_TYPE"
				+ " AND e.project_id = pr.subject_project_id"
				+ " AND e.nd_experiment_id = nd_ep.nd_experiment_id"
				+ " AND stock.stock_id = e.stock_id" + " AND ldp.germplasm_id = stock.dbxref_id"
				+ " AND nd_ep.value in (:PLOT_NO)"
				+ " AND nd_ep.nd_experiment_id = e.nd_experiment_id"
				+ " AND e.nd_geolocation_id = geo.nd_geolocation_id"
				+ " AND geo.description = :INSTANCE_NUMBER"
				+ " AND ( EXISTS (" + " SELECT 1" + " FROM listnms cl"
				+ " WHERE cl.listid = ldp.list_id" + " AND cl.listtype = 'CHECK'" + " AND NOT EXISTS ("
				+ " SELECT 1 FROM listnms nl" + " WHERE nl.listid = ldp.list_id" + " AND nl.listtype = :LIST_TYPE"
				+ " )) OR EXISTS (" + " SELECT 1 FROM listnms nl" + " WHERE nl.listid = ldp.list_id"
				+ " AND nl.listtype = :LIST_TYPE" + " ))";

			final SQLQuery query = this.getSession().createSQLQuery(queryStr);
			query.addEntity("ldp", ListDataProject.class);
			query.setParameter("LIST_TYPE", listType.name());
			query.setParameter("STUDY_ID", studyId);
			query.setParameterList("PLOT_NO", plotNumbers);
			query.setParameter("INSTANCE_NUMBER", instanceNumber);
			query.setParameter("DATASET_TYPE", DataSetType.PLOT_DATA.getId());
			query.setParameterList("PLOT_NO_TERM_IDS",
				new Integer[] { TermId.PLOT_NO.getId(), TermId.PLOT_NNO.getId() });

			return query.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in getStudy=" + studyId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}

	}

	public void deleteByListIdWithList(final int listId) {
		try {
			final String nmsListHql = "FROM GermplasmList nms " + "WHERE nms.id = :list_id";

			final Query query = this.getSession().createQuery(nmsListHql);
			query.setParameter("list_id", listId);

			final GermplasmList germplasmLists = (GermplasmList) query.uniqueResult();

			// delete listdataproj first
			final String deleteListDataProjHQL = "DELETE FROM ListDataProject ldp "
					+ "WHERE ldp.list = :germplasmLists";

			final Query query2 = this.getSession().createQuery(deleteListDataProjHQL);
			query2.setParameter("germplasmLists", germplasmLists);

			query2.executeUpdate();

			// delete listnms data
			final String deleteNMSHql = "DELETE FROM GermplasmList nms " + "WHERE nms.id = :list_id";

			final Query query3 = this.getSession().createQuery(deleteNMSHql);
			query3.setParameter("list_id", listId);
			query3.executeUpdate();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in deleteByListId=" + listId + " in ListDataProjectDAO: " + e.getMessage(), e);
		}
	}

	public long countByListId(final Integer id) {
		try {
			if (id != null) {
				final Criteria criteria = this.getSession().createCriteria(ListDataProject.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", id));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with countByListId(id=" + id + ") query from ListDataProject " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByListIdAndEntryType(final Integer id, final List<Integer> systemDefinedEntryTypeIds) {
		try {
			if (id != null) {
				final Criteria criteria = this.getSession().createCriteria(ListDataProject.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("l.id", id));
				criteria.add(Restrictions.in("checkType", systemDefinedEntryTypeIds));
				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with countByListIdAndEntryType(id=" + id + ") query from ListDataProject " + e.getMessage(),
					e);
		}
		return 0;
	}

	/**
	 * This will return all germplasm list data project records including the
	 * details of their parent germplasm. Note that we're getting the name of
	 * the parents from its preferred name which is indicated by name record
	 * with nstat = 1
	 */
	public List<ListDataProject> getListDataProjectWithParents(final Integer listID) {
		final List<ListDataProject> listDataProjects = new ArrayList<>();
		try {

			final String queryStr = "select lp.listdata_project_id as listdata_project_id, "
					+ " lp.entry_id as entry_id, " + " lp.designation as designation, "
					+ " lp.group_name as group_name, " + " if(g.gpid1 = 0, '" + Name.UNKNOWN + "', femaleParentName.nval) as fnval, " + " g.gpid1 as fpgid, "
					+ " if(g.gpid2 = 0, '" + Name.UNKNOWN + "', maleParentName.nval) as mnval, " + " g.gpid2 as mpgid, " + " g.mgid as mgid, "
					+ " g.gid as gid, " + " lp.seed_source as seed_source, "
					+ " lp.duplicate_notes as duplicate_notes, " + " lp.check_type as check_type, "
					+ " lp.entry_code as entry_code" + " from listdata_project lp "
					+ " left outer join germplsm g on lp.germplasm_id = g.gid "
					+ " left outer join names maleParentName on g.gpid2 = maleParentName.gid and maleParentName.nstat = :preferredNameNstat "
					+ " left outer join names femaleParentName on g.gpid1 = femaleParentName.gid and femaleParentName.nstat = :preferredNameNstat "
					+ " where lp.list_id = :listId " + " group by entry_id";

			final SQLQuery query = this.getSession().createSQLQuery(queryStr);
			query.setParameter("listId", listID);
			query.setParameter("preferredNameNstat", Name.NSTAT_PREFERRED_NAME);

			query.addScalar("listdata_project_id");
			query.addScalar("entry_id");
			query.addScalar("designation");
			query.addScalar("group_name");
			query.addScalar("fnval");
			query.addScalar("fpgid");
			query.addScalar("mnval");
			query.addScalar("mpgid");
			query.addScalar("mgid");
			query.addScalar("gid");
			query.addScalar("seed_source");
			query.addScalar("duplicate_notes");
			query.addScalar("check_type");
			query.addScalar("entry_code");

			this.createListDataProjectRows(listDataProjects, query);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getListDataProjectWithParents=" + listID + " in ListDataProjectDAO: " + e.getMessage(),
					e);
		}

		return listDataProjects;
	}

	@SuppressWarnings("unchecked")
	private void createListDataProjectRows(final List<ListDataProject> listDataProjects, final SQLQuery query) {
		final List<Object[]> result = query.list();

		for (final Object[] row : result) {
			final Integer listDataProjectId = (Integer) row[0];
			final Integer entryId = (Integer) row[1];
			final String designation = (String) row[2];
			final String parentage = (String) row[3];
			final String femaleParent = (String) row[4];
			final Integer fgid = (Integer) row[5];
			final String maleParent = (String) row[6];
			final Integer mpgid = (Integer) row[7];
			final Integer mgid = (Integer) row[8];
			final Integer gid = (Integer) row[9];
			final String seedSource = (String) row[10];
			final String duplicate = (String) row[11];
			final Integer checkType = (Integer) row[12];
			final String entryCode = (String) row[13];

			final ListDataProject listDataProject = new ListDataProject();
			listDataProject.setListDataProjectId(listDataProjectId);
			listDataProject.setEntryId(entryId);
			listDataProject.setDesignation(designation);
			listDataProject.setGroupName(parentage);
			listDataProject.setFemaleParent(new GermplasmParent(fgid, femaleParent, ""));
			listDataProject.addMaleParent(new GermplasmParent(mpgid, maleParent, ""));
			listDataProject.setGroupId(mgid);
			listDataProject.setGermplasmId(gid);
			listDataProject.setSeedSource(seedSource);

			listDataProject.setDuplicate(duplicate);
			listDataProject.setCheckType(checkType);
			listDataProject.setEntryCode(entryCode);

			listDataProjects.add(listDataProject);
		}

	}

	/**
	 * Verify if the gids are used in some entry list
	 * 
	 * @param gids
	 *            gids to check
	 * @return Map with GID as key and CSV of project where it is used
	 */
	public Map<Integer, String> getGermplasmUsedInEntryList(final List<Integer> gids) {
		final Map<Integer, String> resultMap = new HashMap<>();

		final SQLQuery query = this.getSession().createSQLQuery(ListDataProjectDAO.GET_GERMPLASM_USED_IN_ENTRY_LIST);
		query.setParameterList("gids", gids);

		final List<Object[]> results = query.list();
		for (final Object[] result : results) {
			resultMap.put((Integer) result[0], (String) result[1]);
		}

		return resultMap;
	}

	public ListDataProject getByListIdAndGid(final Integer listId, final Integer gid) {
		// Make sure parameters are not null.
		Preconditions.checkNotNull(listId, "List id passed cannot be null.");
		Preconditions.checkNotNull(gid, "Gid passed in cannot be null.");

		ListDataProject result = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(ListDataProject.class);
			criteria.add(Restrictions.eq("list.id", listId));
			criteria.add(Restrictions.eq("germplasmId", gid));
			criteria.addOrder(Order.asc(ListDataProjectDAO.ENTRY_ID));
			result = (ListDataProject) criteria.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByListIdAndGid(listId=" + listId
					+ ") query from ListDataProjectDAO: " + e.getMessage(), e);
		}
		return result;

	}
}
