/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.brapi.v2.observationunit.ObservationLevelRelationship;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * DAO class for {@link ExperimentProperty}.
 */
public class ExperimentPropertyDao extends GenericDAO<ExperimentProperty, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentPropertyDao.class);
	private static final List<Integer> OBSERVATION_LEVEL_RELATIONSHIP_IDS =
		Arrays.asList(TermId.REP_NO.getId(), TermId.PLOT_NO.getId(), TermId.BLOCK_NO.getId());

	private static final String DELETE_EXPERIMENT_PROP_BY_LOCID_AND_TYPE =
		"DELETE FROM nd_experimentprop "
			+ "WHERE nd_experiment_id IN ( "
			+ " 	SELECT e.nd_experiment_id FROM nd_experiment e "
			+ " 	WHERE e.nd_geolocation_id IN (:locationIds) )"
			+ " AND type_id IN (:termIds)";

	public ExperimentPropertyDao(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByPropertyTypeAndValue(final Integer typeId, final String value) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("experiment.ndExperimentId"));

			return criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error at getExperimentIdsByPropertyTypeAndValue=" + typeId + ", " + value
				+ " query at ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<FieldMapDatasetInfo> getFieldMapLabels(final int projectId) {
		List<FieldMapDatasetInfo> datasets = null;

		try {

			final String order = projectId > 0 ? "ASC" : "DESC";
			final StringBuilder sql =
				new StringBuilder()
					.append(" SELECT ")
					.append(" nde.project_id AS datasetId ")
					.append(" , proj.name AS datasetName ")
					.append(" , geo.nd_geolocation_id AS instanceId ")
					.append(" , site.value AS siteName ")
					.append(" , nde.nd_experiment_id AS experimentId ")
					.append(" , s.uniqueName AS entryNumber ")
					.append(" , name.nval AS germplasmName ")
					.append(" , epropRep.value AS rep ")
					.append(" , epropPlot.value AS plotNo ")
					.append(" , row.value AS row ")
					.append(" , col.value AS col ")
					.append(" , blk.value AS block_id ")
					.append(" , inst.description AS trialInstance ")
					//Casting inst.description to signed for natural sort
					.append(" , CAST(inst.description as SIGNED) AS casted_trialInstance")
					.append(" , st.name AS studyName ")
					.append(" , s.dbxref_id AS gid ")
					.append(" , st.start_date as startDate ")
					.append(" , gpSeason.value as season ")
					.append(" , siteId.value AS siteId")
					.append(" , epropBlock.value AS blockNo ")
					.append(" , geo.obs_unit_id as obsUnitId ")
					.append(" , case when geo.json_props like '%geoCoordinates%' then 1 else 0 end as hasGeoJSON ")
					.append(" , case when means.project_id is not null then 1 else 0 end as hasMeansData ")
					.append(" FROM ")
					.append(" nd_experiment nde ")
					.append(" INNER JOIN project proj on proj.project_id = nde.project_id ")
					.append(" INNER JOIN project st ON st.project_id = proj.study_id ")
					.append(" INNER JOIN stock s ON s.stock_id = nde.stock_id ")
					.append(" INNER JOIN names name ON name.gid = s.dbxref_id and name.nstat = 1")
					.append(" LEFT JOIN nd_experimentprop epropRep ON nde.nd_experiment_id = epropRep.nd_experiment_id ")
					.append("       AND epropRep.type_id =  " + TermId.REP_NO.getId())
					// 8210
					.append("       AND epropRep.value IS NOT NULL  AND epropRep.value <> '' ")
					.append(" LEFT JOIN nd_experimentprop epropBlock ON nde.nd_experiment_id = epropBlock.nd_experiment_id ")
					.append("       AND epropBlock.type_id =  " + TermId.BLOCK_NO.getId())
					// 8220
					.append("       AND epropBlock.value IS NOT NULL  AND epropBlock.value <> '' ")
					.append(" INNER JOIN nd_experimentprop epropPlot ON nde.nd_experiment_id = epropPlot.nd_experiment_id ")
					.append("       AND epropPlot.type_id IN (" + TermId.PLOT_NO.getId() + ", " + TermId.PLOT_NNO.getId() + ")  ")
					// 8200, 8380
					.append("       AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' ")
					.append(" INNER JOIN nd_experiment geo ON nde.nd_experiment_id = geo.nd_experiment_id ")
					.append("       AND geo.type_id = ").append(TermId.PLOT_EXPERIMENT.getId())
					.append(" INNER JOIN nd_geolocation inst ON geo.nd_geolocation_id = inst.nd_geolocation_id ")
					.append(" LEFT JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id ")
					.append("       AND site.type_id = ").append(TermId.TRIAL_LOCATION.getId())
					.append("  LEFT JOIN nd_geolocationprop siteId ON siteId.nd_geolocation_id = geo.nd_geolocation_id ")
					.append("    AND siteId.type_id = ").append(TermId.LOCATION_ID.getId())
					.append(" LEFT JOIN nd_geolocationprop blk ON blk.nd_geolocation_id = geo.nd_geolocation_id ")
					.append("       AND blk.type_id = ").append(TermId.BLOCK_ID.getId())
					.append(" LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = nde.nd_experiment_id ")
					.append("       AND row.type_id = ").append(TermId.RANGE_NO.getId())
					.append(" LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = nde.nd_experiment_id ")
					.append("       AND col.type_id = ").append(TermId.COLUMN_NO.getId())
					.append(" LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
					.append("       AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") // -- 8371 (2452)
					.append(" LEFT JOIN project means ON proj.parent_project_id = means.parent_project_id AND means.dataset_type_id = ")
					.append(
						DatasetTypeEnum.MEANS_DATA.getId())
					.append(" WHERE st.project_id = :studyId")
					.append(" ORDER BY casted_trialInstance, inst.description, nde.nd_experiment_id ").append(order);

			final SQLQuery query =
				this.getSession().createSQLQuery(sql.toString());
			query.addScalar("datasetId").addScalar("datasetName")
				.addScalar("instanceId").addScalar("siteName").addScalar("experimentId").addScalar("entryNumber")
				.addScalar("germplasmName").addScalar("rep").addScalar("plotNo").addScalar("row").addScalar("col")
				.addScalar("block_id").addScalar("trialInstance").addScalar("studyName").addScalar("gid")
				.addScalar("startDate").addScalar("season").addScalar("siteId").addScalar("blockNo").addScalar("obsUnitId",
					StringType.INSTANCE).addScalar("hasGeoJSON", BooleanType.INSTANCE).addScalar("hasMeansData", BooleanType.INSTANCE);
			query.setParameter("studyId", projectId);
			final List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				datasets = this.createFieldMapDatasetInfo(list);
			}

		} catch (final HibernateException e) {
			final String message = "Error at getFieldMapLabels(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return datasets;
	}

	@SuppressWarnings("unchecked")
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(final int datasetId, final int instanceId, final Integer blockId) {
		List<FieldMapInfo> fieldmaps = new ArrayList<>();

		try {
			final String order = instanceId > 0 ? "ASC" : "DESC";
			final StringBuilder sql =
				new StringBuilder().append(" SELECT ").append(" p.project_id AS datasetId ").append(" , p.name AS datasetName ")
					.append(" , st.name AS studyName ").append(" , e.nd_geolocation_id AS instanceId ")
					.append(" , site.value AS siteName ").append(" , siteId.value AS siteId")
					.append(" , e.nd_experiment_id AS experimentId ").append(" , s.uniqueName AS entryNumber ")
					.append(" , name.nval AS germplasmName ").append(" , epropRep.value AS rep ")
					.append(" , epropPlot.value AS plotNo ").append(" , row.value AS row ").append(" , col.value AS col ")
					.append(" , blk.value AS blockId ").append(" , st.project_id AS studyId ")
					.append(" , geo.description AS trialInstance ").append(" , s.dbxref_id AS gid ")
					.append(" , st.start_date as startDate ").append(" , gpSeason.value as season ")
					.append(" , epropBlock.value AS blockNo ")
					.append(" , e.obs_unit_id as obsUnitId ")
					.append(" FROM ").append("  nd_experiment e ")
					.append("  LEFT JOIN nd_geolocationprop blk ON e.nd_geolocation_id = blk.nd_geolocation_id ")
					.append(" AND blk.type_id =").append(TermId.BLOCK_ID.getId())
					.append("  INNER JOIN nd_geolocation geo ON geo.nd_geolocation_id = e.nd_geolocation_id ")
					.append("  INNER JOIN project p ON p.project_id = e.project_id ")
					.append("  INNER JOIN project st ON st.project_id = p.study_id ")
					.append("  INNER JOIN stock s ON e.stock_id = s.stock_id ")
					.append("  INNER JOIN names name ON name.gid = s.dbxref_id and name.nstat = 1 ")
					.append("  LEFT JOIN nd_experimentprop epropRep ON epropRep.nd_experiment_id = e.nd_experiment_id ")
					.append("    AND epropRep.type_id = ").append(TermId.REP_NO.getId()).append(" AND epropRep.value <> '' ")
					.append("  LEFT JOIN nd_experimentprop epropBlock ON epropBlock.nd_experiment_id = e.nd_experiment_id ")
					.append("    AND epropBlock.type_id = ").append(TermId.BLOCK_NO.getId()).append(" AND epropBlock.value <> '' ")
					.append("  INNER JOIN nd_experimentprop epropPlot ON epropPlot.nd_experiment_id = e.nd_experiment_id ")
					.append("    AND epropPlot.type_id IN (").append(TermId.PLOT_NO.getId()).append(", ")
					.append(TermId.PLOT_NNO.getId()).append(") ").append(" AND epropPlot.value <> '' ")
					.append("  LEFT JOIN nd_geolocationprop site ON site.nd_geolocation_id = e.nd_geolocation_id ")
					.append("    AND site.type_id = ").append(TermId.TRIAL_LOCATION.getId())
					.append("  LEFT JOIN nd_geolocationprop siteId ON siteId.nd_geolocation_id = e.nd_geolocation_id ")
					.append("    AND siteId.type_id = ").append(TermId.LOCATION_ID.getId())
					.append("  LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = e.nd_experiment_id ")
					.append("    AND row.type_id = ").append(TermId.RANGE_NO.getId())
					.append("  LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = e.nd_experiment_id ")
					.append("    AND col.type_id = ").append(TermId.COLUMN_NO.getId())
					.append("  LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
					.append("     AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" "); // -- 8371 (2452)

			if (blockId != null) {
				sql.append(" WHERE blk.value = :blockId ");
			} else {
				sql.append(" WHERE 1 = CASE ")
					.append("WHEN blk.value is NULL AND e.project_id = :datasetId AND e.nd_geolocation_id = :instanceId THEN 1 ")
					.append("WHEN blk.value IN (SELECT DISTINCT bval.value FROM nd_geolocationprop bval ")
					.append(" INNER JOIN nd_experiment bexp ON bexp.nd_geolocation_id = bval.nd_geolocation_id ")
					.append(" AND bexp.nd_geolocation_id = :instanceId ")
					.append(" AND bexp.project_id = :datasetId ").append(" WHERE bval.type_id = ").append(TermId.BLOCK_ID.getId())
					.append(") THEN 1 ")
					.append("ELSE 0 END");
			}
			sql.append(" ORDER BY e.nd_experiment_id ").append(order);

			final SQLQuery query =
				this.getSession().createSQLQuery(sql.toString());
			query.addScalar("datasetId").addScalar("datasetName").addScalar("studyName")
				.addScalar("instanceId").addScalar("siteName").addScalar("siteId").addScalar("experimentId").addScalar("entryNumber")
				.addScalar("germplasmName").addScalar(
					"rep").addScalar("plotNo").addScalar("row")
				.addScalar("col").addScalar("blockId").addScalar("studyId").addScalar("trialInstance").addScalar("gid")
				.addScalar("startDate").addScalar("season").addScalar("blockNo").addScalar("obsUnitId", StringType.INSTANCE);

			if (blockId != null) {
				query.setParameter("blockId", blockId);
			} else {
				query.setParameter("datasetId", datasetId);
				query.setParameter("instanceId", instanceId);
			}

			final List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				fieldmaps = this.createFieldMapLabels(list);
			}

		} catch (final HibernateException e) {
			final String message =
				"Error at getAllFieldMapsInBlockByTrialInstanceId(" + instanceId + ") at ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return fieldmaps;
	}

	private List<FieldMapDatasetInfo> createFieldMapDatasetInfo(final List<Object[]> list) {
		final List<FieldMapDatasetInfo> datasets = new ArrayList<>();
		FieldMapDatasetInfo dataset = null;
		List<FieldMapTrialInstanceInfo> trialInstances = null;
		FieldMapTrialInstanceInfo trialInstance = null;
		List<FieldMapLabel> labels = null;
		Integer datasetId = null;
		Integer instanceId = null;
		String datasetName = null;
		String siteName = null;
		String trialInstanceNo = null;
		Integer blockId = null;
		Boolean hasGeoJSON = false;
		Boolean hasMeansData = false;
		Integer siteId = null;
		for (final Object[] row : list) {
			if (instanceId == null) {
				trialInstance = new FieldMapTrialInstanceInfo();
				labels = new ArrayList<>();
			} else {
				// if trial instance or dataset has changed, add previously saved trial instance
				if (!instanceId.equals(row[2]) || !datasetId.equals(row[0])) {
					trialInstance.setInstanceId(instanceId);
					trialInstance.setSiteName(siteName);
					trialInstance.setLocationName(siteName);
					trialInstance.setLocationId(siteId);
					trialInstance.setTrialInstanceNo(trialInstanceNo);
					trialInstance.setBlockId(blockId);
					trialInstance.setFieldMapLabels(labels);
					if (blockId != null) {
						trialInstance.setHasFieldMap(true);
					}
					trialInstance.setHasGeoJSON(hasGeoJSON);
					trialInstance.setHasMeansData(hasMeansData);
					trialInstances.add(trialInstance);
					trialInstance = new FieldMapTrialInstanceInfo();
					labels = new ArrayList<>();
				}
			}

			if (datasetId == null) {
				dataset = new FieldMapDatasetInfo();
				trialInstances = new ArrayList<>();
			} else {
				// if dataset has changed, add previously saved dataset to the list
				if (!datasetId.equals(row[0])) {
					dataset.setDatasetId(datasetId);
					dataset.setDatasetName(datasetName);
					dataset.setTrialInstances(trialInstances);
					datasets.add(dataset);
					dataset = new FieldMapDatasetInfo();
					trialInstances = new ArrayList<>();
				}
			}

			final Integer experimentId = (Integer) row[4];
			final String entryNumber = (String) row[5];
			final String germplasmName = (String) row[6];
			final String rep = (String) row[7];
			final String blockNo = (String) row[18];
			final String plotNo = (String) row[8];
			final Integer gid = (Integer) row[14];
			final String startDate = (String) row[15];
			final String season = (String) row[16];

			final FieldMapLabel label =
				new FieldMapLabel(experimentId, entryNumber == null || entryNumber.equals("null") || entryNumber.equals("") ? null
					: Integer.parseInt(entryNumber), germplasmName, rep == null || rep.equals("null") ? 1 : Integer.parseInt(rep),
					plotNo == null || plotNo.equals("null") ? 0 : Integer.parseInt(plotNo));
			if (NumberUtils.isNumber((String) row[9])) {
				label.setColumn(Integer.parseInt((String) row[9]));
			}
			if (NumberUtils.isNumber((String) row[10])) {
				label.setRange(Integer.parseInt((String) row[10]));
			}
			if ((rep == null || rep.equals("null")) && blockNo != null && !blockNo.equalsIgnoreCase("null")
				&& NumberUtils.isNumber(blockNo)) {
				label.setRep(Integer.parseInt(blockNo));
			}
			label.setBlockNo(this.getIntegerValue(blockNo));
			label.setStudyName((String) row[13]);
			label.setGid(gid);
			label.setStartYear(startDate != null && !startDate.equals("null") && startDate.length() > 3 ? startDate.substring(0, 4) : null);
			label.setSeason(Season.getSeason(season));
			label.setObsUnitId((row[19] == null) ? "" : (String) row[19]);
			labels.add(label);

			datasetId = (Integer) row[0];
			datasetName = (String) row[1];
			instanceId = (Integer) row[2];
			siteName = (String) row[3];
			if (row[17] != null && NumberUtils.isNumber((String) row[17])) {
				siteId = Integer.valueOf((String) row[17]);
			} else {
				siteId = null;
			}
			trialInstanceNo = (String) row[12];
			blockId = row[11] != null ? Integer.valueOf((String) row[11]) : null;
			hasGeoJSON = (Boolean) row[20];
			hasMeansData = (Boolean) row[21];
		}
		// add last trial instance and dataset
		trialInstance.setInstanceId(instanceId);
		trialInstance.setSiteName(siteName);
		trialInstance.setLocationName(siteName);
		trialInstance.setLocationId(siteId);
		trialInstance.setBlockId(blockId);
		trialInstance.setTrialInstanceNo(trialInstanceNo);
		trialInstance.setFieldMapLabels(labels);
		trialInstance.setHasGeoJSON(hasGeoJSON);
		trialInstance.setHasMeansData(hasMeansData);

		if (blockId != null) {
			trialInstance.setHasFieldMap(true);
		}

		trialInstances.add(trialInstance);
		dataset.setDatasetId(datasetId);
		dataset.setDatasetName(datasetName);
		dataset.setTrialInstances(trialInstances);
		datasets.add(dataset);

		return datasets;
	}

	private List<FieldMapInfo> createFieldMapLabels(final List<Object[]> rows) {

		final List<FieldMapInfo> infos = new ArrayList<>();

		final Map<Integer, FieldMapInfo> infoMap = new HashMap<>();
		final Map<Integer, FieldMapDatasetInfo> datasetMap = new HashMap<>();
		final Map<String, FieldMapTrialInstanceInfo> trialMap = new HashMap<>();

		for (final Object[] row : rows) {
			final FieldMapLabel label = new FieldMapLabel();
			final String startDate = (String) row[17];
			label.setStudyName((String) row[2]);
			label.setExperimentId(this.getIntegerValue(row[6]));
			label.setEntryNumber(this.getIntegerValue(row[7]));
			label.setRep(this.getIntegerValue(row[9]));
			label.setPlotNo(this.getIntegerValue(row[10]));
			label.setColumn(this.getIntegerValue(row[12]));
			label.setRange(this.getIntegerValue(row[11]));
			label.setGermplasmName((String) row[8]);
			label.setDatasetId((Integer) row[0]);
			label.setInstanceId((Integer) row[3]);
			label.setSiteName((String) row[4]);
			label.setGid((Integer) row[16]);
			label.setStartYear(startDate != null && !startDate.equals("null") && startDate.length() > 3 ? startDate.substring(0, 4) : null);
			label.setSeason(Season.getSeason((String) row[18]));
			label.setBlockNo(this.getIntegerValue(row[19]));
			label.setObsUnitId((String) row[20]);

			final String trialKey = this.getTrialKey((Integer) row[0], (Integer) row[3]);
			FieldMapTrialInstanceInfo trial = trialMap.get(trialKey);
			if (trial == null) {
				trial = new FieldMapTrialInstanceInfo();
				trial.setInstanceId((Integer) row[3]);
				trial.setSiteName((String) row[4]);
				trial.setLocationName((String) row[4]);
				if (row[5] != null && NumberUtils.isNumber((String) row[5])) {
					trial.setLocationId(Integer.valueOf((String) row[5]));
				}
				if (row[13] != null && NumberUtils.isNumber((String) row[13])) {
					trial.setBlockId(Integer.valueOf((String) row[13]));
				}
				trial.setTrialInstanceNo((String) row[15]);
				trialMap.put(trialKey, trial);
			}

			FieldMapDatasetInfo dataset = datasetMap.get(row[0]);
			if (dataset == null) {
				dataset = new FieldMapDatasetInfo();
				dataset.setDatasetId((Integer) row[0]);
				dataset.setDatasetName((String) row[1]);
				datasetMap.put(dataset.getDatasetId(), dataset);

				FieldMapInfo study = infoMap.get(row[14]);
				if (study == null) {
					study = new FieldMapInfo();
					study.setFieldbookId((Integer) row[14]);
					study.setFieldbookName((String) row[2]);
					infoMap.put(study.getFieldbookId(), study);
				}
				if (study.getDatasets() == null) {
					study.setDatasets(new ArrayList<FieldMapDatasetInfo>());
				}
				if (study.getDataSet(dataset.getDatasetId()) == null) {
					study.getDatasets().add(dataset);
				}
			}
			if (dataset.getTrialInstances() == null) {
				dataset.setTrialInstances(new ArrayList<FieldMapTrialInstanceInfo>());
			}
			if (dataset.getTrialInstance(trial.getInstanceId()) == null) {
				dataset.getTrialInstances().add(trial);
			}

			if (trial.getFieldMapLabels() == null) {
				trial.setFieldMapLabels(new ArrayList<FieldMapLabel>());
			}
			trial.getFieldMapLabels().add(label);
		}

		final Set<Integer> keys = infoMap.keySet();
		for (final Integer key : keys) {
			infos.add(infoMap.get(key));
		}
		return infos;
	}

	private String getTrialKey(final int datasetId, final int trialId) {
		return datasetId + "-" + trialId;
	}

	private Integer getIntegerValue(final Object obj) {
		Integer value = null;
		if (obj != null) {
			if (obj instanceof Integer) {
				value = (Integer) obj;
			} else if (obj instanceof String && NumberUtils.isNumber((String) obj)) {
				value = Integer.valueOf((String) obj);
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public List<String> getTreatmentFactorValues(final int levelId, final int amountId, final int measurementDatasetId) {
		try {

			final StringBuilder sql =
				new StringBuilder().append("SELECT DISTINCT levelprop.value level_value, ep.value ")
					.append(" FROM nd_experimentprop ep ")
					.append(" INNER JOIN nd_experimentprop levelprop ON levelprop.nd_experiment_id = ep.nd_experiment_id ")
					.append("   AND levelprop.type_id = ").append(levelId)
					.append(" INNER JOIN nd_experiment e ON ep.nd_experiment_id= e.nd_experiment_id ")
					.append(" WHERE ep.type_id = ").append(amountId)
					.append("   AND e.project_id = ").append(measurementDatasetId)
					.append(" ORDER BY CAST(levelprop.value AS UNSIGNED) ");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			final List<Object[]> list = query.list();
			final List<String> returnData = new ArrayList();
			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					returnData.add((String) row[1]);
				}

			}
			return returnData;

		} catch (final HibernateException e) {
			final String message = "Error at getTreatmentFactorValues=" + levelId + ", " + amountId + ", " + measurementDatasetId
				+ " at ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<ObservationLevelRelationship> getObservationLevelRelationships(final List<Integer> experimentIds) {
		final List<ObservationLevelRelationship> observationLevelRelationships = new ArrayList<>();
		try {
			final StringBuilder sql =
				new StringBuilder().append("SELECT e.nd_experiment_id AS experimentId, e.value AS value, var.name AS name ")
					.append("	FROM nd_experimentprop e ")
					.append("	INNER JOIN cvterm var on e.type_id = var.cvterm_id ")
					.append("	WHERE e.nd_experiment_id IN (:experimentIds)")
					.append("		AND e.type_id IN (:observationLevelRelationshipIds)")
					.append("		AND e.type_id NOT IN (" + TermId.COLUMN_NO.getId() + ", " + TermId.RANGE_NO.getId() + ")");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("experimentIds", experimentIds);
			query.setParameterList("observationLevelRelationshipIds", OBSERVATION_LEVEL_RELATIONSHIP_IDS);
			query.addScalar("experimentId");
			query.addScalar("value");
			query.addScalar("name");

			final List<Object> results = query.list();
			for (final Object result : results) {

				final Object[] row = (Object[]) result;
				final ObservationLevelRelationship observationLevelRelationship =
					new ObservationLevelRelationship((Integer) row[0], (String) row[1], (String) row[2], null);
				observationLevelRelationships.add(observationLevelRelationship);
			}

			return observationLevelRelationships;
		} catch (final HibernateException e) {
			final String message =
				"Error at getObservationLevelRelationships=" + experimentIds + " at ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteExperimentPropInProjectByTermId(final int projectId, final int termId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final StringBuilder sql =
				new StringBuilder().append("DELETE FROM nd_experimentprop ").append(" WHERE nd_experiment_id IN ( ")
					.append(" SELECT e.nd_experiment_id ").append(" FROM nd_experiment e ")
					.append(" WHERE e.project_id = ").append(projectId);
			sql.append(") ").append(" AND type_id =").append(termId);

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE ND_EXPERIMENTPROP ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (final HibernateException e) {
			final String message = "Error in deleteExperimentPropInProjectByTermId(" + projectId + ", " + termId
				+ ") in ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public ExperimentProperty getExperimentProperty(final Integer experimentId, final Integer experimentPropertyId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("ndExperimentpropId", experimentPropertyId));
		criteria.add(Restrictions.eq("experiment.ndExperimentId", experimentId));
		return (ExperimentProperty) criteria.uniqueResult();
	}

	public List<ExperimentProperty> getExperimentPropertiesByType(final Integer experimentId, final List<Integer> typeIds) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("experiment.ndExperimentId", experimentId));
		criteria.add(Restrictions.in("typeId", typeIds));
		return (List<ExperimentProperty>) criteria.list();
	}

	public Map<String, List<String>> getPlotObservationLevelRelationshipsByGeolocations(final Set<String> geolocationIds) {
		if (isEmpty(geolocationIds)) {
			return emptyMap();
		}

		final StringBuilder sql = new StringBuilder()
			.append("SELECT e.nd_geolocation_id AS studyDbId, eprop.value AS levelCode ")
			.append(" FROM nd_experiment e INNER JOIN nd_experimentprop eprop on e.nd_experiment_id = eprop.nd_experiment_id ")
			.append(" WHERE e.nd_geolocation_id IN(:geolocationIds) AND eprop.type_id =").append(TermId.PLOT_NO.getId());
		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		query.addScalar("studyDbId", StringType.INSTANCE);
		query.addScalar("levelCode");
		query.setParameterList("geolocationIds", geolocationIds);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		final List<Map<String, String>> queryResults = (List<Map<String, String>>) query.list();
		final Map<String, List<String>> plotObservationLevelRelationships = new HashMap<>();
		queryResults
			.stream()
			.forEach(result -> {
				if (plotObservationLevelRelationships.get(result.get("studyDbId")) == null) {
					plotObservationLevelRelationships.put(result.get("studyDbId"), new ArrayList<>());
				}

				plotObservationLevelRelationships.get(result.get("studyDbId")).add(result.get("levelCode"));

			});
		return plotObservationLevelRelationships;
	}

	public void deleteExperimentPropByLocationIds(final List<Integer> locationIds, final List<Integer> termIds) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final SQLQuery query = this.getSession().createSQLQuery(DELETE_EXPERIMENT_PROP_BY_LOCID_AND_TYPE);
			query.setParameterList("locationIds", locationIds);
			query.setParameterList("termIds", termIds);
			query.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error in deleteExperimentPropByLocationIds("
				+ locationIds.stream().map(id -> id.toString()).collect(Collectors.joining(","))
				+ " , " + termIds.stream().map(termId -> termId.toString()).collect(Collectors.joining(","))
				+ ") in ExperimentPropertyDao: " + e.getMessage();
			ExperimentPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

}
