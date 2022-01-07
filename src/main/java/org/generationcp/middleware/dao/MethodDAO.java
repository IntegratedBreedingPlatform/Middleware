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

package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.breedingmethod.BreedingMethodDTO;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodSearchRequest;
import org.generationcp.middleware.api.program.ProgramFavoriteDTO;
import org.generationcp.middleware.dao.breedingmethod.BreedingMethodSearchDAOQuery;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for {@link Method}.
 */
public class MethodDAO extends GenericDAO<Method, Integer> {

	private static final String METHOD_NAME = "mname";

	private static final Logger LOG = LoggerFactory.getLogger(MethodDAO.class);

	private static final String COUNT_BREEDING_METHODS_WITH_VARIABLE =
		" SELECT count(1) FROM methods where "
			+ "\t    prefix like CONCAT('%[ATTRSC.',:variableId,']%') "
			+ "\t or prefix like CONCAT('%[ATTRFP.',:variableId,']%') "
			+ "\t or prefix like CONCAT('%[ATTRMP.',:variableId,']%') "
			+ "\t or suffix like CONCAT('%[ATTRSC.',:variableId,']%') "
			+ "\t or suffix like CONCAT('%[ATTRFP.',:variableId,']%') "
			+ "\t or suffix like CONCAT('%[ATTRMP.',:variableId,']%') ";

	public MethodDAO(final Session session) {
		super(session);
	}

	@SuppressWarnings("unchecked")
	public List<Method> getMethodsByIds(final List<Integer> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}

