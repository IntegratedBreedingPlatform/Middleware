package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.util.VariableValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public class GermplasmAttributeServiceImpl implements GermplasmAttributeService {

	private final DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	public GermplasmAttributeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	//FIXME Should receive a filter object, to be addressed in IBP-4765
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

	@Override
	public List<AttributeDTO> getAttributesByGUID(
		final String germplasmUUID, final List<String> attributeDbIds, final Pageable pageable) {
		return this.daoFactory.getAttributeDAO().getAttributesByGUIDAndAttributeIds(germplasmUUID, attributeDbIds, pageable);
	}

	@Override
	public long countAttributesByGUID(final String gemrplasmUUID, final List<String> attributeDbIds) {
		return this.daoFactory.getAttributeDAO().countAttributesByGUID(gemrplasmUUID, attributeDbIds);
	}

	@Override
	public List<Variable> getGermplasmAttributeVariables(final List<Integer> gids, final String programUUID) {
		return this.daoFactory.getGermplasmDao().getGermplasmAttributeVariables(gids, programUUID);
	}

	@Override
	public Map<Integer, List<AttributeDTO>> getAttributesByGIDsMap(final List<Integer> gids) {
		return this.daoFactory.getAttributeDAO().getAttributesByGidsMap(gids);
	}

	@Override
	public boolean isLocationUsedInAttribute(final Integer locationId) {
		return this.daoFactory.getAttributeDAO().isLocationUsedInAttribute(locationId);
	}

}
