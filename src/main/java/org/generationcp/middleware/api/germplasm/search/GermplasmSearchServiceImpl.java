package org.generationcp.middleware.api.germplasm.search;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class GermplasmSearchServiceImpl implements GermplasmSearchService {

	private final DaoFactory daoFactory;

	public GermplasmSearchServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmSearchResponse> searchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable,
		final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().searchGermplasm(germplasmSearchRequest, pageable, programUUID);
	}

	@Override
	public long countSearchGermplasm(final GermplasmSearchRequest germplasmSearchRequest, final String programUUID) {
		return this.daoFactory.getGermplasmSearchDAO().countSearchGermplasm(germplasmSearchRequest, programUUID);
	}

	@Override
	public List<UserDefinedField> getGermplasmNameTypes(final GermplasmSearchRequest germplasmSearchRequest) {
		return this.daoFactory.getGermplasmSearchDAO().getGermplasmNameTypes(germplasmSearchRequest);
	}

	@Override
	public Map<Integer, Map<Integer, String>> getGermplasmAttributeValues(final GermplasmSearchRequest germplasmSearchRequest) {
		final List<Attribute> attributes = this.daoFactory.getGermplasmSearchDAO().getGermplasmAttributeValues(germplasmSearchRequest);

		final HashMap<Integer, Map<Integer, String>> attributeMapByGid = new HashMap<>();
		for (final Attribute attribute : attributes) {
			Map<Integer, String> attrByType = attributeMapByGid.get(attribute.getGermplasmId());
			if (attrByType == null) {
				attrByType = new HashMap<>();
			}
			attrByType.put(attribute.getTypeId(), attribute.getAval());
			attributeMapByGid.put(attribute.getGermplasmId(), attrByType);
		}
		return attributeMapByGid;
	}

	@Override
	public Map<Integer, Map<Integer, String>> getGermplasmNameValues(final GermplasmSearchRequest germplasmSearchRequest) {
		final List<Name> names = this.daoFactory.getGermplasmSearchDAO().getGermplasmNameValues(germplasmSearchRequest);

		final HashMap<Integer, Map<Integer, String>> nameMapByGid = new HashMap<>();
		for (final Name name : names) {
			Map<Integer, String> nameByType = nameMapByGid.get(name.getGermplasm().getGid());
			if (nameByType == null) {
				nameByType = new HashMap<>();
			}
			nameByType.put(name.getTypeId(), name.getNval());
			nameMapByGid.put(name.getGermplasm().getGid(), nameByType);
		}
		return nameMapByGid;
	}
}