		try {
			return this.getSession().createCriteria(Method.class).add(Restrictions.in("mid", ids)).addOrder(Order.asc(METHOD_NAME)).list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByIds", "ids", ids.toString(), e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethod() {
		try {
			final Query query = this.getSession().getNamedQuery(Method.GET_ALL);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getAllMethod", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethodOrderByMname() {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getAllMethodOrderByMname", "", null, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(final String type) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByType(final String type, final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.addOrder(Order.asc(METHOD_NAME));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByType", "type", type, e.getMessage(), "Method"), e);
		}
	}

	public long countByType(final String type) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mtype", type));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countMethodsByType", "type", type, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupIncludesGgroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			final Criterion group1 = Restrictions.eq("mgrp", group);
			final Criterion group2 = Restrictions.eq("mgrp", "G");
			final LogicalExpression orExp = Restrictions.or(group1, group2);
			criteria.add(orExp);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroup(final String group, final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.addOrder(Order.asc(METHOD_NAME));
			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndType(final String group, final String type) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			final Criterion group1 = Restrictions.eq("mgrp", group);
			final Criterion group2 = Restrictions.eq("mgrp", "G");
			final LogicalExpression orExp = Restrictions.or(group1, group2);
			final Criterion filterType = Restrictions.eq("mtype", type);
			final LogicalExpression andExp = Restrictions.and(orExp, filterType);

			criteria.add(andExp);
			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByGroupAndTypeAndName(final String group, final String type, final String name) {
		try {

			final Criteria criteria = this.getSession().createCriteria(Method.class);

			if (type != null && !type.isEmpty()) {
				criteria.add(Restrictions.eq("mtype", type));
			}

			if (name != null && !name.isEmpty()) {
				criteria.add(Restrictions.like(METHOD_NAME, "%" + name.trim() + "%"));
			}

			if (group != null && !group.isEmpty()) {
				final Criterion group1 = Restrictions.eq("mgrp", group);
				final Criterion group2 = Restrictions.eq("mgrp", "G");
				final LogicalExpression orExp = Restrictions.or(group1, group2);

				criteria.add(orExp);
			}

			criteria.addOrder(Order.asc(METHOD_NAME));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByGroupAndType", "group|type", group + "|" + type, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getAllMethodsNotGenerative() {
		try {
			final List<Integer> validMethodClasses = new ArrayList<Integer>();
			validMethodClasses.addAll(Method.BULKED_CLASSES);
			validMethodClasses.addAll(Method.NON_BULKED_CLASSES);

			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.ne("mtype", "GEN"));
			criteria.add(Restrictions.in("geneq", validMethodClasses));
			criteria.addOrder(Order.asc(METHOD_NAME));

			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getAllMethodsNotGenerative", "", null, e.getMessage(), "Method"), e);
		}
	}

	public long countByGroup(final String group) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mgrp", group));
			criteria.setProjection(Projections.rowCount());
			return ((Long) criteria.uniqueResult()).longValue(); // count
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("countMethodsByGroup", "group", group, e.getMessage(), "Method"),
				e);
		}
	}

	public Method getByCode(final String code) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.eq("mcode", code));
			return (Method) criteria.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getMethodsByCode", "code", code, e.getMessage(), "Method"), e);
		}
	}

	public List<Method> getByCode(final List<String> codes) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Method.class);
			criteria.add(Restrictions.in("mcode", codes));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getMethodsByCode", "codes", codes.toString(), e.getMessage(), "Method"),
				e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Method> getByName(final String name) {
		try {
			final StringBuilder queryString = this.createSelectMethodString();
			queryString.append("FROM methods m WHERE m.mname = :mname");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter(METHOD_NAME, name);
			this.addMethodScalar(query);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(this.getLogExceptionMessage("getByName", "name", name, e.getMessage(), "Method"), e);
		}
	}

	public List<Method> getMethodsFromExperiments(final int studyDbid, final Integer variableID, final List<String> trialInstances) {

		try {
			final StringBuilder sql = this.createSelectMethodString();
			sql.append(" FROM phenotype p ")
				.append(" INNER JOIN nd_experiment e ON e.nd_experiment_id = p.nd_experiment_id ")
				.append(" INNER JOIN methods m ON m.mcode = p.value ")
				.append(" INNER JOIN nd_geolocation location ON e.nd_geolocation_id = location.nd_geolocation_id ")
				.append(" WHERE e.project_id = :studyDbid AND p.observable_id = :variableId AND location.description IN (:trialInstances) ");
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("studyDbid", studyDbid);
			query.setParameter("variableId", variableID);
			query.setParameterList("trialInstances", trialInstances);
			this.addMethodScalar(query);

			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Errow with getMethodsFromExperiments: " + e.getMessage(), e);
		}

	}

	void addMethodScalar(final SQLQuery query) {
		query.addScalar("mid")
			.addScalar("mtype")
			.addScalar("mgrp")
			.addScalar("mcode")
			.addScalar("mname")
			.addScalar("mdesc")
			.addScalar("mref")
			.addScalar("mprgn")
			.addScalar("mfprg")
			.addScalar("mattr")
			.addScalar("geneq")
			.addScalar("muid")
			.addScalar("lmid")
			.addScalar("mdate");
		query.setResultTransformer(Transformers.aliasToBean(Method.class));
	}

	StringBuilder createSelectMethodString() {
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT m.mid AS mid, m.mtype AS mtype, m.mgrp AS mgrp, m.mcode AS mcode, m.mname AS mname, m.mdesc AS mdesc, ")
			.append(" m.mref AS mref, m.mprgn AS mprgn, m.mfprg AS mfprg, m.mattr AS mattr, m.geneq AS geneq, m.muid AS muid, m.lmid AS lmid, ")
			.append(" m.mdate AS mdate ");
		return sql;
	}

	public List<Method> getFavoriteMethodsByMethodType(final String methodType, final String programUUID) {
		try {
			final Query query = this.getSession().getNamedQuery(Method.GET_FAVORITE_METHODS_BY_TYPE);
			query.setParameter("mType", methodType);
			query.setParameter("programUUID", programUUID);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getFavoriteMethodsByMethodType", "", null, e.getMessage(), "Method"), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getMethodCodeByMethodIds(final Set<Integer> methodIds) {
		final List<String> methodsCodes = new ArrayList<String>();
		final StringBuilder queryString = new StringBuilder();
		queryString.append("SELECT mcode FROM methods WHERE mid  IN (:mids)");
		final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
		query.setParameterList("mids", methodIds);

		methodsCodes.addAll(query.list());
		return methodsCodes;
	}

	public List<BreedingMethodDTO> searchBreedingMethods(final BreedingMethodSearchRequest methodSearchRequest,
			final Pageable pageable, final String programUUID) {
		final SQLQueryBuilder queryBuilder = BreedingMethodSearchDAOQuery.getSelectQuery(methodSearchRequest, pageable,
				programUUID);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);
		queryBuilder.addScalarsToQuery(query);

		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		final List<Map<String, Object>> results = query.list();

		return results.stream().map(row -> {
			final BreedingMethodDTO breedingMethodDTO = new BreedingMethodDTO();
			breedingMethodDTO.setMid((Integer) row.get(BreedingMethodSearchDAOQuery.ID_ALIAS));
			breedingMethodDTO.setCode((String) row.get(BreedingMethodSearchDAOQuery.ABBREVIATION_ALIAS));
			breedingMethodDTO.setName((String) row.get(BreedingMethodSearchDAOQuery.NAME_ALIAS));
			breedingMethodDTO.setDescription((String) row.get(BreedingMethodSearchDAOQuery.DESCRIPTION_ALIAS));
			breedingMethodDTO.setType((String) row.get(BreedingMethodSearchDAOQuery.TYPE_ALIAS));
			breedingMethodDTO.setGroup((String) row.get(BreedingMethodSearchDAOQuery.GROUP_ALIAS));
			breedingMethodDTO.setMethodClass((Integer) row.get(BreedingMethodSearchDAOQuery.CLASS_ID_ALIAS));
			breedingMethodDTO.setMethodClassName((String) row.get(BreedingMethodSearchDAOQuery.CLASS_NAME_ALIAS));
			breedingMethodDTO.setNumberOfProgenitors((Integer) row.get(BreedingMethodSearchDAOQuery.NUMBER_OF_PROGENITORS_ALIAS));
			breedingMethodDTO.setSeparator((String) row.get(BreedingMethodSearchDAOQuery.SEPARATOR_ALIAS));
			breedingMethodDTO.setPrefix((String) row.get(BreedingMethodSearchDAOQuery.PREFIX_ALIAS));
			breedingMethodDTO.setCount((String) row.get(BreedingMethodSearchDAOQuery.COUNT_ALIAS));
			breedingMethodDTO.setSuffix((String) row.get(BreedingMethodSearchDAOQuery.SUFFIX_ALIAS));
			breedingMethodDTO.setDate((Date) row.get(BreedingMethodSearchDAOQuery.DATE_ALIAS));
			final Integer programFavoriteId = (Integer) row.get(BreedingMethodSearchDAOQuery.FAVORITE_PROGRAM_ID_ALIAS);
			if (programFavoriteId != null) {
				final ProgramFavoriteDTO programFavoriteDTO =
						new ProgramFavoriteDTO(programFavoriteId, ProgramFavorite.FavoriteType.LOCATION, breedingMethodDTO.getMid(),
								(String) row.get(BreedingMethodSearchDAOQuery.FAVORITE_PROGRAM_UUID_ALIAS));
				breedingMethodDTO.setProgramFavorites(Arrays.asList(programFavoriteDTO));
			}

			return breedingMethodDTO;
		}).collect(Collectors.toList());
	}

	public Long countSearchBreedingMethods(final BreedingMethodSearchRequest methodSearchRequest,
			final String programUUID) {
		final SQLQueryBuilder queryBuilder = BreedingMethodSearchDAOQuery.getCountQuery(methodSearchRequest, programUUID);
		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countByVariable(final int variableId){
		try {
			final SQLQuery query = this.getSession().createSQLQuery(MethodDAO.COUNT_BREEDING_METHODS_WITH_VARIABLE);
			query.setParameter("variableId", variableId);


			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countByVariable=" + variableId + " in AttributeDAO: " + e.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

}
