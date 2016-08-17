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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link ExperimentProperty}.
 * 
 */
public class ExperimentPropertyDao extends GenericDAO<ExperimentProperty, Integer> {

	@SuppressWarnings("unchecked")
	public List<Integer> getExperimentIdsByPropertyTypeAndValue(final Integer typeId, final String value) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			criteria.setProjection(Projections.property("experiment.ndExperimentId"));

			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getExperimentIdsByPropertyTypeAndValue=" + typeId + ", " + value
					+ " query at ExperimentDao: " + e.getMessage(), e);
		}
		return new ArrayList<Integer>();
	}

	@SuppressWarnings("unchecked")
	public List<FieldMapDatasetInfo> getFieldMapLabels(final int projectId) throws MiddlewareQueryException {
		List<FieldMapDatasetInfo> datasets = null;

		try {
//			StringBuilder sqlGetListId = new StringBuilder("SELECT min(listid) AS listId from listnms where projectid = :projectId");
//			final Query queryGetListId =
//					this.getSession().createSQLQuery(sqlGetListId.toString()).addScalar("listId");
//			queryGetListId.setParameter("projectId", projectId);
//			final Integer listId = (Integer) queryGetListId.uniqueResult();
			final String order = projectId > 0 ? "ASC" : "DESC";
			final StringBuilder sql =
					new StringBuilder()
							.append(" SELECT ")
							.append(" eproj.project_id AS datasetId ")
							.append(" , proj.name AS datasetName ")
							.append(" , geo.nd_geolocation_id AS geolocationId ")
							.append(" , site.value AS siteName ")
							.append(" , eproj.nd_experiment_id AS experimentId ")
							.append(" , s.uniqueName AS entryNumber ")
							.append(" , s.name AS germplasmName ")
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
							.append(" , ppStartDate.value as startDate ")
							.append(" , gpSeason.value as season ")
							.append(" , siteId.value AS siteId")
							.append(" , epropBlock.value AS blockNo ")
							.append(" , ldp.group_name AS pedigree ")
							.append(" FROM ")
							.append(" nd_experiment_project eproj ")
							.append(" INNER JOIN project_relationship pr ON pr.object_project_id = :projectId AND pr.type_id = ")
							.append(TermId.BELONGS_TO_STUDY.getId())
							.append(" INNER JOIN project st ON st.project_id = pr.object_project_id ")
							.append(" LEFT JOIN projectprop ppStartDate ON ppStartDate.project_id = pr.object_project_id ")
							.append("       AND ppStartDate.type_id =  ")
							.append(TermId.START_DATE.getId())
							.append(" ")
							// 8050
							.append(" INNER JOIN nd_experiment_stock es ON eproj.nd_experiment_id = es.nd_experiment_id  ")
							.append("       AND eproj.project_id = pr.subject_project_id ")
							.append(" INNER JOIN stock s ON es.stock_id = s.stock_id ")
							.append(" LEFT JOIN nd_experimentprop epropRep ON eproj.nd_experiment_id = epropRep.nd_experiment_id ")
							.append("       AND epropRep.type_id =  " + TermId.REP_NO.getId()
									+ "  AND eproj.project_id = pr.subject_project_id ")
							// 8210
							.append("       AND epropRep.value IS NOT NULL  AND epropRep.value <> '' ")
							.append(" LEFT JOIN nd_experimentprop epropBlock ON eproj.nd_experiment_id = epropBlock.nd_experiment_id ")
							.append("       AND epropBlock.type_id =  " + TermId.BLOCK_NO.getId()
									+ "  AND eproj.project_id = pr.subject_project_id ")
							// 8220
							.append("       AND epropBlock.value IS NOT NULL  AND epropBlock.value <> '' ")
							.append(" INNER JOIN nd_experimentprop epropPlot ON eproj.nd_experiment_id = epropPlot.nd_experiment_id ")
							.append("       AND epropPlot.type_id IN (" + TermId.PLOT_NO.getId() + ", " + TermId.PLOT_NNO.getId() + ")  ")
							// 8200, 8380
							.append("       AND eproj.project_id = pr.subject_project_id ")
							.append("       AND epropPlot.value IS NOT NULL  AND epropPlot.value <> '' ")
							.append(" INNER JOIN nd_experiment geo ON eproj.nd_experiment_id = geo.nd_experiment_id ")
							.append("       AND geo.type_id = ").append(TermId.PLOT_EXPERIMENT.getId())
							.append(" INNER JOIN nd_geolocation inst ON geo.nd_geolocation_id = inst.nd_geolocation_id ")
							.append(" LEFT JOIN nd_geolocationprop site ON geo.nd_geolocation_id = site.nd_geolocation_id ")
							.append("       AND site.type_id = ").append(TermId.TRIAL_LOCATION.getId())
							.append("  LEFT JOIN nd_geolocationprop siteId ON siteId.nd_geolocation_id = geo.nd_geolocation_id ")
							.append("    AND siteId.type_id = ").append(TermId.LOCATION_ID.getId())
							.append(" LEFT JOIN nd_geolocationprop blk ON blk.nd_geolocation_id = geo.nd_geolocation_id ")
							.append("       AND blk.type_id = ").append(TermId.BLOCK_ID.getId())
							.append(" INNER JOIN project proj on proj.project_id = eproj.project_id ")
							.append(" LEFT JOIN nd_experimentprop row ON row.nd_experiment_id = eproj.nd_experiment_id ")
							.append("       AND row.type_id = ").append(TermId.RANGE_NO.getId())
							.append(" LEFT JOIN nd_experimentprop col ON col.nd_experiment_id = eproj.nd_experiment_id ")
							.append("       AND col.type_id = ").append(TermId.COLUMN_NO.getId())
							.append(" LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
							.append("       AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") // -- 8371 (2452)
//							.append("   INNER JOIN (select distinct germplasm_id, group_name from listdata_project where list_id = :listId ) ldp ")
//							.append("        on ldp.germplasm_id = s.dbxref_id")
							.append("LEFT JOIN listdata_project ldp on ldp.listdata_project_id = pr.object_project_id ")
							.append("		INNER JOIN listnms lnms ON lnms.projectid = ldp.listdata_project_id AND lnms.listtype in ('TRIAL', 'NURSERY')")
							.append(" ORDER BY casted_trialInstance, inst.description, eproj.nd_experiment_id ").append(order);

			final Query query =
					this.getSession().createSQLQuery(sql.toString()).addScalar("datasetId").addScalar("datasetName")
							.addScalar("geolocationId").addScalar("siteName").addScalar("experimentId").addScalar("entryNumber")
							.addScalar("germplasmName").addScalar("rep").addScalar("plotNo").addScalar("row").addScalar("col")
							.addScalar("block_id").addScalar("trialInstance").addScalar("studyName").addScalar("gid")
							.addScalar("startDate").addScalar("season").addScalar("siteId").addScalar("blockNo").addScalar("pedigree");
			query.setParameter("projectId", projectId);
			//query.setParameter("listId", listId);
			final List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				datasets = this.createFieldMapDatasetInfo(list);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error at getFieldMapLabels(projectId=" + projectId + ") at ExperimentPropertyDao: " + e.getMessage(), e);
		}

		return datasets;
	}

	@SuppressWarnings("unchecked")
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(final int datasetId, final int geolocationId, final Integer blockId)
			throws MiddlewareQueryException {
		List<FieldMapInfo> fieldmaps = new ArrayList<FieldMapInfo>();

		try {
			final String order = geolocationId > 0 ? "ASC" : "DESC";
			final StringBuilder sql =
					new StringBuilder().append(" SELECT ").append(" p.project_id AS datasetId ").append(" , p.name AS datasetName ")
							.append(" , st.name AS studyName ").append(" , e.nd_geolocation_id AS geolocationId ")
							.append(" , site.value AS siteName ").append(" , siteId.value AS siteId")
							.append(" , e.nd_experiment_id AS experimentId ").append(" , s.uniqueName AS entryNumber ")
							.append(" , s.name AS germplasmName ").append(" , epropRep.value AS rep ")
							.append(" , epropPlot.value AS plotNo ").append(" , row.value AS row ").append(" , col.value AS col ")
							.append(" , blk.value AS blockId ").append(" , st.project_id AS studyId ")
							.append(" , geo.description AS trialInstance ").append(" , s.dbxref_id AS gid ")
							.append(" , ppStartDate.value as startDate ").append(" , gpSeason.value as season ")
							.append(" , epropBlock.value AS blockNo ").append(" FROM ").append("  nd_geolocationprop blk ")
							.append("  INNER JOIN nd_experiment e ON e.nd_geolocation_id = blk.nd_geolocation_id ")
							.append("  INNER JOIN nd_geolocation geo ON geo.nd_geolocation_id = e.nd_geolocation_id ")
							.append("  INNER JOIN nd_experiment_project eproj ON eproj.nd_experiment_id = e.nd_experiment_id ")
							.append("  INNER JOIN project p ON p.project_id = eproj.project_id ")
							.append("  INNER JOIN project_relationship pr ON pr.subject_project_id = p.project_id ")
							.append("     AND pr.type_id = ").append(TermId.BELONGS_TO_STUDY.getId())
							.append("  INNER JOIN project st ON st.project_id = pr.object_project_id ")
							.append("  INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id ")
							.append("  INNER JOIN stock s ON es.stock_id = s.stock_id ")
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
							.append("  LEFT JOIN projectprop ppStartDate ON ppStartDate.project_id = pr.object_project_id ")
							.append("     AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ")
							.append("  LEFT JOIN nd_geolocationprop gpSeason ON geo.nd_geolocation_id = gpSeason.nd_geolocation_id ")
							.append("     AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") // -- 8371 (2452)
							.append(" WHERE blk.type_id = ").append(TermId.BLOCK_ID.getId());

			if (blockId != null) {
				sql.append(" AND blk.value = :blockId ");
			} else {
				sql.append(" AND blk.value IN (SELECT DISTINCT bval.value FROM nd_geolocationprop bval ")
						.append(" INNER JOIN nd_experiment bexp ON bexp.nd_geolocation_id = bval.nd_geolocation_id ")
						.append(" AND bexp.nd_geolocation_id = :geolocationId ")
						.append(" INNER JOIN nd_experiment_project bep ON bep.nd_experiment_id = bexp.nd_experiment_id ")
						.append(" AND bep.project_id = :datasetId ").append(" WHERE bval.type_id = ").append(TermId.BLOCK_ID.getId())
						.append(")");
			}
			sql.append(" ORDER BY eproj.nd_experiment_id ").append(order);

			final Query query =
					this.getSession().createSQLQuery(sql.toString()).addScalar("datasetId").addScalar("datasetName").addScalar("studyName")
							.addScalar("geolocationId").addScalar("siteName").addScalar("siteId").addScalar("experimentId")
							.addScalar("entryNumber").addScalar("germplasmName").addScalar("rep").addScalar("plotNo").addScalar("row")
							.addScalar("col").addScalar("blockId").addScalar("studyId").addScalar("trialInstance").addScalar("gid")
							.addScalar("startDate").addScalar("season").addScalar("blockNo");

			if (blockId != null) {
				query.setParameter("blockId", blockId);
			} else {
				query.setParameter("datasetId", datasetId);
				query.setParameter("geolocationId", geolocationId);
			}

			final List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				fieldmaps = this.createFieldMapLabels(list);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getAllFieldMapsInBlockByTrialInstanceId(" + geolocationId + ")", e);
		}

		return fieldmaps;
	}

	public int countExperimentPropObservations(final int datasetId, final String nonEditableFactors) throws MiddlewareQueryException {
		try {

			final StringBuilder sql =
					new StringBuilder().append("SELECT COUNT(eprop.nd_experimentprop_id) ").append("FROM nd_experiment e ")
							.append("INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
							.append("INNER JOIN nd_experimentprop eprop ON eprop.nd_experiment_id = e.nd_experiment_id ")
							.append("WHERE ep.project_id = ").append(datasetId).append(" AND eprop.type_id NOT IN (")
							.append(nonEditableFactors).append(")");
			final Query query = this.getSession().createSQLQuery(sql.toString());

			return ((BigInteger) query.uniqueResult()).intValue();

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error at countExperimentPropObservations=" + datasetId + " at ExperimentPropertyDao: " + e.getMessage(), e);
		}
		return 0;
	}

	private List<FieldMapDatasetInfo> createFieldMapDatasetInfo(final List<Object[]> list) {
		final List<FieldMapDatasetInfo> datasets = new ArrayList<FieldMapDatasetInfo>();
		FieldMapDatasetInfo dataset = null;
		List<FieldMapTrialInstanceInfo> trialInstances = null;
		FieldMapTrialInstanceInfo trialInstance = null;
		List<FieldMapLabel> labels = null;
		Integer datasetId = null;
		Integer geolocationId = null;
		String datasetName = null;
		String siteName = null;
		String trialInstanceNo = null;
		Integer blockId = null;
		Integer siteId = null;
		for (final Object[] row : list) {
			if (geolocationId == null) {
				trialInstance = new FieldMapTrialInstanceInfo();
				labels = new ArrayList<FieldMapLabel>();
			} else {
				// if trial instance or dataset has changed, add previously saved trial instance
				if (!geolocationId.equals(row[2]) || !datasetId.equals(row[0])) {
					trialInstance.setGeolocationId(geolocationId);
					trialInstance.setSiteName(siteName);
					trialInstance.setLocationName(siteName);
					trialInstance.setLocationId(siteId);
					trialInstance.setTrialInstanceNo(trialInstanceNo);
					trialInstance.setBlockId(blockId);
					trialInstance.setFieldMapLabels(labels);
					if (blockId != null) {
						trialInstance.setHasFieldMap(true);
					}
					trialInstances.add(trialInstance);
					trialInstance = new FieldMapTrialInstanceInfo();
					labels = new ArrayList<FieldMapLabel>();
				}
			}

			if (datasetId == null) {
				dataset = new FieldMapDatasetInfo();
				trialInstances = new ArrayList<FieldMapTrialInstanceInfo>();
			} else {
				// if dataset has changed, add previously saved dataset to the list
				if (!datasetId.equals(row[0])) {
					dataset.setDatasetId(datasetId);
					dataset.setDatasetName(datasetName);
					dataset.setTrialInstances(trialInstances);
					datasets.add(dataset);
					dataset = new FieldMapDatasetInfo();
					trialInstances = new ArrayList<FieldMapTrialInstanceInfo>();
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
			label.setPedigree((String) row[19]);
			labels.add(label);

			datasetId = (Integer) row[0];
			datasetName = (String) row[1];
			geolocationId = (Integer) row[2];
			siteName = (String) row[3];
			if (row[17] != null && NumberUtils.isNumber((String) row[17])) {
				siteId = Integer.valueOf((String) row[17]);
			} else {
				siteId = null;
			}
			trialInstanceNo = (String) row[12];
			blockId = row[11] != null ? Integer.valueOf((String) row[11]) : null;
		}
		// add last trial instance and dataset
		trialInstance.setGeolocationId(geolocationId);
		trialInstance.setSiteName(siteName);
		trialInstance.setLocationName(siteName);
		trialInstance.setLocationId(siteId);
		trialInstance.setBlockId(blockId);
		trialInstance.setTrialInstanceNo(trialInstanceNo);
		trialInstance.setFieldMapLabels(labels);

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

		final List<FieldMapInfo> infos = new ArrayList<FieldMapInfo>();

		final Map<Integer, FieldMapInfo> infoMap = new HashMap<Integer, FieldMapInfo>();
		final Map<Integer, FieldMapDatasetInfo> datasetMap = new HashMap<Integer, FieldMapDatasetInfo>();
		final Map<String, FieldMapTrialInstanceInfo> trialMap = new HashMap<String, FieldMapTrialInstanceInfo>();

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
			label.setGeolocationId((Integer) row[3]);
			label.setSiteName((String) row[4]);
			label.setGid((Integer) row[16]);
			label.setStartYear(startDate != null && !startDate.equals("null") && startDate.length() > 3 ? startDate.substring(0, 4) : null);
			label.setSeason(Season.getSeason((String) row[18]));
			label.setBlockNo(this.getIntegerValue(row[19]));

			final String trialKey = this.getTrialKey((Integer) row[0], (Integer) row[3]);
			FieldMapTrialInstanceInfo trial = trialMap.get(trialKey);
			if (trial == null) {
				trial = new FieldMapTrialInstanceInfo();
				trial.setGeolocationId((Integer) row[3]);
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
			if (dataset.getTrialInstance(trial.getGeolocationId()) == null) {
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
	public List<String> getTreatmentFactorValues(final int levelId, final int amountId, final int measurementDatasetId)
			throws MiddlewareQueryException {
		try {

			final StringBuilder sql =
					new StringBuilder().append("SELECT DISTINCT levelprop.value level_value, ep.value ")
							.append(" FROM nd_experimentprop ep ")
							.append(" INNER JOIN nd_experiment_project eproj ON eproj.nd_experiment_id = ep.nd_experiment_id ")
							.append("   AND eproj.project_id = ").append(measurementDatasetId)
							.append(" INNER JOIN nd_experimentprop levelprop ON levelprop.nd_experiment_id = ep.nd_experiment_id ")
							.append("   AND levelprop.type_id = ").append(levelId).append(" WHERE ep.type_id = ").append(amountId)
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
			this.logAndThrowException("Error at getTreatmentFactorValues=" + levelId + ", " + amountId + ", " + measurementDatasetId
					+ " at ExperimentPropertyDao: " + e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	public void deleteExperimentPropInProjectByTermId(final int projectId, final int termId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final StringBuilder sql =
					new StringBuilder().append("DELETE FROM nd_experimentprop ").append(" WHERE nd_experiment_id IN ( ")
							.append(" SELECT e.nd_experiment_id ").append(" FROM nd_experiment e ")
							.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = e.nd_experiment_id ")
							.append(" AND ep.project_id = ").append(projectId);
			sql.append(") ").append(" AND type_id =").append(termId);

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE ND_EXPERIMENTPROP ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in deleteExperimentPropInProjectByTermId(" + projectId + ", " + termId
					+ ") in ExperimentPropertyDao: " + e.getMessage(), e);
		}
	}
}
