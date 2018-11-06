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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByAlias(final List<String> variableNames)
			 {

		final List<String> propertyNamesInUpperCase = Lists.transform(variableNames, new Function<String, String>() {

			public String apply(String s) {
				return s.toUpperCase();
			}
		});

		try {
			if (!propertyNamesInUpperCase.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass(), "property")
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
				List<Object[]> results = criteria.list();
				return convertToVariablestandardVariableIdsWithTypeMap(results);
			}
		} catch (HibernateException e) {
			final String message =
					"Error in getStandardVariableIdsWithTypeByPropertyNames=" + variableNames + " in ProjectPropertyDao: " + e.getMessage();
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return new HashMap<>();
	}

	protected Map<String, Map<Integer, VariableType>> convertToVariablestandardVariableIdsWithTypeMap(List<Object[]> queryResult) {

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

	public ProjectProperty getByStandardVariableId(DmsProject project, int standardVariableId) {
		ProjectProperty projectProperty;
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project", project));
			criteria.add(Restrictions.eq("variableId", standardVariableId));

			projectProperty = (ProjectProperty) criteria.uniqueResult();

		} catch (HibernateException e) {
			final String message = "Error in getByStandardVariableId(" + project.getProjectId() + ", " + standardVariableId + ")";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return projectProperty;
	}

	public int getNextRank(int projectId) {
		try {
			String sql = "SELECT max(rank) FROM projectprop WHERE project_id = :projectId";
			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			return (Integer) query.uniqueResult() + 1;

		} catch (HibernateException e) {
			final String message = "Error in getNextRank(" + projectId + ")";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProjectProperty> getByTypeAndValue(int typeId, String value) {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq(TYPE_ID, typeId));
			criteria.add(Restrictions.eq("value", value));
			return criteria.list();

		} catch (HibernateException e) {
			final String message = "Error in getByTypeAndValue(" + typeId + ", " + value + ")";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getVariablesOfSiblingDatasets(int datasetId) {
		List<Integer> ids;
		try {
			String sql = "SELECT dprop.variable_id " + " FROM project_relationship mpr "
					+ " INNER JOIN project_relationship pr ON pr.object_project_id = mpr.object_project_id " + "   AND pr.type_id = "
					+ TermId.BELONGS_TO_STUDY.getId() + " AND pr.subject_project_id <> " + datasetId
					+ " INNER JOIN projectprop dprop ON dprop.project_id = pr.subject_project_id " + " WHERE mpr.subject_project_id = "
					+ datasetId + " AND mpr.type_id = " + TermId.BELONGS_TO_STUDY.getId();
			Query query = this.getSession().createSQLQuery(sql);
			ids = query.list();

		} catch (HibernateException e) {
			final String message = "Error in getVariablesOfSiblingDatasets(" + datasetId + ")";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getDatasetVariableIdsForGivenStoredInIds(Integer projectId, List<Integer> storedInIds,
			List<Integer> varIdsToExclude) {
		List<Integer> variableIds = new ArrayList<>();
		String mainSql = " SELECT variable_id " + " FROM projectprop pp " + " WHERE project_id = :projectId ";
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
	public List<ProjectProperty> getByProjectId(final Integer projectId) {
		List<ProjectProperty> list;
		DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(projectId);
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("project", dmsProject));

			list = criteria.list();

		} catch (HibernateException e) {
			final String message = "Error in getByProjectId(" + dmsProject.getProjectId() + ") in ProjectPropertyDao";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getProjectPropsAndValuesByStudy(final Integer studyId) {
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
			+ TermId.SEASON_VAR.getId() + ", "
			+ TermId.LOCATION_ID.getId() + ") "
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
	
	public void deleteProjectVariables(final Integer projectId, final List<Integer> variableIds) {
		final String sql = "DELETE FROM projectprop WHERE project_id = :projectId and variable_id IN (:variableIds)";
		final Query query =
				this.getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		query.setParameterList("variableIds", variableIds);
		query.executeUpdate();
	}
}
