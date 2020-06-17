package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InstanceDao extends GenericDAO<ExperimentModel, Integer> {

	public List<ExperimentModel> getEnvironmentsByDataset(final Integer datasetId, final boolean isEnvironmentDataset) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass(), "environment");

		if (isEnvironmentDataset) {
			criteria.add(Restrictions.eq("project.projectId", datasetId));
		} else {
			final DetachedCriteria childExperimentsCriteria = DetachedCriteria.forClass(ExperimentModel.class, "childExperiment");
			childExperimentsCriteria.add(Restrictions.eq("childExperiment.project.projectId", datasetId));
			childExperimentsCriteria.setProjection(Projections.distinct(Projections.property("childExperiment.parent.ndExperimentId")));
			criteria.add(Property.forName("ndExperimentId").in(childExperimentsCriteria));
		}
		return criteria.list();
	}

	public Boolean instancesExist(final Set<Integer> instanceIds) {
		for (final Integer instanceId : instanceIds) {
			if (this.getById(instanceId) == null) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	public List<ExperimentModel> getEnvironmentsForInstances(final Integer studyId, final List<Integer> instanceNumbers) {
		List<ExperimentModel> returnList = new ArrayList<>();
		if (studyId != null) {
			final String sql = "SELECT DISTINCT exp.* " + //
				" FROM nd_experiment exp " + //
				" INNER JOIN project envdataset on (envdataset.project_id = exp.project_id) " + //
				" WHERE envdataset.study_id = :studyId and envdataset.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId();
			final StringBuilder sb = new StringBuilder(sql);
			if (!CollectionUtils.isEmpty(instanceNumbers)) {
				sb.append(" AND exp.observation_unit_no IN (:instanceNumbers) ");
			}
			sb.append(" ORDER by exp.observation_unit_no ASC");
			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.addEntity("exp", ExperimentModel.class);
			query.setParameter("studyId", studyId);
			if (!CollectionUtils.isEmpty(instanceNumbers)) {
				query.setParameterList("instanceNumbers", instanceNumbers);
			}
			returnList = query.list();

		}
		return returnList;
	}

}
