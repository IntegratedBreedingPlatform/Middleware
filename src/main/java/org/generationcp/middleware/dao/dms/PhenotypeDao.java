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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.domain.h2h.CharacterTraitInfo;
import org.generationcp.middleware.domain.h2h.NumericTraitInfo;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.domain.h2h.ObservationKey;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.h2h.TraitObservation;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchObservationDTO;
import org.generationcp.middleware.service.api.phenotype.PhenotypeSearchRequestDTO;
import org.generationcp.middleware.service.impl.study.PhenotypeQuery;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link Phenotype}.
 *
 */
@SuppressWarnings("unchecked")
public class PhenotypeDao extends GenericDAO<Phenotype, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeDao.class);

	private static final String GET_OBSERVATIONS = "SELECT p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value "
			+ "FROM nd_experiment e "
			+ "INNER JOIN stock s ON e.stock_id = s.stock_id "
			+ "INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
			+ "AND p.observable_id IN (:traitIds) ";

	private static final String COUNT_OBSERVATIONS =
			"SELECT COUNT(*) " + "FROM nd_experiment e "
					+ "INNER JOIN stock s ON e.stock_id = s.stock_id "
					+ "INNER JOIN phenotype p ON e.nd_experiment_id = p.nd_experiment_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
					+ "AND p.observable_id IN (:traitIds) ";

	private static final String ORDER_BY_OBS = "ORDER BY p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value ";

	//FIXME BMS-5055
	private static final String HAS_OUT_OF_SYNC = "SELECT "
		+ "    COUNT(1)"
		+ " FROM"
		+ "    phenotype pheno"
		+ "        INNER JOIN"
		+ "    (SELECT "
		+ "        MAX(p.phenotype_id) id,"
		+ "            p.nd_experiment_id exp_id,"
		+ "            p.observable_id obs_id"
		+ "    FROM"
		+ "        phenotype p"
		+ "    GROUP BY p.nd_experiment_id , p.observable_id) ph ON (ph.id = pheno.phenotype_id"
		+ "        AND ph.exp_id = pheno.nd_experiment_id"
		+ "        AND ph.obs_id = pheno.observable_id)"
		+ "        INNER JOIN"
		+ "    nd_experiment n ON pheno.nd_experiment_id = n.nd_experiment_id"
		+ " WHERE"
		+ "    pheno.status = '" + Phenotype.ValueStatus.OUT_OF_SYNC
		+ "'        AND n.project_id = :projectId";

	public List<NumericTraitInfo> getNumericTraitInfoList(final List<Integer> environmentIds, final List<Integer> numericVariableIds) {
		final List<NumericTraitInfo> numericTraitInfoList = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
							+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
							+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
							+ "IF (MIN(p.value * 1) IS NULL, 0, MIN(p.value * 1))  AS min_value, "
							+ "IF (MAX(p.value * 1) IS NULL, 0, MAX(p.value * 1)) AS max_value " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
							+ "    AND p.observable_id IN (:numericVariableIds) " + "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("numericVariableIds", numericVariableIds);

			final List<Object[]> list;

			if (!environmentIds.isEmpty() && !numericVariableIds.isEmpty()) {
				list = query.list();

				for (final Object[] row : list) {
					final Integer id = (Integer) row[0];
					final Long locationCount = ((BigInteger) row[1]).longValue();
					final Long germplasmCount = ((BigInteger) row[2]).longValue();
					final Long observationCount = ((BigInteger) row[3]).longValue();
					final Double minValue = (Double) row[4];
					final Double maxValue = (Double) row[5];

					final NumericTraitInfo numericTraitInfo =
							new NumericTraitInfo(null, id, null, locationCount, germplasmCount, observationCount, minValue, maxValue, 0);
					numericTraitInfoList.add(numericTraitInfo);
				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getNumericTraitInfoList() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return numericTraitInfoList;

	}

	public List<TraitInfo> getTraitInfoCounts(final List<Integer> environmentIds, final List<Integer> variableIds) {
		final List<TraitInfo> traitInfoList = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
							+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
							+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
							+ "    AND p.observable_id IN (:variableIds) " + "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("variableIds", variableIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !variableIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final long locationCount = ((BigInteger) row[1]).longValue();
				final long germplasmCount = ((BigInteger) row[2]).longValue();
				final long observationCount = ((BigInteger) row[3]).longValue();

				traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitInfoList;

	}

	public List<TraitInfo> getTraitInfoCounts(final List<Integer> environmentIds) {
		final List<TraitInfo> traitInfoList = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
							+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
							+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "    INNER JOIN stock s ON e.stock_id = s.stock_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
							+ "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final long locationCount = ((BigInteger) row[1]).longValue();
				final long germplasmCount = ((BigInteger) row[2]).longValue();
				final long observationCount = ((BigInteger) row[3]).longValue();

				traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitInfoList;

	}

	public Map<Integer, List<Double>> getNumericTraitInfoValues(final List<Integer> environmentIds,
			final List<NumericTraitInfo> traitInfoList) {
		final Map<Integer, List<Double>> traitValues = new HashMap<>();

		// Get trait IDs
		final List<Integer> traitIds = new ArrayList<>();
		for (final NumericTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, p.value * 1 " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id IN (:traitIds) ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final Double value = (Double) row[1];

				List<Double> values = new ArrayList<>();
				values.add(value);
				// If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
				if (traitValues.containsKey(traitId)) {
					values = traitValues.get(traitId);
					values.add(value);
					traitValues.remove(traitId);
				}
				traitValues.put(traitId, values);

			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitValues;

	}

	public Map<Integer, List<Double>> getNumericTraitInfoValues(final List<Integer> environmentIds, final Integer trait) {
		final Map<Integer, List<Double>> traitValues = new HashMap<>();

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, p.value * 1 " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id = :traitId ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameter("traitId", trait);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final Double value = (Double) row[1];

				List<Double> values = new ArrayList<>();
				values.add(value);
				// If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
				if (traitValues.containsKey(traitId)) {
					values = traitValues.get(traitId);
					values.add(value);
					traitValues.remove(traitId);
				}
				traitValues.put(traitId, values);

			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitValues;

	}

	public Map<Integer, List<String>> getCharacterTraitInfoValues(final List<Integer> environmentIds,
			final List<CharacterTraitInfo> traitInfoList) {

		final Map<Integer, List<String>> traitValues = new HashMap<>();

		// Get trait IDs
		final List<Integer> traitIds = new ArrayList<>();
		for (final CharacterTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT DISTINCT p.observable_id, p.value " + "FROM phenotype p "
							+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id IN (:traitIds) "
							+ "ORDER BY p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final String value = (String) row[1];

				List<String> values = new ArrayList<>();
				values.add(value);
				// If the trait exists in the map, add the value found. Else, just add the <trait, values> pair.
				if (traitValues.containsKey(traitId)) {
					values = traitValues.get(traitId);
					values.add(value);
					traitValues.remove(traitId);
				}
				traitValues.put(traitId, values);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getCharacterTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return traitValues;

	}

	public void setCategoricalTraitInfoValues(final List<CategoricalTraitInfo> traitInfoList, final List<Integer> environmentIds) {

		// Get trait IDs
		final List<Integer> traitIds = new ArrayList<>();
		for (final CategoricalTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount " + "FROM phenotype p "
							+ "INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id "
							+ "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
							+ "  AND e.nd_geolocation_id IN (:environmentIds) " + "GROUP BY p.observable_id, p.cvalue_id ");
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final Integer cValueId = (Integer) row[1];
				final Long count = ((BigInteger) row[2]).longValue();

				for (final CategoricalTraitInfo traitInfo : traitInfoList) {
					if (traitInfo.getId() == traitId) {
						traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
						break;
					}
				}

			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

	}

	public List<Observation> getObservationForTraitOnGermplasms(final List<Integer> traitIds, final List<Integer> germplasmIds,
			final List<Integer> environmentIds) {
		final List<Observation> observationFinal = new ArrayList<>();

		try {
			final StringBuilder sb = new StringBuilder(PhenotypeDao.GET_OBSERVATIONS);
			sb.append(" AND s.dbxref_id IN (:germplasmIds) ");
			sb.append(PhenotypeDao.ORDER_BY_OBS);
			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("germplasmIds", germplasmIds);
			query.setParameterList("environmentIds", environmentIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final Integer germplasmId = (Integer) row[1];
				final Integer environmentId = (Integer) row[2];
				final String value = (String) row[3];

				final ObservationKey rowKey = new ObservationKey(traitId, germplasmId, environmentId);
				final Observation observation = new Observation(rowKey, value);
				observationFinal.add(observation);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getObservationForTraitOnGermplasms() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return observationFinal;
	}

	public long countObservationForTraits(final List<Integer> traitIds, final List<Integer> environmentIds) {

		try {
			final SQLQuery query = this.getSession().createSQLQuery(PhenotypeDao.COUNT_OBSERVATIONS);
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public List<Observation> getObservationForTraits(final List<Integer> traitIds, final List<Integer> environmentIds, final int start,
			final int numOfRows) {

		final List<Observation> toReturn = new ArrayList<>();

		try {
			final StringBuilder sb = new StringBuilder(PhenotypeDao.GET_OBSERVATIONS);
			sb.append(PhenotypeDao.ORDER_BY_OBS);
			final SQLQuery query = this.getSession().createSQLQuery(sb.toString());

			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);
			this.setStartAndNumOfRows(query, start, numOfRows);
			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer traitId = (Integer) row[0];
				final Integer germplasmId = (Integer) row[1];
				final Integer environmentId = (Integer) row[2];
				final String value = (String) row[3];

				toReturn.add(new Observation(new ObservationKey(traitId, germplasmId, environmentId), value));

			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<TraitObservation> getObservationsForTrait(final int traitId, final List<Integer> environmentIds) {
		final List<TraitObservation> traitObservationList = new ArrayList<>();

		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT p.observable_id, p.value, s.dbxref_id, e.nd_experiment_id, l.lname, gp.value as locationId ");
			queryString.append("FROM phenotype p ");
			queryString.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ");
			queryString.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
					+ TermId.LOCATION_ID.getId() + " ");
			queryString.append(" LEFT JOIN location l ON l.locid = gp.value ");
			queryString.append("INNER JOIN stock s ON s.stock_id = e.stock_id ");
			queryString.append("WHERE p.observable_id = :traitId AND e.nd_geolocation_id IN ( :environmentIds ) ");
			queryString.append("ORDER BY s.dbxref_id ");

			PhenotypeDao.LOG.debug(queryString.toString());

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("traitId", traitId).setParameterList("environmentIds", environmentIds);
			query.addScalar("observable_id", Hibernate.INTEGER);
			query.addScalar("value", Hibernate.STRING);
			query.addScalar("dbxref_id", Hibernate.INTEGER);
			query.addScalar("nd_experiment_id", Hibernate.INTEGER);
			query.addScalar("lname", Hibernate.STRING);
			query.addScalar("locationId", Hibernate.INTEGER);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String value = (String) row[1];
				final Integer gid = (Integer) row[2];
				final Integer observationId = (Integer) row[3];
				final String locationName = (String) row[4];
				final Integer locationId = (Integer) row[5];

				traitObservationList.add(new TraitObservation(id, value, gid, observationId, locationName, locationId));
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getObservationsForTrait() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitObservationList;
	}

	public List<TrialEnvironment> getEnvironmentTraits(final Set<TrialEnvironment> trialEnvironments, final List<Integer> experimentTypes) {
		final List<TrialEnvironment> environmentDetails = new ArrayList<>();

		if (trialEnvironments.isEmpty()) {
			return environmentDetails;
		}
		final List<Integer> environmentIds = new ArrayList<>();
		for (final TrialEnvironment environment : trialEnvironments) {
			environmentIds.add(environment.getId());
			environmentDetails.add(environment);
		}

		final StringBuilder sql = new StringBuilder()
				.append("SELECT DISTINCT e.nd_geolocation_id as nd_geolocation_id, p.observable_id as observable_id, trait.name as name, property.name as property, trait.definition as definition, c_scale.name as scale, cr_type.object_id as object_id ")
				.append("	FROM phenotype p ")
				.append("	INNER JOIN nd_experiment e ON p.nd_experiment_id = e.nd_experiment_id  AND e.nd_geolocation_id IN (:environmentIds) AND e.type_id in (:experimentTypes)")
				.append("	LEFT JOIN cvterm_relationship cr_scale ON p.observable_id = cr_scale.subject_id AND cr_scale.type_id = 1220 ")
				.append("	LEFT JOIN cvterm_relationship cr_type ON cr_type.subject_id = cr_scale.object_id  AND cr_type.type_id = 1105 ")
				.append("	LEFT JOIN cvterm_relationship cr_property ON p.observable_id = cr_property.subject_id AND cr_property.type_id = 1200 ")
				.append("	LEFT JOIN cvterm c_scale ON c_scale.cvterm_id = cr_scale.object_id ")
				.append("	LEFT JOIN cvterm trait ON trait.cvterm_id = p.observable_id ")
				.append("	LEFT JOIN cvterm property ON property.cvterm_id = cr_property.object_id ");

		try {

			final Query query = this.getSession().createSQLQuery(sql.toString()).addScalar("nd_geolocation_id").addScalar("observable_id")
					.addScalar("name").addScalar("property").addScalar("definition").addScalar("scale").addScalar("object_id")
					.setParameterList("environmentIds", environmentIds).setParameterList("experimentTypes", experimentTypes);

			final List<Object[]> result = query.list();

			for (final Object[] row : result) {
				final Integer environmentId = (Integer) row[0];
				final Integer traitId = (Integer) row[1];
				final String traitName = (String) row[2];
				final String property = (String) row[3];
				final String traitDescription = (String) row[4];
				final String scaleName = (String) row[5];
				final Integer typeId = (Integer) row[6];

				final int index = environmentDetails.indexOf(new TrialEnvironment(environmentId));
				final TrialEnvironment environment = environmentDetails.get(index);
				environment.addTrait(new TraitInfo(traitId, traitName, property, traitDescription, scaleName, typeId));
				environmentDetails.set(index, environment);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getEnvironmentTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return environmentDetails;
	}

	public void deletePhenotypesByProjectIdAndLocationId(final Integer projectId, final Integer locationId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete phenotypes and experiment phenotypes
			final String sql = "delete pheno " + " from nd_experiment e,"
					+ "  phenotype pheno" + " where e.project_id = " + projectId
					+ " and e.nd_geolocation_id = " + locationId + " and e.nd_experiment_id = e.nd_experiment_id"
					+ " and e.nd_experiment_id = pheno.nd_experiment_id";
			final SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.executeUpdate();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in deletePhenotypesByProjectIdAndLocationId=" + projectId + ", " + locationId
					+ " in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public int updatePhenotypesByProjectIdAndLocationId(final Integer projectId, final Integer locationId, final Integer stockId,
			final Integer cvTermId, final String value) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// update the value of phenotypes
			final String sql =
				"UPDATE nd_experiment exp "
					+ "INNER JOIN phenotype pheno ON exp.nd_experiment_id = pheno.nd_experiment_id " + "SET pheno.value = '" + value + "'"
					+ " WHERE exp.project_id = " + projectId + " AND exp.nd_geolocation_id = " + locationId + " AND exp.type_id = 1170 "
					+ " AND exp.stock_id = " + stockId + " AND pheno.observable_id = " + cvTermId;

			final SQLQuery statement = this.getSession().createSQLQuery(sql);
			final int returnVal = statement.executeUpdate();

			return returnVal;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in updatePhenotypesByProjectIdAndLocationId=" + projectId + ", " + locationId
					+ " in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(final Integer projectId, final Integer locationId, final List<Integer> plotNos,
			final List<Integer> cvTermIds) {
		try {
			// get the phenotype_id
			final String sql = "SELECT  expprop.value, pheno.observable_id, pheno.phenotype_id FROM nd_experiment e "
					+ "INNER JOIN nd_experiment exp ON e.nd_experiment_id = exp.nd_experiment_id "
					+ "INNER JOIN nd_experimentprop expprop ON expprop.nd_experiment_id = exp.nd_experiment_id "
					+ "INNER JOIN phenotype pheno ON  exp.nd_experiment_id = pheno.nd_experiment_id " + "WHERE ep.project_id = :projectId "
					+ "AND exp.nd_geolocation_id = :locationId " + "AND pheno.observable_id IN (:cvTermIds) "
					+ "AND expprop.value IN (:plotNos) " + "AND exp.type_id = 1155 " + "AND expprop.type_id in (8200, 8380)";

			final SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.setParameter("projectId", projectId);
			statement.setParameter("locationId", locationId);
			statement.setParameterList("cvTermIds", cvTermIds);
			statement.setParameterList("plotNos", plotNos);

			final List<Object[]> returnVal = statement.list();

			return returnVal;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getPhenotypeIdsByLocationAndPlotNo=" + projectId + ", " + locationId + " in PhenotypeDao: " + e.getMessage(),
					e);
		}
	}

	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(final Integer projectId, final Integer locationId, final Integer plotNo,
			final List<Integer> cvTermIds) {
		try {
			if (cvTermIds.isEmpty()) {
				return new ArrayList<>();
			}

			// get the phenotype_id
			final String sql = "SELECT  expprop.value, pheno.observable_id, pheno.phenotype_id FROM "
					+ "nd_experiment exp "
					+ "INNER JOIN nd_experimentprop expprop ON expprop.nd_experiment_id = exp.nd_experiment_id "
					+ "INNER JOIN phenotype pheno ON exp.nd_experiment_id = pheno.nd_experiment_id " + "WHERE exp.project_id = :projectId "
					+ "AND exp.nd_geolocation_id = :locationId " + "AND pheno.observable_id IN (:cvTermIds) "
					+ "AND expprop.value = :plotNo " + "AND exp.type_id = 1155 " + "AND expprop.type_id in (8200, 8380)";

			final SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.setParameter("projectId", projectId);
			statement.setParameter("locationId", locationId);
			statement.setParameterList("cvTermIds", cvTermIds);
			statement.setParameter("plotNo", plotNo);

			final List<Object[]> returnVal = statement.list();

			return returnVal;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getPhenotypeIdsByLocationAndPlotNo=" + projectId + ", " + locationId + " in PhenotypeDao: " + e.getMessage(),
					e);
		}
	}

	public int countRecordedVariatesOfStudy(final Integer projectId, final List<Integer> variateIds) {
		try {

			if (variateIds != null && !variateIds.isEmpty()) {
				final StringBuilder sql = new StringBuilder();

				sql.append("SELECT COUNT(p.phenotype_id) FROM phenotype p ")
						.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
						.append("WHERE e.project_id = ").append(projectId).append(" AND p.observable_id IN (");
				for (int i = 0; i < variateIds.size(); i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append(variateIds.get(i));
				}
				sql.append(") AND (p.value <> '' or p.cvalue_id <> '')");
				final Query query = this.getSession().createSQLQuery(sql.toString());

				return ((BigInteger) query.uniqueResult()).intValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countPlantsSelectedOfNursery() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public int countVariatesDataOfStudy(final Integer projectId, final List<Integer> variateIds) {
		try {

			if (variateIds != null && !variateIds.isEmpty()) {
				final StringBuilder sql = new StringBuilder();

				sql.append("SELECT COUNT(p.phenotype_id) FROM phenotype p ")
						.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
						.append("WHERE e.project_id = ").append(projectId).append(" AND p.observable_id IN (");
				for (int i = 0; i < variateIds.size(); i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append(variateIds.get(i));
				}
				sql.append(") AND ((value IS NOT NULL AND value <> '') OR (cvalue_id IS NOT NULL AND cvalue_id <> ''))");

				final Query query = this.getSession().createSQLQuery(sql.toString());

				return ((BigInteger) query.uniqueResult()).intValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countVariatesDataOfStudy() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public Map<Integer, Integer> countVariatesDataOfStudy(final Integer projectId) {
		final Map<Integer, Integer> map = new HashMap<>();
		try {

			final StringBuilder sql = new StringBuilder();

			sql.append("SELECT COUNT(p.phenotype_id), p.observable_id FROM phenotype p ")
					.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
					.append("WHERE e.project_id = ").append(projectId).append(" AND (p.value <> '' OR p.cvalue_id > 0) ")
					.append(" GROUP BY p.observable_id ");
			final Query query = this.getSession().createSQLQuery(sql.toString());

			final List<Object[]> result = query.list();
			if (result != null) {
				for (final Object[] row : result) {
					map.put((Integer) row[1], ((BigInteger) row[0]).intValue());
				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at countVariatesDataOfStudy() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return map;
	}

	public List<Phenotype> getByTypeAndValue(final int typeId, final String value, final boolean isEnumeration) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("observableId", typeId));
			if (isEnumeration) {
				criteria.add(Restrictions.eq("cValueId", Integer.parseInt(value)));
			} else {
				criteria.add(Restrictions.eq("value", value));
			}
			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getByTypeAndValue(" + typeId + ", " + value + ") in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public void deletePhenotypesInProjectByTerm(final List<Integer> ids, final int termId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out
			// of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			final StringBuilder sql = new StringBuilder().append("DELETE FROM phenotype ").append(" WHERE phenotype_id IN ( ")
					.append(" SELECT ph.phenotype_id ").append(" FROM (SELECT * FROM phenotype) ph ")
					.append(" INNER JOIN nd_experiment ep ON ep.nd_experiment_id = ph.nd_experiment_id ")
					.append(" AND ep.project_id IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append(ids.get(i));
			}
			sql.append(")) ").append(" AND observable_id = ").append(termId);

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE PHENOTYPE ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in deletePhenotypesInProjectByTerm(" + ids + ", " + termId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public Integer getPhenotypeIdByProjectAndType(final int projectId, final int typeId) {
		try {
			final StringBuilder sql = new StringBuilder().append(" SELECT p.phenotype_id ").append(" FROM phenotype p ")
					.append(" INNER JOIN nd_experiment ep ON ep.nd_experiment_id = p.nd_experiment_id ")
					.append("   AND ep.project_id = ").append(projectId).append(" WHERE p.observable_id = ").append(typeId);
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			final List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getPhenotypeIdByProjectAndType(" + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return null;
	}

	public Phenotype getPhenotypeByProjectExperimentAndType(final int projectId, final int experimentId, final int typeId) {
		try {
			final StringBuilder sql = new StringBuilder()
					.append(" SELECT p.phenotype_id, p.uniquename, p.name, p.observable_id, p.attr_id, p.value, p.cvalue_id, p.assay_id ")
					.append(" FROM phenotype p ")
					.append(" INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
					.append("   AND e.project_id = ").append(projectId).append(" WHERE p.observable_id in ( ").append(typeId)
					.append(") AND e.nd_experiment_id = ").append(experimentId);
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			final List<Object[]> list = query.list();
			Phenotype phenotype = null;
			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					phenotype = new Phenotype((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (Integer) row[4],
							(String) row[5], (Integer) row[6], (Integer) row[7]);
				}
			}

			return phenotype;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getPhenotypeByProjectExperimentAndType(" + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(),
					e);
		}
	}

	public Boolean containsAtLeast2CommonEntriesWithValues(final int projectId, final int locationId, final int germplasmTermId) {

		String groupByGermplasm = "stock.stock_id";
		if (germplasmTermId == TermId.DESIG.getId()) {
			groupByGermplasm = "stock.name";
		} else if (germplasmTermId == TermId.GID.getId()) {
			groupByGermplasm = "stock.dbxref_id";
		} else if (germplasmTermId == TermId.ENTRY_NO.getId()) {
			groupByGermplasm = "stock.uniquename";
		}

		final StringBuilder sql = new StringBuilder().append(" SELECT phenotype.observable_id,count(phenotype.observable_id) ")
				.append(" FROM nd_experiment nd_exp ")
				.append(" INNER JOIN stock ON nd_exp.stock_id = stock.stock_id ")
				.append(" LEFT JOIN phenotype  ON nd_exp.nd_experiment_id = phenotype.nd_experiment_id ").append(" where nd_exp.project_id = ")
				.append(projectId).append(" and nd_exp.nd_geolocation_id = ").append(locationId)
				.append(" and ((phenotype.value <> '' and phenotype.value is not null) or ")
				.append(" (phenotype.cvalue_id <> '' and phenotype.cvalue_id is not null)) ").append(" group by nd_exp.nd_geolocation_id, ")
				.append(groupByGermplasm).append(" , phenotype.observable_id ")
				.append(" having count(phenotype.observable_id) >= 2 LIMIT 1 ");

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

		return !query.list().isEmpty();
	}

	public List<Phenotype> getByProjectAndType(final int projectId, final int typeId) {
		final List<Phenotype> phenotypes = new ArrayList<>();
		try {
			final StringBuilder sql = new StringBuilder()
					.append(" SELECT p.phenotype_id, p.uniquename, p.name, p.observable_id, p.attr_id, p.value, p.cvalue_id, p.assay_id ")
					.append(" FROM phenotype p ")
					.append(" INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
					.append("   AND e.project_id = ").append(projectId).append(" WHERE p.observable_id = ").append(typeId);
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			final List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					phenotypes.add(new Phenotype((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (Integer) row[4],
							(String) row[5], (Integer) row[6], (Integer) row[7]));
				}
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error in getByProjectAndType(" + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return phenotypes;
	}

	public Phenotype getByExperimentAndTrait(final Integer experimentId, final Integer termId) {
		try {
			final ExperimentModel experiment = new ExperimentModel();
			experiment.setNdExperimentId(experimentId);
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("observableId", termId));
			criteria.add(Restrictions.eq("experiment", experiment));
			criteria.addOrder(Order.desc("phenotypeId"));
			final List list = criteria.list();
			return (list.size() > 0 ? (Phenotype) list.get(0) : null);

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in getByExperimentAndTrait(" + experimentId + ", " + termId + ") in PhenotypeDao: " + e.getMessage(), e);
		}

	}

	public List<PhenotypeSearchDTO> searchPhenotypes(final Integer pageSize, final Integer pageNumber, final PhenotypeSearchRequestDTO requestDTO) {
		final StringBuilder queryString = new StringBuilder(PhenotypeQuery.PHENOTYPE_SEARCH);

		final List<String> cvTermIds = requestDTO.getCvTermIds();

		if (requestDTO.getStudyDbIds() != null && !requestDTO.getStudyDbIds().isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER);
		}

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATION_FILTER);
		}

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());

		if (pageNumber != null && pageSize != null) {
			sqlQuery.setFirstResult(pageSize * (pageNumber - 1));
			sqlQuery.setMaxResults(pageSize);
		}

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			sqlQuery.setParameterList("cvTermIds", cvTermIds);
		}

		if (requestDTO.getStudyDbIds() != null && !requestDTO.getStudyDbIds().isEmpty()) {
			sqlQuery.setParameterList("studyDbIds", requestDTO.getStudyDbIds());
		}

		sqlQuery.addScalar("nd_experiment_id").addScalar("observationUnitDbId", new StringType()).addScalar("observationUnitName")
				.addScalar("observationLevel").addScalar("plantNumber", new IntegerType()).addScalar("germplasmDbId", new StringType())
				.addScalar("germplasmName").addScalar("studyDbId", new StringType()).addScalar("studyName").addScalar("programName")
				.addScalar("FieldMapRow").addScalar("FieldMapCol").addScalar("plotNumber", new StringType())
				.addScalar("blockNumber", new StringType()).addScalar("replicate", new StringType()).addScalar("COL").addScalar("ROW")
				.addScalar("studyLocationDbId", new StringType()).addScalar("studyLocation", new StringType()).addScalar("entryType")
				.addScalar("entryNumber", new StringType());

		final List<Object[]> results = sqlQuery.list();

		final Map<Integer, PhenotypeSearchDTO> observationUnitsByNdExpId = new LinkedHashMap<>();

		if (results != null && !results.isEmpty()) {

			// Process ObservationUnits (Measurement row)
			for (final Object[] row : results) {
				final PhenotypeSearchDTO observationUnit = new PhenotypeSearchDTO();

				final Integer ndExperimentId = (Integer) row[0];
				observationUnit.setObservationUnitDbId((String) row[1]); // OBS_UNIT_ID
				observationUnit.setObservationUnitName((String) row[2]);
				observationUnit.setObservationLevel((String) row[3]);
				observationUnit.setObservationLevels("1");
				observationUnit.setPlantNumber((String) row[4]);
				observationUnit.setGermplasmDbId((String) row[5]);
				observationUnit.setGermplasmName((String) row[6]);
				observationUnit.setStudyDbId((String) row[7]);
				observationUnit.setStudyName((String) row[8]);
				observationUnit.setProgramName((String) row[9]);
				String x = (String) row[15]; // ROW
				String y = (String) row[16]; // COL
				if (StringUtils.isBlank(x) || StringUtils.isBlank(y)) {
					x = (String) row[10]; // fieldMapRow
					y = (String) row[11]; // fieldMapCol
				}
				observationUnit.setX(x);
				observationUnit.setY(y);
				observationUnit.setPlotNumber((String) row[12]);
				observationUnit.setBlockNumber((String) row[13]);
				observationUnit.setReplicate((String) row[14]);
				observationUnit.setStudyLocationDbId((String) row[17]);
				observationUnit.setStudyLocation((String) row[18]);
				observationUnit.setEntryType((String) row[19]);
				observationUnit.setEntryNumber((String) row[20]);

				observationUnitsByNdExpId.put(ndExperimentId, observationUnit);
			}

			// Get observations (Traits)
			final SQLQuery observationsQuery = this.getSession().createSQLQuery(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATIONS);
			observationsQuery.setParameterList("ndExperimentIds", observationUnitsByNdExpId.keySet());
			observationsQuery.addScalar("expid").addScalar("phen_id").addScalar("cvterm_id")
					.addScalar("cvterm_name", new StringType()).
					addScalar("value", new StringType()).addScalar("crop_ontology_id", new StringType());
			final List<Object[]> observationResults = observationsQuery.list();

			for (final Object[] result : observationResults) {
				final Integer ndExperimentId = (Integer) result[0];

				final PhenotypeSearchObservationDTO observation = new PhenotypeSearchObservationDTO();
				final String variableId = (result[5] != null && !((String) result[5]).isEmpty()) ? (String) result[5] : String.valueOf(result[2]);
				observation.setObservationVariableDbId(variableId);
				observation.setObservationVariableName((String) result[3]);
				observation.setObservationTimeStamp(StringUtils.EMPTY);
				observation.setSeason(StringUtils.EMPTY);
				observation.setCollector(StringUtils.EMPTY);
				observation.setObservationDbId((Integer) result[1]);
				observation.setValue((String) result[4]);

				final PhenotypeSearchDTO observationUnit = observationUnitsByNdExpId.get(ndExperimentId);
				// TODO solve duplicate nd_experiment_phenotype_id
				observationUnit.getObservations().add(observation);
			}
		}

		return new ArrayList<>(observationUnitsByNdExpId.values());
	}

	public long countPhenotypes(final PhenotypeSearchRequestDTO requestDTO) {
		final StringBuilder queryString = new StringBuilder(PhenotypeQuery.PHENOTYPE_SEARCH);

		final List<String> cvTermIds = requestDTO.getCvTermIds();
		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_OBSERVATION_FILTER);
		}

		if (requestDTO.getStudyDbIds() != null && !requestDTO.getStudyDbIds().isEmpty()) {
			queryString.append(PhenotypeQuery.PHENOTYPE_SEARCH_STUDY_DB_ID_FILTER);
		}

		final SQLQuery query = this.getSession().createSQLQuery("SELECT COUNT(1) FROM (" + queryString + ") T");

		if (cvTermIds != null && !cvTermIds.isEmpty()) {
			query.setParameterList("cvTermIds", cvTermIds);
		}

		if (requestDTO.getStudyDbIds() != null && !requestDTO.getStudyDbIds().isEmpty()) {
			query.setParameterList("studyDbIds", requestDTO.getStudyDbIds());
		}

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	@Override
	public Phenotype save(final Phenotype phenotype) {
		try {
			final Date date = new Date();
			this.savePhenotype(phenotype, date);
			return phenotype;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in save(" + phenotype + "): " + e.getMessage(), e);
		}
	}

	@Override
	public Phenotype saveOrUpdate(final Phenotype entity) {
		try {
			final Date date = new Date();
			if (entity.getPhenotypeId() == null) {
				this.savePhenotype(entity, date);
			}
			else {
				entity.setUpdatedDate(date);
			}
			this.getSession().saveOrUpdate(entity);
			return entity;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(entity): " + e.getMessage(), e);
		}
	}

	@Override
	public Phenotype merge(final Phenotype entity) {
		try {
			final Date date = new Date();
			if (entity.getPhenotypeId() == null) {
				this.savePhenotype(entity, date);
			}
			else {
				entity.setUpdatedDate(date);
			}
			this.getSession().merge(entity);
			return entity;
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in merge(entity): " + e.getMessage(), e);
		}
	}

	private void savePhenotype(final Phenotype phenotype,final Date date) {
		final Session currentSession = this.getSession();
		phenotype.setCreatedDate(date);
		phenotype.setUpdatedDate(date);
		currentSession.save(phenotype);
	}

	public Boolean hasOutOfSync(final Integer projectId) {
		final SQLQuery query = this.getSession().createSQLQuery(HAS_OUT_OF_SYNC);
		query.setParameter("projectId", projectId);
		final BigInteger result = (BigInteger) query.uniqueResult();
		return result.intValue() > 0;
	}
}
