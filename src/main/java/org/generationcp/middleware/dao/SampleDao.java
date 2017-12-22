
package org.generationcp.middleware.dao;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.sample.SampleGermplasmDetailDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleDao extends GenericDAO<Sample, Integer> {

	protected static final String SQL_SAMPLES_AND_EXPERIMENTS =
			"SELECT  nde.nd_experiment_id, (SELECT COALESCE(NULLIF(COUNT(sp.sample_id), 0), '-')\n FROM plant pl INNER JOIN\n"
					+ "            						sample AS sp ON pl.plant_id = sp.sample_id\n" + "        WHERE\n"
					+ "            						nde.nd_experiment_id = pl.nd_experiment_id) 'SAMPLES'"
					+ "		FROM project p INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id\n"
					+ "			INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id\n"
					+ "			INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id\n"
					+ "		WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr "
					+ "								INNER JOIN project p ON p.project_id = pr.subject_project_id\n"
					+ "        						WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))\n"
					+ "GROUP BY nde.nd_experiment_id";

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Sample.class);
		criteria.add(Restrictions.eq("sampleId", sampleId));
		return (Sample) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Sample> getByPlotId(final String plotId) {
		return this.getSession().createCriteria(Sample.class, "sample").createAlias("sample.plant", "plant")
				.createAlias("plant.experiment", "experiment").add(Restrictions.eq("experiment.plotId", plotId)).list();
	}

	public Map<Integer, String> getExperimentSampleMap(final Integer studyDbId) {
		final Map<Integer, String> samplesMap = new HashMap<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_SAMPLES_AND_EXPERIMENTS);

			query.setParameter("studyId", studyDbId);
			final List results = query.list();

			for (final Object o : results) {
				final Object[] result = (Object[]) o;
				if (result != null) {
					samplesMap.put((Integer) result[0], (String) result[1]);
				}
			}

		} catch (HibernateException he) {
			throw new MiddlewareException(
					"Unexpected error in executing getExperimentSampleMap(studyDbId = " + studyDbId + ") query: " + he.getMessage(), he);
		}
		return samplesMap;
	}

	public Sample getBySampleBk(final String sampleBk) {
		Sample sample;
		try {
			sample = (Sample) this.getSession().createCriteria(Sample.class, "sample").add(Restrictions.eq("sampleBusinessKey", sampleBk))
					.uniqueResult();
		} catch (HibernateException he) {
			throw new MiddlewareException(
					"Unexpected error in executing getBySampleBk(sampleBusinessKey = " + sampleBk + ") query: " + he.getMessage(), he);
		}
		return sample;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> getGIDsBySampleIds(final Set<Integer> sampleIds) {
		final Map<Integer, Integer> map = new HashMap<>();

		final List<Object[]> result = this.getSession().createCriteria(Sample.class, "sample").createAlias("sample.plant", "plant")
				.createAlias("plant.experiment", "experiment").createAlias("experiment.experimentStocks", "experimentStocks")
			.createAlias("experimentStocks.stock", "stock")
			.add(Restrictions.in("sampleId", sampleIds))
			.setProjection(Projections.projectionList()
				.add(Projections.property("sample.sampleId"))
				.add(Projections.property("stock.dbxrefId")))
				.list();
		for (final Object[] row : result) {
			map.put((Integer) row[0], (Integer) row[1]);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<Sample> getBySampleBks(final Set<String> sampleBks) {
		return this.getSession().createCriteria(Sample.class, "sample").add(Restrictions.in("sampleBusinessKey", sampleBks)).list();
	}

	public List<SampleGermplasmDetailDTO> getByGid(final Integer gid) {
		final List<Object[]> result = this.getSession()

				.createCriteria(Sample.class, "sample").createAlias("sample.plant", "plant")//
			.createAlias("sample.sampleList", "sampleList", Criteria.LEFT_JOIN)//
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)//
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)//

				.createAlias("plant.experiment", "experiment")//
				.createAlias("experiment.experimentStocks", "experimentStocks")//
				.createAlias("experimentStocks.stock", "stock")//
				.createAlias("experiment.project", "project")//
				.createAlias("project.relatedTos", "relatedTos")//
				.createAlias("relatedTos.objectProject", "objectProject")//
				.add(Restrictions.eq("stock.dbxrefId", gid))//
				.add(Restrictions.ne("project." + DmsProjectDao.DELETED, true))
			.addOrder(Order.desc("sample.sampleBusinessKey"))//

				.setProjection(Projections.distinct(Projections.projectionList()//
						.add(Projections.property("sampleList.listName"))//
						.add(Projections.property("sample.sampleBusinessKey"))//
						.add(Projections.property("experiment.plotId"))//
						.add(Projections.property("plant.plantBusinessKey"))//
						.add(Projections.property("dataset.datasetId"))//
						.add(Projections.property("dataset.datasetName"))//

						.add(Projections.property("objectProject.projectId"))//
						.add(Projections.property("objectProject.name"))//
						.add(Projections.property("objectProject.programUUID"))//
						.add(Projections.property("objectProject.studyType"))))//

				.list();//

		final HashMap<String, SampleGermplasmDetailDTO> samplesMap = new HashMap<>();
		for (final Object[] row : result) {
			final SampleGermplasmDetailDTO sample;

			final String sampleListName = (String) row[0];
			final String sampleBk = (String) row[1];
			final String plotId = (String) row[2];
			final String plantBk = (String) row[3];
			final Integer datasetId = (Integer) row[4];
			final String datasetName = (String) row[5];
			final Integer projectId = (Integer) row[6];
			final String studyName = (String) row[7];
			final String programUuid = (String) row[8];
			final StudyType studyType = (StudyType) row[9];

			if (samplesMap.containsKey(sampleBk)) {
				sample = samplesMap.get(sampleBk);
				sample.addDataset(datasetId, datasetName);
			} else {
				sample = new SampleGermplasmDetailDTO();
				sample.setSampleListName(sampleListName);
				sample.setSampleBk(sampleBk);
				sample.setPlotId(plotId);
				sample.setPlantBk(plantBk);
				sample.setStudy(new StudyReference(projectId, studyName, "", programUuid, studyType));
				sample.addDataset(datasetId, datasetName);
				samplesMap.put(sampleBk, sample);
			}
		}
		return new ArrayList<>(samplesMap.values());
	}
}
