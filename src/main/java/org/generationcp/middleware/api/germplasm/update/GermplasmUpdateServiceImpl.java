package org.generationcp.middleware.api.germplasm.update;

import org.generationcp.middleware.api.breedingmethod.BreedingMethodDTO;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.service.api.userdefinedfield.UserDefinedFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class GermplasmUpdateServiceImpl implements GermplasmUpdateService {

	@Autowired
	private UserDefinedFieldService userDefinedFieldService;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private BreedingMethodService breedingMethodService;

	private final DaoFactory daoFactory;

	public GermplasmUpdateServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void saveGermplasmUpdates(final int userId, final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		final GermplasmDAO germplasmDAO = this.daoFactory.getGermplasmDao();
		final NameDAO nameDAO = this.daoFactory.getNameDao();
		final AttributeDAO attributeDAO = this.daoFactory.getAttributeDAO();

		final Set<String> attributesAndNamesCodes = germplasmUpdateDTOList.get(0).getData().keySet();

		// Retrieve the field id of attributes and names
		final Map<String, Integer> attributeCodesFieldNoMap =
			this.userDefinedFieldService
				.getByTableAndCodesInMap(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(), new ArrayList<>(attributesAndNamesCodes));
		final Map<String, Integer> nameCodesFieldNoMap =
			this.userDefinedFieldService
				.getByTableAndCodesInMap(UDTableType.NAMES_NAME.getTable(), new ArrayList<>(attributesAndNamesCodes));

		final Set<Integer> gids = germplasmUpdateDTOList.stream().map(o -> o.getGid()).collect(Collectors.toSet());
		final Set<String> germplasmUUIDs =
			germplasmUpdateDTOList.stream().map(o -> (o.getGid() == null) ? o.getGermplasmUUID() : "").collect(Collectors.toSet());

		final List<Germplasm> germplasmList = new ArrayList<>();
		germplasmList.addAll(germplasmDAO.getByGIDList(new ArrayList<>(gids)));
		germplasmList.addAll(germplasmDAO.getByUUIDList(germplasmUUIDs));

		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap = new HashMap<>();
		for (final GermplasmUpdateDTO germplasmUpdateDTO : germplasmUpdateDTOList) {
			germplasmUpdateDTOMap
				.put(germplasmUpdateDTO.getGid() == null ? germplasmUpdateDTO.getGermplasmUUID() :
						String.valueOf(germplasmUpdateDTO.getGid()),
					germplasmUpdateDTO);
		}

		// Retrieve location and method IDs in one go
		final Map<String, Integer> locationAbbreviationIdMap = this.getLocationAbbreviationIdMap(germplasmUpdateDTOList);
		final Map<String, Integer> methodCodeIdMap = this.getMethodCodeIdMap(germplasmUpdateDTOList);

		// Retrieve the names and attributes associated to GIDs in one go.
		final Map<Integer, List<Name>> namesMap =
			nameDAO.getNamesByGidsInMap(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
		final List<Attribute> attributes =
			attributeDAO.getAttributeValuesGIDList(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
		final Map<Integer, List<Attribute>> attributesMap =
			attributes.stream().collect(Collectors.groupingBy(Attribute::getGermplasmId, LinkedHashMap::new, Collectors.toList()));

		for (final Germplasm germplasm : germplasmList) {
			this.saveGermplasmUpdateDTO(userId, germplasmDAO, nameDAO, attributeDAO, attributeCodesFieldNoMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, methodCodeIdMap, namesMap, attributesMap, germplasm);
		}

	}

	private void saveGermplasmUpdateDTO(final int userId, final GermplasmDAO germplasmDAO, final NameDAO nameDAO,
		final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes, final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Integer> methodCodeIdMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			final int locationId = locationAbbreviationIdMap.get(germplasmUpdateDTO.getLocationAbbreviation());
			final int methodId = methodCodeIdMap.get(germplasmUpdateDTO.getBreedingMethod());
			final int germplasmDate = Integer.parseInt(germplasmUpdateDTO.getCreationDate());

			this.saveGermplasm(germplasmDAO, germplasm, locationId, methodId, germplasmDate);

			for (final Map.Entry<String, String> entryData : germplasmUpdateDTO.getData().entrySet()) {
				// Save or update the Names
				this.saveOrUpdateName(userId, nameDAO, nameCodes, namesMap, germplasm, locationId, germplasmDate, entryData);
				this.saveOrUpdateAttribute(userId, attributeDAO, attributeCodes, attributesMap, germplasm, locationId, germplasmDate,
					entryData);

			}
		}
	}

	private void saveGermplasm(final GermplasmDAO germplasmDAO, final Germplasm germplasm, final int locationId, final int methodId,
		final int germplasmDate) {
		germplasm.setLocationId(locationId);
		germplasm.setMethodId(methodId);
		germplasm.setGdate(germplasmDate);
		germplasmDAO.update(germplasm);
	}

	private void saveOrUpdateName(final int userId, final NameDAO nameDAO, final Map<String, Integer> nameCodes,
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm, final int locationId, final int germplasmDate,
		final Map.Entry<String, String> entryData) {
		if (nameCodes.containsKey(entryData.getKey())) {
			final String nameCode = entryData.getKey();
			final Integer nameTypeId = nameCodes.get(nameCode);

			// TODO: Determine the preferred name
			// TODO: Add check if there are multiple names with same type
			final Optional<Name> optionalName =
				namesMap.get(germplasm.getGid()).stream().filter(n -> n.getTypeId().equals(nameTypeId)).findFirst();
			if (optionalName.isPresent()) {
				final Name name = optionalName.get();
				name.setNval(entryData.getValue());
				nameDAO.update(name);
			} else {
				nameDAO.save(new Name(null, germplasm.getGid(), nameTypeId, 0, userId,
					entryData.getValue(), locationId, germplasmDate, 0));
			}
		}
	}

	private void saveOrUpdateAttribute(final int userId, final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final int locationId, final int germplasmDate,
		final Map.Entry<String, String> entryData) {
		// Save or update the Attributes
		if (attributeCodes.containsKey(entryData.getKey())) {
			final String attributeCode = entryData.getKey();
			final Integer attributeTypeId = attributeCodes.get(attributeCode);

			// TODO: Add check if there are multiple attributes with same type
			final Optional<Attribute> optionalAttribute =
				attributesMap.get(germplasm.getGid()).stream().filter(n -> n.getTypeId().equals(attributeTypeId)).findFirst();
			if (optionalAttribute.isPresent()) {
				final Attribute attribute = optionalAttribute.get();
				attribute.setAval(entryData.getValue());
				attributeDAO.update(attribute);
			} else {
				attributeDAO
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, userId, entryData.getValue(), locationId,
						0, germplasmDate));
			}
		}
	}

	private Optional<GermplasmUpdateDTO> getGermplasmUpdateDTOByGidOrUUID(final Germplasm germplasm,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap) {
		if (germplasmUpdateDTOMap.containsKey(String.valueOf(germplasm.getGid()))) {
			return Optional.of(germplasmUpdateDTOMap.get(String.valueOf(germplasm.getGid())));
		} else if (germplasmUpdateDTOMap.containsKey(germplasm.getGermplasmUUID())) {
			return Optional.of(germplasmUpdateDTOMap.get(germplasm.getGermplasmUUID()));
		}
		return Optional.empty();
	}

	private Map<String, Integer> getLocationAbbreviationIdMap(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> locationAbbreviationSet =
			germplasmUpdateDTOList.stream().map(dto -> dto.getLocationAbbreviation()).collect(Collectors.toSet());
		return
			this.locationDataManager.getLocationsByAbbreviation(locationAbbreviationSet).stream()
				.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Integer> getMethodCodeIdMap(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> breedingMethodCodes =
			germplasmUpdateDTOList.stream().map(dto -> dto.getBreedingMethod()).collect(Collectors.toSet());
		return this.breedingMethodService.getBreedingMethodsByCodes(breedingMethodCodes).stream()
			.collect(Collectors.toMap(BreedingMethodDTO::getCode, BreedingMethodDTO::getId));
	}

}
