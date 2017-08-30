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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for {@link ProjectProperty}.
 * 
 */
public class ProjectPropertyDao extends GenericDAO<ProjectProperty, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectPropertyDao.class);

	@SuppressWarnings("unchecked")
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByPropertyNames(
			List<String> propertyNames) throws MiddlewareQueryException {
		Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects = new HashMap<String, Map<Integer, VariableType>>();

		// Store the names in the map in uppercase
		for (int i = 0, size = propertyNames.size(); i < size; i++) {
			propertyNames.set(i, propertyNames.get(i).toUpperCase());
		}

		try {

			if (!propertyNames.isEmpty()) {

				StringBuilder sqlString = new StringBuilder()
						.append("SELECT DISTINCT alias, variable_id, type_id ")
						.append("FROM projectprop   ")
						.append("    WHERE type_id in (")
						.append(Util.convertCollectionToCSV(VariableType.ids()))
						.append(")")
						.append("    AND alias IN (:propertyNames) ");
				SQLQuery query = this.getSession().createSQLQuery(
						sqlString.toString());
				query.setParameterList("propertyNames", propertyNames);

				List<Object[]> results = query.list();

				Map<Integer, VariableType> stdVarIdKeyTypeValueList = new HashMap<Integer, VariableType>();
				for (Object[] row : results) {
					String name = ((String) row[0]).trim().toUpperCase();
					String stdVarId = String.valueOf(row[1]);
					Integer variableTypeId = (Integer) row[2];

					if (standardVariableIdsWithTypeInProjects.containsKey(name)) {
						stdVarIdKeyTypeValueList = standardVariableIdsWithTypeInProjects
								.get(name);
					} else {
						stdVarIdKeyTypeValueList = new HashMap<Integer, VariableType>();
					}
					try {
						stdVarIdKeyTypeValueList.put(
								Integer.parseInt(stdVarId),
								VariableType.getById(variableTypeId));
						standardVariableIdsWithTypeInProjects.put(name,
								stdVarIdKeyTypeValueList);
					} catch (NumberFormatException e) {
						// Ignore
					}
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getStandardVariableIdsWithTypeByPropertyNames="
							+ propertyNames + " in ProjectPropertyDao: "
							+ e.getMessage(), e);
		}

		return standardVariableIdsWithTypeInProjects;
	}

	public ProjectProperty getByStandardVariableId(DmsProject project,
			int standardVariableId) throws MiddlewareQueryException {
		ProjectProperty projectProperty = null;
		try {
			Criteria criteria = this.getSession().createCriteria(
					this.getPersistentClass());
			criteria.add(Restrictions.eq("project", project));
			criteria.add(Restrictions.eq("variableId",
					standardVariableId));

			projectProperty = (ProjectProperty) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByStandardVariableId("
					+ project.getProjectId() + ", " + standardVariableId
					+ ") in ProjectPropertyDao: " + e.getMessage(), e);
		}
		return projectProperty;
	}

	public int getNextRank(int projectId) throws MiddlewareQueryException {
		try {
			String sql = "SELECT max(rank) FROM projectprop WHERE project_id = :projectId";
			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			return (Integer) query.uniqueResult() + 1;

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getNextRank(" + projectId
					+ ") in ProjectPropertyDao: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctStandardVariableValues(int stdVarId)
			throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();

		try {
			String sql = "SELECT DISTINCT value "
					+ " FROM projectprop WHERE type_id = :stdVarId ";
			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("stdVarId", stdVarId);

			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getDistinctStandardVariableValues(" + stdVarId + ") in ProjectPropertyDao: " + e.getMessage(), e);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ProjectProperty> getByTypeAndValue(int typeId, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("typeId", typeId));
			criteria.add(Restrictions.eq("value", value));
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByTypeAndValue(" + typeId + ", " + value + ") in ProjectPropertyDao: " + e.getMessage(),
					e);
		}
		return new ArrayList<ProjectProperty>();
	}

	@SuppressWarnings("unchecked")
	public String getValueByProjectIdAndTypeId(int projectId, int typeId) throws MiddlewareQueryException {
		try {
			String sql = "SELECT value FROM projectprop WHERE project_id = " + projectId + " AND type_id = " + typeId;
			Query query = this.getSession().createSQLQuery(sql);
			List<String> results = query.list();
			if (results != null && !results.isEmpty()) {
				return results.get(0);
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getByProjectIdAndTypeId(" + projectId + ", " + typeId + ") in ProjectPropertyDao: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getVariablesOfSiblingDatasets(int datasetId) throws MiddlewareQueryException {
		List<Integer> ids = new ArrayList<Integer>();
		try {
			String sql =
					"SELECT dprop.value " + " FROM project_relationship mpr "
							+ " INNER JOIN project_relationship pr ON pr.object_project_id = mpr.object_project_id "
							+ "   AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId() + " AND pr.subject_project_id <> " + datasetId
							+ " INNER JOIN projectprop dprop ON dprop.project_id = pr.subject_project_id " + " AND dprop.type_id = "
							+ TermId.STANDARD_VARIABLE.getId() + " WHERE mpr.subject_project_id = " + datasetId + " AND mpr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId();
			Query query = this.getSession().createSQLQuery(sql);
			List<String> results = query.list();
			if (results != null && !results.isEmpty()) {
				for (String result : results) {
					if (NumberUtils.isNumber(result)) {
						ids.add(Integer.valueOf(result));
					}
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getVariablesOfSiblingDatasets(" + datasetId + ") in ProjectPropertyDao: " + e.getMessage(),
					e);
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getDatasetVariableIdsForGivenStoredInIds(Integer projectId, List<Integer> storedInIds,
			List<Integer> varIdsToExclude) {
		List<Integer> variableIds = new ArrayList<Integer>();
		String mainSql = " SELECT variable_id " + " FROM projectprop p " + " WHERE project_id = :projectId ";
		String existsClause = " AND pp.type_id in (:storedInIds) ORDER BY rank ";
		boolean doExcludeIds = varIdsToExclude != null && !varIdsToExclude.isEmpty();

		StringBuilder sb = new StringBuilder(mainSql);
		if (doExcludeIds) {
			sb.append("AND variable_id NOT IN (:excludeIds) ");
		}
		sb.append(existsClause);

		Query query = this.getSession().createSQLQuery(sb.toString());
		query.setParameter("projectId", projectId);
		if (doExcludeIds) {
			query.setParameterList("excludeIds", varIdsToExclude);
		}
		query.setParameterList("storedInIds", storedInIds);
		List<String> results = query.list();
		for (String value : results) {
			variableIds.add(Integer.parseInt(value));
		}

		return variableIds;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<Integer>> getProjectPropertyIDsPerVariableId(Integer projectId) {
		Map<Integer, List<Integer>> projectPropertyIDsMap = new LinkedHashMap<Integer, List<Integer>>();
		String sql =
				"SELECT rank, projectprop_id, value, type_id " + "FROM projectprop " + "WHERE project_id = :projectId " + "ORDER BY rank ";
		Query query = this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		List<Object[]> results = query.list();

		List<Integer> projectPropIds = new ArrayList<Integer>();
		Integer currentRank = 1;
		Integer currentVariableId = 1;
		for (Object[] row : results) {
			Integer rank = (Integer) row[0];
			if (rank.compareTo(currentRank) > 0) {
				projectPropertyIDsMap.put(currentVariableId, projectPropIds);
				projectPropIds = new ArrayList<>();
				currentRank = rank;
			}

			String value = (String) row[2];
			Integer typeId = (Integer) row[3];
			if (typeId == TermId.STANDARD_VARIABLE.getId()) {
				currentVariableId = Integer.parseInt(value);
			}
			projectPropIds.add((Integer) row[1]);
		}
		if (!projectPropIds.isEmpty()) {
			projectPropertyIDsMap.put(currentVariableId, projectPropIds);
		}

		return projectPropertyIDsMap;
	}

	public void updateRank(List<Integer> projectPropIds, int rank) {
		String sql = " UPDATE projectprop SET rank = " + rank + " WHERE projectprop_id IN (:projectPropIds)";

		Query query = this.getSession().createSQLQuery(sql);
		query.setParameterList("projectPropIds", projectPropIds);
		query.executeUpdate();
	}

	public Map<String, String> getProjectPropsAndValuesByStudy(final Integer studyId) throws MiddlewareQueryException {
		Preconditions.checkNotNull(studyId);
		Map<String, String> geoProperties = new HashMap<>();
		String sql = " SELECT  "
			+ "     cvterm.definition AS name, pp.value AS value "
			+ " FROM "
			+ "     projectprop pp "
			+ "         INNER JOIN "
			+ "     cvterm cvterm ON cvterm.cvterm_id = pp.variable_id "
			+ " WHERE "
			+ "     pp.project_id = :studyId "
			+ "         AND pp.variable_id NOT IN ("
			+ TermId.START_DATE.getId() + " , "
			+ TermId.END_DATE.getId() + ", "
			+ TermId.SEASON_VAR.getId() + ", "
			+ TermId.STUDY_STATUS.getId() + ", " + TermId.LOCATION_ID.getId() + ", "
			+ TermId.STUDY_TYPE.getId() + ") "
			+ "         AND pp.variable_id NOT IN (SELECT  "
			+ "             variable.cvterm_id "
			+ "         FROM "
			+ "             cvterm scale "
			+ "                 INNER JOIN "
			+ "             cvterm_relationship r ON (r.object_id = scale.cvterm_id) "
			+ "                 INNER JOIN "
			+ "             cvterm variable ON (r.subject_id = variable.cvterm_id) "
			+ "         WHERE "
			+ "             object_id = 1901) ";

		try {
			Query query =
					this.getSession().createSQLQuery(sql).addScalar("name").addScalar("value").setParameter("studyId", studyId);
			List<Object> results = query.list();
			for (Object obj : results) {
				Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], (String) row[1]);
			}
			return geoProperties;
		} catch (MiddlewareQueryException e) {
			final String message = "Error with getProjectPropsAndValuesByStudy() query from studyId: " + studyId;
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
