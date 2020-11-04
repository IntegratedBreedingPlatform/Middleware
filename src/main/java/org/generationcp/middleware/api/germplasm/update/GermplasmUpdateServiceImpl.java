package org.generationcp.middleware.api.germplasm.update;

import liquibase.util.StringUtils;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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

		// Get the codes specified in the headers as well as the codes specified in the preferred name column.
		final Set<String> attributesAndNamesCodes = new HashSet<>(germplasmUpdateDTOList.get(0).getData().keySet());
		attributesAndNamesCodes
			.addAll(germplasmUpdateDTOList.stream().map(o -> o.getPreferredName()).filter(Objects::nonNull).collect(Collectors.toSet()));

		// Retrieve the field id of attributes and names
		final Map<String, Integer> attributeCodesFieldNoMap =
			this.userDefinedFieldService
				.getByTableAndCodesInMap(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(), new ArrayList<>(attributesAndNamesCodes));
		final Map<String, Integer> nameCodesFieldNoMap =
			this.userDefinedFieldService
				.getByTableAndCodesInMap(UDTableType.NAMES_NAME.getTable(), new ArrayList<>(attributesAndNamesCodes));

		// germplasm UUID should be the priority in getting germplasm
		final Set<String> germplasmUUIDs =
			germplasmUpdateDTOList.stream().map(o -> o.getGermplasmUUID()).collect(Collectors.toSet());
		// If there's no UUID, use GID
		final Set<Integer> gids =
			germplasmUpdateDTOList.stream().map(o -> StringUtils.isEmpty(o.getGermplasmUUID()) ? o.getGid() : null).filter(
				Objects::nonNull)
				.collect(Collectors.toSet());

		final List<Germplasm> germplasmList = new ArrayList<>();
		germplasmList.addAll(germplasmDAO.getByUUIDListWithMethod(germplasmUUIDs));
		germplasmList.addAll(germplasmDAO.getByGIDListWithMethod(gids));

		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap = new HashMap<>();
		for (final GermplasmUpdateDTO germplasmUpdateDTO : germplasmUpdateDTOList) {
			germplasmUpdateDTOMap
				.put(germplasmUpdateDTO.getGid() == null ? germplasmUpdateDTO.getGermplasmUUID() :
						String.valueOf(germplasmUpdateDTO.getGid()),
					germplasmUpdateDTO);
		}

		// Retrieve location and method IDs in one go
		final Map<String, Integer> locationAbbreviationIdMap = this.getLocationAbbreviationIdMap(germplasmUpdateDTOList);
		final Map<String, BreedingMethodDTO> codeBreedingMethodDTOMap = this.getCodeBreedingMethodDTOMap(germplasmUpdateDTOList);

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
				locationAbbreviationIdMap, codeBreedingMethodDTOMap, namesMap, attributesMap, germplasm, conflictErrors);
		}

		if (!conflictErrors.isEmpty()) {
			throw new GermplasmUpdateConflictException(conflictErrors);
		}

	}

	private void saveGermplasmUpdateDTO(final GermplasmDAO germplasmDAO, final NameDAO nameDAO,
		final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes, final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, BreedingMethodDTO> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final List<String> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			final BreedingMethodDTO breedingMethodDTO = codeBreedingMethodDTOMap.get(germplasmUpdateDTO.getBreedingMethod());
			final int locationId = locationAbbreviationIdMap.get(germplasmUpdateDTO.getLocationAbbreviation());
			final int germplasmDate = Integer.parseInt(germplasmUpdateDTO.getCreationDate());

			this.updateGermplasm(germplasmDAO, germplasm, locationId, breedingMethodDTO, germplasmDate, conflictErrors);
			this.saveAttributesAndNames(nameDAO, attributeDAO, attributeCodes, nameCodes, namesMap, attributesMap, germplasm,
				conflictErrors,
				germplasmUpdateDTO);
			this.updatePreferredName(nameDAO, nameCodes, namesMap, germplasm, germplasmUpdateDTO, conflictErrors);

		}
	}

	private void saveAttributesAndNames(final NameDAO nameDAO, final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes,
		final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap, final Map<Integer, List<Attribute>> attributesMap,
		final Germplasm germplasm, final List<String> conflictErrors, final GermplasmUpdateDTO germplasmUpdateDTO) {
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getData().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateName(nameDAO, nameCodes, namesMap, germplasm, code, value,
				conflictErrors);
			this.saveOrUpdateAttribute(attributeDAO, attributeCodes, attributesMap, germplasm,
				code, value, conflictErrors);
		}
	}

	private void updatePreferredName(final NameDAO nameDAO, final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap,
		final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO, final List<String> conflictErrors) {
		// Update preferred name
		final Integer preferredNameTypeId = nameCodes.get(germplasmUpdateDTO.getPreferredName());
		final List<Name> names = namesMap.get(germplasm.getGid());
		final List<Name> preferredNames =
			names.stream().filter(n -> n.getTypeId().equals(preferredNameTypeId)).collect(Collectors.toList());

		if (preferredNames.size() == 1) {
			for (final Name name : names) {
				if (preferredNameTypeId != null) {
					name.setNstat(name.getTypeId().equals(preferredNameTypeId) ? 1 : 0);
					nameDAO.save(name);
				}
			}
		} else if (preferredNames.size() > 1) {
			// TODO: Move to poperty file
			conflictErrors.add(String
				.format("Multiple names for the %s where found for germplasm %s. Name cannot be set as preferred via this batch process.",
					germplasmUpdateDTO.getPreferredName(),
					germplasm.getGid()));
		} else if (!StringUtils.isEmpty(germplasmUpdateDTO.getPreferredName())) {
			// TODO: Move to poperty file
			conflictErrors.add(String
				.format("Name %s not found in germplasm %s. Can’t be assigned as preferred name", germplasmUpdateDTO.getPreferredName(),
					germplasm.getGid()));
		}
	}

	private void updateGermplasm(final GermplasmDAO germplasmDAO, final Germplasm germplasm, final int locationId,
		final BreedingMethodDTO breedingMethodDTO,
		final int germplasmDate, final List<String> conflictErrors) {
		final String oldMethodType = germplasm.getMethod().getMtype();
		final String newMethodType = breedingMethodDTO.getType();

		// Only update the method if the new method has the same type as the old method.
		if (this.isMethodTypeMatch(newMethodType, oldMethodType)) {
			germplasm.setMethodId(breedingMethodDTO.getId());
		} else {
			// TODO: Move to poperty file
			conflictErrors.add(String
				.format("Cannot change method for germplasm %s, the breeding method should be the same type as %s", germplasm.getGid(),
					germplasm.getMethod().getMtype()));
		}
		germplasm.setLocationId(locationId);
		germplasm.setGdate(germplasmDate);
		germplasmDAO.update(germplasm);
	}

	private void saveOrUpdateName(final NameDAO nameDAO, final Map<String, Integer> nameCodes,
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm,
		final String code, final String value, final List<String> conflictErrors) {

		// Check first if the code to save is a valid Name
		if (nameCodes.containsKey(code)) {
			final Integer nameTypeId = nameCodes.get(code);
			final List<Name> germplasmNames = namesMap.get(germplasm.getGid());
			final List<Name> namesByType =
				germplasmNames.stream().filter(n -> n.getTypeId().equals(nameTypeId)).collect(Collectors.toList());

			// Check if there are multiple names with same type
			if (namesByType.size() > 1) {
				// TODO: Move to poperty file
				conflictErrors.add(String.format(
					"Multiple names for the %s where found for germplasm %s. Name cannot be updated via this batch process.",
					code, germplasm.getGid()));
			} else if (namesByType.size() == 1) {
				// Update if name is existing
				final Name name = namesByType.get(0);
				name.setNval(value);
				nameDAO.update(name);
			} else {
				// Create new record if name not yet exists
				final Name name = new Name(null, germplasm.getGid(), nameTypeId, 0, germplasm.getUserId(),
					value, germplasm.getLocationId(), germplasm.getGdate(), 0);
				nameDAO.save(name);
				germplasmNames.add(name);
			}
		}
	}

	private void saveOrUpdateAttribute(final AttributeDAO attributeDAO, final Map<String, Integer> attributeCodes,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final String code, final String value, final List<String> conflictErrors) {
		// Check first if the code to save is a valid Attribute
		if (attributeCodes.containsKey(code)) {
			final Integer attributeTypeId = attributeCodes.get(code);
			final List<Attribute> germplasmAttributes = attributesMap.get(germplasm.getGid());
			final List<Attribute> attributesByType =
				germplasmAttributes.stream().filter(n -> n.getTypeId().equals(attributeTypeId)).collect(Collectors.toList());

			// Check if there are multiple attributes with same type
			if (attributesByType.size() > 1) {
				// TODO: Move to poperty file
				conflictErrors.add(String.format(
					"Multiple attribute for the %s where found for germplasm %s. Attribute cannot be updated via this batch process.",
					code, germplasm.getGid()));
			} else if (attributesByType.size() == 1) {
				final Attribute attribute = attributesByType.get(0);
				attribute.setLocationId(germplasm.getLocationId());
				attribute.setUserId(germplasm.getUserId());
				attribute.setAdate(germplasm.getGdate());
				attribute.setAval(value);
				attributeDAO.update(attribute);
			} else {
				attributeDAO
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, germplasm.getGid(), value,
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

	private Map<String, BreedingMethodDTO> getCodeBreedingMethodDTOMap(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> breedingMethodCodes =
			germplasmUpdateDTOList.stream().map(dto -> dto.getBreedingMethod()).collect(Collectors.toSet());
		return this.breedingMethodService.getBreedingMethodsByCodes(breedingMethodCodes).stream()
			.collect(Collectors.toMap(BreedingMethodDTO::getCode, Function.identity()));
	}

	private boolean isMethodTypeMatch(final String newMethodType, final String oldMethodType) {
		return this.isGenerative(newMethodType) && this.isGenerative(oldMethodType)
			|| this.isMaintenanceOrDerivative(newMethodType) && this.isMaintenanceOrDerivative(oldMethodType);
	}

	private boolean isGenerative(final String methodType) {
		return methodType.equals("GEN");
	}

	private boolean isMaintenanceOrDerivative(final String methodType) {
		return methodType.equals("DER") || methodType.equals("MAN");
	}

}
