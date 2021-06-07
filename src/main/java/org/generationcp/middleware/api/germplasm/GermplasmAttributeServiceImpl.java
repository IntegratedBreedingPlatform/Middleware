package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;

import java.util.List;

public class GermplasmAttributeServiceImpl implements GermplasmAttributeService {

	private final DaoFactory daoFactory;

	public GermplasmAttributeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmAttributeDto> getGermplasmAttributeDtos(final Integer gid, final Integer variableTypeId) {
		return this.daoFactory.getAttributeDAO().getGermplasmAttributeDtos(gid, variableTypeId);
	}

	@Override
	public Integer createGermplasmAttribute(final Integer gid, final GermplasmAttributeRequestDto dto, final Integer userId) {
		final Attribute newAttribute = new Attribute();
		newAttribute.setGermplasmId(gid);
		newAttribute.setTypeId(dto.getVariableId());
		newAttribute.setAval(dto.getValue());
		newAttribute.setLocationId(dto.getLocationId());
		newAttribute.setReferenceId(0);
		newAttribute.setAdate(Integer.valueOf(dto.getDate()));
		return this.daoFactory.getAttributeDAO().save(newAttribute).getAid();
	}

	@Override
	public void updateGermplasmAttribute(final Integer attributeId, final GermplasmAttributeRequestDto dto) {
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		attribute.setAval(dto.getValue());
		attribute.setLocationId(dto.getLocationId());
		attribute.setAdate(Integer.valueOf(dto.getDate()));
		this.daoFactory.getAttributeDAO().update(attribute).getAid();
	}

	@Override
	public void deleteGermplasmAttribute(final Integer attributeId) {
		final Attribute attribute = this.daoFactory.getAttributeDAO().getById(attributeId);
		this.daoFactory.getAttributeDAO().makeTransient(attribute);
	}

}
