package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

@Service
@Transactional
public class GermplasmServiceImpl implements GermplasmService {

	private final DaoFactory daoFactory;

	private static final String DEFAULT_BIBREF_FIELD = "-";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public GermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Germplasm> getGermplasmByGUIDs(final List<String> guids) {
		return daoFactory.getGermplasmDao().getGermplasmByGUIDs(guids);
	}

	@Override
	public Map<Integer, GermplasmImportResponseDto> importGermplasm(final Integer userId, final String cropName,
		final GermplasmImportRequestDto germplasmImportRequestDto) {
		final Map<Integer, GermplasmImportResponseDto> results = new HashMap<>();
		final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtoList = germplasmImportRequestDto.getGermplasmList();
		final Map<String, Method> methodsMapByAbbr = this.getBreedingMethodsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> locationsMapByAbbr = this.getLocationsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> attributesMapByName = this.getAttributesMapByName(germplasmDtoList);
		final Map<String, Integer> nameTypesMapByName = this.getNameTypesMapByName(germplasmDtoList);

		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropName);

		for (final GermplasmImportRequestDto.GermplasmDto germplasmDto : germplasmDtoList) {
			final Germplasm germplasm = new Germplasm();

			final Method method = methodsMapByAbbr.get(germplasmDto.getBreedingMethodAbbr().toUpperCase());
			germplasm.setMethodId(method.getMid());

			//Given that parents are not provided, gnpgs = method.mprgn
			//If parents are provided and method type = GEN and mprgn = 0, then, gnpgs = number or parents provided
			germplasm.setGnpgs(method.getMprgn());

			//First iteration, parents not provided, default = 0
			germplasm.setGpid1(0);
			germplasm.setGpid2(0);

			germplasm.setGrplce(0);
			germplasm.setMgid(0);
			germplasm.setUserId(userId);
			germplasm.setLgid(0);
			germplasm.setLocationId(locationsMapByAbbr.get(germplasmDto.getLocationAbbr().toUpperCase()));
			germplasm.setDeleted(Boolean.FALSE);
			germplasm.setGdate(Util.convertDateToIntegerValue(germplasmDto.getCreationDate()));

			if (StringUtils.isEmpty(germplasmDto.getGuid())) {
				GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(germplasm));
			} else {
				germplasm.setGermplasmUUID(germplasmDto.getGuid());
			}

			if (!StringUtils.isEmpty(germplasmDto.getReference())) {
				final Bibref bibref =
					new Bibref(null, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, germplasmDto.getReference(), DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD);
				this.daoFactory.getBibrefDAO().save(bibref);
				germplasm.setReferenceId(bibref.getRefid());
			} else {
				germplasm.setReferenceId(0);
			}

			this.daoFactory.getGermplasmDao().save(germplasm);

			germplasmDto.getNames().forEach((k, v) -> {
				final Name name = new Name(null, germplasm.getGid(), nameTypesMapByName.get(k.toUpperCase()),
					(k.equalsIgnoreCase(germplasmDto.getPreferredName())) ? 1 : 0, userId, v, germplasm.getLocationId(),
					Util.getCurrentDateAsIntegerValue(), 0);
				this.daoFactory.getNameDao().save(name);
			});

