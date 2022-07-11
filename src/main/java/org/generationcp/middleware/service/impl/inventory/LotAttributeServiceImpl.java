package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.shared.AttributeDto;
import org.generationcp.middleware.domain.shared.AttributeRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.service.api.inventory.LotAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public class LotAttributeServiceImpl implements LotAttributeService {

	private final DaoFactory daoFactory;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	public LotAttributeServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<AttributeDto> getLotAttributeDtos(final Integer lotId, final String programUUID) {
		return this.daoFactory.getLotAttributeDAO().getLotAttributeDtos(lotId, programUUID);
	}

	@Override
	public Integer createLotAttribute(final Integer lotId, final AttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		return this.daoFactory.getLotAttributeDAO().createAttribute(lotId, dto, variable);
	}

	@Override
	public Map<Integer, Map<Integer, String>> getAttributesByLotIdsMap(final List<Integer> lotIds) {
		return this.daoFactory.getLotAttributeDAO().getAttributesByLotIdsMap(lotIds);
	}

	@Override
	public void updateLotAttribute(final Integer attributeId, final AttributeRequestDto dto) {
		final Variable variable = this.ontologyVariableDataManager.getVariable(null, dto.getVariableId(), false);
		this.daoFactory.getLotAttributeDAO().updateAttribute(attributeId, dto, variable);
	}

	@Override
	public void deleteLotAttribute(final Integer attributeId) {
		this.daoFactory.getLotAttributeDAO().deleteAttribute(attributeId);
	}

	@Override
	public List<Variable> getLotAttributeVariables(final List<Integer> lotIds, final String programUUID) {
		return this.daoFactory.getLotAttributeDAO().getLotAttributeVariables(lotIds, programUUID);
	}

}
