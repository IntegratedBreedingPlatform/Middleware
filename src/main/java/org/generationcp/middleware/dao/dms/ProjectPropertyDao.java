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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link ProjectProperty}.
 */
public class ProjectPropertyDao extends GenericDAO<ProjectProperty, Integer> {

	private static final String TYPE_ID = "typeId";

	/**
	 * @param variableNames
	 * @return a map with Property names (In UPPERCASE) as keys and a map(variableId, variableType) as Value
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByAlias(final List<String> variableNames,
		final String programUUID) {

		final List<String> propertyNamesInUpperCase = Lists.transform(variableNames, String::toUpperCase);

		try {
			if (!propertyNamesInUpperCase.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass(), "property")
					.setProjection(Projections.distinct(Projections.projectionList()
						.add(Projections.property("alias"))
						.add(Projections.property("variableId"))
						.add(Projections.property(TYPE_ID))));

				/* Exclude variables used as condition such that variable type in projectprop "Study Detail" as "Study Detail"
				 * is not one of the standard categorizations in Ontology Mapping so it will lead to variable being unmapped
				 */
				final List<Integer> variableTypes = VariableType.ids();
				variableTypes.remove(VariableType.STUDY_DETAIL.getId());
				criteria.add(Restrictions.in(TYPE_ID, variableTypes));

