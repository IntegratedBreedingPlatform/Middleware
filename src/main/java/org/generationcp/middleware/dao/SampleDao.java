
package org.generationcp.middleware.dao;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.SampleDetailsBean;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.SampleSearchRequestDTO;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
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
		"SELECT  nde.nd_experiment_id, (SELECT COALESCE(NULLIF(COUNT(sp.sample_id), 0), '-') FROM "
			+ " sample AS sp WHERE nde.nd_experiment_id = sp.nd_experiment_id) 'SAMPLES'"
			+ " FROM project p "
			+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id "
			+ " WHERE p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
			+ " GROUP BY nde.nd_experiment_id";

	public static final String SQL_STUDY_HAS_SAMPLES = "SELECT COUNT(sp.sample_id) AS Sample "
		+ " FROM project p "
		+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id "
		+ " INNER JOIN sample AS sp ON nde.nd_experiment_id = sp.nd_experiment_id "
		+ " WHERE p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
		+ " GROUP BY sp.nd_experiment_id";

	public static final String SQL_STUDY_ENTRY_HAS_SAMPLES = "SELECT COUNT(sp.sample_id) AS Sample "
			+ " FROM project p "
			+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id "
			+ " INNER JOIN sample AS sp ON nde.nd_experiment_id = sp.nd_experiment_id "
			+ " WHERE p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
			+ " 	AND nde.stock_id = :entryId "
			+ " GROUP BY sp.nd_experiment_id";

	private static final String MAX_SEQUENCE_NUMBER_QUERY = "SELECT st.dbxref_id as gid," + " max(IF(           convert("
		+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1),               SIGNED) = 0,           0,"
		+ " SUBSTRING_INDEX(SAMPLE_NAME, ':', -1))*1) AS max_sequence_no"
		+ " FROM nd_experiment nde "
		+ " INNER JOIN stock st ON st.stock_id = nde.stock_id "
		+ " INNER JOIN sample s ON s.nd_experiment_id = nde.nd_experiment_id WHERE st.dbxref_id IN (:gids)"
		+ " GROUP BY st.dbxref_id;";

	private static final String MAX_SAMPLE_NUMBER_QUERY =
		"select nde.nd_experiment_id  as nd_experiment_id,  max(sp.sample_no) as max_sample_no from nd_experiment nde"
			+ " inner join sample sp on sp.nd_experiment_id = nde.nd_experiment_id"
			+ " where nde.nd_experiment_id in (:experimentIds)  group by nde.nd_experiment_id";

	private static final String SAMPLE = "sample";
	private static final String SAMPLE_EXPERIMENT = "sample.experiment";
	private static final String EXPERIMENT = "experiment";
	private static final String SAMPLE_BUSINESS_KEY = "sampleBusinessKey";
	public static final String SAMPLE_ID = "sampleId";

	public SampleDao(final Session session) {
		super(session);
	}

	public List<SampleDTO> filter(final Integer ndExperimentId, final Integer listId, final Pageable pageable) {

		final Criteria criteria = this.createSampleDetailsCriteria();
		final int pageSize = pageable.getPageSize();
		final int start = pageSize * pageable.getPageNumber();

		addOrder(criteria, pageable);
		if (ndExperimentId != null) {
			criteria.add(Restrictions.or(
				Restrictions.eq("experiment.ndExperimentId", ndExperimentId),
				Restrictions.eq("experiment.parent.ndExperimentId", ndExperimentId)));
		}
		if (listId != null) {
			criteria.add(Restrictions.eq("sampleList.id", listId));
		}

		final List<SampleDetailsBean> results = criteria
			// FIXME IBP-3834
			.setFirstResult(start)
			.setMaxResults(pageSize)
			.list();

		return this.convertToSampleDTOs(results);
	}

	public Sample getBySampleId(final Integer sampleId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Sample.class);
		criteria.add(Restrictions.eq(SAMPLE_ID, sampleId));
		return (Sample) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
	}

	public long countFilter(final Integer ndExperimentId, final Integer listId) {

		final Criteria criteria = this.getSession().createCriteria(Sample.class, SAMPLE);

		if (ndExperimentId != null) {
			criteria.add(Restrictions.or(
				Restrictions.eq("experiment.ndExperimentId", ndExperimentId),
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

	public long countBySampleUIDs(final Set<String> sampleUIDs, final Integer listId) {
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

	public long countByDatasetId(final Integer datasetId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.createAlias("experiment", "experiment");
		criteria.createAlias("experiment.project", "project");
		criteria.add(Restrictions
			.or(Restrictions.eq("experiment.project.projectId", datasetId), Restrictions.eq("project.parent.projectId", datasetId)));
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
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.createAlias("germplasm.names", "name",
				Criteria.LEFT_JOIN, Restrictions.eq("name.nstat", 1))
			.createAlias("sample.accMetadataSets", "accMetadataSets", Criteria.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", Criteria.LEFT_JOIN)
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(SAMPLE_ID)) //row[0]
				.add(Projections.property("sampleName")) //row[1]
				.add(Projections.property(SAMPLE_BUSINESS_KEY)) //row[2]
				.add(Projections.property("sample.takenBy")) //row[3]
				.add(Projections.property("sampleList.listName")) //row[4]
				.add(Projections.property("dataset.datasetId")) //row[5]
				.add(Projections.property("dataset.datasetName")) //row[6]
				.add(Projections.property("germplasm.gid")) //row[7]
				.add(Projections.property("name.nval")) //row[8]
				.add(Projections.property("samplingDate")) //row[9]
				.add(Projections.property("entryNumber")) //row[10]
				.add(Projections.property("sample.plateId")) //row[11]
				.add(Projections.property("sample.well")) //row[12]
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
				dto.setEntryNo((Integer) row[10]);
				dto.setSampleId(sampleId);
				dto.setSampleName((String) row[1]);
				dto.setSampleBusinessKey((String) row[2]);
				dto.setTakenByUserId((Integer) row[3]);
				dto.setSampleList((String) row[4]);
				dto.setGid((Integer) row[7]);
				dto.setDesignation((String) row[8]);
				if (row[9] != null) {
					dto.setSamplingDate((Date) row[9]);
				}
				dto.setDatasets(new HashSet<SampleDTO.Dataset>());
				dto.setPlateId((String) row[11]);
				dto.setWell((String) row[12]);
			}

			if ((row[5] != null) && (row[6] != null)) {
				final SampleDTO.Dataset dataset;
				dataset = new SampleDTO().new Dataset((Integer) row[5], (String) row[6]);
				dto.getDatasets().add(dataset);
			}

			sampleDTOMap.put(sampleId, dto);
		}

		return new ArrayList<>(sampleDTOMap.values());
	}

	private Criteria createSampleDetailsCriteria() {
		return this.getSession().createCriteria(Sample.class, SAMPLE)
			.createAlias("sample.sampleList", "sampleList")
			.createAlias(SAMPLE_EXPERIMENT, EXPERIMENT)
			.createAlias("experiment.project", "project")
			.createAlias(
				"project.datasetType", "datasetType")
			.createAlias(
				"experiment.properties", "experimentProperty", Criteria.LEFT_JOIN, Restrictions.eq("typeId", TermId.PLOT_NO.getId()))
			.createAlias("project.study", "study")
			.createAlias("study.studyType", "studyType", Criteria.LEFT_JOIN)
			.createAlias("experiment.stock", "stock")
			.createAlias("stock.germplasm", "germplasm")
			.createAlias("germplasm.names", "name",
				Criteria.LEFT_JOIN, Restrictions.eq("name.nstat", 1))
			.createAlias("sample.accMetadataSets", "accMetadataSets", CriteriaSpecification.LEFT_JOIN)
			.createAlias("accMetadataSets.dataset", "dataset", CriteriaSpecification.LEFT_JOIN)
			.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.alias(Projections.property("sample.sampleId"), "sampleId"))
				.add(Projections.alias(Projections.property("sample.entryNumber"), "entryNumber"))
				.add(Projections.alias(Projections.property("germplasm.gid"), "gid"))
				.add(Projections.alias(Projections.property("sample.takenBy"), "takenBy"))
				.add(Projections.alias(Projections.property("name.nval"), "designation"))
				.add(Projections.alias(Projections.property("sample.sampleNumber"), "sampleNumber"))
				.add(Projections.alias(Projections.property("sample.sampleName"), "sampleName"))
				.add(Projections.alias(Projections.property("sample.sampleBusinessKey"), "sampleBusinessKey"))
				.add(Projections.alias(Projections.property("sampleList.listName"), "sampleList"))
				.add(Projections.alias(Projections.property("sample.samplingDate"), "samplingDate"))
				.add(Projections.alias(Projections.property("sample.plateId"), "plateId"))
				.add(Projections.alias(Projections.property("sample.well"), "well"))
				.add(Projections.alias(Projections.property("datasetType.name"), "datasetTypeName"))
				.add(Projections.alias(Projections.property("datasetType.isSubObservationType"), "subObservationDatasetType"))
				.add(Projections.alias(Projections.property("study.projectId"), "studyId"))
				.add(Projections.alias(Projections.property("study.name"), "studyName"))
				.add(Projections.alias(Projections.property("experiment.observationUnitNo"), "observationUnitNo"))
				.add(Projections.alias(Projections.property("experimentProperty.value"), "plotNo"))
				.add(Projections.alias(Projections.property("experiment.obsUnitId"), "observationUnitId"))
				.add(Projections.alias(Projections.property("dataset.datasetId"), "gdmsDatasetId"))
				.add(Projections.alias(Projections.property("dataset.datasetName"), "gdmsDatasetName")))
			).setResultTransformer(Transformers.aliasToBean(SampleDetailsBean.class));
	}

	public List<SampleDTO> getSamples(final Integer sampleListId, final List<Integer> sampleIds) {
		return this.getSampleDTOS(this.getSession().createCriteria(Sample.class, SAMPLE) //
				.add(Restrictions.in(SAMPLE_ID, sampleIds)));
	}

	@SuppressWarnings("unchecked")
	private List<SampleDTO> convertToSampleDTOs(final List<SampleDetailsBean> sampleDetailsBeans) {
		if (sampleDetailsBeans.isEmpty()) {
			return Collections.<SampleDTO>emptyList();
		}
		final HashMap<Integer, SampleDTO> samplesMap = new LinkedHashMap<>();

		for (final SampleDetailsBean sampleDetail : sampleDetailsBeans) {
			if (samplesMap.containsKey(sampleDetail.getSampleId())) {
				final SampleDTO sample = samplesMap.get(sampleDetail.getSampleId());
				if (sampleDetail.getGdmsDatasetId() != null) {
					sample.addDataset(sampleDetail.getGdmsDatasetId(), sampleDetail.getGdmsDatasetName());
				}
			} else {

				final SampleDTO sampleDTO = new SampleDTO();


				sampleDTO.setStudyName(sampleDetail.getStudyName());
				sampleDTO.setStudyId(sampleDetail.getStudyId());

				sampleDTO.setSampleId(sampleDetail.getSampleId());
				sampleDTO.setEntryNo(sampleDetail.getEntryNumber());
				sampleDTO.setGid(sampleDetail.getGid());
				sampleDTO.setDesignation(sampleDetail.getDesignation());
				sampleDTO.setSampleNumber(sampleDetail.getSampleNumber());
				sampleDTO.setSampleName(sampleDetail.getSampleName());
				sampleDTO.setSampleBusinessKey(sampleDetail.getSampleBusinessKey());
				sampleDTO.setTakenByUserId(sampleDetail.getTakenBy());
				sampleDTO.setSampleList(sampleDetail.getSampleList());
				sampleDTO.setSamplingDate(sampleDetail.getSamplingDate());
				sampleDTO.setPlateId(sampleDetail.getPlateId());
				sampleDTO.setWell(sampleDetail.getWell());
				sampleDTO.setDatasetType(sampleDetail.getDatasetTypeName());

				// Enumerator (a.k.a Observation Unit Number) is null if the sample was created from observation dataset, in that case,
				// we should use PlotNo
				sampleDTO.setEnumerator(sampleDetail.getObservationUnitNo() != null ? String.valueOf(sampleDetail.getObservationUnitNo()) :
					sampleDetail.getPlotNo());
				sampleDTO.setObservationUnitId(sampleDetail.getObservationUnitId());

				if (sampleDetail.getGdmsDatasetId() != null) {
					sampleDTO.addDataset(sampleDetail.getGdmsDatasetId(), sampleDetail.getGdmsDatasetName());
				}

				samplesMap.put(sampleDetail.getSampleId(), sampleDTO);
			}
		}

		return new ArrayList<>(samplesMap.values());

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
	public List<SampleDTO> getByGid(final Integer gid) {
		final List<SampleDetailsBean> results = this.createSampleDetailsCriteria()
			.add(Restrictions.eq("germplasm.gid", gid))
			.add(Restrictions.ne("project." + DmsProjectDao.DELETED, true))
			.addOrder(Order.desc("sample.sampleBusinessKey"))
			.list();

		return this.convertToSampleDTOs(results);
	}

	public boolean hasSamples(final Integer studyId) {
		final List queryResults;
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_STUDY_HAS_SAMPLES);
			query.setParameter("studyId", studyId);
			queryResults = query.list();

		} catch (final HibernateException he) {
			throw new MiddlewareException(
				"Unexpected error in executing hasSamples(studyId = " + studyId + ") query: " + he.getMessage(),
				he);
		}
		return queryResults.isEmpty() ? false : true;
	}

	public boolean studyEntryHasSamples(final Integer studyId, final Integer entryId) {
		final List queryResults;
		try {
			final SQLQuery query = this.getSession().createSQLQuery(SQL_STUDY_ENTRY_HAS_SAMPLES);
			query.setParameter("studyId", studyId);
			query.setParameter("entryId", entryId);
			queryResults = query.list();

		} catch (final HibernateException he) {
			throw new MiddlewareException(
					"Unexpected error in executing studyEntryHasSamples(studyId = " + studyId + ",entryId=" + entryId + ") query: " + he.getMessage(),
					he);
		}
		return queryResults.isEmpty() ? false : true;
	}

	public Map<Integer, Integer> getMaxSampleNumber(final Collection<Integer> experimentIds) {
		final SQLQuery createSQLQuery = this.getSession().createSQLQuery(MAX_SAMPLE_NUMBER_QUERY);
		createSQLQuery.addScalar("nd_experiment_id", new IntegerType());
		createSQLQuery.addScalar("max_sample_no", new IntegerType());
		createSQLQuery.setParameterList("experimentIds", experimentIds);
		return this.mapResults(createSQLQuery.list());

	}

	private Map<Integer, Integer> mapResults(final List<Object[]> results) {

		final Map<Integer, Integer> map = new HashMap<>();
		if (results != null && !results.isEmpty()) {
			for (final Object[] row : results) {
				map.put((Integer) row[0], (Integer) row[1]);
			}
		}
		return map;
	}

	public Map<Integer, Integer> getMaxSequenceNumber(final Collection<Integer> gids) {
		final SQLQuery createSQLQuery = this.getSession().createSQLQuery(SampleDao.MAX_SEQUENCE_NUMBER_QUERY);
		createSQLQuery.addScalar("gid", new StringType());
		createSQLQuery.addScalar("max_sequence_no", new IntegerType());
		createSQLQuery.setParameterList("gids", gids);
		return this.mapResultsToSampleSequence(createSQLQuery.list());
	}

	public long countSampleObservationDtos(final SampleSearchRequestDTO requestDTO) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.createCountSamplesQueryString(requestDTO));
		this.addSampleSearchParameters(sqlQuery, requestDTO);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public void deleteBySampleIds(final Integer sampleListId, final List<Integer> sampleIds) {
		Preconditions.checkNotNull(sampleListId, "sampleListId can not be null.");
		Preconditions.checkArgument(org.apache.commons.collections.CollectionUtils.isNotEmpty(sampleIds), "sampleIds passed cannot be empty.");
		final String query =
				"DELETE s FROM sample s "
					+ " WHERE s.sample_id IN (:sampleIds) AND s.sample_list = :sampleListId";

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
		sqlQuery.setParameterList("sampleIds", sampleIds);
		sqlQuery.setParameter("sampleListId", sampleListId);
		sqlQuery.executeUpdate();
	}

	/**
	 * Reset the entry numbers (entry_no) based on the order of current entry_no.
	 *
	 * @param sampleListId
	 */
	public void reOrderEntries(final Integer sampleListId) {
		final String sql = "UPDATE sample s \n"
				+ "    JOIN (SELECT @position \\:= 0) r\n"
				+ "    INNER JOIN (\n"
				+ "        SELECT sample_id, entry_no\n"
				+ "        FROM sample innerSample\n"
				+ "        WHERE innerSample.sample_list = :listId \n"
				+ "        ORDER BY innerSample.entry_no ASC) AS tmp\n"
				+ "    ON s.sample_id = tmp.sample_id\n"
				+ "SET s.entry_no = @position \\:= @position + 1\n"
				+ "WHERE s.sample_list = :listId";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.setParameter("listId", sampleListId);
		sqlQuery.executeUpdate();
	}

	private String createCountSamplesQueryString(final SampleSearchRequestDTO requestDTO) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(DISTINCT s.sample_bk) ");
		this.appendSamplesFromQuery(sql);
		this.appendSampleSeachFilters(sql, requestDTO);
		return sql.toString();
	}


	public List<SampleObservationDto> getSampleObservationDtos(final SampleSearchRequestDTO requestDTO, final Pageable pageable) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.createSamplesQueryString(requestDTO));
		if(pageable != null) {
			sqlQuery.setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
			sqlQuery.setMaxResults(pageable.getPageSize());
		}
		this.addSampleSearchParameters(sqlQuery, requestDTO);

		sqlQuery.addScalar("germplasmDbId", StringType.INSTANCE);
		sqlQuery.addScalar("observationUnitDbId", StringType.INSTANCE);
		sqlQuery.addScalar("plateDbId", StringType.INSTANCE);
		sqlQuery.addScalar("sampleId", IntegerType.INSTANCE);
		sqlQuery.addScalar("sampleTimestamp", DateType.INSTANCE);
		sqlQuery.addScalar("takenById", IntegerType.INSTANCE);
		sqlQuery.addScalar("studyDbId", StringType.INSTANCE);
		sqlQuery.addScalar("trialDbId", StringType.INSTANCE);
		sqlQuery.addScalar("sampleDbId", StringType.INSTANCE);
		sqlQuery.addScalar("plateIndex", IntegerType.INSTANCE);
		sqlQuery.addScalar("plotDbId", StringType.INSTANCE);
		sqlQuery.addScalar("sampleName", StringType.INSTANCE);
		sqlQuery.addScalar("well", StringType.INSTANCE);
		sqlQuery.addScalar("programDbId", StringType.INSTANCE);
		sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(SampleObservationDto.class));
		return sqlQuery.list();
	}

	private void addSampleSearchParameters(final SQLQuery sqlQuery, final SampleSearchRequestDTO requestDTO) {
		if(!CollectionUtils.isEmpty(requestDTO.getSampleDbIds())) {
			sqlQuery.setParameterList("sampleDbIds", requestDTO.getSampleDbIds());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getObservationUnitDbIds())) {
			sqlQuery.setParameterList("observationUnitDbIds", requestDTO.getObservationUnitDbIds());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getPlateDbIds())) {
			sqlQuery.setParameterList("plateDbIds", requestDTO.getPlateDbIds());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getGermplasmDbIds())) {
			sqlQuery.setParameterList("germplasmDbIds", requestDTO.getGermplasmDbIds());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getStudyDbIds())) {
			sqlQuery.setParameterList("studyDbIds", requestDTO.getStudyDbIds());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getExternalReferenceIDs())) {
			sqlQuery.setParameterList("referenceIds", requestDTO.getExternalReferenceIDs());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getExternalReferenceSources())) {
			sqlQuery.setParameterList("referenceSources", requestDTO.getExternalReferenceSources());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getGermplasmNames())) {
			sqlQuery.setParameterList("germplasmNames", requestDTO.getGermplasmNames());
		}
		if(!CollectionUtils.isEmpty(requestDTO.getStudyNames())) {
			sqlQuery.setParameterList("studyNames", requestDTO.getStudyNames());
		}
	}

	private String createSamplesQueryString(final SampleSearchRequestDTO requestDTO) {
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT g.germplsm_uuid AS germplasmDbId, ");
		sql.append("s.sample_id AS sampleId, ");
		sql.append("e.obs_unit_id AS observationUnitDbId, ");
		sql.append("s.plate_id AS plateDbId, ");
		sql.append("pmain.program_uuid AS programDbId, ");
		sql.append("s.sampling_date as sampleTimestamp, ");
		sql.append("s.taken_by as takenById, ");
		sql.append("e.nd_geolocation_id AS studyDbId, ");
		sql.append("pmain.project_id AS trialDbId, ");
		sql.append("s.sample_bk AS sampleDbId, ");
		sql.append("s.sample_no AS plateIndex, ");
		sql.append("eprop.value AS plotDbId, ");
		sql.append("s.sample_name AS sampleName, ");
		sql.append("s.well AS well, ");
		sql.append("pmain.program_uuid AS programDbId ");
		this.appendSamplesFromQuery(sql);
		this.appendSampleSeachFilters(sql, requestDTO);
		return sql.toString();
	}

	private void appendSamplesFromQuery(final StringBuilder sql) {
		sql.append("FROM sample s ");
		sql.append(" INNER JOIN nd_experiment e ON e.nd_experiment_id = s.nd_experiment_id ");
		sql.append(" INNER JOIN nd_geolocation geoloc ON geoloc.nd_geolocation_id = e.nd_geolocation_id ");
		sql.append(" INNER JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id AND eprop.type_id = " + TermId.PLOT_NO.getId());
		sql.append(" INNER JOIN stock stock ON stock.stock_id = e.stock_id ");
		sql.append(" INNER JOIN germplsm g ON g.gid = stock.dbxref_id ");
		sql.append(" INNER JOIN project pmain ON pmain.project_id = stock.project_id ");
		sql.append(" WHERE 1=1 ");
	}

	private void appendSampleSeachFilters(final StringBuilder sql, final SampleSearchRequestDTO requestDTO) {
		if(!CollectionUtils.isEmpty(requestDTO.getSampleDbIds())) {
			sql.append(" AND s.sample_bk in (:sampleDbIds)");
		}
		if(!CollectionUtils.isEmpty(requestDTO.getObservationUnitDbIds())) {
			sql.append(" AND e.obs_unit_id in (:observationUnitDbIds)");
		}
		if(!CollectionUtils.isEmpty(requestDTO.getPlateDbIds())) {
			sql.append(" AND s.plate_id in (:plateDbIds)");
		}
		if(!CollectionUtils.isEmpty(requestDTO.getGermplasmDbIds())) {
			sql.append(" AND g.germplsm_uuid in (:germplasmDbIds)");
		}
		if(!CollectionUtils.isEmpty(requestDTO.getStudyDbIds())) {
			sql.append(" AND e.nd_geolocation_id in (:studyDbIds)");
		}
		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceIDs())) {
			sql.append(" AND EXISTS (SELECT * FROM external_reference_sample sref ");
			sql.append(" WHERE s.sample_id = sref.sample_id AND sref.reference_id in (:referenceIds)) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getExternalReferenceSources())) {
			sql.append(" AND EXISTS (SELECT * FROM external_reference_sample sref ");
			sql.append(" WHERE s.sample_id = sref.sample_id AND sref.reference_source in (:referenceSources)) ");
		}

		// Search preferred names
		if (!CollectionUtils.isEmpty(requestDTO.getGermplasmNames())) {
			sql.append(" AND g.gid IN ( SELECT n.gid ");
			sql.append(" FROM names n WHERE n.nstat = 1 AND n.nval in (:germplasmNames) ) ");
		}

		if (!CollectionUtils.isEmpty(requestDTO.getStudyNames())) {
			sql.append(" AND " + DmsProjectDao.STUDY_NAME_BRAPI + " in (:studyNames)");
		}
	}

	private Map<Integer, Integer> mapResultsToSampleSequence(final List<Object[]> results) {
		final Map<Integer, Integer> map = new HashMap<>();
		if (results != null && !results.isEmpty()) {

			for (final Object[] row : results) {
				map.put(Integer.valueOf((String) row[0]), (Integer) row[1]);
			}
		}
		return map;
	}

}
