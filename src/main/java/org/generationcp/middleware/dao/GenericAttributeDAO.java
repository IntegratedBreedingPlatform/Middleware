/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p>
 * Generation Challenge Programme (GCP)
 * <p>
 * <p>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GenericAttribute;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.util.VariableValueUtil;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GenericAttributeDAO<T extends GenericAttribute> extends GenericDAO<T, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericAttributeDAO.class);

	private static final String VARIABLE_ID = "vid";
	private static final String VARIABLE_NAME = "vn";
	private static final String VARIABLE_DEFINITION = "vd";

	private static final String METHOD_ID = "mid";
	private static final String METHOD_NAME = "mn";
	private static final String METHOD_DEFINITION = "md";

	private static final String PROPERTY_ID = "pid";
	private static final String PROPERTY_NAME = "pn";
	private static final String PROPERTY_DEFINITION = "pd";

	private static final String SCALE_ID = "sid";
	private static final String SCALE_NAME = "sn";
	private static final String SCALE_DEFINITION = "sd";

	private static final String VARIABLE_ALIAS = "p_alias";
	private static final String VARIABLE_EXPECTED_MAX = "p_max_value";
	private static final String VARIABLE_EXPECTED_MIN = "p_min_value";

	private static final String PROGRAM_UUID = "programUUID";
	private static final String RECORD_IDS = "recordIds";
	private static final String VARIABLE_TYPE_IDS = "variableTypeIds";

	private static final String GET_ATTRIBUTE_VARIABLES_QUERY = "select "
		+ " v.cvterm_id AS " + VARIABLE_ID + ", "  //
		+ " v.name AS " + VARIABLE_NAME + ", "  //
		+ " v.definition AS " + VARIABLE_DEFINITION + ", "  //
		+ " vmr.mid AS " + METHOD_ID + ", "  //
		+ " vmr.mn AS " + METHOD_NAME + ", "  //
		+ " vmr.md AS " + METHOD_DEFINITION + ", "  //
		+ " vpr.pid AS " + PROPERTY_ID + ", "  //
		+ " vpr.pn AS " + PROPERTY_NAME + ", "  //
		+ " vpr.pd AS " + PROPERTY_DEFINITION + ", "  //
		+ " vsr.sid AS " + SCALE_ID + ", "  //
		+ " vsr.sn AS " + SCALE_NAME + ", "  //
		+ " vsr.sd AS " + SCALE_DEFINITION + ", "  //
		+ " vpo.alias  AS " + VARIABLE_ALIAS + ", "  //
		+ " vpo.expected_min AS " + VARIABLE_EXPECTED_MIN + ", "  //
		+ " vpo.expected_max AS " + VARIABLE_EXPECTED_MAX
		+ " FROM cvterm v INNER JOIN cvtermprop cp ON cp.type_id = " + TermId.VARIABLE_TYPE.getId()
		+ " and v.cvterm_id = cp.cvterm_id "
		+ " INNER JOIN cvterm varType on varType.name = cp.value AND varType.cvterm_id in (:"
		+ VARIABLE_TYPE_IDS + ") "
		+ " left join (select mr.subject_id vid, m.cvterm_id mid, m.name mn, m.definition md from cvterm_relationship mr inner join cvterm m on m.cvterm_id = mr.object_id and mr.type_id = "
		+ TermRelationshipId.HAS_METHOD.getId() + ") vmr on vmr.vid = v.cvterm_id "
		+ " left join (select pr.subject_id vid, p.cvterm_id pid, p.name pn, p.definition pd from cvterm_relationship pr inner join cvterm p on p.cvterm_id = pr.object_id and pr.type_id = "
		+ TermRelationshipId.HAS_PROPERTY.getId() + ") vpr on vpr.vid = v.cvterm_id "
		+ " left join (select sr.subject_id vid, s.cvterm_id sid, s.name sn, s.definition sd from cvterm_relationship sr inner join cvterm s on s.cvterm_id = sr.object_id and sr.type_id = "
		+ TermRelationshipId.HAS_SCALE.getId() + ") vsr on vsr.vid = v.cvterm_id "
		+ " left join variable_overrides vpo ON vpo.cvterm_id = v.cvterm_id AND vpo.program_uuid = :" + PROGRAM_UUID;

	private static final String GET_ATTRIBUTE_VARIABLES_GROUP_BY = " group by v.cvterm_id";

	public Integer createAttribute(final Integer mainRecordId, final AttributeRequestDto dto, final Variable variable) {
		final GenericAttribute newAttribute = (GenericAttribute) this.getNewAttributeInstance(mainRecordId);
		newAttribute.setTypeId(dto.getVariableId());
		newAttribute.setAval(dto.getValue());
		newAttribute.setLocationId(dto.getLocationId());
		newAttribute.setReferenceId(0);
		newAttribute.setAdate(Integer.valueOf(dto.getDate()));
		newAttribute.setcValueId(VariableValueUtil.resolveCategoricalValueId(variable, dto.getValue()));
		return this.save((T) newAttribute).getAid();
	}

	public void updateAttribute(final Integer attributeId, final AttributeRequestDto dto, final Variable variable) {
		final GenericAttribute attribute = this.getById(attributeId);
		attribute.setAval(dto.getValue());
		attribute.setLocationId(dto.getLocationId());
		attribute.setAdate(Integer.valueOf(dto.getDate()));
		attribute.setcValueId(VariableValueUtil.resolveCategoricalValueId(variable, dto.getValue()));
		this.update((T) attribute).getAid();
	}

	public void deleteAttribute(final Integer attributeId) {
		final GenericAttribute attribute = this.getById(attributeId);
		this.makeTransient((T) attribute);
	}

	public void addQueryScalars(final SQLQuery sqlQuery) {
		sqlQuery.addScalar("id");
		sqlQuery.addScalar("variableId");
		sqlQuery.addScalar("value");
		sqlQuery.addScalar("variableName");
		sqlQuery.addScalar("variableTypeName");
		sqlQuery.addScalar("variableDescription");
		sqlQuery.addScalar("date");
		sqlQuery.addScalar("locationId");
		sqlQuery.addScalar("locationName");
		sqlQuery.addScalar("hasFiles", new BooleanType());
	}

	public long countByVariables(final List<Integer> variablesIds) {
		try {
			final SQLQuery query =
				this.getSession().createSQLQuery(this.getCountAttributeWithVariablesQuery());
			query.setParameterList("variableIds", variablesIds);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countByVariables=" + variablesIds + " in GenericAttributeDAO: " + e.getMessage();
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	/**
	 * Given a list of Records return the Variables related with type specified.
	 * The programUUID is used to return the expected range and alias of the program if it exists.
	 *
	 * @param List            of record Ids
	 * @param programUUID     program's unique id
	 * @param variableTypeIds to check
	 * @return List of Variable or empty list if none found
	 */
	public List<Variable> getAttributeVariables(final List<Integer> recordIds, final String programUUID,
		final List<Integer> variableTypeIds) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append(GET_ATTRIBUTE_VARIABLES_QUERY);
			queryString.append(" inner join " + this.getAttributeTable() + " a  on a.atype = v.cvterm_id "
				+ " WHERE "
				+ " a." + this.getForeignKeyToMainRecord() + " in (:" + RECORD_IDS + ")");
			queryString.append(GET_ATTRIBUTE_VARIABLES_GROUP_BY);
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());

			sqlQuery.setParameter(PROGRAM_UUID, programUUID);
			sqlQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			sqlQuery.addScalar(VARIABLE_ID);
			sqlQuery.addScalar(VARIABLE_NAME);
			sqlQuery.addScalar(VARIABLE_DEFINITION);
			sqlQuery.addScalar(METHOD_ID);
			sqlQuery.addScalar(METHOD_NAME);
			sqlQuery.addScalar(METHOD_DEFINITION);
			sqlQuery.addScalar(PROPERTY_ID);
			sqlQuery.addScalar(PROPERTY_NAME);
			sqlQuery.addScalar(PROPERTY_DEFINITION);
			sqlQuery.addScalar(SCALE_ID);
			sqlQuery.addScalar(SCALE_NAME);
			sqlQuery.addScalar(SCALE_DEFINITION);
			sqlQuery.addScalar(VARIABLE_ALIAS);
			sqlQuery.addScalar(VARIABLE_EXPECTED_MIN);
			sqlQuery.addScalar(VARIABLE_EXPECTED_MAX);
			sqlQuery.setParameterList(RECORD_IDS, recordIds);
			sqlQuery.setParameterList(VARIABLE_TYPE_IDS, variableTypeIds);

			final List<Map<String, Object>> queryResults = (List<Map<String, Object>>) sqlQuery.list();
			final List<Variable> variables = new ArrayList<>();
			for (final Map<String, Object> item : queryResults) {
				final Variable variable =
					new Variable(new Term(Util.typeSafeObjectToInteger(item.get(VARIABLE_ID)),
						(String) item.get(VARIABLE_NAME), (String) item.get(VARIABLE_DEFINITION)));
				variable.setMethod(new Method(
					new Term(Util.typeSafeObjectToInteger(item.get(METHOD_ID)), (String) item.get(METHOD_NAME),
						(String) item.get(METHOD_DEFINITION))));
				variable.setProperty(new Property(new Term(Util.typeSafeObjectToInteger(item.get(PROPERTY_ID)),
					(String) item.get(PROPERTY_NAME), (String) item.get(PROPERTY_DEFINITION))));
				variable.setScale(new Scale(
					new Term(Util.typeSafeObjectToInteger(item.get(SCALE_ID)), (String) item.get(SCALE_NAME),
						(String) item.get(SCALE_DEFINITION))));

				// Alias, Expected Min Value, Expected Max Value
				final String pAlias = (String) item.get(VARIABLE_ALIAS);
				final String pExpMin = (String) item.get(VARIABLE_EXPECTED_MIN);
				final String pExpMax = (String) item.get(VARIABLE_EXPECTED_MAX);

				variable.setAlias(pAlias);
				variable.setMinValue(pExpMin);
				variable.setMaxValue(pExpMax);

				variables.add(variable);
			}
			return variables;
		} catch (final HibernateException e) {
			final String message =
				"Error with getAttributeVariables(record IDs=" + recordIds + ") : " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	protected abstract T getNewAttributeInstance(Integer Id);

	protected abstract String getCountAttributeWithVariablesQuery();

	protected abstract String getAttributeTable();

	protected abstract String getForeignKeyToMainRecord();
}
