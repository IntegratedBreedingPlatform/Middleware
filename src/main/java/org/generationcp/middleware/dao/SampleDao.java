
package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.sample.SampleGermplasmDetailDTO;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

	public static final String SQL_STUDY_HAS_SAMPLES = "SELECT COUNT(sp.sample_id) AS Sample FROM project p INNER JOIN\n"
			+ "    project_relationship pr ON p.project_id = pr.subject_project_id INNER JOIN\n"
			+ "    nd_experiment_project ep ON pr.subject_project_id = ep.project_id INNER JOIN\n"
			+ "    nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id INNER JOIN\n"
			+ "    plant AS pl ON nde.nd_experiment_id = pl.nd_experiment_id INNER JOIN\n"
			+ "    sample AS sp ON pl.plant_id = sp.sample_id WHERE p.project_id = (SELECT \n"
			+ "            p.project_id FROM project_relationship pr INNER JOIN\n"
			+ "            project p ON p.project_id = pr.subject_project_id WHERE\n"
			+ "            (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))\n" + "GROUP BY pl.nd_experiment_id";

	private static final String SAMPLE = "sample";
	private static final String SAMPLE_PLANT = "sample.plant";
	private static final String PLANT = "plant";
	private static final String PLANT_EXPERIMENT = "plant.experiment";
	private static final String EXPERIMENT = "experiment";
	private static final String SAMPLE_BUSINESS_KEY = "sampleBusinessKey";
	public static final String SAMPLE_ID = "sampleId";

	public List<SampleDTO> filter(final String plotId, final Integer listId, final Pageable pageable) {
		final Criteria criteria = getSession().createCriteria(Sample.class, SAMPLE);
		addOrder(criteria, pageable);
		if (StringUtils.isNotBlank(plotId)) {
			criteria.add(Restrictions.eq("experiment.plotId", plotId));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}
		if (pageable != null) {
			return getSampleDTOS(criteria, pageable);
		}
		return getSampleDTOS(criteria);
	}

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Sample.class);
		criteria.add(Restrictions.eq(SAMPLE_ID, sampleId));
		return (Sample) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public long countFilter(final String plotId, final Integer listId) {
		final Criteria criteria = getSession().createCriteria(Sample.class, SAMPLE);
		if (StringUtils.isNotBlank(plotId)) {
			criteria.add(Restrictions.eq("experiment.plotId", plotId));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}
		criteria.createAlias(SAMPLE_PLANT, PLANT)
				.createAlias("sample.sampleList", "sampleList")
				.createAlias(PLANT_EXPERIMENT, EXPERIMENT)
				.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private List<SampleDTO> getSampleDTOS(final Criteria criteria) {
	    if (criteria == null) {
	    	return Collections.<SampleDTO>emptyList();
		}
		final List<Object[]> result = criteria
			.createAlias(SAMPLE_PLANT, PLANT)
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy")
			.createAlias("takenBy.person", "person")
			.createAlias(PLANT_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.experimentStocks", "experimentStocks")
			.createAlias("experimentStocks.stock", "stock")
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(SAMPLE_ID)) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property(SAMPLE_BUSINESS_KEY)) //row[2]
				.add(Projections.property("person.firstName")) //row[3]
				.add(Projections.property("person.lastName")) //row[4]
				.add(Projections.property("sampleList.listName")) //row[5]
				.add(Projections.property("plant.plantNumber")) //row[6]
				.add(Projections.property("plant.plantBusinessKey")) //row[7]
				.add(Projections.property("dataset.datasetId")) //row[8]
				.add(Projections.property("dataset.datasetName")) //row[9]
				.add(Projections.property("stock.dbxrefId")) //row[10]
				.add(Projections.property("stock.name")) //row[11] TODO preferred name
				.add(Projections.property("samplingDate")) //row[12]
				.add(Projections.property("entryNumber")) //row[13]
			)).list();

		return mapSampleDTOS(result);
	}

	private List<SampleDTO> mapSampleDTOS(final List<Object[]> result) {
		final Map<Integer, SampleDTO> sampleDTOMap = new LinkedHashMap<>();
		for (final Object[] row : result) {

			final Integer sampleId = (Integer) row[0];
			SampleDTO dto = sampleDTOMap.get(sampleId);
			if (dto == null) {
				dto = new SampleDTO();
				dto.setEntryNo((Integer) row[13]);
				dto.setSampleId(sampleId);
				dto.setSampleName((String) row[1]);
				dto.setSampleBusinessKey((String) row[2]);
				dto.setTakenBy(row[3] + " " + row[4]);
				dto.setSampleList((String) row[5]);
				dto.setPlantNumber((Integer) row[6]);
				dto.setPlantBusinessKey((String) row[7]);
				dto.setGid((Integer) row[10]);
				dto.setDesignation((String) row[11]);
				if (row[12] != null) {
					dto.setSamplingDate((Date) row[12]);
				}
				dto.setDatasets(new HashSet<SampleDTO.Dataset>());
			}

			if ((row[8] != null) && (row[9] != null)) {
				final SampleDTO.Dataset dataset;
				dataset = new SampleDTO().new Dataset();
				dataset.setDatasetId((Integer) row[8]);
				dataset.setName((String) row[9]);
				dto.getDatasets().add(dataset);
			}

			sampleDTOMap.put(sampleId, dto);
		}

		return new ArrayList<>(sampleDTOMap.values());
	}

	@SuppressWarnings("unchecked")
	private List<SampleDTO> getSampleDTOS(final Criteria criteria, final Pageable pageable) {
		if (criteria == null) {
			return Collections.<SampleDTO>emptyList();
		}

		final int pageSize = pageable.getPageSize();
		int start = pageSize * pageable.getPageNumber();

		// TODO add datasets
		final List<Object[]> result = criteria
			.setFirstResult(start)
			.setMaxResults(pageSize)
			.createAlias(SAMPLE_PLANT, PLANT)
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy")
			.createAlias("takenBy.person", "person")
			.createAlias(PLANT_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.experimentStocks", "experimentStocks")
			.createAlias("experimentStocks.stock", "stock")
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("sampleId")) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property(SAMPLE_BUSINESS_KEY)) //row[2]
				.add(Projections.property("person.firstName")) //row[3]
				.add(Projections.property("person.lastName")) //row[4]
				.add(Projections.property("sampleList.listName")) //row[5]
				.add(Projections.property("plant.plantNumber")) //row[6]
				.add(Projections.property("plant.plantBusinessKey")) //row[7]
				.add(Projections.property("stock.dbxrefId")) //row[8]
				.add(Projections.property("stock.name")) //row[9] TODO preferred name
				.add(Projections.property("samplingDate")) //row[10]
				.add(Projections.property("entryNumber")) //row[11]
			)).list();

		final Map<Integer, SampleDTO> sampleDTOMap = new LinkedHashMap<>();
		for (final Object[] row : result) {

			final Integer sampleId = (Integer) row[0];
			SampleDTO dto = sampleDTOMap.get(sampleId);
			if (dto == null) {
				dto = new SampleDTO();
				dto.setEntryNo((Integer) row[11]);
				dto.setSampleId(sampleId);
				dto.setSampleName((String) row[1]);
				dto.setSampleBusinessKey((String) row[2]);
				dto.setTakenBy(row[3] + " " + row[4]);
				dto.setSampleList((String) row[5]);
				dto.setPlantNumber((Integer) row[6]);
				dto.setPlantBusinessKey((String) row[7]);
				dto.setGid((Integer) row[8]);
				dto.setDesignation((String) row[9]);
				if (row[10] != null) {
					dto.setSamplingDate((Date) row[10]);
				}
				// TODO add datasets
			}

			sampleDTOMap.put(sampleId, dto);
		}

		return new ArrayList<>(sampleDTOMap.values());
	}

	@SuppressWarnings("rawtypes")
	public Map<Integer, String> getExperimentSampleMap(final Integer studyDbId) {
		final Map<Integer, String> samplesMap = new HashMap<>();
		try {
			final SQLQuery query = getSession().createSQLQuery(SQL_SAMPLES_AND_EXPERIMENTS);

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
			sample = (Sample) getSession().createCriteria(Sample.class, SAMPLE).add(Restrictions.eq(SAMPLE_BUSINESS_KEY, sampleBk))
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
		final List<Object[]> result = getSession()
			.createCriteria(Sample.class, SAMPLE)
			.createAlias(SAMPLE_PLANT, PLANT)
			.createAlias(PLANT_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.experimentStocks", "experimentStocks")
			.createAlias("experimentStocks.stock", "stock")
			.add(Restrictions.in(SAMPLE_ID, sampleIds))
			.setProjection(Projections.projectionList()
				.add(Projections.property("sample.sampleId"))
				.add(Projections.property("stock.dbxrefId")))
			.list();
		for (final Object[] row : result) {
			map.put((Integer) row[0], (Integer) row[1]);
		}
		return map;
	}

	public List<SampleDTO> getBySampleBks(final Set<String> sampleUIDs) {
		return getSampleDTOS(getSession().createCriteria(Sample.class, SAMPLE) //
				.add(Restrictions.in(SAMPLE_BUSINESS_KEY, sampleUIDs)));
	}

	@SuppressWarnings("unchecked")
	public List<SampleGermplasmDetailDTO> getByGid(final Integer gid){
		final List<Object[]> result = getSession()

			.createCriteria(Sample.class, SAMPLE).createAlias(SAMPLE_PLANT, PLANT)//
			.createAlias("sample.sampleList", "sampleList", CriteriaSpecification.LEFT_JOIN)//
			.createAlias("sample.accMetadataSets", "accMetadataSets", CriteriaSpecification.LEFT_JOIN)//
			.createAlias("accMetadataSets.dataset", "dataset", CriteriaSpecification.LEFT_JOIN)//
			.createAlias(PLANT_EXPERIMENT, EXPERIMENT)//

			.createAlias("experiment.experimentStocks", "experimentStocks")//
			.createAlias("experimentStocks.stock", "stock")//
			.createAlias("experiment.project", "project")//
			.createAlias("project.relatedTos", "relatedTos")//
			.createAlias("relatedTos.objectProject", "objectProject")//
			.createAlias("objectProject.studyType", "studyType")//
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
				.add(Projections.property("studyType.studyTypeId"))//
				.add(Projections.property("studyType.label"))//
				.add(Projections.property("studyType.name"))//
				.add(Projections.property("studyType.visible"))//
				.add(Projections.property("studyType.cvTermId"))//
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
			final Integer studyTypeId = (Integer) row[9];
			final String label = (String) row[10];
			final String studyTypeName = (String) row[11];
			final boolean visible = ((Boolean) row[12]);
			final Integer cvtermId = (Integer) row[13];
			final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);

			if(samplesMap.containsKey(sampleBk)){
				sample = samplesMap.get(sampleBk);
				sample.addDataset(datasetId, datasetName);
			}else{
				sample = new SampleGermplasmDetailDTO();
				sample.setSampleListName(sampleListName);
				sample.setSampleBk(sampleBk);
				sample.setPlotId(plotId);
				sample.setPlantBk(plantBk);
				sample.setStudy(new StudyReference(projectId, studyName, "", programUuid, studyTypeDto));
				sample.addDataset(datasetId, datasetName);
				samplesMap.put(sampleBk,sample);
			}
		}
		return new ArrayList<>(samplesMap.values());
	}

	public boolean hasSamples(final Integer studyId) {
		final List queryResults;
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_STUDY_HAS_SAMPLES);
			query.setParameter("studyId", studyId);
			queryResults = query.list();

		} catch (final HibernateException he) {
			throw new MiddlewareException("Unexpected error in executing hasSamples(studyId = " + studyId + ") query: " + he.getMessage(),
					he);
		}
		return queryResults.isEmpty() ? false : true;
	}
}