			if (germplasmDto.getAttributes() != null) {
				germplasmDto.getAttributes().forEach((k, v) -> {
					final Attribute attribute = new Attribute(null, germplasm.getGid(), attributesMapByName.get(k.toUpperCase()), userId, v,
						germplasm.getLocationId(),
						0, Util.getCurrentDateAsIntegerValue());
					this.daoFactory.getAttributeDAO().save(attribute);
				});
			}
			results.put(germplasmDto.getClientId(),
				new GermplasmImportResponseDto(GermplasmImportResponseDto.Status.CREATED, Collections.singletonList(germplasm.getGid())));
		}

		return results;
	}

	@Override
	public void importGermplasmUpdates(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();

		// Get the names as well as the codes specified in the preferred name property.
		final Set<String> namesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> namesCode.addAll(g.getNames().keySet().stream().map(n -> n.toUpperCase()).collect(Collectors.toList())));
		namesCode
			.addAll(germplasmUpdateDTOList.stream().map(o -> o.getPreferredName()).filter(Objects::nonNull).collect(Collectors.toSet()));

		final Set<String> attributesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> attributesCode.addAll(g.getAttributes().keySet().stream().map(n -> n.toUpperCase()).collect(Collectors.toList())));

		// Retrieve the field id of attributes and names
		final Map<String, Integer> attributeCodesFieldNoMap =
			this.daoFactory.getUserDefinedFieldDAO().getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType())),
				attributesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<String, Integer> nameCodesFieldNoMap =
			this.daoFactory.getUserDefinedFieldDAO()
				.getByCodes(UDTableType.NAMES_NAME.getTable(),
					Collections.singleton(UDTableType.NAMES_NAME.getType()), namesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));

		// germplasm UUID should be the priority in getting germplasm
		final Set<String> germplasmUUIDs =
			germplasmUpdateDTOList.stream().map(o -> o.getGermplasmUUID()).collect(Collectors.toSet());
		// If there's no UUID, use GID
		final Set<Integer> gids =
			germplasmUpdateDTOList.stream().map(o -> liquibase.util.StringUtils.isEmpty(o.getGermplasmUUID()) ? o.getGid() : null).filter(
				Objects::nonNull)
				.collect(Collectors.toSet());

		final List<Germplasm> germplasmList = new ArrayList<>();
		germplasmList.addAll(this.daoFactory.getGermplasmDao().getByUUIDListWithMethodAndBibref(germplasmUUIDs));
		germplasmList.addAll(this.daoFactory.getGermplasmDao().getByGIDListWithMethodAndBibref(gids));

		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap = new HashMap<>();
		for (final GermplasmUpdateDTO germplasmUpdateDTO : germplasmUpdateDTOList) {
			germplasmUpdateDTOMap
				.put(StringUtils.isNotEmpty(germplasmUpdateDTO.getGermplasmUUID()) ? germplasmUpdateDTO.getGermplasmUUID() :
						String.valueOf(germplasmUpdateDTO.getGid()),
					germplasmUpdateDTO);
		}

		// Retrieve location and method IDs in one go
		final Map<String, Integer> locationAbbreviationIdMap = this.getLocationAbbreviationIdMap(germplasmUpdateDTOList);
		final Map<String, Method> codeBreedingMethodDTOMap =
			this.getCodeBreedingMethodDTOMap(germplasmUpdateDTOList);

		// Retrieve the names and attributes associated to GIDs in one go.
		final Map<Integer, List<Name>> namesMap =
			this.daoFactory.getNameDao().getNamesByGidsInMap(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
		final List<Attribute> attributes =
			this.daoFactory.getAttributeDAO()
				.getAttributeValuesGIDList(germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList()));
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
		final Map<String, Method> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final Multimap<String, Object[]> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			final Optional<Method> breedingMethodDtoOptional =
				Optional.ofNullable(codeBreedingMethodDTOMap.getOrDefault(germplasmUpdateDTO.getBreedingMethodAbbr(), null));
			final Optional<Integer> locationIdOptional =
				Optional.ofNullable(locationAbbreviationIdMap.getOrDefault(germplasmUpdateDTO.getLocationAbbreviation(), null));
			final Optional<Integer> germplasmDateOptional =
				liquibase.util.StringUtils.isEmpty(germplasmUpdateDTO.getCreationDate()) ? Optional.empty() :
					Optional.ofNullable(Integer.parseInt(germplasmUpdateDTO.getCreationDate()));
			final Optional<String> referenceOptional =
				liquibase.util.StringUtils.isEmpty(germplasmUpdateDTO.getReference()) ? Optional.empty() :
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
		final Germplasm germplasm, final Multimap<String, Object[]> conflictErrors, final GermplasmUpdateDTO germplasmUpdateDTO) {
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getNames().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateName(nameCodes, namesMap, germplasm, code, value,
				conflictErrors);
		}
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getAttributes().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateAttribute(attributeCodes, attributesMap, germplasm,
				code, value, conflictErrors);
		}

	}

	private void updatePreferredName(final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap,
		final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO, final Multimap<String, Object[]> conflictErrors) {
		// Update preferred name
		final Integer preferredNameTypeId = nameCodes.get(germplasmUpdateDTO.getPreferredName());
		final List<Name> names = namesMap.get(germplasm.getGid());
		final List<Name> preferredNames =
			names.stream().filter(n -> n.getTypeId().equals(preferredNameTypeId)).collect(Collectors.toList());

		if (preferredNames.size() == 1) {
			for (final Name name : names) {
				if (preferredNameTypeId != null) {
					name.setNstat(name.getTypeId().equals(preferredNameTypeId) ? 1 : 0);
					this.daoFactory.getNameDao().save(name);
				}
			}
		} else if (preferredNames.size() > 1) {
			conflictErrors.put("import.germplasm.update.preferred.name.duplicate.names", new Object[] {
				germplasmUpdateDTO.getPreferredName(),
				germplasm.getGid()});
		} else if (!liquibase.util.StringUtils.isEmpty(germplasmUpdateDTO.getPreferredName())) {
			conflictErrors.put("import.germplasm.update.preferred.name.doesnt.exist", new Object[] {
				germplasmUpdateDTO.getPreferredName(),
				germplasm.getGid()});
		}
	}

	private void updateGermplasm(final Germplasm germplasm,
		final Optional<Integer> locationIdOptional,
		final Optional<Method> breedingMethodDtoOptional,
		final Optional<Integer> germplasmDateOptional, final Optional<String> referenceOptional,
		final Multimap<String, Object[]> conflictErrors) {

		if (breedingMethodDtoOptional.isPresent()) {
			final String oldMethodType = germplasm.getMethod().getMtype();
			final String newMethodType = breedingMethodDtoOptional.get().getMtype();

			// Only update the method if the new method has the same type as the old method.
			if (this.isMethodTypeMatch(newMethodType, oldMethodType)) {
				germplasm.setMethodId(breedingMethodDtoOptional.get().getMid());
			} else {
				conflictErrors.put("import.germplasm.update.breeding.method.mismatch", new Object[] {
					germplasm.getGid(),
					String.format("%s (%s)", germplasm.getMethod().getMname(), germplasm.getMethod().getMtype())});
			}
		}

		if (locationIdOptional.isPresent()) {
			germplasm.setLocationId(locationIdOptional.get());
		}

		if (germplasmDateOptional.isPresent()) {
			germplasm.setGdate(germplasmDateOptional.get());
		}

		this.saveOrUpdateReference(germplasm, referenceOptional);

		this.daoFactory.getGermplasmDao().update(germplasm);
	}

	private void saveOrUpdateReference(final Germplasm germplasm, final Optional<String> referenceOptional) {
		if (referenceOptional.isPresent()) {
			if (germplasm.getBibref() != null) {
				final Bibref bibref = germplasm.getBibref();
				bibref.setAnalyt(referenceOptional.get());
				this.daoFactory.getBibrefDAO().save(bibref);
			} else {
				final Bibref bibref =
					new Bibref(null, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, referenceOptional.get(), DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD,
						DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD);
				this.daoFactory.getBibrefDAO().save(bibref);
				germplasm.setReferenceId(bibref.getRefid());
			}

		}
	}

	private void saveOrUpdateName(final Map<String, Integer> nameCodes,
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm,
		final String code, final String value, final Multimap<String, Object[]> conflictErrors) {

		// Check first if the code to save is a valid Name
		if (nameCodes.containsKey(code) && liquibase.util.StringUtils.isNotEmpty(value)) {
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
				this.daoFactory.getNameDao().update(name);
			} else {
				// Create new record if name not yet exists
				final Name name = new Name(null, germplasm.getGid(), nameTypeId, 0, germplasm.getUserId(),
					value, germplasm.getLocationId(), germplasm.getGdate(), 0);
				this.daoFactory.getNameDao().save(name);
				germplasmNames.add(name);
			}
		}
	}

	private void saveOrUpdateAttribute(final Map<String, Integer> attributeCodes,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final String code, final String value, final Multimap<String, Object[]> conflictErrors) {
		// Check first if the code to save is a valid Attribute
		if (attributeCodes.containsKey(code) && liquibase.util.StringUtils.isNotEmpty(value)) {
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
				this.daoFactory.getAttributeDAO().update(attribute);
			} else {
				this.daoFactory.getAttributeDAO()
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
		final Set<String> locationAbbrs =
			germplasmUpdateDTOList.stream().map(dto -> dto.getLocationAbbreviation()).collect(Collectors.toSet());
		return
			this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbrs)).stream()
				.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Method> getCodeBreedingMethodDTOMap(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> breedingMethodsAbbrs =
			germplasmUpdateDTOList.stream().map(dto -> dto.getBreedingMethodAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethodsAbbrs)).stream()
			.collect(Collectors.toMap(Method::getMcode, Function.identity()));
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

	private Map<String, Integer> getLocationsMapByAbbr(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> locationAbbreviations = germplasmDtos.stream().map(g -> g.getLocationAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbreviations)).stream()
			.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Method> getBreedingMethodsMapByAbbr(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> breedingMethods = germplasmDtos.stream().map(g -> g.getBreedingMethodAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethods)).stream()
			.collect(Collectors.toMap(Method::getMcode, method -> method));
	}

	private Map<String, Integer> getNameTypesMapByName(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> nameTypes = new HashSet<>();
		germplasmDtos.forEach(g -> nameTypes.addAll(g.getNames().keySet()));
		final List<UserDefinedField> nameTypesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(), Collections.singleton(UDTableType.NAMES_NAME.getType()), nameTypes);
		return nameTypesUdfldList.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getAttributesMapByName(final List<GermplasmImportRequestDto.GermplasmDto> germplasmDtos) {
		final Set<String> attributes = new HashSet<>();
		germplasmDtos.forEach(g -> {
			if (g.getAttributes() != null && !g.getAttributes().isEmpty()) {
				attributes.addAll(g.getAttributes().keySet());
			}
		});
		if (!attributes.isEmpty()) {
			final List<UserDefinedField> attributesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
				.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
					new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType())),
					attributes);
			return attributesUdfldList.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		} else {
			return new HashMap<>();
		}
	}

}
