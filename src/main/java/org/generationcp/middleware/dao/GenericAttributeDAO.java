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

import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.pojos.GenericAttribute;
import org.generationcp.middleware.util.VariableValueUtil;
import org.hibernate.SQLQuery;
import org.hibernate.type.BooleanType;

public abstract class GenericAttributeDAO<T extends GenericAttribute> extends GenericDAO<T, Integer> {

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

	protected abstract T getNewAttributeInstance(Integer Id);
}
