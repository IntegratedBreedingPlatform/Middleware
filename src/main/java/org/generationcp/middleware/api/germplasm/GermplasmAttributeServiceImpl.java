package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.util.VariableValueUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GermplasmAttributeServiceImpl implements GermplasmAttributeService {

	private final DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	public GermplasmAttributeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	//FIXME Should receive a filter object, to be addressed in IBP-4659
	public List<GermplasmAttributeDto> getGermplasmAttributeDtos(final Integer gid, final Integer variableTypeId, final String programUUID) {
		return this.daoFactory.getAttributeDAO().getGermplasmAttributeDtos(gid, variableTypeId, programUUID);
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
		newAttribute.setcValueId(VariableValueUtil.resolveCategoricalValueId(variable, dto.getValue()));
		return this.daoFactory.getAttributeDAO().save(newAttribute).getAid();
	}

	@Override
	public void updateGermplasmAttribute(final Integer attributeId, final GermplasmAttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		attribute.setAval(dto.getValue());
		attribute.setLocationId(dto.getLocationId());
		attribute.setAdate(Integer.valueOf(dto.getDate()));
		attribute.setcValueId(VariableValueUtil.resolveCategoricalValueId(variable, dto.getValue()));
		this.daoFactory.getAttributeDAO().update(attribute).getAid();
	}

	@Override
	public void deleteGermplasmAttribute(final Integer attributeId) {
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		this.daoFactory.getAttributeDAO().makeTransient(attribute);
	}

}
