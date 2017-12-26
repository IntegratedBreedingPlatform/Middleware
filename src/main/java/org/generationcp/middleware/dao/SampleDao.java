
package org.generationcp.middleware.dao;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleGermplasmDetailDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

	@SuppressWarnings("unchecked")
	public List<SampleDTO> getByPlotId(final String plotId) {


		final List<Object[]> result = this.getSession().createCriteria(Sample.class, "sample")
			.createAlias("sample.plant", "plant")
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy")
			.createAlias("takenBy.person", "person")
			.createAlias("plant.experiment", "experiment")
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)
			.setFetchMode("accMetadataSets", FetchMode.JOIN)
			.setFetchMode("dataset", FetchMode.JOIN)
			.add(Restrictions.eq("experiment.plotId", plotId))
			.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("sampleId")) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property("sampleBusinessKey")) //row[2]
				.add(Projections.property("person.firstName")) //row[3]
				.add(Projections.property("person.lastName")) //row[4]
				.add(Projections.property("sampleList.listName")) //row[5]
				.add(Projections.property("plant.plantNumber")) //row[6]
				.add(Projections.property("plant.plantBusinessKey")) //row[7]
				.add(Projections.property("dataset.datasetId")) //row[8]
				.add(Projections.property("dataset.datasetName")) //row[9]
			)).list();

		return this.getSampleDTOS(result);
	}

	private List<SampleDTO> getSampleDTOS(final List<Object[]> result) {
		final Map<Integer, SampleDTO> sampleDTOMap = new HashMap<>();
		for (final Object[] row : result) {

			final Integer sampleId = (Integer) row[0];
			SampleDTO dto = sampleDTOMap.get(sampleId);
			if (dto == null) {
				dto = new SampleDTO();
				dto.setSampleId(sampleId);
				dto.setSampleName((String) row[1]);
				dto.setSampleBusinessKey((String) row[2]);
				dto.setTakenBy(row[3] + " " + row[4]);
				dto.setSampleList((String) row[5]);
				dto.setPlantNumber((Integer) row[6]);
				dto.setPlantBusinessKey((String) row[7]);
				dto.setDatasets(new HashSet<SampleDTO.Dataset>());
			}

			if (row[8] != null && row[9] != null) {
				final SampleDTO.Dataset dataset;
				dataset = new SampleDTO().new Dataset();
				dataset.setDatasetId((Integer) row[8]);
				dataset.setName((String) row[9]);
				dto.getDatasets().add(dataset);
			}

			sampleDTOMap.put(sampleId, dto);
		}

		final ArrayList<SampleDTO> list = new ArrayList<>();
		list.addAll(sampleDTOMap.values());
		return list;
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

		} catch (final HibernateException he) {
			throw new MiddlewareException(
				"Unexpected error in executing getExperimentSampleMap(studyDbId = " + studyDbId + ") query: " + he.getMessage(), he);
		}
		return samplesMap;
	}

	public Sample getBySampleBk(final String sampleBk){
		final Sample sample;
		try {
			sample = (Sample) this.getSession().createCriteria(Sample.class, "sample").add(Restrictions.eq("sampleBusinessKey", sampleBk))
				.uniqueResult();
		} catch (final HibernateException he) {
			throw new MiddlewareException(
				"Unexpected error in executing getBySampleBk(sampleBusinessKey = " + sampleBk + ") query: " + he.getMessage(), he);
		}
		return sample;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> getGIDsBySampleIds(final Set<Integer> sampleIds) {
		final Map<Integer, Integer> map = new HashMap<>();
		final List<Object[]> result =  this.getSession()
			.createCriteria(Sample.class, "sample")
			.createAlias("sample.plant", "plant")
			.createAlias("plant.experiment", "experiment")
			.createAlias("experiment.experimentStocks", "experimentStocks")
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
	public List<SampleDTO> getBySampleBks(final Set<String> sampleBks) {

		final List<Object[]> result = this.getSession().createCriteria(Sample.class, "sample")
			.createAlias("sample.plant", "plant")
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy")
			.createAlias("takenBy.person", "person")
			.createAlias("plant.experiment", "experiment")
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)
			.setFetchMode("accMetadataSets", FetchMode.JOIN)
			.setFetchMode("dataset", FetchMode.JOIN)
			.add(Restrictions.in("sampleBusinessKey", sampleBks))
			.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("sampleId")) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property("sampleBusinessKey")) //row[2]
				.add(Projections.property("person.firstName")) //row[3]
				.add(Projections.property("person.lastName")) //row[4]
				.add(Projections.property("sampleList.listName")) //row[5]
				.add(Projections.property("plant.plantNumber")) //row[6]
				.add(Projections.property("plant.plantBusinessKey")) //row[7]
				.add(Projections.property("dataset.datasetId")) //row[8]
				.add(Projections.property("dataset.datasetName")) //row[9]
			)).list();

		return this.getSampleDTOS(result);

	}

	public List<SampleGermplasmDetailDTO> getByGid(final Integer gid){
		final List<Object[]> result =  this.getSession()

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

		final HashMap<String,SampleGermplasmDetailDTO> samplesMap = new HashMap<>();
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

			if(samplesMap.containsKey(sampleBk)){
				sample = samplesMap.get(sampleBk);
				sample.addDataset(datasetId, datasetName);
			}else{
				sample = new SampleGermplasmDetailDTO();
				sample.setSampleListName(sampleListName);
				sample.setSampleBk(sampleBk);
				sample.setPlotId(plotId);
				sample.setPlantBk(plantBk);
				sample.setStudy(new StudyReference(projectId, studyName, "", programUuid, studyType));
				sample.addDataset(datasetId, datasetName);
				samplesMap.put(sampleBk,sample);
			}
		}
		return new ArrayList<>(samplesMap.values());
	}
}
