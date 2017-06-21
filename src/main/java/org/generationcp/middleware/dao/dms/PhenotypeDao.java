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
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.util.Debug;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
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
			+ "FROM nd_experiment e " + "INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
			+ "INNER JOIN stock s ON es.stock_id = s.stock_id "
			+ "INNER JOIN nd_experiment_phenotype ep ON e.nd_experiment_id = ep.nd_experiment_id "
			+ "INNER JOIN phenotype p ON ep.phenotype_id = p.phenotype_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
			+ "AND p.observable_id IN (:traitIds) ";

	private static final String COUNT_OBSERVATIONS = "SELECT COUNT(*) " + "FROM nd_experiment e "
			+ "INNER JOIN nd_experiment_stock es ON e.nd_experiment_id = es.nd_experiment_id "
			+ "INNER JOIN stock s ON es.stock_id = s.stock_id "
			+ "INNER JOIN nd_experiment_phenotype ep ON e.nd_experiment_id = ep.nd_experiment_id "
			+ "INNER JOIN phenotype p ON ep.phenotype_id = p.phenotype_id " + "WHERE e.nd_geolocation_id IN (:environmentIds) "
			+ "AND p.observable_id IN (:traitIds) ";

	private static final String ORDER_BY_OBS = "ORDER BY p.observable_id, s.dbxref_id, e.nd_geolocation_id, p.value ";

	public List<NumericTraitInfo> getNumericTraitInfoList(List<Integer> environmentIds, List<Integer> numericVariableIds)
			throws MiddlewareQueryException {
		List<NumericTraitInfo> numericTraitInfoList = new ArrayList<>();
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
									+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
									+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count , "
									+ "IF (MIN(p.value * 1) IS NULL, 0, MIN(p.value * 1))  AS min_value, "
									+ "IF (MAX(p.value * 1) IS NULL, 0, MAX(p.value * 1)) AS max_value " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
									+ "    INNER JOIN stock s ON es.stock_id = s.stock_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) "
									+ "    AND p.observable_id IN (:numericVariableIds) " + "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("numericVariableIds", numericVariableIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !numericVariableIds.isEmpty()) {
				list = query.list();

				for (Object[] row : list) {
					Integer id = (Integer) row[0];
					Long locationCount = ((BigInteger) row[1]).longValue();
					Long germplasmCount = ((BigInteger) row[2]).longValue();
					Long observationCount = ((BigInteger) row[3]).longValue();
					Double minValue = (Double) row[4];
					Double maxValue = (Double) row[5];

					NumericTraitInfo numericTraitInfo =
							new NumericTraitInfo(null, id, null, locationCount, germplasmCount, observationCount, minValue, maxValue, 0);
					numericTraitInfoList.add(numericTraitInfo);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getNumericTraitInfoList() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return numericTraitInfoList;

	}

	public List<TraitInfo> getTraitInfoCounts(List<Integer> environmentIds, List<Integer> variableIds) throws MiddlewareQueryException {
		List<TraitInfo> traitInfoList = new ArrayList<>();
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
									+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
									+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
									+ "    INNER JOIN stock s ON es.stock_id = s.stock_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id IN (:variableIds) "
									+ "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("variableIds", variableIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !variableIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer id = (Integer) row[0];
				Long locationCount = ((BigInteger) row[1]).longValue();
				Long germplasmCount = ((BigInteger) row[2]).longValue();
				Long observationCount = ((BigInteger) row[3]).longValue();

				traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitInfoList;

	}

	public List<TraitInfo> getTraitInfoCounts(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TraitInfo> traitInfoList = new ArrayList<>();
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, " + "COUNT(DISTINCT e.nd_geolocation_id) AS location_count, "
									+ "COUNT(DISTINCT s.dbxref_id) AS germplasm_count, "
									+ "COUNT(DISTINCT e.nd_experiment_id) AS observation_count " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "    INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id "
									+ "    INNER JOIN stock s ON es.stock_id = s.stock_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "GROUP by p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);

			List<Object[]> list = query.list();

			for (Object[] row : list) {
				Integer id = (Integer) row[0];
				Long locationCount = ((BigInteger) row[1]).longValue();
				Long germplasmCount = ((BigInteger) row[2]).longValue();
				Long observationCount = ((BigInteger) row[3]).longValue();

				traitInfoList.add(new TraitInfo(id, null, null, locationCount, germplasmCount, observationCount));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getTraitInfoCounts() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitInfoList;

	}

	public Map<Integer, List<Double>> getNumericTraitInfoValues(List<Integer> environmentIds, List<NumericTraitInfo> traitInfoList)
			throws MiddlewareQueryException {
		Map<Integer, List<Double>> traitValues = new HashMap<>();

		// Get trait IDs
		List<Integer> traitIds = new ArrayList<>();
		for (NumericTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, p.value * 1 " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id IN (:traitIds) ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Double value = (Double) row[1];

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

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitValues;

	}

	public Map<Integer, List<Double>> getNumericTraitInfoValues(List<Integer> environmentIds, Integer trait)
			throws MiddlewareQueryException {
		Map<Integer, List<Double>> traitValues = new HashMap<>();

		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, p.value * 1 " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id = :traitId ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameter("traitId", trait);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Double value = (Double) row[1];

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

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getNumericTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return traitValues;

	}

	public Map<Integer, List<String>> getCharacterTraitInfoValues(List<Integer> environmentIds, List<CharacterTraitInfo> traitInfoList)
			throws MiddlewareQueryException {

		Map<Integer, List<String>> traitValues = new HashMap<>();

		// Get trait IDs
		List<Integer> traitIds = new ArrayList<>();
		for (CharacterTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT DISTINCT p.observable_id, p.value " + "FROM phenotype p "
									+ "    INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "    INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "WHERE e.nd_geolocation_id IN (:environmentIds) " + "    AND p.observable_id IN (:traitIds) "
									+ "ORDER BY p.observable_id ");
			query.setParameterList("environmentIds", environmentIds);
			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				String value = (String) row[1];

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

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getCharacterraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return traitValues;

	}

	public void setCategoricalTraitInfoValues(List<CategoricalTraitInfo> traitInfoList, boolean isCentral) throws MiddlewareQueryException {
		Map<Integer, String> valueIdName = new HashMap<>();

		// Get trait IDs
		List<Integer> traitIds = new ArrayList<>();
		for (CategoricalTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		String queryString =
				"SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount " + "FROM phenotype p "
						+ "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) " + "GROUP BY p.observable_id, p.cvalue_id ";

		if (isCentral) {
			queryString =
					"SELECT p.observable_id, p.cvalue_id, c.name, COUNT(p.phenotype_id) AS valuesCount " + "FROM phenotype p  "
							+ "INNER JOIN cvterm c on p.cvalue_id = c.cvterm_id "
							+ "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
							+ "GROUP BY p.observable_id, p.cvalue_id, c.name  ";
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = new ArrayList<>();

			if (!traitIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Integer cValueId = (Integer) row[1];
				Long count = 0L;
				if (isCentral) {
					String cValueName = (String) row[2];
					valueIdName.put(cValueId, cValueName);
					count = ((BigInteger) row[3]).longValue();
				} else {
					count = ((BigInteger) row[2]).longValue();
				}

				// add value count to categorical traits
				for (CategoricalTraitInfo traitInfo : traitInfoList) {
					if (traitInfo.getId() == traitId) {
						traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
						break;
					}
				}

			}

			// This step was added since the valueName is not retrieved correctly with the previous query in Java (setCategoricalVariables).
			// Most probably because of the two cvterm id-name present in the query.
			// The steps that follow will just retrieve the name of the categorical values in each variable.

			if (isCentral) {
				for (CategoricalTraitInfo traitInfo : traitInfoList) {
					List<CategoricalValue> values = traitInfo.getValues();
					for (CategoricalValue value : values) {
						String name = valueIdName.get(value.getId());
						value.setName(name);
					}
					traitInfo.setValues(values);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

	}

	public void setCategoricalTraitInfoValues(List<CategoricalTraitInfo> traitInfoList, List<Integer> environmentIds)
			throws MiddlewareQueryException {

		// Get trait IDs
		List<Integer> traitIds = new ArrayList<>();
		for (CategoricalTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT p.observable_id, p.cvalue_id, COUNT(p.phenotype_id) AS valuesCount " + "FROM phenotype p "
									+ "INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id "
									+ "INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id "
									+ "WHERE p.cvalue_id IS NOT NULL AND p.observable_id IN (:traitIds) "
									+ "  AND e.nd_geolocation_id IN (:environmentIds) " + "GROUP BY p.observable_id, p.cvalue_id ");
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Integer cValueId = (Integer) row[1];
				Long count = ((BigInteger) row[2]).longValue();

				for (CategoricalTraitInfo traitInfo : traitInfoList) {
					if (traitInfo.getId() == traitId) {
						traitInfo.addValueCount(new CategoricalValue(cValueId), count.longValue());
						break;
					}
				}

			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getCategoricalTraitInfoValues() query on PhenotypeDao: " + e.getMessage(), e);
		}

	}

	public List<Observation> getObservationForTraitOnGermplasms(List<Integer> traitIds, List<Integer> germplasmIds,
			List<Integer> environmentIds) throws MiddlewareQueryException {
		List<Observation> observationFinal = new ArrayList<>();

		try {
			StringBuilder sb = new StringBuilder(PhenotypeDao.GET_OBSERVATIONS);
			sb.append(" AND s.dbxref_id IN (:germplasmIds) ");
			sb.append(PhenotypeDao.ORDER_BY_OBS);
			SQLQuery query = this.getSession().createSQLQuery(sb.toString());
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("germplasmIds", germplasmIds);
			query.setParameterList("environmentIds", environmentIds);

			List<Object[]> list = new ArrayList<>();

			if (!environmentIds.isEmpty() && !traitIds.isEmpty()) {
				list = query.list();
			}

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Integer germplasmId = (Integer) row[1];
				Integer environmentId = (Integer) row[2];
				String value = (String) row[3];

				ObservationKey rowKey = new ObservationKey(traitId, germplasmId, environmentId);
				Observation observation = new Observation(rowKey, value);
				observationFinal.add(observation);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getObservationForTraitOnGermplasms() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return observationFinal;
	}

	public long countObservationForTraits(List<Integer> traitIds, List<Integer> environmentIds) throws MiddlewareQueryException {

		try {
			SQLQuery query = this.getSession().createSQLQuery(PhenotypeDao.COUNT_OBSERVATIONS);
			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);
			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Observation> getObservationForTraits(List<Integer> traitIds, List<Integer> environmentIds, int start, int numOfRows)
			throws MiddlewareQueryException {

		List<Observation> toReturn = new ArrayList<>();

		try {
			StringBuilder sb = new StringBuilder(PhenotypeDao.GET_OBSERVATIONS);
			sb.append(PhenotypeDao.ORDER_BY_OBS);
			SQLQuery query = this.getSession().createSQLQuery(sb.toString());

			query.setParameterList("traitIds", traitIds);
			query.setParameterList("environmentIds", environmentIds);
			this.setStartAndNumOfRows(query, start, numOfRows);
			List<Object[]> list = query.list();

			for (Object[] row : list) {
				Integer traitId = (Integer) row[0];
				Integer germplasmId = (Integer) row[1];
				Integer environmentId = (Integer) row[2];
				String value = (String) row[3];

				toReturn.add(new Observation(new ObservationKey(traitId, germplasmId, environmentId), value));

			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getObservationForTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return toReturn;
	}

	public List<TraitObservation> getObservationsForTrait(int traitId, List<Integer> environmentIds) throws MiddlewareQueryException {
		List<TraitObservation> toreturn = new ArrayList<>();

		try {
			StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT p.observable_id, p.value, s.dbxref_id, e.nd_experiment_id, l.lname, gp.value as locationId ");
			queryString.append("FROM phenotype p ");
			queryString.append("INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id ");
			queryString.append("INNER JOIN nd_experiment e ON e.nd_experiment_id = eph.nd_experiment_id ");
			queryString.append("INNER JOIN nd_geolocationprop gp ON gp.nd_geolocation_id = e.nd_geolocation_id AND gp.type_id = "
					+ TermId.LOCATION_ID.getId() + " ");
			queryString.append(" LEFT JOIN location l ON l.locid = gp.value ");
			queryString.append("INNER JOIN nd_experiment_stock es ON es.nd_experiment_id = e.nd_experiment_id ");
			queryString.append("INNER JOIN stock s ON s.stock_id = es.stock_id ");
			queryString.append("WHERE p.observable_id = :traitId AND e.nd_geolocation_id IN ( :environmentIds ) ");
			queryString.append("ORDER BY s.dbxref_id ");

			LOG.debug(queryString.toString());

			SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("traitId", traitId).setParameterList("environmentIds", environmentIds);
			query.addScalar("observable_id", Hibernate.INTEGER);
			query.addScalar("value", Hibernate.STRING);
			query.addScalar("dbxref_id", Hibernate.INTEGER);
			query.addScalar("nd_experiment_id", Hibernate.INTEGER);
			query.addScalar("lname", Hibernate.STRING);
			query.addScalar("locationId", Hibernate.INTEGER);

			List<Object[]> list = query.list();

			for (Object[] row : list) {
				Integer id = (Integer) row[0];
				String value = (String) row[1];
				Integer gid = (Integer) row[2];
				Integer observationId = (Integer) row[3];
				String locationName = (String) row[4];
				Integer locationId = (Integer) row[5];

				toreturn.add(new TraitObservation(id, value, gid, observationId, locationName, locationId));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getObservationsForTrait() query on PhenotypeDao: " + e.getMessage(), e);

		}

		return toreturn;
	}

	public List<TrialEnvironment> getEnvironmentTraits(final Set<TrialEnvironment> trialEnvironments, final boolean filterByTraits, final boolean filterByAnalysis) throws MiddlewareQueryException {
		List<TrialEnvironment> environmentDetails = new ArrayList<>();

		if (trialEnvironments.isEmpty()) {
			return environmentDetails;
		}
		List<Integer> environmentIds = new ArrayList<>();
		for (TrialEnvironment environment : trialEnvironments) {
			environmentIds.add(environment.getId());
			environmentDetails.add(environment);
		}

		StringBuilder sql = new StringBuilder().append(
			"SELECT DISTINCT e.nd_geolocation_id as nd_geolocation_id, p.observable_id as observable_id, trait.name as name, property.name as property, trait.definition as definition, c_scale.name as scale, cr_type.object_id as object_id ")
			.append("	FROM phenotype p ")
			.append("	INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
			.append("	INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id  AND e.nd_geolocation_id IN (:environmentIds) ")
			.append("	LEFT JOIN cvterm_relationship cr_scale ON p.observable_id = cr_scale.subject_id AND cr_scale.type_id = 1220 ")
			.append("	LEFT JOIN cvterm_relationship cr_type ON cr_type.subject_id = cr_scale.object_id  AND cr_type.type_id = 1105 ")
			.append("	LEFT JOIN cvterm_relationship cr_property ON p.observable_id = cr_property.subject_id AND cr_property.type_id = 1200 ")
			.append("	LEFT JOIN cvterm c_scale ON c_scale.cvterm_id = cr_scale.object_id ")
			.append("	LEFT JOIN cvterm trait ON trait.cvterm_id = p.observable_id ")
			.append("	LEFT JOIN cvterm property ON property.cvterm_id = cr_property.object_id ");

		if (filterByTraits) {
			sql.append(
				"	where exists(SELECT * FROM nd_experiment e2 WHERE e.nd_experiment_id = e2.nd_experiment_id and e2.type_id = 1155) ");
		} else if (filterByAnalysis) {
			sql.append(
				"	where not exists(SELECT * FROM nd_experiment e2 WHERE e.nd_experiment_id = e2.nd_experiment_id and e2.type_id = 1155) ");
		}

		try {

			Query query =
				this.getSession().createSQLQuery(sql.toString()).addScalar("nd_geolocation_id").addScalar("observable_id").addScalar("name")
					.addScalar("property").addScalar("definition").addScalar("scale").addScalar("object_id")
					.setParameterList("environmentIds", environmentIds);

			List<Object[]> result = query.list();

			for (Object[] row : result) {
				final Integer environmentId = (Integer) row[0];
				final Integer traitId = (Integer) row[1];
				final String traitName = (String) row[2];
				final String traitProperty = (String) row[3];
				final String traitDescription = (String) row[4];
				final String scaleName = (String) row[5];
				final Integer typeId = (Integer) row[6];

				int index = environmentDetails.indexOf(new TrialEnvironment(environmentId));
				final TrialEnvironment environment = environmentDetails.get(index);
				environment.addTrait(new TraitInfo(traitId, traitName, traitProperty, traitDescription, scaleName, typeId));
				environmentDetails.set(index, environment);
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getEnvironmentTraits() query on PhenotypeDao: " + e.getMessage(), e);
		}

		return environmentDetails;
	}

	public void deletePhenotypesByProjectIdAndLocationId(Integer projectId, Integer locationId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete phenotypes and experiment phenotypes
			String sql =
					"delete pheno, epheno" + " from nd_experiment_project ep, nd_experiment e,"
							+ " nd_experiment_phenotype epheno, phenotype pheno" + " where ep.project_id = " + projectId
							+ " and e.nd_geolocation_id = " + locationId + " and e.nd_experiment_id = ep.nd_experiment_id"
							+ " and e.nd_experiment_id = epheno.nd_experiment_id" + " and epheno.phenotype_id = pheno.phenotype_id";
			SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.executeUpdate();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in deletePhenotypesByProjectIdAndLocationId=" + projectId + ", " + locationId
					+ " in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public int updatePhenotypesByProjectIdAndLocationId(Integer projectId, Integer locationId, Integer stockId, Integer cvTermId,
			String value) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// update the value of phenotypes
			String sql =
					"UPDATE nd_experiment_project ep " + "INNER JOIN nd_experiment exp ON ep.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experiment_stock expstock ON expstock.nd_experiment_id = exp.nd_experiment_id  "
							+ "INNER JOIN stock ON expstock.stock_id = stock.stock_id "
							+ "INNER JOIN nd_experiment_phenotype expp ON ep.nd_experiment_id = expp.nd_experiment_id  "
							+ "INNER JOIN phenotype pheno ON expp.phenotype_id = pheno.phenotype_id " + "SET pheno.value = '" + value + "'"
							+ " WHERE ep.project_id = " + projectId + " AND exp.nd_geolocation_id = " + locationId
							+ " AND exp.type_id = 1170 " + " AND stock.stock_id = " + stockId + " AND pheno.observable_id = " + cvTermId;

			SQLQuery statement = this.getSession().createSQLQuery(sql);
			int returnVal = statement.executeUpdate();

			return returnVal;

		} catch (HibernateException e) {
			this.logAndThrowException("Error in updatePhenotypesByProjectIdAndLocationId=" + projectId + ", " + locationId
					+ " in PhenotypeDao: " + e.getMessage(), e);
			return 0;
		}
	}

	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(Integer projectId, Integer locationId, List<Integer> plotNos,
			List<Integer> cvTermIds) throws MiddlewareQueryException {
		try {
			// get the phenotype_id
			String sql =
					"SELECT  expprop.value, pheno.observable_id, pheno.phenotype_id FROM nd_experiment_project ep "
							+ "INNER JOIN nd_experiment exp ON ep.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experimentprop expprop ON expprop.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experiment_phenotype expp ON ep.nd_experiment_id = expp.nd_experiment_id  "
							+ "INNER JOIN phenotype pheno ON expp.phenotype_id = pheno.phenotype_id " + "WHERE ep.project_id = :projectId "
							+ "AND exp.nd_geolocation_id = :locationId " + "AND pheno.observable_id IN (:cvTermIds) "
							+ "AND expprop.value IN (:plotNos) " + "AND exp.type_id = 1155 " + "AND expprop.type_id in (8200, 8380)";

			SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.setParameter("projectId", projectId);
			statement.setParameter("locationId", locationId);
			statement.setParameterList("cvTermIds", cvTermIds);
			statement.setParameterList("plotNos", plotNos);

			List<Object[]> returnVal = statement.list();

			return returnVal;

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getPhenotypeIdsByLocationAndPlotNo=" + projectId + ", " + locationId + " in PhenotypeDao: "
					+ e.getMessage(), e);
			return null;
		}
	}

	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(Integer projectId, Integer locationId, Integer plotNo, List<Integer> cvTermIds)
			throws MiddlewareQueryException {
		try {
			if (cvTermIds.isEmpty()) {
				return new ArrayList<>();
			}

			// get the phenotype_id
			String sql =
					"SELECT  expprop.value, pheno.observable_id, pheno.phenotype_id FROM nd_experiment_project ep "
							+ "INNER JOIN nd_experiment exp ON ep.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experimentprop expprop ON expprop.nd_experiment_id = exp.nd_experiment_id "
							+ "INNER JOIN nd_experiment_phenotype expp ON ep.nd_experiment_id = expp.nd_experiment_id  "
							+ "INNER JOIN phenotype pheno ON expp.phenotype_id = pheno.phenotype_id " + "WHERE ep.project_id = :projectId "
							+ "AND exp.nd_geolocation_id = :locationId " + "AND pheno.observable_id IN (:cvTermIds) "
							+ "AND expprop.value = :plotNo " + "AND exp.type_id = 1155 " + "AND expprop.type_id in (8200, 8380)";

			SQLQuery statement = this.getSession().createSQLQuery(sql);
			statement.setParameter("projectId", projectId);
			statement.setParameter("locationId", locationId);
			statement.setParameterList("cvTermIds", cvTermIds);
			statement.setParameter("plotNo", plotNo);

			List<Object[]> returnVal = statement.list();

			return returnVal;

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getPhenotypeIdsByLocationAndPlotNo=" + projectId + ", " + locationId + " in PhenotypeDao: "
					+ e.getMessage(), e);
			return null;
		}
	}

	public int countRecordedVariatesOfStudy(Integer projectId, List<Integer> variateIds) throws MiddlewareQueryException {
		try {

			if (variateIds != null && !variateIds.isEmpty()) {
				StringBuilder sql = new StringBuilder();

				sql.append("SELECT COUNT(p.phenotype_id) FROM phenotype p ")
				.append("INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
				.append("INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
				.append("WHERE e.project_id = ").append(projectId).append(" AND p.observable_id IN (");
				for (int i = 0; i < variateIds.size(); i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append(variateIds.get(i));
				}
				sql.append(") AND (p.value <> '' or p.cvalue_id <> '')");
				Query query = this.getSession().createSQLQuery(sql.toString());

				return ((BigInteger) query.uniqueResult()).intValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error at countPlantsSelectedOfNursery() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public int countVariatesDataOfStudy(Integer projectId, List<Integer> variateIds) throws MiddlewareQueryException {
		try {

			if (variateIds != null && !variateIds.isEmpty()) {
				StringBuilder sql = new StringBuilder();

				sql.append("SELECT COUNT(p.phenotype_id) FROM phenotype p ")
				.append("INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
				.append("INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
				.append("WHERE e.project_id = ").append(projectId).append(" AND p.observable_id IN (");
				for (int i = 0; i < variateIds.size(); i++) {
					if (i > 0) {
						sql.append(",");
					}
					sql.append(variateIds.get(i));
				}
				sql.append(") AND ((value IS NOT NULL AND value <> '') OR (cvalue_id IS NOT NULL AND cvalue_id <> ''))");

				Query query = this.getSession().createSQLQuery(sql.toString());

				return ((BigInteger) query.uniqueResult()).intValue();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error at countVariatesDataOfStudy() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return 0;
	}

	public Map<Integer, Integer> countVariatesDataOfStudy(Integer projectId) throws MiddlewareQueryException {
		Map<Integer, Integer> map = new HashMap<>();
		try {

			StringBuilder sql = new StringBuilder();

			sql.append("SELECT COUNT(p.phenotype_id), p.observable_id FROM phenotype p ")
			.append("INNER JOIN nd_experiment_phenotype ep ON p.phenotype_id = ep.phenotype_id ")
			.append("INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
			.append("WHERE e.project_id = ").append(projectId).append(" AND (p.value <> '' OR p.cvalue_id > 0) ")
			.append(" GROUP BY p.observable_id ");
			Query query = this.getSession().createSQLQuery(sql.toString());

			List<Object[]> result = query.list();
			if (result != null) {
				for (Object[] row : result) {
					map.put((Integer) row[1], ((BigInteger) row[0]).intValue());
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at countVariatesDataOfStudy() query on PhenotypeDao: " + e.getMessage(), e);
		}
		return map;
	}

	public List<Phenotype> getByTypeAndValue(int typeId, String value, boolean isEnumeration) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("observableId", typeId));
			if (isEnumeration) {
				criteria.add(Restrictions.eq("cValueId", Integer.parseInt(value)));
			} else {
				criteria.add(Restrictions.eq("value", value));
			}
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByTypeAndValue(" + typeId + ", " + value + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public void deletePhenotypesInProjectByTerm(List<Integer> ids, int termId) throws MiddlewareQueryException {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			StringBuilder sql =
					new StringBuilder().append("DELETE FROM phenotype ").append(" WHERE phenotype_id IN ( ")
					.append(" SELECT eph.phenotype_id ").append(" FROM nd_experiment_phenotype eph ")
					.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = eph.nd_experiment_id ")
					.append(" AND ep.project_id IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i > 0) {
					sql.append(",");
				}
				sql.append(ids.get(i));
			}
			sql.append(")) ").append(" AND observable_id = ").append(termId);

			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			Debug.println("DELETE PHENOTYPE ROWS FOR " + termId + " : " + query.executeUpdate());

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in deletePhenotypesInProjectByTerm(" + ids + ", " + termId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
	}

	public Integer getPhenotypeIdByProjectAndType(int projectId, int typeId) throws MiddlewareQueryException {
		try {
			StringBuilder sql =
					new StringBuilder().append(" SELECT p.phenotype_id ").append(" FROM phenotype p ")
					.append(" INNER JOIN nd_experiment_phenotype eph ON eph.phenotype_id = p.phenotype_id ")
					.append(" INNER JOIN nd_experiment_project ep ON ep.nd_experiment_id = eph.nd_experiment_id ")
					.append("   AND ep.project_id = ").append(projectId).append(" WHERE p.observable_id = ").append(typeId);
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getPhenotypeIdByProjectAndType(" + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return null;
	}

	public Phenotype getPhenotypeByProjectExperimentAndType(int projectId, int experimentId, int typeId) throws MiddlewareQueryException {
		try {
			StringBuilder sql =
					new StringBuilder()
							.append(" SELECT p.phenotype_id, p.uniquename, p.name, p.observable_id, p.attr_id, p.value, p.cvalue_id, p.assay_id ")
							.append(" FROM phenotype p ")
							.append(" INNER JOIN nd_experiment_phenotype ep ON ep.phenotype_id = p.phenotype_id ")
							.append(" INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
							.append("   AND e.project_id = ").append(projectId).append(" WHERE p.observable_id in ( ").append(typeId)
							.append(") AND e.nd_experiment_id = ").append(experimentId);
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			List<Object[]> list = query.list();
			Phenotype phenotype = null;
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					phenotype =
							new Phenotype((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (Integer) row[4],
									(String) row[5], (Integer) row[6], (Integer) row[7]);
				}
			}

			return phenotype;
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getPhenotypeByProjectExperimentAndType(" + projectId + ", " + typeId
					+ ") in PhenotypeDao: " + e.getMessage(), e);
		}
		return null;
	}

	public Boolean containsAtLeast2CommonEntriesWithValues(int projectId, int locationId, int germplasmTermId) {

		String groupByGermplasm = "nd_exp_stock.stock_id";
		if (germplasmTermId == TermId.DESIG.getId()) {
			groupByGermplasm = "stock.name";
		} else if (germplasmTermId == TermId.GID.getId()) {
			groupByGermplasm = "stock.dbxref_id";
		} else if (germplasmTermId == TermId.ENTRY_NO.getId()) {
			groupByGermplasm = "stock.uniquename";
		}

		StringBuilder sql =
				new StringBuilder()
		.append(" SELECT phenotype.observable_id,count(phenotype.observable_id) ")
		.append(" FROM  ")
		.append(" nd_experiment_project a ")
		.append(" INNER JOIN nd_experiment nd_exp ON a.nd_experiment_id = nd_exp.nd_experiment_id ")
		.append(" INNER JOIN nd_experiment_stock nd_exp_stock ON nd_exp.nd_experiment_id = nd_exp_stock.nd_experiment_id ")
		.append(" INNER JOIN stock ON nd_exp_stock.stock_id = stock.stock_id ")
		.append(" LEFT JOIN nd_experiment_phenotype nd_exp_pheno ON nd_exp.nd_experiment_id = nd_exp_pheno.nd_experiment_id ")
		.append(" LEFT JOIN phenotype  ON nd_exp_pheno.phenotype_id = phenotype.phenotype_id ")
		.append(" where a.project_id = ").append(projectId).append(" and nd_exp.nd_geolocation_id = ").append(locationId)
		.append(" and ((phenotype.value <> '' and phenotype.value is not null) or ")
		.append(" (phenotype.cvalue_id <> '' and phenotype.cvalue_id is not null)) ")
		.append(" group by nd_exp.nd_geolocation_id, ").append(groupByGermplasm).append(" , phenotype.observable_id ")
		.append(" having count(phenotype.observable_id) >= 2 LIMIT 1 ");

		SQLQuery query = this.getSession().createSQLQuery(sql.toString());

		if (!query.list().isEmpty()) {
			return true;
		} else {
			return false;
		}

	}

	public List<Phenotype> getByProjectAndType(int projectId, int typeId) throws MiddlewareQueryException {
		List<Phenotype> phenotypes = new ArrayList<>();
		try {
			StringBuilder sql =
					new StringBuilder()
							.append(" SELECT p.phenotype_id, p.uniquename, p.name, p.observable_id, p.attr_id, p.value, p.cvalue_id, p.assay_id ")
							.append(" FROM phenotype p ")
							.append(" INNER JOIN nd_experiment_phenotype ep ON ep.phenotype_id = p.phenotype_id ")
							.append(" INNER JOIN nd_experiment_project e ON e.nd_experiment_id = ep.nd_experiment_id ")
							.append("   AND e.project_id = ").append(projectId).append(" WHERE p.observable_id = ").append(typeId);
			SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					phenotypes.add(new Phenotype((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3], (Integer) row[4],
							(String) row[5], (Integer) row[6], (Integer) row[7]));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByProjectAndType(" + projectId + ", " + typeId + ") in PhenotypeDao: " + e.getMessage(),
					e);
		}
		return phenotypes;
	}

	@Override
	public Phenotype save(final Phenotype phenotype) throws MiddlewareQueryException {
		try {
			this.savePhenotype(phenotype);
			return phenotype;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in save(" + phenotype + "): " + e.getMessage(), e);
		}
	}

	@Override
	public Phenotype saveOrUpdate(Phenotype entity) throws MiddlewareQueryException {
		try {
			if (entity.getPhenotypeId() == null) {
				this.savePhenotype(entity);
			}
			this.getSession().saveOrUpdate(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in saveOrUpdate(entity): " + e.getMessage(), e);
		}
	}

	@Override
	public Phenotype merge(Phenotype entity) throws MiddlewareQueryException {
		try {
			if (entity.getPhenotypeId() == null) {
				this.savePhenotype(entity);
			}
			this.getSession().merge(entity);
			return entity;
		} catch (HibernateException e) {
			throw new MiddlewareQueryException("Error in merge(entity): " + e.getMessage(), e);
		}
	}

	private void savePhenotype(final Phenotype phenotype) {
		final Session currentSession = this.getSession();
		currentSession.save(phenotype);
	}
}