				criteria.add(Restrictions.in("alias", variableNames));
				criteria.createAlias("property.variable", "variable").add(Restrictions.eq("variable.isObsolete", 0));
				criteria.createAlias("property.project", "project").add(Restrictions.eq("project.programUUID", programUUID));
				final List<Object[]> results = criteria.list();
				return this.convertToVariablestandardVariableIdsWithTypeMap(results);
			}
		} catch (final HibernateException e) {
			final String message =
				"Error in getStandardVariableIdsWithTypeByPropertyNames=" + variableNames + " in ProjectPropertyDao: " + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}

		return new HashMap<>();
	}

	Map<String, Map<Integer, VariableType>> convertToVariablestandardVariableIdsWithTypeMap(final List<Object[]> queryResult) {

		final Map<String, Map<Integer, VariableType>> standardVariableIdsWithTypeInProjects = new HashMap<>();

		for (final Object[] row : queryResult) {
			final String alias = (String) row[0];
			final Integer variableId = (Integer) row[1];
			final Integer typeId = (Integer) row[2];
			Map<Integer, VariableType> stdVarIdKeyTypeValueList = new HashMap<>();

			if (standardVariableIdsWithTypeInProjects.containsKey(alias.toUpperCase())) {
				stdVarIdKeyTypeValueList = standardVariableIdsWithTypeInProjects.get(alias.toUpperCase());
			}

			stdVarIdKeyTypeValueList.put(variableId, VariableType.getById(typeId));
			standardVariableIdsWithTypeInProjects.put(alias.toUpperCase(), stdVarIdKeyTypeValueList);
		}

		return standardVariableIdsWithTypeInProjects;
	}

	public ProjectProperty getByStandardVariableId(final DmsProject project, final int standardVariableId) {
		final ProjectProperty projectProperty;
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project", project));
			criteria.add(Restrictions.eq("variableId", standardVariableId));

			projectProperty = (ProjectProperty) criteria.uniqueResult();

		} catch (final HibernateException e) {
			final String message = "Error in getByStandardVariableId(" + project.getProjectId() + ", " + standardVariableId + ")";
			throw new MiddlewareQueryException(message, e);
		}
		return projectProperty;
	}

	public int getNextRank(final int projectId) {
		try {
			final String sql = "SELECT max(rank) FROM projectprop WHERE project_id = :projectId";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			final Integer maxRank = (Integer) query.uniqueResult();
			return (maxRank != null ? maxRank : 0) + 1;

		} catch (final HibernateException e) {
			final String message = "Error in getNextRank(" + projectId + ")";
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProjectProperty> getByTypeAndValue(final int typeId, final String value) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq(TYPE_ID, typeId));
			criteria.add(Restrictions.eq("value", value));
			return criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getByTypeAndValue(" + typeId + ", " + value + ")";
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, List<ProjectProperty>> getPropsForProjectIds(final List<Integer> projectIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("project.projectId", projectIds));
			final List<ProjectProperty> results = criteria.list();

			final Map<Integer, List<ProjectProperty>> projectPropMap = new HashMap<>();
			for (final ProjectProperty prop : results) {
				final Integer projectId = prop.getProject().getProjectId();
				projectPropMap.putIfAbsent(projectId, new ArrayList<>());
				projectPropMap.get(projectId).add(prop);
			}
			return projectPropMap;
		} catch (final HibernateException e) {
			final String message = "Error in getPropsForProjectIds(" + projectIds + ")";
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, List<Integer>> getEnvironmentDatasetVariables(final List<Integer> studyIds) {
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap = new HashMap<>();
		final StringBuilder queryString =
			new StringBuilder("SELECT p_main.project_id AS projectId, pp.variable_id AS variableId FROM projectprop pp ");
		queryString.append("INNER JOIN project p_env ON pp.project_id = p_env.project_id ");
		queryString.append("INNER JOIN project p_main ON p_main.project_id = p_env.study_id ");
		queryString.append("WHERE p_main.project_id IN (:studyIds) AND p_env.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId());

		final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
		query.setParameterList("studyIds", studyIds);
		query.addScalar("projectId", IntegerType.INSTANCE);
		query.addScalar("variableId", IntegerType.INSTANCE);
		final List<Object[]> results = query.list();
		for (final Object[] row : results) {
			final Integer studyId = (Integer) row[0];
			studyIdEnvironmentVariablesMap.putIfAbsent(studyId, new ArrayList<>());
			studyIdEnvironmentVariablesMap.get(studyId).add((Integer) row[1]);
		}
		return studyIdEnvironmentVariablesMap;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getDatasetVariableIdsForVariableTypeIds(final Integer projectId, final List<Integer> variableTypeIds,
		final List<Integer> varIdsToExclude) {
		final String mainSql = " SELECT variable_id " + " FROM projectprop pp " + " WHERE project_id = :projectId ";
		final String existsClause = " AND pp.type_id IN (:variableTypeIds) ORDER BY rank ";
		final boolean doExcludeIds = varIdsToExclude != null && !varIdsToExclude.isEmpty();

		final StringBuilder sb = new StringBuilder(mainSql);
		if (doExcludeIds) {
			sb.append("AND variable_id NOT IN (:excludeIds) ");
		}
		sb.append(existsClause);

		final Query query = this.getSession().createSQLQuery(sb.toString());
		query.setParameter("projectId", projectId);
		if (doExcludeIds) {
			query.setParameterList("excludeIds", varIdsToExclude);
		}
		query.setParameterList("variableTypeIds", variableTypeIds);
		return query.list();

	}

	@SuppressWarnings("unchecked")
	public List<ProjectProperty> getByProjectId(final Integer projectId) {
		final List<ProjectProperty> list;
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(projectId);
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project", dmsProject));

			list = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getByProjectId(" + dmsProject.getProjectId() + ") in ProjectPropertyDao";
			throw new MiddlewareQueryException(message, e);
		}
		return list;
	}

	public Map<Integer, Map<String, String>> getProjectPropsAndValuesByStudyIds(final List<Integer> studyIds) {
		Preconditions.checkNotNull(studyIds);
		final List<Integer> excludedIds = new ArrayList<>();
		excludedIds.add(TermId.SEASON_VAR.getId());
		excludedIds.add(TermId.LOCATION_ID.getId());
		final String sql =
			"	SELECT  "
				+ "     cvterm.definition AS name,"
				+ "		(CASE WHEN scale_type.object_id = " + TermId.CATEGORICAL_VARIABLE.getId()
				+ "		THEN (SELECT incvterm.definition FROM cvterm incvterm WHERE incvterm.cvterm_id = pp.value) "
				+ "		ELSE pp.value "
				+ "		END) value, "
				+ "		pp.project_id AS projectId "
				+ " FROM projectprop pp "
				+ " INNER JOIN cvterm cvterm ON cvterm.cvterm_id = pp.variable_id "
				+ " INNER JOIN cvterm_relationship scale ON scale.subject_id = pp.variable_id AND scale.type_id = " + TermId.HAS_SCALE
				.getId()
				+ " INNER JOIN cvterm_relationship scale_type ON scale_type.subject_id = scale.object_id AND scale_type.type_id = "
				+ TermId.HAS_TYPE.getId()
				+ " WHERE "
				+ " pp.project_id IN (:studyIds) "
				+ " AND pp.variable_id NOT IN (:excludedIds) "
				//Exclude Variables with scale PersonId (1901)
				+ " AND scale.object_id != " + TermId.PERSON_ID.getId();

		try {
			final Query query =
				this.getSession().createSQLQuery(sql).addScalar("name").addScalar("value").addScalar("projectId")
					.setParameterList("studyIds", studyIds).setParameterList("excludedIds", excludedIds);
			final List<Object> results = query.list();
			final Map<Integer, Map<String, String>> projectPropMap = new HashMap<>();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				final Integer studyId = (Integer) row[2];
				projectPropMap.putIfAbsent(studyId, new HashMap<>());
				projectPropMap.get(studyId).put((String) row[0], row[1] == null ? "" : (String) row[1]);
			}
			return projectPropMap;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getProjectPropsAndValuesByStudyIds() query from studyIds: " + studyIds;
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteProjectVariables(final Integer projectId, final List<Integer> variableIds) {
		final String sql = "DELETE FROM projectprop WHERE project_id = :projectId and variable_id IN (:variableIds)";
		final Query query =
			this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		query.setParameterList("variableIds", variableIds);
		query.executeUpdate();
	}

	public void deleteProjectNameTypes(final Integer projectId, final List<Integer> nameTypes) {
		final String sql = "DELETE FROM projectprop WHERE project_id = :projectId and name_fldno IN (:nameTypes)";
		final Query query =
			this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		query.setParameterList("nameTypes", nameTypes);
		query.executeUpdate();
	}

	public void deleteNameTypeFromStudies(final Integer nameType) {
		final String sql = "DELETE FROM projectprop WHERE name_fldno = :nameType";
		final Query query =
			this.getSession().createSQLQuery(sql);
		query.setParameter("nameType", nameType);
		query.executeUpdate();
	}

	public void deleteDatasetVariablesByVariableTypes(final Integer projectId, final List<Integer> variableTypeIds) {
		final String sql = "DELETE FROM projectprop WHERE project_id = :projectId AND type_id IN (:variableTypeIds)";
		final Query query =
			this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		query.setParameterList("variableTypeIds", variableTypeIds);
		query.executeUpdate();
	}

	public Map<Integer, String> getGermplasmDescriptors(final int studyIdentifier) {
		return this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Lists.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId()));

	}

	public Map<Integer, String> getDesignFactors(final int studyIdentifier) {
		return this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.getId(), VariableType.TREATMENT_FACTOR.getId()));
	}

	private Map<Integer, String> findPlotDatasetVariablesByTypesForStudy(final int studyIdentifier, final List<Integer> variableTypeIds) {
		final String variablesQuery =
			" SELECT cvt.name, cvt.cvterm_id "
				+ " FROM  projectprop pp "
				+ " INNER JOIN project ds ON ds.project_id = pp.project_ID AND ds.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
				+ " INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.variable_id "
				+ " WHERE pp.type_id IN (:variableTypeIds)"
				+ " AND ds.study_id = :studyId";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(variablesQuery);
		sqlQuery.addScalar("name");
		sqlQuery.addScalar("cvterm_id");
		sqlQuery.setParameter("studyId", studyIdentifier);
		sqlQuery.setParameterList("variableTypeIds", variableTypeIds);
		final Map<Integer, String> map = Maps.newHashMap();
		final List<Object[]> list = sqlQuery.list();
		for (final Object[] row : list) {
			map.put((Integer) row[1], (String) row[0]);
		}
		return map;
	}

	public List<ProjectProperty> getByStudyAndStandardVariableIds(final int studyId, final List<Integer> standardVariableIds) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("project.study", "study");
			criteria.add(Restrictions.eq("study.projectId", studyId));
			criteria.add(Restrictions.in("variableId", standardVariableIds));
			return criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getByStudyAndStandardVariableIds(" + standardVariableIds + ")";
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Integer> getVariableIdsForDataset(final Integer datasetId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("project.projectId", datasetId));
		criteria.setProjection(Projections.property("variableId"));
		return criteria.list();
	}

	private List<MeasurementVariableDto> getVariablesByStudy(final int studyIdentifier, final String query,
		final Integer... variableTypes) {
		final SQLQuery variableSqlQuery = this.getSession().createSQLQuery(query);
		variableSqlQuery.addScalar("cvterm_id");
		variableSqlQuery.addScalar("name");
		variableSqlQuery.setParameter("studyId", studyIdentifier);
		variableSqlQuery.setParameterList("variablesTypes", variableTypes);
		final List<Object[]> measurementVariables = variableSqlQuery.list();
		final List<MeasurementVariableDto> variableList = new ArrayList<>();
		for (final Object[] rows : measurementVariables) {
			variableList.add(new MeasurementVariableDto((Integer) rows[0], (String) rows[1]));
		}
		return variableList;
	}

	public List<MeasurementVariableDto> getVariablesForDataset(final int datasetId, final Integer... variableTypes) {
		final String queryString = " SELECT \n"
			+ "   cvterm_id, \n"
			+ "   name \n"
			+ " FROM cvterm cvt \n"
			+ "   INNER JOIN projectprop pp ON (pp.variable_id = cvt.cvterm_id) \n"
			+ " WHERE pp.type_id IN (:variablesTypes) AND pp.project_id = :studyId ";
		final List<MeasurementVariableDto> measurementVariables =
			this.getVariablesByStudy(datasetId, queryString, variableTypes);
		if (!measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}

	public List<ProjectProperty> getByProjectIdAndVariableIds(final Integer projectId, final List<Integer> standardVariableIds) {
		final List<ProjectProperty> list;
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(projectId);
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project", dmsProject));
			criteria.add(Restrictions.in("variableId", standardVariableIds));

			list = criteria.list();

		} catch (final HibernateException e) {
			final String message = "Error in getByProjectIdAndVariableIds(" + dmsProject.getProjectId() + ", " + standardVariableIds
				+ ") in ProjectPropertyDao";
			throw new MiddlewareQueryException(message, e);
		}
		return list;
	}

	public List<MeasurementVariableDto> getVariables(final int studyIdentifier, final Integer... variableTypes) {
		final String queryString = " SELECT \n"
			+ "   cvterm_id, \n"
			+ "   cvt.name \n"
			+ " FROM cvterm cvt \n"
			+ "   INNER JOIN projectprop pp ON (pp.variable_id = cvt.cvterm_id) \n"
			+ "   INNER JOIN project p ON p.project_id = pp.project_id \n"
			+ " WHERE pp.type_id IN (:variablesTypes) AND p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA
			.getId()
			+ " \n";
		final List<MeasurementVariableDto> measurementVariables =
			this.getVariablesByStudy(studyIdentifier, queryString, variableTypes);
		if (!measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}

	public long countNameTypeInUse(final Integer nameTypeId) {
		try {
			final String sql = "SELECT count(1) FROM projectprop where name_fldno = :nameTypeId";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("nameTypeId", nameTypeId);
			return ((BigInteger) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String message = "Error with isNameTypeUsedInStudies(nameTypeId=" + nameTypeId + "): " + e.getMessage();
			throw new MiddlewareQueryException(message, e);
		}
	}
}
