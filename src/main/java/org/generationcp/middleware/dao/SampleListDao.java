
package org.generationcp.middleware.dao;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	public static final String ROOT_FOLDER = "Samples";

	private static final Logger LOG = LoggerFactory.getLogger(SampleListDao.class);

	public SampleList getBySampleListName(final String sampleListName) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.like("listName", sampleListName));
		return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public SampleList getRootSampleList() {
		return this.getBySampleListName(ROOT_FOLDER);
	}

	/**
	 * Find a SampleList given the parent folder ID and the sample list name
	 * @param sampleListName
	 * @param parentId
	 * @return SampleList, null when not found
	 * @throws Exception
	 */
	public SampleList getSampleListByParentAndName(final String sampleListName, final Integer parentId) throws Exception {
		Preconditions.checkNotNull(sampleListName);
		Preconditions.checkNotNull(parentId);
		try {
			final SampleList parent = new SampleList();
			parent.setId(parentId);
			final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
			criteria.add(Restrictions.eq("listName", sampleListName));
			criteria.add(Restrictions.eq("hierarchy", parent));
			return (SampleList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
		} catch (Exception e) {
			final String message = "Error with getSampleListByParentAndName(sampleListName=" + sampleListName + ", parentId= " + parentId
				+ " ) query from SampleList: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public List getSampleLists(final Integer trialId) {
		Criteria criteria = this.getSession().createCriteria(SampleList.class);
		criteria.createAlias("samples", "samples", CriteriaSpecification.INNER_JOIN)
			.createAlias("samples.plant", "plant", CriteriaSpecification.INNER_JOIN)
			.createAlias("plant.experiment", "experiment", CriteriaSpecification.INNER_JOIN)
			.createAlias("experiment.project", "project", CriteriaSpecification.INNER_JOIN)
			.createAlias("project.relatedTos", "relatedTos", CriteriaSpecification.INNER_JOIN)
			.createAlias("relatedTos.objectProject", "objectProject", CriteriaSpecification.INNER_JOIN)
			.add(Restrictions.eq("objectProject.projectId", trialId))
		;

		return criteria.list();
	}
}
