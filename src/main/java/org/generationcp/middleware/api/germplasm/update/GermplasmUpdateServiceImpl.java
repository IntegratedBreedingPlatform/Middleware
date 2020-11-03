package org.generationcp.middleware.api.germplasm.update;

import org.generationcp.middleware.api.breedingmethod.BreedingMethodDTO;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.exceptions.GermplasmUpdateConflictException;
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

@Transactional(rollbackFor = GermplasmUpdateConflictException.class)
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
	public void saveGermplasmUpdates(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) throws
		GermplasmUpdateConflictException {

		final List<String> conflictErrors = new ArrayList<>();
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
			this.saveGermplasmUpdateDTO(germplasmDAO, nameDAO, attributeDAO, attributeCodesFieldNoMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, methodCodeIdMap, namesMap, attributesMap, germplasm, conflictErrors);
		}

		if (!conflictErrors.isEmpty()) {
			throw new GermplasmUpdateConflictException(conflictErrors);
		}

	}

	private void saveGermplasmUpdateDTO(final GermplasmDAO germplasmDAO, final NameDAO nameDAO,
		final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes, final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Integer> methodCodeIdMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final List<String> conflictErrors) {
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
				this.saveOrUpdateName(nameDAO, nameCodes, namesMap, germplasm, entryData,
					conflictErrors);
				this.saveOrUpdateAttribute(attributeDAO, attributeCodes, attributesMap, germplasm,
					entryData, conflictErrors);

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

	private void saveOrUpdateName(final NameDAO nameDAO, final Map<String, Integer> nameCodes,
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm,
		final Map.Entry<String, String> entryData, final List<String> conflictErrors) {
		if (nameCodes.containsKey(entryData.getKey())) {
			final String nameCode = entryData.getKey();
			final Integer nameTypeId = nameCodes.get(nameCode);

			// TODO: Update Reference
			// TODO: Determine the preferred name
			final List<Name> germplasmNames = namesMap.get(germplasm.getGid());
			final List<Name> namesByType =
				germplasmNames.stream().filter(n -> n.getTypeId().equals(nameTypeId)).collect(Collectors.toList());

			// Check if there are multiple names with same type
			if (namesByType.size() > 1) {
				conflictErrors.add(String.format(
					"Multiple names for the %s where found for germplasm %s. Name cannot be updated via this batch process.",
					nameCode, germplasm.getGid()));
			} else if (namesByType.size() == 1) {
				// Update if name is existing
				final Name name = namesByType.get(0);
				name.setNval(entryData.getValue());
				nameDAO.update(name);
			} else {
				// Create new record if name not yet exists
				nameDAO.save(new Name(null, germplasm.getGid(), nameTypeId, 0, germplasm.getUserId(),
					entryData.getValue(), germplasm.getLocationId(), germplasm.getGdate(), 0));
			}
		}
	}

	private void saveOrUpdateAttribute(final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final Map.Entry<String, String> entryData, final List<String> conflictErrors) {
		// Save or update the Attributes
		if (attributeCodes.containsKey(entryData.getKey())) {
			final String attributeCode = entryData.getKey();
			final Integer attributeTypeId = attributeCodes.get(attributeCode);
			final List<Attribute> germplasmAttributes = attributesMap.get(germplasm.getGid());
			final List<Attribute> attributesByType =
				germplasmAttributes.stream().filter(n -> n.getTypeId().equals(attributeTypeId)).collect(Collectors.toList());

			// Check if there are multiple attributes with same type
			if (attributesByType.size() > 1) {
				conflictErrors.add(String.format(
					"Multiple attribute for the %s where found for germplasm %s. Attribute cannot be updated via this batch process.",
					attributeCode, germplasm.getGid()));
			} else if (attributesByType.size() == 1) {
				final Attribute attribute = attributesByType.get(0);
				attribute.setLocationId(germplasm.getLocationId());
				attribute.setUserId(germplasm.getUserId());
				attribute.setAdate(germplasm.getGdate());
				attribute.setAval(entryData.getValue());
				attributeDAO.update(attribute);
			} else {
				attributeDAO
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, germplasm.getGid(), entryData.getValue(),
						germplasm.getLocationId(),
						0, germplasm.getGdate()));
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
