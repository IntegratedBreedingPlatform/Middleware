package org.generationcp.middleware.dao;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.util.projection.CustomProjections;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(SampleListDao.class);

	protected static final Integer ROOT_FOLDER_ID = 1;
	protected static final String LIST_NAME = "listName";
	protected static final String PROGRAMUUID = "programUUID";
	protected static final String SAMPLES = "samples";
	protected static final Criterion RESTRICTED_LIST;
	protected static final String SEARCH_SAMPLE_LIST_CONTAINS =
		"SELECT DISTINCT sample_list.list_id as id, sample_list.list_name as listName, sample_list.description as description FROM sample_list \n"
			+ "LEFT JOIN sample ON sample.sample_list=sample_list.list_id\n"
			+ "WHERE sample_list.type = :listType AND (sample_list.program_uuid = :program_uuid OR sample_list.program_uuid IS NULL) AND (sample_list.list_name LIKE :searchString\n"
			+ "OR sample.sample_name LIKE :searchString\n" + "OR sample.sample_bk LIKE :searchString) ";
	protected static final String SEARCH_SAMPLE_LIST_EXACT_MATCH =
		"SELECT DISTINCT sample_list.list_id as id, sample_list.list_name as listName, sample_list.description as description FROM sample_list \n"
			+ "LEFT JOIN sample ON sample.sample_list=sample_list.list_id\n"
			+ "WHERE sample_list.type = :listType AND (sample_list.program_uuid = :program_uuid OR sample_list.program_uuid IS NULL) AND (sample_list.list_name = :searchString\n"
			+ "OR sample.sample_name = :searchString\n" + "OR sample.sample_bk = :searchString) ";

	static {
		RESTRICTED_LIST = Restrictions.not(Restrictions.eq("type", SampleListType.SAMPLE_LIST));
	}

	public SampleListDao(final Session session) {
		super(session);
	}

	public SampleList getRootSampleList() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.eq("id", SampleListDao.ROOT_FOLDER_ID));
		criteria.add(Restrictions.isNull(SampleListDao.PROGRAMUUID));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public SampleList getParentSampleFolder(final Integer id) {
		return this.getById(id);
	}

	/**
	 * Find a SampleList given the parent folder ID and the sample list name
	 *
	 * @param sampleListName
	 * @param parentId
	 * @param programUUID
	 * @return SampleList, null when not found
	 * @throws Exception
	 */
	public SampleList getSampleListByParentAndName(final String sampleListName, final Integer parentId, final String programUUID) {
		Preconditions.checkNotNull(sampleListName);
		Preconditions.checkNotNull(parentId);
		try {
			final SampleList parent = new SampleList();
			parent.setId(parentId);
			final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
			criteria.add(Restrictions.eq(SampleListDao.LIST_NAME, sampleListName));
			criteria.add(Restrictions.eq("hierarchy", parent));
			criteria.add(Restrictions.eq(SampleListDao.PROGRAMUUID, programUUID));
			return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
		} catch (final Exception e) {
			final String message = "Error with getSampleListByParentAndName(sampleListName=" + sampleListName + ", parentId= " + parentId
				+ " ) query from SampleList: " + e.getMessage();
			SampleListDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public List<SampleListDTO> getSampleLists(final List<Integer> datasetIds) {

		final Criteria criteria = this.getSession().createCriteria(SampleList.class);

		final ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("id")), "listId");
		projectionList.add(Projections.property(SampleListDao.LIST_NAME), SampleListDao.LIST_NAME);

		criteria.createAlias(SampleListDao.SAMPLES, SampleListDao.SAMPLES)
			.createAlias("samples.experiment", "experiment").createAlias("experiment.project", "project")
			.add(Restrictions.in("project.projectId", datasetIds)).setProjection(projectionList)
			.setResultTransformer(Transformers.aliasToBean(SampleListDTO.class));

		return criteria.list();
	}

	public List<SampleList> searchSampleLists(
		final String searchString, final boolean exactMatch, final String programUUID, final Pageable pageable) {

		final SQLQuery query = this.getSession()
			.createSQLQuery(this.addOrder(exactMatch ? SEARCH_SAMPLE_LIST_EXACT_MATCH : SEARCH_SAMPLE_LIST_CONTAINS, pageable));

		query.setParameter("searchString", searchString + (exactMatch ? "" : "%"));
		query.setParameter("listType", SampleListType.SAMPLE_LIST.toString());
		query.setParameter("program_uuid", programUUID);

		query.addScalar("id", new IntegerType());
		query.addScalar("listName", new StringType());
		query.addScalar("description", new StringType());
		query.setResultTransformer(Transformers.aliasToBean(SampleList.class));

		return query.list();

	}

	public List<SampleDetailsDTO> getSampleDetailsDTO(final Integer sampleListId) {

		final Criteria criteria = this.getSession().createCriteria(SampleList.class);

		final ProjectionList projectionList = Projections.projectionList();

		projectionList.add(Projections.property("name.nval"), "designation");
		projectionList.add(Projections.property("sample.sampleNumber"), "sampleNumber");
		;
		projectionList.add(Projections.property("sample.sampleId"), "sampleId");
		projectionList.add(Projections.property("sample.sampleName"), "sampleName");
		projectionList.add(Projections.property("sample.entryNumber"), "entryNumber");
		projectionList.add(Projections.property("sample.takenBy"), "takenByUserId");
		projectionList.add(Projections.property("sample.sampleBusinessKey"), "sampleBusinessKey");
		projectionList.add(Projections.property("experiment.obsUnitId"), "obsUnitId");
		// TODO move PLOT_NO to nd_exp observation_unit_no and change type to OBSERVATION_UNIT
		projectionList.add(CustomProjections.coalesce("experiment.observationUnitNo", "properties.value"), "observationUnitNumber");
		projectionList.add(Projections.property("sample.samplingDate"), "sampleDate");
		projectionList.add(Projections.property("germplasm.gid"), "gid");
		projectionList.add(Projections.property("sample.plateId"), "plateId");
		projectionList.add(Projections.property("sample.well"), "well");
		projectionList.add(Projections.property("parent_properties.value"), "plotNo");

		criteria.createAlias(SampleListDao.SAMPLES, "sample")
			.createAlias("samples.experiment", "experiment")
			.createAlias("experiment.properties", "properties", JoinType.LEFT_OUTER_JOIN,
				Restrictions.eq("properties.typeId", TermId.PLOT_NO.getId()))
			.createAlias("experiment.parent", "parent_experiment", JoinType.LEFT_OUTER_JOIN)
			.createAlias("parent_experiment.properties", "parent_properties", JoinType.LEFT_OUTER_JOIN,
				Restrictions.eq("parent_properties.typeId", TermId.PLOT_NO.getId()))
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.createAlias(
				"germplasm.names", "name", JoinType.LEFT_OUTER_JOIN, Restrictions.eq("name.nstat", 1))
			.add(Restrictions.eq("id", sampleListId))
			.setProjection(projectionList)
			.setResultTransformer(Transformers.aliasToBean(SampleDetailsDTO.class)).addOrder(Order.asc("sample.sampleId"));

		return criteria.list();
	}

	public List<SampleList> getAllTopLevelLists(final String programUUID) {
		final Criteria criteria;
		try {
			final Criterion topFolder = Restrictions.eq("hierarchy.id", SampleListDao.ROOT_FOLDER_ID);
			final Criterion nullFolder = Restrictions.isNull("hierarchy");
			final Criterion sameProgramUUID = Restrictions.eq(SampleListDao.PROGRAMUUID, programUUID);
			criteria = this.getSession().createCriteria(SampleList.class);
			criteria.createAlias("hierarchy", "hierarchy");
			criteria.add(Restrictions.or(topFolder, nullFolder));

			if (programUUID != null) {
				criteria.add(sameProgramUUID);
			} else {
				criteria.add(Restrictions.isNull(SampleListDao.PROGRAMUUID));
			}

			criteria.addOrder(Order.asc("listName"));

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAllTopLevelLists() query from SampleList: " + e.getMessage(), e);
		}
		return criteria.list();
	}

	/**
	 * Gets the germplasm list children.
	 *
	 * @param parentId    the parent id
	 * @param programUUID the program UUID
	 * @return the sample list children
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<SampleList> getByParentFolderId(final Integer parentId, final String programUUID) {
		try {

			if (parentId != null) {
				final Criteria criteria = this.getSession().createCriteria(SampleList.class);
				criteria.add(Restrictions.eq("hierarchy.id", parentId));
				criteria.add(Restrictions.eq(SampleListDao.PROGRAMUUID, programUUID));
				criteria.addOrder(Order.asc(SampleListDao.LIST_NAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getByParentFolderId(parentId=" + parentId + ") query from SampleList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	/**
	 * @param folderIds a group of folder ids/sample lists for which we want to return metadata
	 * @return the resultant map which contains the object meta data
	 */
	public Map<Integer, ListMetadata> getSampleListMetadata(final List<Integer> folderIds) {
		final List<ListMetadata> list;
		if (folderIds.isEmpty()) {
			return Collections.emptyMap();
		}

		try {
			final String folderMetaDataQuery = "SELECT parent.list_id AS listId," + "  COUNT(child.list_id) AS numberOfChildren, "
				+ "  COUNT(s.sample_id) AS numberOfEntries " + " FROM sample_list parent"
				+ "   LEFT OUTER JOIN sample_list child ON child.hierarchy = parent.list_id "
				+ "   LEFT OUTER JOIN sample s ON s.sample_list = parent.list_id "
				+ " WHERE parent.list_id IN (:folderIds) GROUP BY parent.list_id";
			final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
			setResultTransformer.setParameterList("folderIds", folderIds);
			setResultTransformer.addScalar("listId", new IntegerType());
			setResultTransformer.addScalar("numberOfChildren", new IntegerType());
			setResultTransformer.addScalar("numberOfEntries", new IntegerType());
			setResultTransformer.setResultTransformer(Transformers.aliasToBean(ListMetadata.class));
			list = setResultTransformer.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getSampleListMetadata(folderIds=" + folderIds + ") query from sample_list: " + e.getMessage(),
				e);
		}
		return Maps.uniqueIndex(list, new Function<ListMetadata, Integer>() {

			@Override
			public Integer apply(final ListMetadata folderMetaData) {
				return folderMetaData.getListId();
			}
		});
	}

	public long countSampleGenotypesBySampleList(final Integer listId) {
		try {
			final String sql = "SELECT COUNT(1) FROM sample s "
				+ " INNER JOIN sample_list list ON s.sample_list = list.list_id "
				+ " INNER JOIN genotype g ON g.sample_id = s.sample_id "
				+ " WHERE list.list_id = :listId ";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("listId", listId);
			return ((BigInteger) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String message = "Error with countSampleGenotypesBySampleList(listId=" + listId + "): " + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}
	}

	@Nullable
	public SampleList getLastCreatedByUserID(final Integer userID, final String programUUID) {
		try {
			if (userID != null) {
				final Criteria criteria = this.getSession().createCriteria(SampleList.class);
				criteria.add(Restrictions.eq("createdByUserId", userID));

				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

				this.hideSnapshotListTypes(criteria);

				criteria.addOrder(Order.desc("id"));

				final List result = criteria.list();
				if (!result.isEmpty()) {
					return (SampleList) result.get(0);
				} else {
					return null;
				}
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getByUserID(userID=" + userID + ") query from GermplasmList: " + e.getMessage(),
				e);
		}
		return null;
	}

	public List<SampleListDTO> getSampleListsByStudy(final Integer studyId, final boolean withGenotypesOnly) {

		final StringBuilder sql =
			new StringBuilder("SELECT DISTINCT sl.list_id as listId, sl.list_name as listName, sl.description as description FROM "
				+ "sample_list sl "
				+ "INNER JOIN sample s ON s.sample_list = sl.list_id "
				+ "LEFT JOIN nd_experiment nde ON nde.nd_experiment_id = s.nd_experiment_id "
				+ "LEFT JOIN project p ON p.project_id = nde.project_id "
				+ "WHERE sl.type = 'SAMPLE_LIST' AND p.study_id = :studyId ");

		if (withGenotypesOnly) {
			sql.append(" AND EXISTS(SELECT 1 FROM genotype WHERE sample_id = s.sample_id) ");
		}

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql.toString());
		sqlQuery.setParameter("studyId", studyId);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SampleListDTO.class));
		return sqlQuery.list();
	}

	private void addCriteriaForProgramUUIDInLists(final String programUUID, final Criteria criteria) {
		final Criterion sameProgramUUID = Restrictions.eq(SampleListDao.PROGRAMUUID, programUUID);
		final Criterion nullProgramUUID = Restrictions.isNull(SampleListDao.PROGRAMUUID);
		criteria.add(Restrictions.or(sameProgramUUID, nullProgramUUID));
	}

	protected void hideSnapshotListTypes(final Criteria criteria) {
		criteria.add(this.getRestrictedSnapshopTypes());
	}

	protected Criterion getRestrictedSnapshopTypes() {
		return SampleListDao.RESTRICTED_LIST;
	}

	String addOrder(final String queryString, final Pageable pageable) {

		final StringBuilder stringBuilder = new StringBuilder(queryString);

		if (pageable == null || pageable.getSort() == null) {
			return stringBuilder.toString();
		}

		stringBuilder.append("ORDER BY ");

		final Iterator<Sort.Order> iterator = pageable.getSort().iterator();
		while (iterator.hasNext()) {
			final Sort.Order order = iterator.next();
			stringBuilder.append(order.getProperty() + " " + order.getDirection().name());
			if (iterator.hasNext())
				stringBuilder.append(",");
		}

		return stringBuilder.toString();
	}

}
