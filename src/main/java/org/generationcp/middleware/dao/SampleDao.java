
package org.generationcp.middleware.dao;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.SampleDetailsBean;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
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
import org.hibernate.transform.Transformers;
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
		"SELECT  nde.nd_experiment_id, (SELECT COALESCE(NULLIF(COUNT(sp.sample_id), 0), '-')\n FROM \n"
			+ "            						sample AS sp \n" + "        WHERE\n"
			+ "            						nde.nd_experiment_id = sp.nd_experiment_id) 'SAMPLES'"
			+ "		FROM project p INNER JOIN nd_experiment nde ON nde.project_id = p.project_id\n"
			+ "		WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr "
			+ "								INNER JOIN project p ON p.project_id = pr.subject_project_id\n"
			+ "        						WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))\n"
			+ "GROUP BY nde.nd_experiment_id";

	public static final String SQL_STUDY_HAS_SAMPLES = "SELECT COUNT(sp.sample_id) AS Sample FROM project p INNER JOIN\n"
			+ "    nd_experiment nde ON nde.project_id = p.project_id INNER JOIN\n"
			+ "    sample AS sp ON nde.nd_experiment_id = sp.nd_experiment_id WHERE p.project_id = (SELECT \n"
			+ "            p.project_id FROM project_relationship pr INNER JOIN\n"
			+ "            project p ON p.project_id = pr.subject_project_id WHERE\n"
			+ "            (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA'))\n" + "GROUP BY sp.nd_experiment_id";

	private static final String SAMPLE = "sample";
	private static final String SAMPLE_EXPERIMENT = "sample.experiment";
	private static final String EXPERIMENT = "experiment";
	private static final String SAMPLE_BUSINESS_KEY = "sampleBusinessKey";
	public static final String SAMPLE_ID = "sampleId";

	public List<SampleDTO> filter(final Integer ndExperimentId, final Integer listId, final Pageable pageable) {
		final Criteria criteria = this.getSession().createCriteria(Sample.class, SAMPLE);
		addOrder(criteria, pageable);
		if (ndExperimentId != null) {
			criteria.add(Restrictions.or(Restrictions.eq("experiment.ndExperimentId", ndExperimentId),
				Restrictions.eq("experiment.parent.ndExperimentId", ndExperimentId)));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}
		return this.getSampleDTOS(criteria, pageable);
	}

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Sample.class);
		criteria.add(Restrictions.eq(SAMPLE_ID, sampleId));
		return (Sample) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public long countFilter(final Integer ndExperimentId, final Integer listId) {

		final Criteria criteria = this.getSession().createCriteria(Sample.class, SAMPLE);

		if (ndExperimentId != null) {
			criteria.add(Restrictions.or(Restrictions.eq("experiment.ndExperimentId", ndExperimentId),
				Restrictions.eq("experiment.parent.ndExperimentId", ndExperimentId)));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}
		criteria.createAlias("sample.sampleList", "sampleList")
				.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
				.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	public long countBySampleUIDs(final Set<String> sampleUIDs , final Integer listId) {
		final Criteria criteria = this.getSession().createCriteria(Sample.class, SAMPLE);
		if (!sampleUIDs.isEmpty()) {
			criteria.add(Restrictions.in(SAMPLE_BUSINESS_KEY, sampleUIDs));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}

		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	private List<SampleDTO> getSampleDTOS(final Criteria criteria) {
	    if (criteria == null) {
	    	return Collections.<SampleDTO>emptyList();
		}
		final List<Object[]> result = criteria
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy", Criteria.LEFT_JOIN)
			.createAlias("takenBy.person", "person", Criteria.LEFT_JOIN)
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(SAMPLE_ID)) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property(SAMPLE_BUSINESS_KEY)) //row[2]
				.add(Projections.property("person.firstName")) //row[3]
				.add(Projections.property("person.lastName")) //row[4]
				.add(Projections.property("sampleList.listName")) //row[5]
				.add(Projections.property("dataset.datasetId")) //row[6]
				.add(Projections.property("dataset.datasetName")) //row[7]
				.add(Projections.property("germplasm.gid")) //row[8]
				.add(Projections.property("stock.name")) //row[9] TODO preferred name
				.add(Projections.property("samplingDate")) //row[10]
				.add(Projections.property("entryNumber")) //row[11]
				.add(Projections.property("sample.plateId")) //row[12]
				.add(Projections.property("sample.well")) //row[13]
			)).list();
		return this.mapSampleDTOS(result);
	}

	private List<SampleDTO> mapSampleDTOS(final List<Object[]> result) {
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
				if(row[3] != null && row[4] != null) dto.setTakenBy(row[3] + " " + row[4]);
				dto.setSampleList((String) row[5]);
				dto.setGid((Integer) row[8]);
				dto.setDesignation((String) row[9]);
				if (row[10] != null) {
					dto.setSamplingDate((Date) row[10]);
				}
				dto.setDatasets(new HashSet<SampleDTO.Dataset>());
				dto.setPlateId((String) row[12]);
				dto.setWell((String) row[13]);
			}

			if ((row[6] != null) && (row[7] != null)) {
				final SampleDTO.Dataset dataset;
				dataset = new SampleDTO().new Dataset();
				dataset.setDatasetId((Integer) row[6]);
				dataset.setName((String) row[7]);
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
		final int start = pageSize * pageable.getPageNumber();

		final List<SampleDetailsBean> result = criteria
			.setFirstResult(start)
			.setMaxResults(pageSize)
			.createAlias("sample.sampleList", "sampleList")
			.createAlias("sample.takenBy", "takenBy", Criteria.LEFT_JOIN)
			.createAlias("takenBy.person", "person", Criteria.LEFT_JOIN)
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.project", "project")
			.createAlias("experiment.properties", "experimentProperty", Criteria.LEFT_JOIN, Restrictions.eq("typeId", TermId.PLOT_NO.getId()))
			.createAlias("project.properties", "projectProperty", Criteria.INNER_JOIN, Restrictions.eq("variableId", TermId.DATASET_TYPE.getId()))
			.createAlias("project.relatedTos", "relatedTos")
			.createAlias("relatedTos.objectProject", "objectProject")
			.createAlias("objectProject.studyType", "studyType", Criteria.LEFT_JOIN)
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.createAlias("sample.accMetadataSets", "accMetadataSets", CriteriaSpecification.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", CriteriaSpecification.LEFT_JOIN)
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.alias(Projections.property("sample.sampleId"), "sampleId"))
				.add(Projections.alias(Projections.property("germplasm.gid"), "gid"))
				.add(Projections.alias(Projections.property("stock.name"), "designation"))
				.add(Projections.alias(Projections.property("sample.sampleName"), "sampleName"))
				.add(Projections.alias(Projections.property("sample.sampleBusinessKey"), "sampleBusinessKey"))
				.add(Projections.alias(Projections.property("person.firstName"), "takenByFirstName"))
				.add(Projections.alias(Projections.property("person.lastName"), "takenByLastName"))
				.add(Projections.alias(Projections.property("sampleList.listName"), "sampleList"))
				.add(Projections.alias(Projections.property("sample.samplingDate"), "samplingDate"))
				.add(Projections.alias(Projections.property("sample.plateId"), "plateId"))
				.add(Projections.alias(Projections.property("sample.well"), "well"))
				.add(Projections.alias(Projections.property("projectProperty.value"), "datasetType"))
				.add(Projections.alias(Projections.property("objectProject.name"), "studyName"))
				.add(Projections.alias(Projections.property("experiment.observationUnitNo"), "enumerator"))
				.add(Projections.alias(Projections.property("experimentProperty.value"), "plotNo"))
				.add(Projections.alias(Projections.property("experiment.obsUnitId"), "observationUnitId"))
				.add(Projections.alias(Projections.property("dataset.datasetId"), "gdmsDatasetId"))
				.add(Projections.alias(Projections.property("dataset.datasetName"), "gdmsDatasetName")))
			).setResultTransformer(Transformers.aliasToBean(SampleDetailsBean.class))
			.list();


		final List<SampleDTO> sampleDTOs = new ArrayList<>();

		for (final SampleDetailsBean resultBean : result) {

			final SampleDTO sampleDTO = new SampleDTO();

			sampleDTO.setSampleId(resultBean.getSampleId());
			sampleDTO.setGid(resultBean.getGid());
			sampleDTO.setDesignation(resultBean.getDesignation());
			sampleDTO.setSampleName(resultBean.getSampleName());
			sampleDTO.setSampleBusinessKey(resultBean.getSampleBusinessKey());
			if (resultBean.getTakenByFirstName() != null && resultBean.getTakenByLastName() != null) {
				sampleDTO.setTakenBy(resultBean.getTakenByFirstName() + " " + resultBean.getTakenByLastName());
			}
			sampleDTO.setSampleList(resultBean.getSampleList());
			sampleDTO.setSamplingDate(resultBean.getSamplingDate());
			sampleDTO.setPlateId(resultBean.getPlateId());
			sampleDTO.setWell(resultBean.getWell());

			if (resultBean.getDatasetType() !=  null) {
				sampleDTO.setDatasetType(DataSetType.findById(Integer.valueOf(resultBean.getDatasetType())).name());
			}

			sampleDTO.setStudyName(resultBean.getStudyName());
			sampleDTO.setEnumerator(resultBean.getEnumerator() != null ? resultBean.getEnumerator() : resultBean.getPlotNo());
			sampleDTO.setObservationUnitId(resultBean.getObservationUnitId());
			if (resultBean.getGdmsDatasetId() != null) {
				sampleDTO.setDatasets(new HashSet<SampleDTO.Dataset>());
				final SampleDTO.Dataset dataset = sampleDTO.new Dataset();
				dataset.setDatasetId(resultBean.getGdmsDatasetId());
				dataset.setName(resultBean.getGdmsDatasetName());
			}
			sampleDTOs.add(sampleDTO);
		}

		return sampleDTOs;

	}

	@SuppressWarnings("rawtypes")
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
			sample = (Sample) this.getSession().createCriteria(Sample.class, SAMPLE).add(Restrictions.eq(SAMPLE_BUSINESS_KEY, sampleBk))
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
		final List<Object[]> result = this.getSession()
			.createCriteria(Sample.class, SAMPLE)
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.add(Restrictions.in(SAMPLE_ID, sampleIds))
			.setProjection(Projections.projectionList()
				.add(Projections.property("sample.sampleId"))
				.add(Projections.property("germplasm.gid")))
			.list();
		for (final Object[] row : result) {
			map.put((Integer) row[0], (Integer) row[1]);
		}
		return map;
	}

	public List<SampleDTO> getBySampleBks(final Set<String> sampleUIDs) {
		return this.getSampleDTOS(this.getSession().createCriteria(Sample.class, SAMPLE) //
				.add(Restrictions.in(SAMPLE_BUSINESS_KEY, sampleUIDs)));
	}

	@SuppressWarnings("unchecked")
	public List<SampleGermplasmDetailDTO> getByGid(final Integer gid){
		final List<Object[]> result = this.getSession()

			.createCriteria(Sample.class, SAMPLE)
			.createAlias("sample.sampleList", "sampleList", CriteriaSpecification.LEFT_JOIN)//
			.createAlias("sample.accMetadataSets", "accMetadataSets", CriteriaSpecification.LEFT_JOIN)//
			.createAlias("accMetadataSets.dataset", "dataset", CriteriaSpecification.LEFT_JOIN)//
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)//

			.createAlias("experiment.stock", "stock")//
			.createAlias("stock.germplasm", "germplasm")
			.createAlias("experiment.project", "project")//
			.createAlias("project.relatedTos", "relatedTos")//
			.createAlias("relatedTos.objectProject", "objectProject")//
			.createAlias("objectProject.studyType", "studyType")//
			.add(Restrictions.eq("germplasm.gid", gid))//
			.add(Restrictions.ne("project." + DmsProjectDao.DELETED, true))

			.addOrder(Order.desc("sample.sampleBusinessKey"))//

			.setProjection(Projections.distinct(Projections.projectionList()//
				.add(Projections.property("sampleList.listName"))//
				.add(Projections.property("sample.sampleBusinessKey"))//
				.add(Projections.property("experiment.obsUnitId"))//
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
				.add(Projections.property("sample.plateId"))//
				.add(Projections.property("sample.well"))))//
			.list();//

		final HashMap<String,SampleGermplasmDetailDTO> samplesMap = new HashMap<>();
		for (final Object[] row : result) {
			final SampleGermplasmDetailDTO sample;

			final String sampleListName = (String) row[0];
			final String sampleBk = (String) row[1];
			final String obsUnitId = (String) row[2];
			final Integer datasetId = (Integer) row[3];
			final String datasetName = (String) row[4];
			final Integer projectId = (Integer) row[5];
			final String studyName = (String) row[6];
			final String programUuid = (String) row[7];
			final Integer studyTypeId = (Integer) row[9];
			final String label = (String) row[9];
			final String studyTypeName = (String) row[10];
			final boolean visible = ((Boolean) row[11]);
			final Integer cvtermId = (Integer) row[12];
			final String plateId = (String) row[13];
			final String well = (String) row[14];
			final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);

			if(samplesMap.containsKey(sampleBk)){
				sample = samplesMap.get(sampleBk);
				sample.addDataset(datasetId, datasetName);
			}else{
				sample = new SampleGermplasmDetailDTO();
				sample.setSampleListName(sampleListName);
				sample.setSampleBk(sampleBk);
				sample.setObsUnitId(obsUnitId);
				sample.setStudy(new StudyReference(projectId, studyName, "", programUuid, studyTypeDto));
				sample.setPlateId(plateId);
				sample.setWell(well);
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
