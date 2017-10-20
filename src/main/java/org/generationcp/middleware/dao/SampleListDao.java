
package org.generationcp.middleware.dao;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
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

	protected static final String ROOT_FOLDER = "Samples";
	protected static final String LIST_NAME = "listName";

	private static final Logger LOG = LoggerFactory.getLogger(SampleListDao.class);

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
		criteria.add(Restrictions.isNull("programUUID"));
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

	public List<SampleList> getAllTopLevelLists(final String programUUID) {
		try {
			//return this.getRootSampleList().getChildren();

			final Criterion topFolder = Restrictions.eq("hierarchy.id", this.getRootSampleList().getId());
			final Criterion nullFolder = Restrictions.isNull("hierarchy");
			final Criteria criteria = this.getSession().createCriteria(SampleList.class);
			criteria.add(Restrictions.or(topFolder, nullFolder));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
			this.hideSnapshotListTypes(criteria);

			criteria.addOrder(Order.asc("listName"));
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getAllTopLevelLists() query from SampleList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
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
				criteria.addOrder(Order.asc(SampleListDao.LIST_NAME));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(
				"Error with getByParentFolderId(parentId=" + parentId + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	/**
	 * @param folderIds a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	public Map<Integer, GermplasmFolderMetadata> getSampleFolderMetadata(final List<Integer> folderIds) {

		if (folderIds.isEmpty()) {
			return Collections.emptyMap();
		}

		final String folderMetaDataQuery = "SELECT parent.list_id AS listId, COUNT(child.list_id) AS numberOfChildren "
			+ "FROM sample_list parent  LEFT OUTER JOIN sample_list child ON child.hierarchy = parent.list_id "
			+ "WHERE parent.list_id IN (:folderIds) GROUP BY parent.list_id";
		final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
		setResultTransformer.setParameterList("folderIds", folderIds);
		setResultTransformer.addScalar("listId", new IntegerType());
		setResultTransformer.addScalar("numberOfChildren", new IntegerType());
		setResultTransformer.setResultTransformer(Transformers.aliasToBean(GermplasmFolderMetadata.class));
		final List<GermplasmFolderMetadata> list = setResultTransformer.list();
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
			this.logAndThrowException("Error with getByUserID(userID=" + userID + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return null;
	}

	private void addCriteriaForProgramUUIDInLists(final String programUUID, final Criteria criteria) {
		final Criterion sameProgramUUID = Restrictions.eq("programUUID", programUUID);
		final Criterion nullProgramUUID = Restrictions.isNull("programUUID");
		criteria.add(Restrictions.or(sameProgramUUID, nullProgramUUID));
	}

	protected void hideSnapshotListTypes(final Criteria criteria) {
		criteria.add(this.getRestrictedSnapshopTypes());
	}

	protected Criterion getRestrictedSnapshopTypes() {
		return SampleListDao.RESTRICTED_LIST;
	}

}
