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

package org.generationcp.middleware.dao.oms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.h2h.CategoricalTraitInfo;
import org.generationcp.middleware.domain.h2h.CategoricalValue;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link CVTerm}.
 */
@SuppressWarnings("unchecked")
public class CVTermDao extends GenericDAO<CVTerm, Integer> {

	public static final String SHOULD_NOT_OBSOLETE = "is_obsolete = 0";

	public CVTerm getByCvIdAndDefinition(final Integer cvId, final String definition) {
		CVTerm term = null;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			criteria.add(Restrictions.eq("definition", definition));
			criteria.add(Restrictions.eq("isObsolete", 0));

			term = (CVTerm) criteria.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getByCvIdAndDefinition=" + cvId + ", " + definition
					+ " query on CVTermDao: " + e.getMessage(), e);
		}

		return term;
	}

	public List<Integer> getTermsByNameOrSynonym(final String nameOrSynonym, final int cvId) {
		final List<Integer> termIds = new ArrayList<>();
		try {

			final StringBuilder sqlString = new StringBuilder().append("SELECT DISTINCT cvt.cvterm_id ")
					.append("FROM cvterm cvt ").append("WHERE cvt.cv_id = :cvId and cvt.name = :nameOrSynonym ")
					.append("UNION ").append("SELECT DISTINCT cvt.cvterm_id ")
					.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
					.append("AND cvt.cv_id = :cvId AND syn.synonym = :nameOrSynonym ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("cvId", cvId);
			query.setParameter("nameOrSynonym", nameOrSynonym);

			final List<Object> results = query.list();
			for (final Object row : results) {
				termIds.add((Integer) row);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in getTermsByNameOrSynonym=" + nameOrSynonym + " in CVTermDao: " + e.getMessage(), e);
		}
		return termIds;
	}

	public Map<String, Map<Integer, VariableType>> getTermIdsWithTypeByNameOrSynonyms(final List<String> nameOrSynonyms,
			final int cvId) {
		final Map<String, Map<Integer, VariableType>> stdVarMap = new HashMap<String, Map<Integer, VariableType>>();

		// Store the names in the map in uppercase
		for (int i = 0, size = nameOrSynonyms.size(); i < size; i++) {
			nameOrSynonyms.set(i, nameOrSynonyms.get(i).toUpperCase());
		}

		try {
			if (!nameOrSynonyms.isEmpty()) {

				final StringBuilder sqlString = new StringBuilder().append("SELECT cvt.name, cvt.cvterm_id ")
						.append("FROM cvterm cvt ")
						.append("WHERE cvt.cv_id = :cvId and cvt.name IN (:nameOrSynonyms) AND cvt.is_obsolete = 0 ")
						.append("UNION ").append("SELECT syn.synonym, cvt.cvterm_id ")
						.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
						.append("AND cvt.cv_id = :cvId AND syn.synonym IN (:nameOrSynonyms) AND cvt.is_obsolete = 0");

				final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
				query.setParameter("cvId", cvId);
				query.setParameterList("nameOrSynonyms", nameOrSynonyms);

				final List<Object[]> results = query.list();

				for (final Object[] row : results) {
					final String nameOrSynonym = ((String) row[0]).trim().toUpperCase();
					final Integer cvtermId = (Integer) row[1];

					Map<Integer, VariableType> stdVarIdsWithType = null;
					if (stdVarMap.containsKey(nameOrSynonym)) {
						stdVarIdsWithType = stdVarMap.get(nameOrSynonym);
					} else {
						stdVarIdsWithType = new HashMap<Integer, VariableType>();
						stdVarMap.put(nameOrSynonym, stdVarIdsWithType);
					}
					stdVarIdsWithType.put(cvtermId, this.getDefaultVariableType(cvtermId));
				}

			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in getTermsByNameOrSynonyms=" + nameOrSynonyms + " in CVTermDao: " + e.getMessage(), e);
		}
		return stdVarMap;
	}

	private VariableType getDefaultVariableType(final Integer cvTermId) {
		final Criteria criteria = this.getSession().createCriteria(CVTermProperty.class);
		criteria.add(Restrictions.eq("cvTermId", cvTermId));
		criteria.add(Restrictions.eq("typeId", TermId.VARIABLE_TYPE.getId()));
		criteria.addOrder(Order.asc("cvTermPropertyId"));
		final List<CVTermProperty> variableTypes = criteria.list();
		if (variableTypes != null) {
			for (final CVTermProperty cvTermProperty : variableTypes) {
				return VariableType.getByName(cvTermProperty.getValue());
			}
		}
		return null;
	}

	public CVTerm getByNameAndCvId(final String name, final int cvId) {
		CVTerm term = null;

		try {

			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ").append("FROM cvterm cvt ")
					.append("WHERE cvt.cv_id = :cvId and cvt.name = :nameOrSynonym ").append("UNION ")
					.append("	SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ")
					.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
					.append("AND cvt.cv_id = :cvId AND syn.synonym = :nameOrSynonym ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("cvId", cvId);
			query.setParameter("nameOrSynonym", name);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				final Object[] row = results.get(0);
				final Integer cvtermId = (Integer) row[0];
				final Integer cvtermCvId = (Integer) row[1];
				final String cvtermName = (String) row[2];
				final String cvtermDefinition = (String) row[3];
				final Integer dbxrefId = (Integer) row[4];
				final Integer isObsolete = (Integer) row[5];
				final Integer isRelationshipType = (Integer) row[6];

				term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete,
						isRelationshipType);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error at getByNameAndCvId=" + name + ", " + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}

		return term;
	}

	public CVTerm getByName(final String name) {
		CVTerm term = null;

		try {

			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ").append("FROM cvterm cvt ")
					.append("WHERE cvt.name = :nameOrSynonym ").append("UNION ")
					.append("	SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append(", cvt.dbxref_id, cvt.is_obsolete, cvt.is_relationshiptype  ")
					.append("FROM cvterm cvt INNER JOIN cvtermsynonym syn ON  syn.cvterm_id = cvt.cvterm_id ")
					.append("AND syn.synonym = :nameOrSynonym ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("nameOrSynonym", name);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				final Object[] row = results.get(0);
				final Integer cvtermId = (Integer) row[0];
				final Integer cvtermCvId = (Integer) row[1];
				final String cvtermName = (String) row[2];
				final String cvtermDefinition = (String) row[3];
				final Integer dbxrefId = (Integer) row[4];
				final Integer isObsolete = (Integer) row[5];
				final Integer isRelationshipType = (Integer) row[6];

				term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete,
						isRelationshipType);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getByName=" + name + " query on CVTermDao: " + e.getMessage(), e);
		}

		return term;
	}

	public List<Term> getTermByCvId(final int cvId) {

		final List<Term> terms = new ArrayList<>();

		try {

			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT DISTINCT cvt.cvterm_id, cvt.cv_id, cvt.name, cvt.definition ")
					.append("FROM cvterm cvt ").append("WHERE cvt.cv_id = :cvId");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("cvId", cvId);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {

				for (final Object[] row : results) {

					final Integer cvtermId = (Integer) row[0];
					final Integer cvtermCvId = (Integer) row[1];
					final String cvtermName = (String) row[2];
					final String cvtermDefinition = (String) row[3];

					final Term term = new Term();
					term.setId(cvtermId);
					term.setName(cvtermName);
					term.setDefinition(cvtermDefinition);
					term.setVocabularyId(cvtermCvId);
					terms.add(term);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTermByCvId=" + cvId + " query on CVTermDao: " + e.getMessage(), e);
		}

		return terms;
	}

	public List<CVTerm> getByIds(final List<Integer> ids) {
		List<CVTerm> terms = new ArrayList<>();

		if (ids != null && !ids.isEmpty()) {
			try {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in("cvTermId", ids));

				terms = criteria.list();

			} catch (final HibernateException e) {
				this.logAndThrowException("Error at GetByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);
			}
		}

		return terms;
	}

	public List<CVTerm> getValidCvTermsByIds(final List<Integer> ids, final int storedInId, final int dataTypeId) {
		final List<CVTerm> terms = new ArrayList<>();

		if (ids != null && !ids.isEmpty()) {
			try {
				final StringBuilder queryString = new StringBuilder()
						.append("SELECT cvt.cvterm_id, cvt.name, cvt.definition ").append("FROM cvterm cvt ")
						.append("INNER JOIN cvterm_relationship datatype ON datatype.subject_id = cvt.cvterm_id ")
						.append(" AND datatype.type_id = ").append(TermId.HAS_TYPE.getId())
						.append(" INNER JOIN cvterm_relationship stored_in ON datatype.subject_id = stored_in.subject_id ")
						.append(" AND stored_in.type_id = ").append(TermId.STORED_IN.getId())
						.append(" WHERE cvt.cvterm_id in (:ids)")
						.append(" AND (stored_in.object_id <> :storedIn OR (stored_in.object_id = :storedIn ")
						.append(" AND datatype.object_id = :datatype))");

				final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.setParameterList("ids", ids);
				query.setParameter("storedIn", storedInId);
				query.setParameter("datatype", dataTypeId);

				final List<Object[]> list = query.list();

				for (final Object[] row : list) {
					final Integer id = (Integer) row[0];
					final String name = (String) row[1];
					final String definition = (String) row[2];

					final CVTerm cvTerm = new CVTerm();
					cvTerm.setCvTermId(id);
					cvTerm.setName(name);
					cvTerm.setDefinition(definition);
					terms.add(cvTerm);
				}
			} catch (final HibernateException e) {
				this.logAndThrowException(
						"Error at getValidCvTermsByIds=" + ids + " query on CVTermDao: " + e.getMessage(), e);
			}
		}
		return terms;
	}

	public List<CVTerm> getVariablesByType(final List<Integer> types) {
		final List<CVTerm> terms = new ArrayList<>();

		try {
			final StringBuilder queryString = new StringBuilder()
					.append("SELECT variable.cvterm_id, variable.name, variable.definition FROM cvterm variable ")
					.append("INNER JOIN cvterm_relationship hasScale ON hasScale.subject_id = variable.cvterm_id AND hasScale.type_id = "
							+ TermId.HAS_SCALE.getId() + " ")
					.append("INNER JOIN cvterm_relationship numericScale ON hasScale.object_id = numericScale.subject_id  AND numericScale.type_id = "
							+ TermId.HAS_TYPE.getId() + " AND numericScale.object_id IN (:types)");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("types", types);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String name = (String) row[1];
				final String definition = (String) row[2];

				final CVTerm cvTerm = new CVTerm();
				cvTerm.setCvTermId(id);
				cvTerm.setName(name);
				cvTerm.setDefinition(definition);
				terms.add(cvTerm);
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getVariablesByType=" + types + " query on CVTermDao: " + e.getMessage(),
					e);
		}

		return terms;
	}

	public List<CategoricalTraitInfo> setCategoricalVariables(final List<CategoricalTraitInfo> traitInfoList) {
		final List<CategoricalTraitInfo> categoricalTraitInfoList = new ArrayList<>();

		// Get trait IDs
		final List<Integer> traitIds = new ArrayList<>();
		for (final CategoricalTraitInfo trait : traitInfoList) {
			traitIds.add(trait.getId());
		}

		try {
			SQLQuery query = this.getSession().createSQLQuery(
					"SELECT cvt_categorical.cvterm_id, cvt_categorical.name, cvt_categorical.definition, cvr_value.object_id, cvt_value.name "
							+ "FROM cvterm_relationship cvr_categorical  "
							+ "INNER JOIN cvterm cvt_categorical ON cvr_categorical.subject_id = cvt_categorical.cvterm_id "
							+ "INNER JOIN cvterm_relationship cvr_scale ON cvr_categorical.subject_id = cvr_scale.subject_id "
							+ "INNER JOIN cvterm_relationship cvr_scale_type ON cvr_scale.object_id = cvr_scale_type.subject_id "
							+ "INNER JOIN cvterm_relationship cvr_value ON cvr_scale.object_id = cvr_value.subject_id and cvr_value.type_id = 1190 "
							+ "INNER JOIN cvterm cvt_value ON cvr_value.object_id = cvt_value.cvterm_id "
							+ "WHERE cvr_scale.type_id = 1220 and cvr_scale_type.type_id = 1105 AND cvr_scale_type.object_id = 1130 "
							+ "    AND cvt_categorical.cvterm_id in (:traitIds) ");

			query.setParameterList("traitIds", traitIds);

			List<Object[]> list = query.list();

			final Map<Integer, String> valueIdName = new HashMap<Integer, String>();
			for (final Object[] row : list) {
				final Integer variableId = (Integer) row[0];
				final String variableName = (String) row[1];
				final String variableDescription = (String) row[2];
				final Integer valueId = (Integer) row[3];
				final String valueName = (String) row[4];

				valueIdName.put(valueId, valueName);

				for (final CategoricalTraitInfo traitInfo : traitInfoList) {
					if (traitInfo.getId() == variableId) {
						traitInfo.setName(variableName);
						traitInfo.setDescription(variableDescription);
						traitInfo.addValue(new CategoricalValue(valueId, valueName));
						break;
					}
				}
			}

			// Remove non-categorical variable from the list
			for (final CategoricalTraitInfo traitInfo : traitInfoList) {
				if (traitInfo.getName() != null) {
					categoricalTraitInfoList.add(traitInfo);
				}
			}

			// This step was added since the valueName is not retrieved
			// correctly with the above query in Java.
			// Most probably because of the two cvterm id-name present in the
			// query.
			// The steps that follow will just retrieve the name of the
			// categorical values in each variable.

			final List<Integer> valueIds = new ArrayList<>();
			valueIds.addAll(valueIdName.keySet());

			if (valueIds != null && !valueIds.isEmpty()) {
				query = this.getSession().createSQLQuery(
						"SELECT cvterm_id, cvterm.name " + "FROM cvterm " + "WHERE cvterm_id IN (:ids) ");
				query.setParameterList("ids", valueIds);

				list = query.list();
			}

			for (final Object[] row : list) {
				final Integer variableId = (Integer) row[0];
				final String variableName = (String) row[1];

				valueIdName.put(variableId, variableName);
			}

			for (final CategoricalTraitInfo traitInfo : categoricalTraitInfoList) {
				final List<CategoricalValue> values = traitInfo.getValues();
				for (final CategoricalValue value : values) {
					final String name = valueIdName.get(value.getId());
					value.setName(name);
				}
				traitInfo.setValues(values);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at setCategoricalVariables() query on CVTermDao: " + e.getMessage(), e);
		}

		return categoricalTraitInfoList;
	}

	public List<TraitInfo> getTraitInfo(final List<Integer> traitIds) {
		final List<TraitInfo> traits = new ArrayList<>();

		try {

			final StringBuilder sql = new StringBuilder()
					.append("SELECT cvt.cvterm_id, cvt.name, cvt.definition,  c_scale.scaleName, cr_type.object_id ")
					.append("FROM cvterm cvt ")
					.append("	INNER JOIN cvterm_relationship cr_scale ON cvt.cvterm_id = cr_scale.subject_id ")
					.append("   INNER JOIN (SELECT cvterm_id, name AS scaleName FROM cvterm) c_scale ON c_scale.cvterm_id = cr_scale.object_id ")
					.append("        AND cr_scale.type_id = ").append(TermId.HAS_SCALE.getId()).append(" ")
					.append("	INNER JOIN cvterm_relationship cr_type ON cr_type.subject_id = cr_scale.subject_id ")
					.append("		AND cr_type.type_id = ").append(TermId.HAS_TYPE.getId()).append(" ")
					.append("WHERE cvt.cvterm_id in (:traitIds) ");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("traitIds", traitIds);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String name = (String) row[1];
				final String description = (String) row[2];
				final String scaleName = (String) row[3];
				final Integer typeId = (Integer) row[4];

				traits.add(new TraitInfo(id, name, description, scaleName, typeId));

			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTraitInfo() query on CVTermDao: " + e.getMessage(), e);
		}
		return traits;
	}

	public Integer getStandadardVariableIdByPropertyScaleMethod(final Integer propertyId, final Integer scaleId,
			final Integer methodId, final String sortOrder) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvr.subject_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append(
					"WHERE cvrp.object_id = :propertyId AND cvrs.object_id = :scaleId AND cvrm.object_id = :methodId ");
			queryString.append("ORDER BY cvr.subject_id ").append(sortOrder).append(" LIMIT 0,1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("propertyId", propertyId);
			query.setParameter("scaleId", scaleId);
			query.setParameter("methodId", methodId);

			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStandadardVariableIdByPropertyScaleMethod :" + e.getMessage(), e);
		}
		return null;

	}

	public List<Integer> getStandardVariableIdsByPhenotypicType(final PhenotypicType type) {
		try {
			// Standard variable has the combination of property-scale-method
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvr.subject_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append(
					"INNER JOIN cvterm_relationship storedIn ON cvr.subject_id = storedIn.subject_id AND storedIn.type_id = 1044 ");
			queryString.append("INNER JOIN cvterm term ON cvr.subject_id = term.cvterm_id ");
			queryString.append("WHERE storedIn.object_id IN (:type) ORDER BY term.name");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("type", type.getTypeStorages());

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStandardVariableIdsByPhenotypicType :" + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<CVTerm> getTermsByCvId(final CvId cvId, final int start, final int numOfRows) {
		final List<CVTerm> terms = new ArrayList<>();

		try {

			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT cvterm_id, name, definition, dbxref_id, is_obsolete, is_relationshiptype "
							+ "FROM cvterm " + "WHERE cv_id = :cvId " + "ORDER BY cvterm_id, name ");
			query.setParameter("cvId", cvId.getId());
			this.setStartAndNumOfRows(query, start, numOfRows);
			final List<Object[]> list = query.list();
			for (final Object[] row : list) {
				final Integer termId = (Integer) row[0];
				final String name = (String) row[1];
				final String definition = (String) row[2];
				final Integer dbxrefId = (Integer) row[3];
				final Integer isObsolete = (Integer) row[4];
				final Integer isRelationshipType = (Integer) row[5];

				terms.add(new CVTerm(termId, cvId.getId(), name, definition, dbxrefId, isObsolete, isRelationshipType));

			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
		}

		return terms;
	}

	public long countTermsByCvId(final CvId cvId) {

		try {

			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT COUNT(cvterm_id) " + "FROM cvterm " + "WHERE cv_id = :cvId ");
			query.setParameter("cvId", cvId.getId());

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at countTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
		}

		return 0;
	}

	public List<Integer> findMethodTermIdsByTrait(final Integer traitId) {
		try {
			// Standard variable has the combination of property-scale-method
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvrm.object_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append("WHERE cvrp.object_id = :traitId");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setInteger("traitId", traitId);

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at findMethodTermIdsByTrait :" + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<Integer> findScaleTermIdsByTrait(final Integer traitId) {
		try {
			// Standard variable has the combination of property-scale-method
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvrs.object_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id AND cvrp.type_id = 1200 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id AND cvrs.type_id = 1220 ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id AND cvrm.type_id = 1210 ");
			queryString.append("WHERE cvrp.object_id = :traitId");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setInteger("traitId", traitId);

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at findScaleTermIdsByTrait :" + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	/**
	 * Returns standard variables associated to the given list of trait names or
	 * synonyms
	 *
	 * @param propertyNameOrSynonyms
	 * @return Map of name-(standard variable ids-variable type) of the given
	 *         trait name or synonyms
	 */
	public Map<String, Map<Integer, VariableType>> getStandardVariableIdsWithTypeByProperties(
			final List<String> propertyNameOrSynonyms) {
		final Map<String, Map<Integer, VariableType>> stdVarMap = new HashMap<String, Map<Integer, VariableType>>();

		// Store the names in the map in uppercase
		for (int i = 0, size = propertyNameOrSynonyms.size(); i < size; i++) {
			propertyNameOrSynonyms.set(i, propertyNameOrSynonyms.get(i).toUpperCase());
		}

		try {
			if (!propertyNameOrSynonyms.isEmpty()) {

				final StringBuilder sqlString = new StringBuilder()
						.append("SELECT DISTINCT cvtr.name, syn.synonym, cvt.cvterm_id ")
						.append("FROM cvterm_relationship cvr ")
						.append("INNER JOIN cvterm cvtr ON cvr.object_id = cvtr.cvterm_id AND cvr.type_id = 1200 AND cvtr.is_obsolete = 0 ")
						.append("INNER JOIN cvterm cvt ON cvr.subject_id = cvt.cvterm_id AND cvt.cv_id = 1040 AND cvt.is_obsolete = 0  ")
						.append(", cvtermsynonym syn ")
						.append("WHERE (cvtr.cvterm_id = syn.cvterm_id AND syn.synonym IN (:propertyNameOrSynonyms) ")
						.append("OR cvtr.name IN (:propertyNameOrSynonyms)) ");

				final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
				query.setParameterList("propertyNameOrSynonyms", propertyNameOrSynonyms);

				final List<Object[]> results = query.list();

				for (final Object[] row : results) {
					final String cvtermName = ((String) row[0]).trim().toUpperCase();
					final String cvtermSynonym = ((String) row[1]).trim().toUpperCase();
					final Integer cvtermId = (Integer) row[2];

					Map<Integer, VariableType> stdVarIdsWithType = new HashMap<Integer, VariableType>();
					if (propertyNameOrSynonyms.contains(cvtermName)) {
						if (stdVarMap.containsKey(cvtermName)) {
							stdVarIdsWithType = stdVarMap.get(cvtermName);
						}
						stdVarIdsWithType.put(cvtermId, this.getDefaultVariableType(cvtermId));
						stdVarMap.put(cvtermName, stdVarIdsWithType);

					}
					if (propertyNameOrSynonyms.contains(cvtermSynonym)) {
						if (stdVarMap.containsKey(cvtermSynonym)) {
							stdVarIdsWithType = stdVarMap.get(cvtermSynonym);
						}
						stdVarIdsWithType.put(cvtermId, this.getDefaultVariableType(cvtermId));
						stdVarMap.put(cvtermSynonym, stdVarIdsWithType);
					}

				}

			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStandardVariableIdsWithTypeByProperties=" + propertyNameOrSynonyms
					+ " in CVTermDao: " + e.getMessage(), e);
		}

		return stdVarMap;

	}

	public List<CVTerm> getIsAOfTermsByCvId(final CvId cvId, final int start, final int numOfRows) {
		final List<CVTerm> terms = new ArrayList<>();

		try {

			final SQLQuery query = this.getSession().createSQLQuery(
					"SELECT DISTINCT isA.cvterm_id, isA.name, isA.definition, isA.dbxref_id, isA.is_obsolete, isA.is_relationshiptype "
							+ "FROM cvterm isA, cvterm_relationship rel, cvterm subj " + "WHERE subj.cv_id = :cvId "
							+ "AND subj.cvterm_id = rel.subject_id " + "AND rel.object_id = isA.cvterm_id "
							+ "AND rel.type_id = " + TermId.IS_A.getId() + " " + "ORDER BY isA.name, isA.cvterm_id ");
			query.setParameter("cvId", cvId.getId());
			this.setStartAndNumOfRows(query, start, numOfRows);
			final List<Object[]> list = query.list();
			for (final Object[] row : list) {
				final Integer termId = (Integer) row[0];
				final String name = (String) row[1];
				final String definition = (String) row[2];
				final Integer dbxrefId = (Integer) row[3];
				final Integer isObsolete = (Integer) row[4];
				final Integer isRelationshipType = (Integer) row[5];

				terms.add(new CVTerm(termId, cvId.getId(), name, definition, dbxrefId, isObsolete, isRelationshipType));

			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
		}

		return terms;
	}

	public long countIsAOfTermsByCvId(final CvId cvId) {

		try {

			final SQLQuery query = this.getSession()
					.createSQLQuery("SELECT COUNT(DISTINCT isA.cvterm_id) "
							+ "FROM cvterm isA, cvterm_relationship rel, cvterm subj " + "WHERE subj.cv_id = :cvId "
							+ "AND subj.cvterm_id = rel.subject_id " + "AND rel.object_id = isA.cvterm_id "
							+ "AND rel.type_id = " + TermId.IS_A.getId() + " ");
			query.setParameter("cvId", cvId.getId());

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at countTermsByCvId() query on CVTermDao: " + e.getMessage(), e);
		}

		return 0;
	}

	public CVTerm getTermOfProperty(final int termId, final int cvId) {
		CVTerm term = null;

		try {
			final StringBuilder sqlString = new StringBuilder().append("SELECT * ").append("FROM cvterm ")
					.append("WHERE cv_id = :cvId AND cvterm_id = :termId");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("termId", termId);
			query.setParameter("cvId", cvId);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				final Object[] row = results.get(0);
				final Integer cvtermId = (Integer) row[0];
				final Integer cvtermCvId = (Integer) row[1];
				final String cvtermName = (String) row[2];
				final String cvtermDefinition = (String) row[3];
				final Integer dbxrefId = (Integer) row[4];
				final Integer isObsolete = (Integer) row[5];
				final Integer isRelationshipType = (Integer) row[6];

				term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete,
						isRelationshipType);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTermOfProperty=" + termId + " query on CVTermDao: " + e.getMessage(),
					e);
		}

		return term;
	}

	public CVTerm getTermOfClassOfProperty(final int termId, final int cvId, final int isATermId) {
		CVTerm term = null;

		try {
			final StringBuilder sqlString = new StringBuilder().append("SELECT cvt.* ").append("FROM cvterm cvt ")
					.append("INNER JOIN cvterm_relationship cvr on cvr.object_id = cvt.cvterm_id ")
					.append("INNER JOIN cvterm v on cvr.subject_id = v.cvterm_id ")
					.append("WHERE cvr.type_id = :isAtermId AND v.cv_id = :cvId AND v.cvterm_id = :termId");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameter("termId", termId);
			query.setParameter("isAtermId", isATermId);
			query.setParameter("cvId", cvId);

			final List<Object[]> results = query.list();

			if (!results.isEmpty()) {
				final Object[] row = results.get(0);
				final Integer cvtermId = (Integer) row[0];
				final Integer cvtermCvId = (Integer) row[1];
				final String cvtermName = (String) row[2];
				final String cvtermDefinition = (String) row[3];
				final Integer dbxrefId = (Integer) row[4];
				final Integer isObsolete = (Integer) row[5];
				final Integer isRelationshipType = (Integer) row[6];

				term = new CVTerm(cvtermId, cvtermCvId, cvtermName, cvtermDefinition, dbxrefId, isObsolete,
						isRelationshipType);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error at getTermOfClassOfProperty=" + termId + " query on CVTermDao: " + e.getMessage(), e);
		}

		return term;
	}

	/**
	 * Returns the entries in cvterm of all trait classes (with subject_id entry
	 * in cvterm_relationship where object_id = classType and type_id = 1225)
	 */
	public List<TraitClassReference> getTraitClasses(final TermId classType) {

		final List<TraitClassReference> traitClasses = new ArrayList<>();

		try {
			/*
			 * SELECT cvterm_id, name, definition FROM cvterm cvt JOIN
			 * cvterm_relationship cvr ON cvt.cvterm_id = cvr.subject_id AND
			 * cvr.type_id = 1225 AND cvr.object_id = 1330; -- 1330 for Ontology
			 * Trait Class, 1045 for Ontology Research Class
			 */

			final StringBuilder sqlString = new StringBuilder().append("SELECT cvterm_id, name, definition ")
					.append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
					.append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId())
					.append(" AND cvr.object_id = ").append(classType.getId()).append(" ")
					.append(" AND cvt.cv_id in (" + CvId.TRAIT_CLASS.getId() + "," + CvId.IBDB_TERMS.getId() + ")");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer cvtermId = (Integer) row[0];
				final String cvtermName = (String) row[1];
				final String cvtermDefinition = (String) row[2];

				traitClasses.add(new TraitClassReference(cvtermId, cvtermName, cvtermDefinition, classType.getId()));
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTraitClasses() query on CVTermDao: " + e.getMessage(), e);
		}

		return traitClasses;

	}

	/**
	 * Retrieves all the trait classes (id, name, definition, parent trait
	 * class)
	 *
	 * @return List of trait class references
	 */
	public List<TraitClassReference> getAllTraitClasses() {
		final List<TraitClassReference> traitClasses = new ArrayList<>();

		try {
			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT cvterm_id, name, definition, cvr.object_id ")
					.append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
					.append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId())
					.append(" ")
					.append("WHERE cv_id in (" + CvId.TRAIT_CLASS.getId() + "," + CvId.IBDB_TERMS.getId() + ") ")
					.append("AND object_id NOT IN (1000, 1002, 1003)  ").append("ORDER BY cvr.object_id ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0];
				final String name = (String) row[1];
				final String definition = (String) row[2];
				final Integer isAId = (Integer) row[3];

				traitClasses.add(new TraitClassReference(id, name, definition, isAId));
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getAllTraitClasses() query on CVTermDao: " + e.getMessage(), e);
		}

		return traitClasses;

	}

	/**
	 * Retrieves the properties of a Trait Class
	 */
	public List<PropertyReference> getPropertiesOfTraitClass(final Integer traitClassId) {
		final List<Integer> traitClasses = new ArrayList<>();
		traitClasses.add(traitClassId);
		return this.getPropertiesOfTraitClasses(traitClasses).get(traitClassId);
	}

	/**
	 * Retrieves the properties of Trait Classes
	 */
	public Map<Integer, List<PropertyReference>> getPropertiesOfTraitClasses(final List<Integer> traitClassIds) {

		final Map<Integer, List<PropertyReference>> propertiesOfTraitClasses = new HashMap<>();

		if (traitClassIds.isEmpty()) {
			return propertiesOfTraitClasses;
		}

		Collections.sort(traitClassIds);

		try {

			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT cvterm_id, name, definition, cvr.object_id ")
					.append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
					.append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.IS_A.getId())
					.append(" ").append(" AND cvr.object_id  IN (:traitClassIds) ").append("WHERE cv_id =  ")
					.append(CvId.PROPERTIES.getId()).append(" ").append("ORDER BY cvr.object_id ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("traitClassIds", traitClassIds);

			final List<Object[]> list = query.list();

			List<PropertyReference> properties = new ArrayList<>();
			Integer prevTraitClassId = traitClassIds.get(0);

			for (final Object[] row : list) {
				final Integer cvtermId = (Integer) row[0];
				final String cvtermName = (String) row[1];
				final String cvtermDefinition = (String) row[2];
				final Integer traitClassId = (Integer) row[3];

				if (!prevTraitClassId.equals(traitClassId)) {
					propertiesOfTraitClasses.put(prevTraitClassId, properties);
					properties = new ArrayList<>();
					prevTraitClassId = traitClassId;
				}
				properties.add(new PropertyReference(cvtermId, cvtermName, cvtermDefinition));
			}

			propertiesOfTraitClasses.put(prevTraitClassId, properties);

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getTraitClassProperties() query on CVTermDao: " + e.getMessage(), e);
		}

		return propertiesOfTraitClasses;
	}

	/**
	 * Retrieves the standard variables of a property
	 */
	public List<StandardVariableReference> getStandardVariablesOfProperty(final Integer propertyId) {
		final List<Integer> properties = new ArrayList<>();
		properties.add(propertyId);
		return this.getStandardVariablesOfProperties(properties).get(propertyId);
	}

	/**
	 * Retrieves the standard variables of trait properties
	 */
	public Map<Integer, List<StandardVariableReference>> getStandardVariablesOfProperties(
			final List<Integer> propertyIds) {
		final Map<Integer, List<StandardVariableReference>> variablesOfProperties = new HashMap<>();

		if (propertyIds.isEmpty()) {
			return variablesOfProperties;
		}

		Collections.sort(propertyIds);

		try {
			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT cvterm_id, name, definition, cvr.object_id ")
					.append("FROM cvterm cvt JOIN cvterm_relationship cvr ")
					.append("ON cvt.cvterm_id = cvr.subject_id AND cvr.type_id = ").append(TermId.HAS_PROPERTY.getId())
					.append(" AND cvr.object_id  IN (:propertyIds) ").append("ORDER BY cvr.object_id ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("propertyIds", propertyIds);

			final List<Object[]> list = query.list();

			List<StandardVariableReference> variables = new ArrayList<>();
			Integer prevPropertyId = propertyIds.get(0);

			for (final Object[] row : list) {
				final Integer cvtermId = (Integer) row[0];
				final String cvtermName = (String) row[1];
				final String cvtermDefinition = (String) row[2];
				final Integer traitClassId = (Integer) row[3];

				if (!prevPropertyId.equals(traitClassId)) {
					variablesOfProperties.put(prevPropertyId, variables);
					variables = new ArrayList<>();
					prevPropertyId = traitClassId;
				}
				variables.add(new StandardVariableReference(cvtermId, cvtermName, cvtermDefinition));
			}

			variablesOfProperties.put(prevPropertyId, variables);

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error at getStandardVariablesOfProperties() query on CVTermDao: " + e.getMessage(), e);
		}

		return variablesOfProperties;
	}

	/*
	 * Retrieves the standard variable linked to an ontology
	 */
	public Integer getStandardVariableIdByTermId(final int cvTermId, final TermId termId) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT cvr.subject_id ");
			queryString.append("FROM cvterm_relationship cvr ");
			queryString.append(
					"INNER JOIN cvterm_relationship cvrt ON cvr.subject_id = cvrt.subject_id AND cvrt.type_id = :typeId ");
			queryString.append("WHERE cvr.object_id = :cvTermId ");
			queryString.append("ORDER BY cvr.subject_id ").append(" LIMIT 0,1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("typeId", termId.getId());
			query.setParameter("cvTermId", cvTermId);

			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStandadardVariableIdByTermId :" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Gets the all standard variables based on the parameters with values. At
	 * least one parameter needs to have a value. If a standard variable has no
	 * trait class, it is not included in the result.
	 *
	 * @param traitClassId
	 * @param propertyId
	 * @param methodId
	 * @param scaleId
	 * @return List of standard variable ids
	 */
	public List<Integer> getStandardVariableIds(final Integer traitClassId, final Integer propertyId,
			final Integer methodId, final Integer scaleId) {
		final List<Integer> standardVariableIds = new ArrayList<>();
		try {
			final StringBuilder queryString = new StringBuilder().append("SELECT DISTINCT cvr.subject_id ")
					.append("FROM cvterm_relationship cvr ");

			if (traitClassId != null) {
				// Trait class via 'IS A' of property
				queryString.append("INNER JOIN cvterm_relationship cvrpt ON cvr.subject_id = cvrpt.subject_id ");
				queryString.append("    AND cvrpt.type_id = ").append(TermId.HAS_PROPERTY.getId()).append(" ");
				queryString.append("INNER JOIN cvterm_relationship cvrt ON cvrpt.object_id = cvt.subject_id ");
				queryString.append("    AND cvrt.object_id = :traitClassId AND cvrt.type_id = ")
						.append(TermId.IS_A.getId()).append(" ");
			}
			if (propertyId != null) {
				queryString.append("INNER JOIN cvterm_relationship cvrp ON cvr.subject_id = cvrp.subject_id ");
				queryString.append("    AND cvr.object_id = :propertyId AND cvr.type_id = ")
						.append(TermId.HAS_PROPERTY.getId()).append(" ");
			}
			if (methodId != null) {
				queryString.append("INNER JOIN cvterm_relationship cvrm ON cvr.subject_id = cvrm.subject_id ");
				queryString.append("    AND cvr.object_id = :methodId AND cvr.type_id = ")
						.append(TermId.HAS_METHOD.getId()).append(" ");
			}
			if (scaleId != null) {
				queryString.append("INNER JOIN cvterm_relationship cvrs ON cvr.subject_id = cvrs.subject_id ");
				queryString.append("    AND  cvr.object_id = :scaleId AND cvr.type_id = ")
						.append(TermId.HAS_SCALE.getId()).append(" ");
			}

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			if (traitClassId != null) {
				query.setParameter("traitClassId", traitClassId);
			}
			if (propertyId != null) {
				query.setParameter("propertyId", propertyId);
			}
			if (methodId != null) {
				query.setParameter("methodId", methodId);
			}
			if (scaleId != null) {
				query.setParameter("scaleId", scaleId);
			}

			final List<Integer> result = query.list();

			if (result != null && !result.isEmpty()) {
				for (final Integer row : result) {
					standardVariableIds.add(row);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStandardVariableIds :" + e.getMessage(), e);
		}
		return standardVariableIds;
	}

	public List<Property> getAllPropertiesWithTraitClass() {
		final List<Property> properties = new ArrayList<>();
		try {
			final StringBuilder sql = new StringBuilder()
					.append("SELECT p.cvterm_id, p.name, p.definition, pr.object_id, coId.value ")
					.append(" FROM cvterm p ")
					.append(" INNER JOIN cvterm_relationship pr ON pr.subject_id = p.cvterm_id AND pr.type_id = ")
					.append(TermId.IS_A.getId())
					.append(" LEFT JOIN cvtermprop coId ON coId.cvterm_id = p.cvterm_id AND coId.type_id = ")
					.append(TermId.CROP_ONTOLOGY_ID.getId()).append(" WHERE p.cv_id = ").append(CvId.PROPERTIES.getId())
					.append(" AND p.is_obsolete = 0 ");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			final List<Object[]> result = query.list();

			if (result != null && !result.isEmpty()) {
				for (final Object[] row : result) {
					properties.add(new Property(new Term((Integer) row[0], (String) row[1], (String) row[2]),
							new Term((Integer) row[3], null, null), (String) row[4]));
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getStandadardVariableIdByTermId :" + e.getMessage(), e);
		}
		return properties;
	}

	public Integer getStandadardVariableIdByPropertyScaleMethodRole(final Integer propertyId, final Integer scaleId,
			final Integer methodId, final PhenotypicType role) {
		// for the new ontology manager changes, role is already not defined by
		// the stored in id and variable is already unique by PSM
		return this.getStandadardVariableIdByPropertyScaleMethod(propertyId, scaleId, methodId, "ASC");

	}

	public boolean hasPossibleTreatmentPairs(final int cvTermId, final int propertyId,
			final List<Integer> hiddenFields) {
		try {
			final StringBuilder sqlString = new StringBuilder().append("SELECT count(c.cvterm_id) ")
					.append(" FROM cvterm c ").append(" INNER JOIN cvterm_relationship pr ON pr.type_id = ")
					.append(TermId.HAS_PROPERTY.getId()).append("   AND pr.subject_id = c.cvterm_id ")
					.append("   AND pr.object_id = ").append(propertyId)
					.append(" INNER JOIN cvterm_relationship sr ON sr.type_id = ").append(TermId.HAS_SCALE.getId())
					.append("   AND sr.subject_id = c.cvterm_id ")
					.append(" INNER JOIN cvterm_relationship mr ON mr.type_id = ").append(TermId.HAS_METHOD.getId())
					.append("   AND mr.subject_id = c.cvterm_id ")
					.append(" INNER JOIN cvtermprop cvprop ON cvprop.type_id = ").append(TermId.VARIABLE_TYPE.getId())
					.append("   AND cvprop.cvterm_id = c.cvterm_id AND cvprop.value = '")
					.append(VariableType.TREATMENT_FACTOR.getName()).append("' WHERE c.cvterm_id <> ").append(cvTermId)
					.append("   AND c.cvterm_id NOT IN (:hiddenFields) ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString());
			query.setParameterList("hiddenFields", hiddenFields);
			final long count = ((BigInteger) query.uniqueResult()).longValue();
			return count > 0;

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllPossibleTreatmentPairs in CVTermDao: " + e.getMessage(), e);
		}
		return false;
	}

	public List<StandardVariable> getAllPossibleTreatmentPairs(final int cvTermId, final int propertyId,
			final List<Integer> hiddenFields) {

		final List<StandardVariable> list = new ArrayList<>();

		try {
			final StringBuilder sqlString = new StringBuilder()
					.append("SELECT c.cvterm_id, c.name, c.definition, pr.object_id AS propertyId, sr.object_id AS scaleId, mr.object_id AS methodId ")
					.append(" FROM cvterm c ").append(" INNER JOIN cvterm_relationship pr ON pr.type_id = ")
					.append(TermId.HAS_PROPERTY.getId())
					.append("   AND pr.subject_id = c.cvterm_id and pr.object_id = ").append(propertyId)
					.append(" INNER JOIN cvterm_relationship sr ON sr.type_id = ").append(TermId.HAS_SCALE.getId())
					.append("   AND sr.subject_id = c.cvterm_id ")
					.append(" INNER JOIN cvterm_relationship mr ON mr.type_id = ").append(TermId.HAS_METHOD.getId())
					.append("   AND mr.subject_id = c.cvterm_id ")
					.append(" INNER JOIN cvtermprop cvprop ON cvprop.type_id = ").append(TermId.VARIABLE_TYPE.getId())
					.append("   AND cvprop.cvterm_id = c.cvterm_id AND cvprop.value = '")
					.append(VariableType.TREATMENT_FACTOR.getName()).append("' WHERE c.cvterm_id <> ").append(cvTermId)
					.append("   AND c.cvterm_id NOT IN (:hiddenFields) ");

			final SQLQuery query = this.getSession().createSQLQuery(sqlString.toString()).addScalar("cvterm_id")
					.addScalar("name").addScalar("definition").addScalar("propertyId").addScalar("scaleId")
					.addScalar("methodId");

			query.setParameterList("hiddenFields", hiddenFields);

			final List<Object[]> results = query.list();
			for (final Object[] row : results) {
				final StandardVariable variable = new StandardVariable();
				variable.setId((Integer) row[0]);
				variable.setName((String) row[1]);
				variable.setDescription((String) row[2]);
				variable.setProperty(new Term((Integer) row[3], null, null));
				variable.setScale(new Term((Integer) row[4], null, null));
				variable.setMethod(new Term((Integer) row[5], null, null));
				list.add(variable);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllPossibleTreatmentPairs in CVTermDao: " + e.getMessage(), e);
		}

		return list;
	}

	public List<Scale> getAllInventoryScales() {
		final List<Scale> list = new ArrayList<>();
		try {
			final StringBuilder sql = this.buildQueryForInventoryScales();

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString()).addScalar("id")
					.addScalar("scalename").addScalar("methodname").addScalar("name").addScalar("definition");
			final List<Object[]> result = query.list();
			if (result != null && !result.isEmpty()) {
				for (final Object[] row : result) {
					final String displayName = row[1] + " - " + row[2];

					final Scale scale = new Scale(new Term((Integer) row[0], row[3].toString(), row[4].toString()));
					scale.setDisplayName(displayName);
					list.add(scale);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllInventoryScales in CVTermDao: " + e.getMessage(), e);
		}
		return list;
	}

	public Scale getInventoryScaleByName(final String name) {
		Scale scale = new Scale();
		try {
			final StringBuilder sql = this.buildQueryForInventoryScales();
			sql.append(" AND prs.name = :name");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString()).addScalar("id")
					.addScalar("scalename").addScalar("methodname").addScalar("name").addScalar("definition");
			query.setParameter("name", name);
			final Object[] result = (Object[]) query.uniqueResult();
			if (result != null) {
				final String displayName = result[1] + " - " + result[2];
				scale = new Scale(new Term((Integer) result[0], result[3].toString(), result[4].toString()));
				scale.setDisplayName(displayName);
				return scale;
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllInventoryScales in CVTermDao: " + e.getMessage(), e);
		}
		return scale;
	}

	private StringBuilder buildQueryForInventoryScales() {
		final StringBuilder sql = new StringBuilder()
				.append("SELECT pr.subject_id AS id, s.name AS scalename, m.name AS methodname, prs.name as name, prs.definition as definition ")
				.append(" FROM cvterm_relationship pr ")
				.append(" INNER JOIN cvterm_relationship mr ON mr.subject_id = pr.subject_id ")
				.append("    AND mr.type_id = ").append(TermId.HAS_METHOD.getId())
				.append(" INNER JOIN cvterm m ON m.cvterm_id = mr.object_id ")
				.append(" INNER JOIN cvterm_relationship sr ON sr.subject_id = pr.subject_id ")
				.append("    AND sr.type_id = ").append(TermId.HAS_SCALE.getId())
				.append(" INNER JOIN cvterm s ON s.cvterm_id = sr.object_id ")
				.append(" INNER JOIN cvterm prs ON prs.cvterm_id = pr.subject_id ").append(" WHERE pr.type_id = ")
				.append(TermId.HAS_PROPERTY.getId()).append("    AND pr.object_id = ")
				.append(TermId.INVENTORY_AMOUNT_PROPERTY.getId());
		return sql;
	}

	/*-------------------------    AREA FOR USED/CREATED METHOD FOR BMS-36:ONTOLOGY MANAGER REDESIGN -------------------------- */

	public List<CVTerm> getAllByCvId(final Integer cvId, final boolean filterObsolete) {

		List<CVTerm> terms;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("cvId", cvId));
			if (filterObsolete) {
				criteria.add(Restrictions.eq("isObsolete", 0));
			}
			criteria.addOrder(Order.asc("name"));

			terms = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getAllByCvId=" + cvId + " query on CVTermDao", e);
		}

		return terms;
	}

	public List<CVTerm> getAllByCvId(final CvId cvId, final boolean filterObsolete) {
		return this.getAllByCvId(cvId.getId(), filterObsolete);
	}

	public List<CVTerm> getAllByCvId(final List<Integer> termIds, final CvId cvId, final boolean filterObsolete) {

		List<CVTerm> terms;

		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.in("cvTermId", termIds));
			criteria.add(Restrictions.eq("cvId", cvId.getId()));
			if (filterObsolete) {
				criteria.add(Restrictions.eq("isObsolete", 0));
			}
			criteria.addOrder(Order.asc("name"));

			terms = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error at getByCvId=" + cvId + " query on CVTermDao", e);
		}

		return terms;
	}

	public CVTerm save(final String name, final String definition, final CvId cvId) {
		final CVTerm cvTerm = new CVTerm();
		cvTerm.setCv(cvId.getId());
		cvTerm.setName(name);
		cvTerm.setDefinition(definition);
		cvTerm.setIsObsolete(false);
		cvTerm.setIsRelationshipType(false);
		return this.save(cvTerm);
	}

}
