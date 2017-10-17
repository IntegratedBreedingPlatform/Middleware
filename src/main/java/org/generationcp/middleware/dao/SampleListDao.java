
package org.generationcp.middleware.dao;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDetailsDTO;
import org.generationcp.middleware.domain.samplelist.SampleListDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.SampleList;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SampleListDao extends GenericDAO<SampleList, Integer> {

	public static final String ROOT_FOLDER = "Samples";

	private static final Logger LOG = LoggerFactory.getLogger(SampleListDao.class);
	public static final String SAMPLES = "samples";
	public static final String LIST_NAME = "listName";

	public SampleList getBySampleListName(final String sampleListName) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
		criteria.add(Restrictions.like(LIST_NAME, sampleListName));
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
	public SampleList getSampleListByParentAndName(final String sampleListName, final Integer parentId) {
		Preconditions.checkNotNull(sampleListName);
		Preconditions.checkNotNull(parentId);
		try {
			final SampleList parent = new SampleList();
			parent.setId(parentId);
			final DetachedCriteria criteria = DetachedCriteria.forClass(SampleList.class);
			criteria.add(Restrictions.eq(LIST_NAME, sampleListName));
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
			.setResultTransformer(Transformers.aliasToBean(SampleListDTO.class))
			;

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

		criteria.createAlias(SAMPLES, "sample")
			.createAlias("samples.plant", "plant")
			.createAlias("samples.takenBy", "user")
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
}
