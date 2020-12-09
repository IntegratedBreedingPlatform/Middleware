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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO class for {@link ProjectProperty}.
 *
 */
public class ProjectPropertyDao extends GenericDAO<ProjectProperty, Integer> {

	private static final String TYPE_ID = "typeId";
	private static final Logger LOG = LoggerFactory.getLogger(ProjectPropertyDao.class);

	/**
	 *
	 * @param variableNames
	 * @return a map with Property names (In UPPERCASE) as keys and a map(variableId, variableType) as Value
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByAlias(final List<String> variableNames, final String programUUID)
			 {

		final List<String> propertyNamesInUpperCase = Lists.transform(variableNames, new Function<String, String>() {

			public String apply(final String s) {
				return s.toUpperCase();
			}
		});

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
			ProjectPropertyDao.LOG.error(message, e);
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
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return projectProperty;
	}

	public int getNextRank(final int projectId) {
		try {
			final String sql = "SELECT max(rank) FROM projectprop WHERE project_id = :projectId";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			return (Integer) query.uniqueResult() + 1;

		} catch (final HibernateException e) {
			final String message = "Error in getNextRank(" + projectId + ")";
			ProjectPropertyDao.LOG.error(message, e);
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
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return list;
	}

	public Map<String, String> getProjectPropsAndValuesByStudy(final Integer studyId, final List<Integer> excludedVariableIds) {
		Preconditions.checkNotNull(studyId);
		final Map<String, String> geoProperties = new HashMap<>();
		final List<Integer> excludedIds = new ArrayList<>(excludedVariableIds);
		excludedIds.add(TermId.SEASON_VAR.getId());
		excludedIds.add(TermId.LOCATION_ID.getId());
		final String sql =
			"	SELECT  "
			+ "     cvterm.definition AS name,"
			+ "		(CASE WHEN scale_type.object_id = " + TermId.CATEGORICAL_VARIABLE.getId()
			+ "		THEN (SELECT incvterm.definition FROM cvterm incvterm WHERE incvterm.cvterm_id = pp.value) "
			+ "		ELSE pp.value "
			+ "		END) value "
			+ " FROM projectprop pp "
			+ " INNER JOIN cvterm cvterm ON cvterm.cvterm_id = pp.variable_id "
			+ " INNER JOIN cvterm_relationship scale ON scale.subject_id = pp.variable_id AND scale.type_id = " + TermId.HAS_SCALE.getId()
			+ " INNER JOIN cvterm_relationship scale_type ON scale_type.subject_id = scale.object_id AND scale_type.type_id = " + TermId.HAS_TYPE.getId()
			+ " WHERE "
			+ " pp.project_id = :studyId "
			+ " AND pp.variable_id NOT IN (:excludedIds) "
				//Exclude Variables with scale PersonId (1901)
			+ " AND scale.object_id != " + TermId.PERSON_ID.getId();

		try {
			final Query query =
				this.getSession().createSQLQuery(sql).addScalar("name").addScalar("value").setParameter("studyId", studyId)
				.setParameterList("excludedIds", excludedIds);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], row[1] == null ? "" : (String) row[1]);
			}
			return geoProperties;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getProjectPropsAndValuesByStudy() query from studyId: " + studyId;
			ProjectPropertyDao.LOG.error(message, e);
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

	public void deleteDatasetVariablesByVariableTypes(final Integer projectId, final List<Integer> variableTypeIds) {
		final String sql = "DELETE FROM projectprop WHERE project_id = :projectId AND type_id IN (:variableTypeIds)";
		final Query query =
			this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		query.setParameterList("variableTypeIds", variableTypeIds);
		query.executeUpdate();
	}

	public List<String> getGermplasmDescriptors(final int studyIdentifier) {
		final List<String> list = this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Lists.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId()));
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	public List<String> getDesignFactors(final int studyIdentifier) {
		final List<String> list = this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.getId(), VariableType.TREATMENT_FACTOR.getId()));
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	private List<String> findPlotDatasetVariablesByTypesForStudy(final int studyIdentifier, final List<Integer> variableTypeIds) {
		final String nameQuery = variableTypeIds.contains(VariableType.GERMPLASM_DESCRIPTOR.getId()) ? " SELECT CASE WHEN pp.alias IS NOT NULL AND pp.alias != '' THEN pp.alias ELSE cvt.name END as name" : " SELECT cvt.name ";
		final String variablesQuery = nameQuery +
			" FROM  projectprop pp "
			+ " INNER JOIN project ds ON ds.project_id = pp.project_ID AND ds.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
			+ " INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.variable_id "
			+ " WHERE pp.type_id IN (:variableTypeIds)"
			+ " AND ds.study_id = :studyId";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(variablesQuery);
		sqlQuery.addScalar("name");
		sqlQuery.setParameter("studyId", studyIdentifier);
		sqlQuery.setParameterList("variableTypeIds", variableTypeIds);
		return sqlQuery.list();
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
			ProjectPropertyDao.LOG.error(message, e);
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
			final String message = "Error in getByProjectIdAndVariableIds(" + dmsProject.getProjectId() + ", " + standardVariableIds + ") in ProjectPropertyDao";
			ProjectPropertyDao.LOG.error(message, e);
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
			+ " WHERE pp.type_id IN (:variablesTypes) AND p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
			+ " \n";
		final List<MeasurementVariableDto> measurementVariables =
			this.getVariablesByStudy(studyIdentifier, queryString, variableTypes);
		if (!measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}


}
