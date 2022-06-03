package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmAttributeDto;
import org.generationcp.middleware.domain.germplasm.AttributeRequestDto;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
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
	public Integer createGermplasmAttribute(final Integer gid, final AttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		return this.daoFactory.getAttributeDAO().createAttribute(gid, dto, variable);
	}

	@Override
	public void updateGermplasmAttribute(final Integer attributeId, final AttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		this.daoFactory.getAttributeDAO().updateAttribute(attributeId, dto, variable);
	}

	@Override
	public void deleteGermplasmAttribute(final Integer attributeId) {
		this.daoFactory.getAttributeDAO().deleteAttribute(attributeId);
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
