package org.generationcp.middleware.api.germplasm.update;

import liquibase.util.StringUtils;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodDTO;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
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

@Transactional
public class GermplasmUpdateServiceImpl implements GermplasmUpdateService {

	@Autowired
	private UserDefinedFieldService userDefinedFieldService;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private BreedingMethodService breedingMethodService;

	private final DaoFactory daoFactory;

	private GermplasmDAO germplasmDAO;

	private NameDAO nameDAO;

	private AttributeDAO attributeDAO;

	private BibrefDAO bibrefDAO;

	public GermplasmUpdateServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public void saveGermplasmUpdates(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		this.germplasmDAO = this.daoFactory.getGermplasmDao();
		this.nameDAO = this.daoFactory.getNameDao();
		this.attributeDAO = this.daoFactory.getAttributeDAO();
		this.bibrefDAO = this.daoFactory.getBibrefDAO();

		final Map<String, Object[]> conflictErrors = new HashMap<>();

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
		germplasmList.addAll(this.germplasmDAO.getByUUIDListWithMethodAndBibref(germplasmUUIDs));
		germplasmList.addAll(this.germplasmDAO.getByGIDListWithMethodAndBibref(gids));

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
			this.nameDAO.getNamesByGidsInMap(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
		final List<Attribute> attributes =
			this.attributeDAO.getAttributeValuesGIDList(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
		final Map<Integer, List<Attribute>> attributesMap =
			attributes.stream().collect(Collectors.groupingBy(Attribute::getGermplasmId, LinkedHashMap::new, Collectors.toList()));

		for (final Germplasm germplasm : germplasmList) {
			this.saveGermplasmUpdateDTO(attributeCodesFieldNoMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, codeBreedingMethodDTOMap, namesMap, attributesMap, germplasm, conflictErrors);
		}

		if (!conflictErrors.isEmpty()) {
			throw new MiddlewareRequestException(null, conflictErrors);
		}

	}

	private void saveGermplasmUpdateDTO(final Map<String, Integer> attributeCodes, final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, BreedingMethodDTO> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final Map<String, Object[]> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			final Optional<BreedingMethodDTO> breedingMethodDtoOptional =
				Optional.ofNullable(codeBreedingMethodDTOMap.getOrDefault(germplasmUpdateDTO.getBreedingMethod(), null));
			final Optional<Integer> locationIdOptional =
				Optional.ofNullable(locationAbbreviationIdMap.getOrDefault(germplasmUpdateDTO.getLocationAbbreviation(), null));
			final Optional<Integer> germplasmDateOptional = StringUtils.isEmpty(germplasmUpdateDTO.getCreationDate()) ? Optional.empty() :
				Optional.ofNullable(Integer.parseInt(germplasmUpdateDTO.getCreationDate()));
			final Optional<String> referenceOptional = StringUtils.isEmpty(germplasmUpdateDTO.getReference()) ? Optional.empty() :
				Optional.ofNullable(germplasmUpdateDTO.getReference());

			this.updateGermplasm(germplasm, locationIdOptional, breedingMethodDtoOptional, germplasmDateOptional,
				referenceOptional,
				conflictErrors);
			this.saveAttributesAndNames(attributeCodes, nameCodes, namesMap, attributesMap, germplasm,
				conflictErrors,
				germplasmUpdateDTO);
			this.updatePreferredName(nameCodes, namesMap, germplasm, germplasmUpdateDTO, conflictErrors);

		}
	}

	private void saveAttributesAndNames(final Map<String, Integer> attributeCodes,
		final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap, final Map<Integer, List<Attribute>> attributesMap,
		final Germplasm germplasm, final Map<String, Object[]> conflictErrors, final GermplasmUpdateDTO germplasmUpdateDTO) {
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getData().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateName(nameCodes, namesMap, germplasm, code, value,
				conflictErrors);
			this.saveOrUpdateAttribute(attributeCodes, attributesMap, germplasm,
				code, value, conflictErrors);
		}
	}

	private void updatePreferredName(final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap,
		final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO, final Map<String, Object[]> conflictErrors) {
		// Update preferred name
		final Integer preferredNameTypeId = nameCodes.get(germplasmUpdateDTO.getPreferredName());
		final List<Name> names = namesMap.get(germplasm.getGid());
		final List<Name> preferredNames =
			names.stream().filter(n -> n.getTypeId().equals(preferredNameTypeId)).collect(Collectors.toList());

		if (preferredNames.size() == 1) {
			for (final Name name : names) {
				if (preferredNameTypeId != null) {
					name.setNstat(name.getTypeId().equals(preferredNameTypeId) ? 1 : 0);
					this.nameDAO.save(name);
				}
			}
		} else if (preferredNames.size() > 1) {
			conflictErrors.put("import.germplasm.update.preferred.name.duplicate.names", new Object[] {
				germplasmUpdateDTO.getPreferredName(),
				germplasm.getGid()});
		} else if (!StringUtils.isEmpty(germplasmUpdateDTO.getPreferredName())) {
			conflictErrors.put("import.germplasm.update.preferred.name.doesnt.exist", new Object[] {
				germplasmUpdateDTO.getPreferredName(),
				germplasm.getGid()});
		}
	}

