
package org.generationcp.middleware.dao;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(SampleListDao.class);

	protected static final String ROOT_FOLDER = "Samples";
	protected static final String LIST_NAME = "listName";
	protected static final String PROGRAMUUID = "programUUID";
	protected static final String SAMPLES = "samples";
	protected static final Criterion RESTRICTED_LIST;

	static {
		RESTRICTED_LIST = Restrictions.not(Restrictions.eq("type", SampleListType.SAMPLE_LIST));
	}

	public SampleList getBySampleListName(final String sampleListName) {
		final DetachedCriteria criteria = this.getSampleListName(sampleListName);
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	private DetachedCriteria getSampleListName(final String sampleListName){
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		return criteria.add(Restrictions.like(SampleListDao.LIST_NAME, sampleListName));
	}

	public SampleList getRootSampleList() {
		final DetachedCriteria criteria = this.getSampleListName(SampleListDao.ROOT_FOLDER);
		criteria.add(Restrictions.isNull(SampleListDao.PROGRAMUUID));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public SampleList getParentSampleFolder(Integer id) {
		return this.getById(id);
	}
	/**
	 * Find a SampleList given the parent folder ID and the sample list name
	 * @param sampleListName
	 * @param parentId
	 * @return SampleList, null when not found
	 * @throws Exception
	 */
	public SampleList getSampleListByParentAndName(final String sampleListName, final Integer parentId) {
		Preconditions.checkNotNull(sampleListName);
		Preconditions.checkNotNull(parentId);
		try {
			final SampleList parent = new SampleList();
			parent.setId(parentId);
			final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
			criteria.add(Restrictions.eq(SampleListDao.LIST_NAME, sampleListName));
			criteria.add(Restrictions.eq("hierarchy", parent));
			return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
		} catch (Exception e) {
			final String message = "Error with getSampleListByParentAndName(sampleListName=" + sampleListName + ", parentId= " + parentId
				+ " ) query from SampleList: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public List<SampleListDTO> getSampleLists(final Integer trialId) {

		Criteria criteria = this.getSession().createCriteria(SampleList.class);

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Projections.property("id")), "listId");
		projectionList.add(Projections.property(LIST_NAME), LIST_NAME);

		criteria.createAlias(SAMPLES, SAMPLES)
			.createAlias("samples.plant", "plant")
			.createAlias("plant.experiment", "experiment")
			.createAlias("experiment.project", "project")
			.createAlias("project.relatedTos", "relatedTos")
			.createAlias("relatedTos.objectProject", "objectProject")
			.add(Restrictions.eq("objectProject.projectId", trialId))
			.setProjection(projectionList)
			.setResultTransformer(Transformers.aliasToBean(SampleListDTO.class));

		return criteria.list();
	}

	public List<SampleDetailsDTO> getSampleDetailsDTO(final Integer sampleListId) {

		Criteria criteria = this.getSession().createCriteria(SampleList.class);

		ProjectionList projectionList = Projections.projectionList();

		projectionList.add(Projections.property("stock.name"), "designation");
		projectionList.add(Projections.property("properties.value"), "plotNumber");
		projectionList.add(Projections.property("plant.plantNumber"), "plantNo");
		projectionList.add(Projections.property("sample.sampleName"), "sampleName");
		projectionList.add(Projections.property("user.name"), "takenBy");
		projectionList.add(Projections.property("sample.sampleBusinessKey"), "sampleBusinessKey");
		projectionList.add(Projections.property("plant.plantBusinessKey"), "plantBusinessKey");
		projectionList.add(Projections.property("experiment.plotId"), "plotId");
		projectionList.add(Projections.property("sample.samplingDate"), "sampleDate");
		projectionList.add(Projections.property("stock.dbxrefId"), "gid");

		criteria.createAlias(SAMPLES, "sample")
			.createAlias("samples.plant", "plant")
			.createAlias("samples.takenBy", "user", CriteriaSpecification.LEFT_JOIN)
			.createAlias("plant.experiment", "experiment")
			.createAlias("experiment.experimentStocks", "experimentStocks")
			.createAlias("experimentStocks.stock", "stock")
			.createAlias("experiment.properties", "properties")
			.add(Restrictions.eq("id", sampleListId))
			.add(Restrictions.eq("properties.typeId", TermId.PLOT_NO.getId()))
			.setProjection(projectionList)
			.setResultTransformer(Transformers.aliasToBean(SampleDetailsDTO.class))
		    .addOrder(Order.asc("sample.sampleId"));

		return criteria.list();
	}

	public List<SampleList> getAllTopLevelLists(final String programUUID) {
		final Criteria criteria;
		try {
			final Criterion topFolder = Restrictions.like("hierarchy.listName", SampleListDao.ROOT_FOLDER);
			final Criterion nullFolder = Restrictions.isNull("hierarchy.hierarchy");
			criteria = this.getSession().createCriteria(SampleList.class);
			criteria.createAlias("hierarchy", "hierarchy");
			criteria.add(Restrictions.and(topFolder, nullFolder));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
			criteria.addOrder(Order.asc("listName"));

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAllTopLevelLists() query from SampleList: " + e.getMessage(), e);
		}
		return criteria.list();
	}

	/**
	 * Gets the germplasm list children.
	 *
	 * @param parentId the parent id
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
			throw new MiddlewareQueryException("Error with getByParentFolderId(parentId=" + parentId + ") query from SampleList: " + e.getMessage(),
				e);
		}
		return new ArrayList<>();
	}

	/**
	 * @param folderIds a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	public Map<Integer, GermplasmFolderMetadata> getSampleFolderMetadata(final List<Integer> folderIds) {
		final List<GermplasmFolderMetadata> list;
		if (folderIds.isEmpty()) {
			return Collections.emptyMap();
		}

		try {
			final String folderMetaDataQuery = "SELECT parent.list_id AS listId, COUNT(child.list_id) AS numberOfChildren "
				+ "FROM sample_list parent  LEFT OUTER JOIN sample_list child ON child.hierarchy = parent.list_id "
				+ "WHERE parent.list_id IN (:folderIds) GROUP BY parent.list_id";
			final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
			setResultTransformer.setParameterList("folderIds", folderIds);
			setResultTransformer.addScalar("listId", new IntegerType());
			setResultTransformer.addScalar("numberOfChildren", new IntegerType());
			setResultTransformer.setResultTransformer(Transformers.aliasToBean(GermplasmFolderMetadata.class));
			list = setResultTransformer.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getSampleFolderMetadata(folderIds=" + folderIds.toString() + ") query from sample_list: " + e.getMessage(), e);
		}
		return Maps.uniqueIndex(list, new Function<GermplasmFolderMetadata, Integer>() {

			@Override
			public Integer apply(final GermplasmFolderMetadata folderMetaData) {
				return folderMetaData.getListId();
			}
		});
	}

	@Nullable
	public SampleList getLastCreatedByUserID(final Integer userID, final String programUUID) {
		try {
			if (userID != null) {
				final Criteria criteria = this.getSession().createCriteria(SampleList.class);
				criteria.add(Restrictions.eq("createdBy.userid", userID));

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
			throw new MiddlewareQueryException("Error with getByUserID(userID=" + userID + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return null;
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

}
