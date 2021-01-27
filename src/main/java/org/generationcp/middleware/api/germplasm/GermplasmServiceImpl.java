package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmNameDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

	public static final String PLOT_CODE = "PLOTCODE";

	private static final String DEFAULT_BIBREF_FIELD = "-";

	private final DaoFactory daoFactory;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public GermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Germplasm> getGermplasmByGUIDs(final List<String> guids) {
		return this.daoFactory.getGermplasmDao().getGermplasmByGUIDs(guids);
	}

	@Override
	public List<Germplasm> getGermplasmByGIDs(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getByGIDList(gids);
	}

	@Override
	public String getPlotCodeValue(final Integer gid) {
		final UserDefinedField plotCodeAttribute = this.getPlotCodeField();
		final Optional<Attribute> plotCode = this.getAttributesByGID(gid)
			.stream()
			.filter(attribute -> attribute.getTypeId().equals(plotCodeAttribute.getFldno()))
			.findFirst();
		if (plotCode.isPresent()) {
			return plotCode.get().getAval();
		}

		return GermplasmListDataDAO.SOURCE_UNKNOWN;
	}

	@Override
	public Map<Integer, String> getPlotCodeValues(final Set<Integer> gids) {
		final UserDefinedField plotCodeAttribute = this.getPlotCodeField();
		final Map<Integer, String> plotCodeValuesByGids =
			this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(plotCodeAttribute.getFldno(), new ArrayList<>(gids))
				.stream()
				.collect(Collectors.toMap(Attribute::getGermplasmId, Attribute::getAval));

		final Map<Integer, String> plotCodesIndexedByGIDs = new HashMap<>();
		gids.forEach(gid -> {
			final String plotCodeValue = plotCodeValuesByGids.get(gid);
			plotCodesIndexedByGIDs.put(gid, Objects.isNull(plotCodeValue) ? GermplasmListDataDAO.SOURCE_UNKNOWN : plotCodeValue);
		});
		return plotCodesIndexedByGIDs;
	}

	@Override
	public List<Attribute> getAttributesByGID(final Integer gid) {
		return this.daoFactory.getAttributeDAO().getByGID(gid);
	}

	@Override
	public UserDefinedField getPlotCodeField() {
		final List<UserDefinedField> udfldAttributes = this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(UDTableType.ATRIBUTS_PASSPORT.getTable(),
			ImmutableSet.of(UDTableType.ATRIBUTS_PASSPORT.getType()));

		final Optional<UserDefinedField> plotCodeField = udfldAttributes
			.stream()
			.filter(userDefinedField -> PLOT_CODE.equals(userDefinedField.getFcode()))
			.findFirst();
		if (plotCodeField.isPresent()) {
			return plotCodeField.get();
		}

		// Defaulting to a UDFLD with fldno = 0 - this prevents NPEs and DB constraint violations.
		return new UserDefinedField(0);
	}

	@Override
	public Map<Integer, GermplasmImportResponseDto> importGermplasm(final Integer userId, final String cropName,
		final GermplasmImportRequestDto germplasmImportRequestDto) {
		final Map<Integer, GermplasmImportResponseDto> results = new HashMap<>();
		final List<GermplasmImportDTO> germplasmDtoList = germplasmImportRequestDto.getGermplasmList();
		final Map<String, Method> methodsMapByAbbr = this.getBreedingMethodsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> locationsMapByAbbr = this.getLocationsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> attributesMapByName = this.getAttributesMapByName(germplasmDtoList);
		final Map<String, Integer> nameTypesMapByName = this.getNameTypesMapByName(germplasmDtoList);

		final Map<String, Germplasm> progenitors = this.loadProgenitors(germplasmImportRequestDto);

		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropName);

		for (final GermplasmImportDTO germplasmDto : germplasmDtoList) {
			final Germplasm germplasm = new Germplasm();

			final Method method = methodsMapByAbbr.get(germplasmDto.getBreedingMethodAbbr().toUpperCase());
			germplasm.setMethodId(method.getMid());

			germplasm.setGnpgs(this.calculateGnpgs(method, germplasmDto.getProgenitor1(), germplasmDto.getProgenitor2()));
			this.setProgenitors(germplasm, method, germplasmDto, progenitors);

			germplasm.setGrplce(0);
			germplasm.setMgid(0);
			germplasm.setUserId(userId);
			germplasm.setLgid(0);
			germplasm.setLocationId(locationsMapByAbbr.get(germplasmDto.getLocationAbbr().toUpperCase()));
			germplasm.setDeleted(Boolean.FALSE);
			germplasm.setGdate(Integer.valueOf(germplasmDto.getCreationDate()));

			if (StringUtils.isEmpty(germplasmDto.getGermplasmUUID())) {
				GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(germplasm));
			} else {
				germplasm.setGermplasmUUID(germplasmDto.getGermplasmUUID());
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
	public long countGermplasmMatches(final GermplasmMatchRequestDto germplasmMatchRequestDto) {
		return this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto);
	}

	@Override
	public List<GermplasmDto> findGermplasmMatches(final GermplasmMatchRequestDto germplasmMatchRequestDto, final Pageable pageable) {
		final List<GermplasmDto> germplasmDtos = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, pageable);

		if (!germplasmDtos.isEmpty()) {
			final List<Integer> gids = germplasmDtos.stream().map(GermplasmDto::getGid).collect(Collectors.toList());
			final List<GermplasmNameDto> names = this.daoFactory.getNameDao().getGermplasmNamesByGids(gids);

			final Map<Integer, List<GermplasmNameDto>> namesByGid = names.stream().collect(
				Collectors.groupingBy(GermplasmNameDto::getGid, HashMap::new, Collectors.toCollection(ArrayList::new))
			);
			germplasmDtos.forEach(g -> g.setNames(namesByGid.get(g.getGid())));
		}

		return germplasmDtos;
	}

	@Override
	public Set<Integer> importGermplasmUpdates(final Integer userId, final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();

		final Map<String, Integer> nameCodesFieldNoMap = this.getNameTypesMapByCodes(germplasmUpdateDTOList);
		final Map<String, Integer> attributeCodesFieldNoMap = this.getAttributesMapByCodes(germplasmUpdateDTOList);
		final List<Germplasm> germplasmList = this.getGermplasmListByGIDorGermplasmUUID(germplasmUpdateDTOList);

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
		final List<Integer> gids = germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toList());
		final Map<Integer, List<Name>> namesMap =
			this.daoFactory.getNameDao().getNamesByGidsInMap(gids);
		final List<Attribute> attributes =
			this.daoFactory.getAttributeDAO()
				.getAttributeValuesGIDList(gids);
		final Map<Integer, List<Attribute>> attributesMap =
			attributes.stream().collect(Collectors.groupingBy(Attribute::getGermplasmId, LinkedHashMap::new, Collectors.toList()));

		for (final Germplasm germplasm : germplasmList) {
			this.saveGermplasmUpdateDTO(userId, attributeCodesFieldNoMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, codeBreedingMethodDTOMap, namesMap, attributesMap, germplasm, conflictErrors);
		}

		if (!conflictErrors.isEmpty()) {
			throw new MiddlewareRequestException(null, conflictErrors);
		}
		return germplasmList.stream().map(g -> g.getGid()).collect(Collectors.toSet());

	}

	private void saveGermplasmUpdateDTO(final Integer userId, final Map<String, Integer> attributeCodes,
		final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm, final Multimap<String, Object[]> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			this.updateGermplasm(germplasm, germplasmUpdateDTO, locationAbbreviationIdMap, codeBreedingMethodDTOMap,
				conflictErrors);
			this.saveAttributesAndNames(userId, attributeCodes, nameCodes, namesMap, attributesMap, germplasm,
				conflictErrors,
				germplasmUpdateDTO);
			this.updatePreferredName(nameCodes, namesMap, germplasm, germplasmUpdateDTO, conflictErrors);
		}
	}

	private void saveAttributesAndNames(final Integer userId, final Map<String, Integer> attributeCodes,
		final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap, final Map<Integer, List<Attribute>> attributesMap,
		final Germplasm germplasm, final Multimap<String, Object[]> conflictErrors, final GermplasmUpdateDTO germplasmUpdateDTO) {
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getNames().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateName(userId, nameCodes, namesMap, germplasm, code, value,
				conflictErrors);
		}
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getAttributes().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateAttribute(userId, attributeCodes, attributesMap, germplasm,
				code, value, conflictErrors);
		}

	}

	private void updatePreferredName(final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap,
		final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO, final Multimap<String, Object[]> conflictErrors) {
		// Update preferred name
		final Integer preferredNameTypeId = nameCodes.get(
			StringUtils.isNotEmpty(germplasmUpdateDTO.getPreferredNameType()) ? germplasmUpdateDTO.getPreferredNameType().toUpperCase() :
				StringUtils.EMPTY);
		final List<Name> names = namesMap.getOrDefault(germplasm.getGid(), new ArrayList<>());
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
				germplasmUpdateDTO.getPreferredNameType(),
				germplasm.getGid()});
		} else if (!liquibase.util.StringUtils.isEmpty(germplasmUpdateDTO.getPreferredNameType())) {
			conflictErrors.put("import.germplasm.update.preferred.name.doesnt.exist", new Object[] {
				germplasmUpdateDTO.getPreferredNameType(),
				germplasm.getGid()});
		}
	}

	private void updateGermplasm(final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO,
		final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap,
		final Multimap<String, Object[]> conflictErrors) {

		final Optional<Method> breedingMethodDtoOptional =
			Optional.ofNullable(codeBreedingMethodDTOMap.getOrDefault(germplasmUpdateDTO.getBreedingMethodAbbr(), null));
		final Optional<Integer> locationIdOptional =
			Optional.ofNullable(locationAbbreviationIdMap.getOrDefault(
				StringUtils.isNotEmpty(germplasmUpdateDTO.getLocationAbbreviation()) ?
					germplasmUpdateDTO.getLocationAbbreviation().toUpperCase() : StringUtils.EMPTY, null));
		final Optional<Integer> germplasmDateOptional =
			StringUtils.isEmpty(germplasmUpdateDTO.getCreationDate()) ? Optional.empty() :
				Optional.ofNullable(Integer.parseInt(germplasmUpdateDTO.getCreationDate()));
		final Optional<String> referenceOptional = Optional.ofNullable(germplasmUpdateDTO.getReference());

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

		locationIdOptional.ifPresent(germplasm::setLocationId);
		germplasmDateOptional.ifPresent(germplasm::setGdate);

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

	private void saveOrUpdateName(final Integer userId, final Map<String, Integer> nameCodes,
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
				final Name name = new Name(null, germplasm.getGid(), nameTypeId, 0, userId,
					value, germplasm.getLocationId(), germplasm.getGdate(), 0);
				this.daoFactory.getNameDao().save(name);
				germplasmNames.add(name);
				namesMap.putIfAbsent(germplasm.getGid(), germplasmNames);
			}
		}
	}

	private void saveOrUpdateAttribute(final Integer userId, final Map<String, Integer> attributeCodes,
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
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, userId, value,
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
		return methodType.equals(MethodType.GENERATIVE.getCode());
	}

	private boolean isMaintenanceOrDerivative(final String methodType) {
		return methodType.equals(MethodType.DERIVATIVE.getCode()) || methodType.equals(MethodType.MAINTENANCE.getCode());
	}

	private Map<String, Integer> getLocationsMapByAbbr(final List<GermplasmImportDTO> germplasmImportDTOList) {
		final Set<String> locationAbbreviations = germplasmImportDTOList.stream().map(g -> g.getLocationAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbreviations)).stream()
			.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private List<Germplasm> getGermplasmListByGIDorGermplasmUUID(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		// germplasm UUID should be the priority in getting germplasm
		final Set<String> germplasmUUIDs =
			germplasmUpdateDTOList.stream().map(o -> o.getGermplasmUUID()).collect(Collectors.toSet());
		// If there's no UUID, use GID
		final Set<Integer> gids =
			germplasmUpdateDTOList.stream().map(o -> liquibase.util.StringUtils.isEmpty(o.getGermplasmUUID()) ? o.getGid() : null).filter(
				Objects::nonNull)
				.collect(Collectors.toSet());

		return this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(gids, germplasmUUIDs);

	}

	private Map<String, Method> getBreedingMethodsMapByAbbr(final List<GermplasmImportDTO> germplasmDtos) {
		final Set<String> breedingMethods = germplasmDtos.stream().map(g -> g.getBreedingMethodAbbr()).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethods)).stream()
			.collect(Collectors.toMap(Method::getMcode, method -> method));
	}

	private Map<String, Integer> getNameTypesMapByCodes(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		// Get the names as well as the codes specified in the preferred name property.
		final Set<String> namesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> namesCode.addAll(g.getNames().keySet().stream().map(n -> n.toUpperCase()).collect(Collectors.toList())));
		namesCode
			.addAll(
				germplasmUpdateDTOList.stream().map(o -> o.getPreferredNameType()).filter(Objects::nonNull).collect(Collectors.toSet()));
		return this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), namesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getAttributesMapByCodes(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> attributesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> attributesCode.addAll(g.getAttributes().keySet().stream().map(n -> n.toUpperCase()).collect(Collectors.toList())));

		// Retrieve the field id of attributes and names
		return
			this.daoFactory.getUserDefinedFieldDAO().getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType())),
				attributesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getNameTypesMapByName(final List<GermplasmImportDTO> germplasmDtos) {
		final Set<String> nameTypes = new HashSet<>();
		germplasmDtos.forEach(g -> nameTypes.addAll(g.getNames().keySet()));
		final List<UserDefinedField> nameTypesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(), Collections.singleton(UDTableType.NAMES_NAME.getType()), nameTypes);
		return nameTypesUdfldList.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getAttributesMapByName(final List<GermplasmImportDTO> germplasmDtos) {
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

	private Integer calculateGnpgs(final Method method, final String progenitor1, final String progenitor2) {
		if (method.isGenerative()) {
			if ((StringUtils.isEmpty(progenitor1) && StringUtils.isEmpty(progenitor2)) || ("0".equals(progenitor1) && "0"
				.equals(progenitor2))) {
				return 0;
			} else {
				return 2;
			}
		} else {
			return -1;
		}
	}

	private Map<String, Germplasm> loadProgenitors(final GermplasmImportRequestDto germplasmImportRequestDto) {
		final GermplasmImportRequestDto.PedigreeConnectionType connectionType = germplasmImportRequestDto.getConnectUsing();
		if (connectionType != GermplasmImportRequestDto.PedigreeConnectionType.NONE) {
			final Set<String> progenitor1Set = germplasmImportRequestDto.getGermplasmList().stream()
				.filter(g -> StringUtils.isNotEmpty(g.getProgenitor1()) && !"0".equals(g.getProgenitor1())).map(
					GermplasmImportDTO::getProgenitor1).collect(Collectors.toSet());
			final Set<String> progenitor2Set = germplasmImportRequestDto.getGermplasmList().stream()
				.filter(g -> StringUtils.isNotEmpty(g.getProgenitor2()) && !"0".equals(g.getProgenitor2())).map(
					GermplasmImportDTO::getProgenitor2).collect(Collectors.toSet());
			final Set<String> allProgenitors = new HashSet<>(progenitor1Set);
			allProgenitors.addAll(progenitor2Set);
			List<Germplasm> germplasmList;
			if (connectionType == GermplasmImportRequestDto.PedigreeConnectionType.GID) {
				final List<Integer> gids = allProgenitors.stream().map(g -> Integer.valueOf(g)).collect(Collectors.toList());
				germplasmList = this.daoFactory.getGermplasmDao().getByGIDList(gids);
			} else {
				germplasmList = this.daoFactory.getGermplasmDao().getGermplasmByGUIDs(new ArrayList<>(allProgenitors));
			}
			if (germplasmList.size() != allProgenitors.size()) {
				throw new MiddlewareRequestException("", "import.germplasm.invalid.progenitors");
			}
			return germplasmList.stream().collect(Collectors.toMap(
				g -> (connectionType == GermplasmImportRequestDto.PedigreeConnectionType.GID) ? String.valueOf(g.getGid()) :
					g.getGermplasmUUID(), gm -> gm));
		} else {
			return new HashMap<>();
		}
	}

	private void setProgenitors(final Germplasm germplasm, final Method method, final GermplasmImportDTO germplasmImportDTO,
		final Map<String, Germplasm> progenitorsMap) {

		if (!method.isGenerative() && !method.isDerivativeOrMaintenance()) {
			throw new MiddlewareRequestException("", "import.germplasm.invalid.method.type", new String[] {method.getMcode()});
		}
		final String progenitor1 = germplasmImportDTO.getProgenitor1();
		final String progenitor2 = germplasmImportDTO.getProgenitor2();

		if ((StringUtils.isEmpty(progenitor1) && StringUtils.isEmpty(progenitor2)) || ("0".equals(progenitor1) && "0"
			.equals(progenitor2)) || method.isGenerative()) {
			germplasm.setGpid1(resolveGpid(progenitor1, progenitorsMap));
			germplasm.setGpid2(resolveGpid(progenitor2, progenitorsMap));
			return;
		}

		if (method.isDerivativeOrMaintenance()) {
			if ("0".equals(progenitor1)) {
				//If Progenitor1 is "0" and Progenitor2 is defined
				//Then GPID1 = ImmediateSource.GPID1
				germplasm.setGpid1(progenitorsMap.get(progenitor2).getGpid1());
			} else {
				//If Progenitor1 and Progenitor2 are defined, then
				// ImmediateSource.GPID1 must be equals to Progenitor1 GID (Belongs to same group)
				if (!"0".equals(progenitor2) && !progenitorsMap.get(progenitor2).getGpid1()
					.equals(progenitorsMap.get(progenitor1).getGid())) {
					throw new MiddlewareRequestException("", "import.germplasm.invalid.immediate.source.group",
						new String[] {
							String.valueOf(progenitorsMap.get(progenitor2).getGid()),
							String.valueOf(progenitorsMap.get(progenitor1).getGid())});
				}
				germplasm.setGpid1(resolveGpid(progenitor1, progenitorsMap));
			}
			germplasm.setGpid2(resolveGpid(progenitor2, progenitorsMap));
		}
	}

	private Integer resolveGpid(final String progenitor, final Map<String, Germplasm> progenitorsMap) {
		return ("0".equals(progenitor) || StringUtils.isEmpty(progenitor)) ? 0 : progenitorsMap.get(progenitor).getGid();
	}

}
