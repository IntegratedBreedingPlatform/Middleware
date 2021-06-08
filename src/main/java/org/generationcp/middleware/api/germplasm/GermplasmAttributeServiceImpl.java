package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

public class GermplasmAttributeServiceImpl implements GermplasmAttributeService {

	private final DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	public GermplasmAttributeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmAttributeDto> getGermplasmAttributeDtos(final Integer gid, final Integer variableTypeId) {
		return this.daoFactory.getAttributeDAO().getGermplasmAttributeDtos(gid, variableTypeId);
	}

	@Override
	public Integer createGermplasmAttribute(final Integer gid, final GermplasmAttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		final Attribute newAttribute = new Attribute();
		newAttribute.setGermplasmId(gid);
		newAttribute.setTypeId(dto.getVariableId());
		newAttribute.setAval(dto.getValue());
		newAttribute.setLocationId(dto.getLocationId());
		newAttribute.setReferenceId(0);
		newAttribute.setAdate(Integer.valueOf(dto.getDate()));
		newAttribute.setcValueId(this.resolveCategoricalValueId(variable, dto.getValue()));
		return this.daoFactory.getAttributeDAO().save(newAttribute).getAid();
	}

	@Override
	public void updateGermplasmAttribute(final Integer attributeId, final GermplasmAttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		attribute.setAval(dto.getValue());
		attribute.setLocationId(dto.getLocationId());
		attribute.setAdate(Integer.valueOf(dto.getDate()));
		attribute.setcValueId(this.resolveCategoricalValueId(variable, dto.getValue()));
		this.daoFactory.getAttributeDAO().update(attribute).getAid();
	}

	@Override
	public void deleteGermplasmAttribute(final Integer attributeId) {
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		this.daoFactory.getAttributeDAO().makeTransient(attribute);
	}

	private Integer resolveCategoricalValueId(final Variable variable, final String value) {
		Integer categoricalValueId = null;
		if (variable.getScale().getDataType().getId().equals(TermId.CATEGORICAL_VARIABLE.getId())) {
			categoricalValueId =
				variable.getScale().getCategories().stream().filter(category -> value.equalsIgnoreCase(category.getName())).findFirst()
					.map(category -> BigInteger.valueOf(category.getId()).intValue()).orElse(null);
			if (categoricalValueId == null) {
				throw new MiddlewareRequestException("", "germplasm.attribute.invalid.categorical.value", variable.getName());
			}
		}
		return categoricalValueId;
	}

}
