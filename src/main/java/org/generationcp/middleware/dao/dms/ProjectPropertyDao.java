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

	protected Map<String, Map<Integer, VariableType>> convertToVariablestandardVariableIdsWithTypeMap(final List<Object[]> queryResult) {

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
	public List<Integer> getVariablesOfSiblingDatasets(final int datasetId) {
		final List<Integer> ids;
		try {
			final String sql = "SELECT dprop.variable_id "
					+ " FROM project ds "
					+ " INNER JOIN project sib ON sib.study_id = ds.study_id AND sib.parent_project_id = ds.parent_project_id AND sib.project_id <> ds.project_id "
					+ " INNER JOIN projectprop dprop ON dprop.project_id = sib.project_id " +
					" WHERE ds.project_id = :datasetId";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("datasetId", datasetId);
			ids = query.list();

		} catch (final HibernateException e) {
			final String message = "Error in getVariablesOfSiblingDatasets(" + datasetId + ")";
			ProjectPropertyDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getDatasetVariableIdsForGivenStoredInIds(final Integer projectId, final List<Integer> storedInIds,
			final List<Integer> varIdsToExclude) {
		final List<Integer> variableIds = new ArrayList<>();
		final String mainSql = " SELECT variable_id " + " FROM projectprop pp " + " WHERE project_id = :projectId ";
		final String existsClause = " AND pp.type_id in (:storedInIds) ORDER BY rank ";
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
		query.setParameterList("storedInIds", storedInIds);
		final List<String> results = query.list();
		for (final String value : results) {
			variableIds.add(Integer.parseInt(value));
		}

		return variableIds;
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

	@SuppressWarnings("unchecked")
	public Map<String, String> getProjectPropsAndValuesByStudy(final Integer studyId) {
		Preconditions.checkNotNull(studyId);
		final Map<String, String> geoProperties = new HashMap<>();
		final String sql = " SELECT  "
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
			final Query query =
					this.getSession().createSQLQuery(sql).addScalar("name").addScalar("value").setParameter("studyId", studyId);
			final List<Object> results = query.list();
			for (final Object obj : results) {
				final Object[] row = (Object[]) obj;
				geoProperties.put((String) row[0], (String) row[1]);
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

	public List<String> getGermplasmDescriptors(final int studyIdentifier) {
		final List<String> list = this.executeQueryGermplasmDescriptors(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> executeQueryGermplasmDescriptors(final int studyIdentifier) {
		return this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Lists.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId()));

	}

	public List<String> getDesignFactors(final int studyIdentifier) {
		final List<String> list = this.executeDesignFactorsQuery(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> executeDesignFactorsQuery(final int studyIdentifier) {
		return this.findPlotDatasetVariablesByTypesForStudy(studyIdentifier,
			Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.getId(), VariableType.TREATMENT_FACTOR.getId()));
	}

	private List<String> findPlotDatasetVariablesByTypesForStudy(final int studyIdentifier, final List<Integer> variableTypeIds) {
		final String variablesQuery = " SELECT name" +
			" FROM  projectprop pp "
			+ "INNER JOIN project ds ON ds.project_id = pp.project_ID AND ds.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
			+ "INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.variable_id "
			+ " WHERE pp.type_id IN (:variableTypeIds)"
			+ " AND ds.study_id = :studyId";
		;
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(variablesQuery);
		sqlQuery.addScalar("name");
		sqlQuery.setParameter("studyId", studyIdentifier);
		sqlQuery.setParameterList("variableTypeIds", variableTypeIds);
		return sqlQuery.list();
	}


}