	private void updateGermplasm(final Germplasm germplasm,
		final Optional<Integer> locationIdOptional,
		final Optional<BreedingMethodDTO> breedingMethodDtoOptional,
		final Optional<Integer> germplasmDateOptional, final Optional<String> referenceOptional,
		final Map<String, Object[]> conflictErrors) {

		if (breedingMethodDtoOptional.isPresent()) {
			final String oldMethodType = germplasm.getMethod().getMtype();
			final String newMethodType = breedingMethodDtoOptional.get().getType();

			// Only update the method if the new method has the same type as the old method.
			if (this.isMethodTypeMatch(newMethodType, oldMethodType)) {
				germplasm.setMethodId(breedingMethodDtoOptional.get().getId());
			} else {
				conflictErrors.put("import.germplasm.update.breeding.method.mismatch", new Object[] {
					germplasm.getGid(),
					germplasm.getMethod().getMtype()});
			}
		}

		if (locationIdOptional.isPresent()) {
			germplasm.setLocationId(locationIdOptional.get());
		}

		if (germplasmDateOptional.isPresent()) {
			germplasm.setGdate(germplasmDateOptional.get());
		}

		this.saveOrUpdateReference(germplasm, referenceOptional);

		this.germplasmDAO.update(germplasm);
	}

	private void saveOrUpdateReference(final Germplasm germplasm, final Optional<String> referenceOptional) {
		if (referenceOptional.isPresent()) {
			if (germplasm.getBibref() != null) {
				final Bibref bibref = germplasm.getBibref();
				bibref.setAnalyt(referenceOptional.get());
				this.bibrefDAO.save(bibref);
			} else {
				final Bibref bibref = new Bibref(null, "-", "-", referenceOptional.get(), "-", "-", "-", "-",
					"-", "-", "-", "-");
				final Integer fielbookBibrefCode = 1923;
				bibref.setType(new UserDefinedField(fielbookBibrefCode));
				this.bibrefDAO.save(bibref);
				germplasm.setReferenceId(bibref.getRefid());
			}

		}
	}

	private void saveOrUpdateName(final Map<String, Integer> nameCodes,
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm,
		final String code, final String value, final Map<String, Object[]> conflictErrors) {

		// Check first if the code to save is a valid Name
		if (nameCodes.containsKey(code)) {
			final Integer nameTypeId = nameCodes.get(code);
			final List<Name> germplasmNames = namesMap.getOrDefault(germplasm.getGid(), new ArrayList<>());
			final List<Name> namesByType =
				germplasmNames.stream().filter(n -> n.getTypeId().equals(nameTypeId)).collect(Collectors.toList());

			// Check if there are multiple names with same type
			if (namesByType.size() > 1) {
				conflictErrors.put("import.germplasm.update.duplicate.names", new Object[] {
					code, germplasm.getGid()});
			} else if (namesByType.size() == 1) {
				// Update if name is existing
				final Name name = namesByType.get(0);
				name.setLocationId(germplasm.getLocationId());
				name.setNdate(germplasm.getGdate());
				name.setNval(value);
				this.nameDAO.update(name);
			} else {
				// Create new record if name not yet exists
				final Name name = new Name(null, germplasm.getGid(), nameTypeId, 0, germplasm.getUserId(),
					value, germplasm.getLocationId(), germplasm.getGdate(), 0);
				this.nameDAO.save(name);
				germplasmNames.add(name);
			}
		}
	}

	private void saveOrUpdateAttribute(final Map<String, Integer> attributeCodes,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final String code, final String value, final Map<String, Object[]> conflictErrors) {
		// Check first if the code to save is a valid Attribute
		if (attributeCodes.containsKey(code)) {
			final Integer attributeTypeId = attributeCodes.get(code);
			final List<Attribute> germplasmAttributes = attributesMap.getOrDefault(germplasm.getGid(), new ArrayList<>());
			final List<Attribute> attributesByType =
				germplasmAttributes.stream().filter(n -> n.getTypeId().equals(attributeTypeId)).collect(Collectors.toList());

			// Check if there are multiple attributes with same type
			if (attributesByType.size() > 1) {
				conflictErrors.put("import.germplasm.update.duplicate.attributes", new Object[] {
					code, germplasm.getGid()});
			} else if (attributesByType.size() == 1) {
				final Attribute attribute = attributesByType.get(0);
				attribute.setLocationId(germplasm.getLocationId());
				attribute.setAdate(germplasm.getGdate());
				attribute.setAval(value);
				this.attributeDAO.update(attribute);
			} else {
				this.attributeDAO
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, germplasm.getUserId(), value,
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
