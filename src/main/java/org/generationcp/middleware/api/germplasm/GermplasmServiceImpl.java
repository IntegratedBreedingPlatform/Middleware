package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmUpdateRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.Synonym;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmBasicDetailsDto;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmNameDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.ExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class GermplasmServiceImpl implements GermplasmService {

	public static final String PLOT_CODE = "PLOTCODE";

	private static final String DEFAULT_BIBREF_FIELD = "-";
	public static final String PROGENITOR_1 = "PROGENITOR 1";
	public static final String PROGENITOR_2 = "PROGENITOR 2";
	private static final String DEFAULT_METHOD = "UDM";

	private final DaoFactory daoFactory;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmListService germplasmListService;

	private final GermplasmMethodValidator germplasmMethodValidator;

	private final HibernateSessionProvider sessionProvider;

	public GermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
		this.germplasmMethodValidator = new GermplasmMethodValidator();
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
		final List<UserDefinedField> udfldAttributes =
			this.daoFactory.getUserDefinedFieldDAO().getByFieldTableNameAndType(UDTableType.ATRIBUTS_PASSPORT.getTable(),
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
	public Map<Integer, GermplasmImportResponseDto> importGermplasm(final String cropName,
		final GermplasmImportRequestDto germplasmImportRequestDto) {
		final Map<Integer, GermplasmImportResponseDto> results = new HashMap<>();
		final List<GermplasmImportDTO> germplasmDtoList = germplasmImportRequestDto.getGermplasmList();
		final Map<String, Method> methodsMapByAbbr = this.getBreedingMethodsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> locationsMapByAbbr = this.getLocationsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> attributesMapByName = this.getAttributesMapByName(germplasmDtoList);
		final Map<String, Integer> nameTypesMapByName = this.getNameTypesMapByName(germplasmDtoList);
		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropName);

		final Map<String, Germplasm> progenitors = this.loadProgenitors(germplasmImportRequestDto);
		final List<GermplasmDto> germplasmMatches = this.loadGermplasmMatches(germplasmImportRequestDto);
		final Map<String, List<Integer>> gidMatchByUUID =
			germplasmMatches.stream().collect(Collectors.toMap(GermplasmDto::getGermplasmUUID, g -> Arrays.asList(g.getGid())));
		final Map<String, List<Integer>> gidsMatchesByName = new HashMap<>();
		germplasmMatches.forEach(g ->
			g.getNames().forEach(n -> {
				if (gidsMatchesByName.containsKey(n.getName())) {
					gidsMatchesByName.get(n.getName()).add(g.getGid());
				} else {
					gidsMatchesByName.put(n.getName(), Lists.newArrayList(g.getGid()));
				}
			})
		);

		for (final GermplasmImportDTO germplasmDto : germplasmDtoList) {

			if (germplasmImportRequestDto.isSkipIfExists()) {
				if (gidMatchByUUID.containsKey(germplasmDto.getGermplasmUUID())) {
					results.put(germplasmDto.getClientId(),
						new GermplasmImportResponseDto(GermplasmImportResponseDto.Status.FOUND,
							gidMatchByUUID.get(germplasmDto.getGermplasmUUID())));
					continue;
				}
				final Set<Integer> gidSet = new HashSet<>();
				germplasmDto.getNames().values().forEach(n -> {
					if (gidsMatchesByName.containsKey(n)) {
						gidSet.addAll(gidsMatchesByName.get(n));
					}
				});
				if (!gidSet.isEmpty()) {
					results.put(germplasmDto.getClientId(),
						new GermplasmImportResponseDto(GermplasmImportResponseDto.Status.FOUND, new ArrayList<>(gidSet)));
					continue;
				}
			}

			//TODO: use a constructor with the properly arguments
			final Germplasm germplasm = new Germplasm();

			final Method method = methodsMapByAbbr.get(germplasmDto.getBreedingMethodAbbr().toUpperCase());
			germplasm.setMethodId(method.getMid());

			germplasm.setGnpgs(this.calculateGnpgs(method, germplasmDto.getProgenitor1(), germplasmDto.getProgenitor2()));
			this.setProgenitors(germplasm, method, germplasmDto, progenitors);

			germplasm.setGrplce(0);
			germplasm.setMgid(0);
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
					(k.equalsIgnoreCase(germplasmDto.getPreferredName())) ? 1 : 0, v, germplasm.getLocationId(),
					Util.getCurrentDateAsIntegerValue(), 0);
				this.daoFactory.getNameDao().save(name);
			});

			if (germplasmDto.getAttributes() != null) {
				germplasmDto.getAttributes().forEach((k, v) -> {
					final Attribute attribute = new Attribute(null, germplasm.getGid(), attributesMapByName.get(k.toUpperCase()), v,
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
				groupingBy(GermplasmNameDto::getGid, HashMap::new, Collectors.toCollection(ArrayList::new))
			);
			germplasmDtos.forEach(g -> g.setNames(namesByGid.get(g.getGid())));
		}

		return germplasmDtos;
	}

	@Override
	public Set<Integer> importGermplasmUpdates(final Integer userId, final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();

		final List<Germplasm> germplasmList = this.getGermplasmListByGIDorGermplasmUUID(germplasmUpdateDTOList);
		final List<Integer> gidsOfGermplasmWithDescendants = this.getGidsOfGermplasmWithDerivativeOrMaintenanceDescendants(germplasmList);
		final Map<String, Integer> nameCodesFieldNoMap = this.getNameTypesMapByCodes(germplasmUpdateDTOList);
		final Map<String, Integer> attributeCodesFieldNoMap = this.getAttributesMapByCodes(germplasmUpdateDTOList);
		final Map<Integer, Germplasm> progenitorsMapByGid = this.getGermplasmProgenitorsMapByGids(germplasmUpdateDTOList);

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
		final List<Integer> gids = germplasmList.stream().map(Germplasm::getGid).collect(Collectors.toList());
		final Map<Integer, List<Name>> namesMap =
			this.daoFactory.getNameDao().getNamesByGidsInMap(gids);
		final List<Attribute> attributes =
			this.daoFactory.getAttributeDAO()
				.getAttributeValuesGIDList(gids);
		final Map<Integer, List<Attribute>> attributesMap =
			attributes.stream().collect(groupingBy(Attribute::getGermplasmId, LinkedHashMap::new, Collectors.toList()));

		for (final Germplasm germplasm : germplasmList) {
			this.saveGermplasmUpdateDTO(userId, attributeCodesFieldNoMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, codeBreedingMethodDTOMap, namesMap, attributesMap, germplasm,
				progenitorsMapByGid, gidsOfGermplasmWithDescendants, conflictErrors);
		}

		if (!conflictErrors.isEmpty()) {
			throw new MiddlewareRequestException(null, conflictErrors);
		}
		return germplasmList.stream().map(Germplasm::getGid).collect(Collectors.toSet());

	}

	@Override
	public void deleteGermplasm(final List<Integer> gids) {
		this.germplasmListService.performGermplasmListEntriesDeletion(gids);
		this.daoFactory.getGermplasmDao().deleteGermplasm(gids);
	}

	@Override
	public Set<Integer> getCodeFixedGidsByGidList(final List<Integer> gids) {
		final List<Germplasm> germplasmList = this.daoFactory.getGermplasmDao().getByGIDList(gids);
		return germplasmList.stream().filter(germplasm -> germplasm.getMgid() > 0).map(Germplasm::getGid).collect(Collectors.toSet());
	}

	@Override
	public Set<Integer> getGidsWithOpenLots(final List<Integer> gids) {
		final LotDAO dao = this.daoFactory.getLotDao();
		return dao.getGermplasmsWithOpenLots(gids);
	}

	@Override
	public Set<Integer> getGidsOfGermplasmWithDescendants(final List<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getGidsOfGermplasmWithDescendants(Sets.newHashSet(gids));
	}

	@Override
	public Set<Integer> getGermplasmUsedInLockedList(final List<Integer> gids) {
		return new HashSet<>(this.daoFactory.getGermplasmListDAO().getGermplasmUsedInLockedList(gids));
	}

	@Override
	public Set<Integer> getGermplasmUsedInStudies(final List<Integer> gids) {
		return new HashSet<>(this.daoFactory.getStockDao().getGermplasmUsedInStudies(gids));
	}

	@Override
	public PedigreeDTO getPedigree(final Integer gid, final String notation, final Boolean includeSiblings) {
		return this.daoFactory.getGermplasmDao().getPedigree(gid, notation, includeSiblings);
	}

	@Override
	public ProgenyDTO getProgeny(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getProgeny(gid);
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

  private List<Integer> getGidsOfGermplasmWithDerivativeOrMaintenanceDescendants(final List<Germplasm> germplasmList) {
	  final List<Integer> gids = germplasmList.stream().map(Germplasm::getGid).collect(Collectors.toList());

	  // Get all DER/MAN germplasm that has existing derivative progeny.
		return Lists.newArrayList(this.daoFactory.getGermplasmDao().getGidsOfGermplasmWithDerivativeOrMaintenanceDescendants(Sets.newHashSet(gids)));
  }

  private void saveGermplasmUpdateDTO(final Integer userId, final Map<String, Integer> attributeCodes,
		final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final Map<Integer, Germplasm> progenitorsMapByGid,
		final List<Integer> gidsOfGermplasmWithDescendants,
		final Multimap<String, Object[]> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			this.updateGermplasm(userId, germplasm, germplasmUpdateDTO, locationAbbreviationIdMap, codeBreedingMethodDTOMap, progenitorsMapByGid,
				gidsOfGermplasmWithDescendants,
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
			conflictErrors.put("germplasm.update.preferred.name.duplicate.names", new String[] {
				germplasmUpdateDTO.getPreferredNameType(),
				String.valueOf(germplasm.getGid())});
		} else if (!liquibase.util.StringUtils.isEmpty(germplasmUpdateDTO.getPreferredNameType())) {
			conflictErrors.put("germplasm.update.preferred.name.doesnt.exist", new String[] {
				germplasmUpdateDTO.getPreferredNameType(),
				String.valueOf(germplasm.getGid())});
		}
	}

	private void updateGermplasm(final Integer createdBy, final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO,
		final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap,
		final Map<Integer, Germplasm> progenitorsMapByGid,
		final List<Integer> gidsOfGermplasmWithDescendants,
		final Multimap<String, Object[]> conflictErrors) {

		final Optional<Method> breedingMethodOptional =
			Optional.ofNullable(codeBreedingMethodDTOMap.getOrDefault(germplasmUpdateDTO.getBreedingMethodAbbr(), null));
		final Optional<Integer> locationIdOptional =
			Optional.ofNullable(locationAbbreviationIdMap.getOrDefault(
				StringUtils.isNotEmpty(germplasmUpdateDTO.getLocationAbbreviation()) ?
					germplasmUpdateDTO.getLocationAbbreviation().toUpperCase() : StringUtils.EMPTY, null));
		final Optional<Integer> germplasmDateOptional =
			StringUtils.isEmpty(germplasmUpdateDTO.getCreationDate()) ? Optional.empty() :
				Optional.of(Integer.parseInt(germplasmUpdateDTO.getCreationDate()));
		final Optional<String> referenceOptional = Optional.ofNullable(germplasmUpdateDTO.getReference());

		locationIdOptional.ifPresent(germplasm::setLocationId);
		germplasmDateOptional.ifPresent(germplasm::setGdate);

		this.saveOrUpdateReference(germplasm, referenceOptional);
		this.updateBreedingMethodAndProgenitors(germplasmUpdateDTO, germplasm, breedingMethodOptional, progenitorsMapByGid,
			gidsOfGermplasmWithDescendants,
			conflictErrors);

		this.daoFactory.getGermplasmDao().update(germplasm);
	}

	private void updateBreedingMethodAndProgenitors(final GermplasmUpdateDTO germplasmUpdateDTO, final Germplasm germplasm,
		final Optional<Method> breedingMethodOptional,
		final Map<Integer, Germplasm> progenitorsMapByGid,
		final List<Integer> gidsOfGermplasmWithDescendants,
		final Multimap<String, Object[]> conflictErrors) {

		final Integer femaleParentGid = germplasmUpdateDTO.getProgenitors().get(PROGENITOR_1);
		final Integer maleParentGid = germplasmUpdateDTO.getProgenitors().get(PROGENITOR_2);

		if (!breedingMethodOptional.isPresent()) {
			// If breeding method is not specified, update the progenitors based on existing method
			this.updateProgenitors(germplasm, progenitorsMapByGid, gidsOfGermplasmWithDescendants, conflictErrors, femaleParentGid,
				maleParentGid,
				germplasm.getMethod());

		} else if (this.germplasmMethodValidator
			.isNewBreedingMethodValid(germplasm.getMethod(), breedingMethodOptional.get(), String.valueOf(germplasm.getGid()),
				conflictErrors)) {

			final Method breedingMethod = breedingMethodOptional.get();

			// Only update the method if the new method has the same type as the old method.
			germplasm.setMethodId(breedingMethod.getMid());

			// Update the progenitors based on the new method
			this.updateProgenitors(germplasm, progenitorsMapByGid, gidsOfGermplasmWithDescendants, conflictErrors, femaleParentGid,
				maleParentGid,
				breedingMethod);
		}

	}

	private void updateProgenitors(final Germplasm germplasm, final Map<Integer, Germplasm> progenitorsMapByGid,
		final List<Integer> gidsOfGermplasmWithDescendants, final Multimap<String, Object[]> conflictErrors, final Integer femaleParentGid,
		final Integer maleParentGid, final Method breedingMethod) {
		if (breedingMethod.getMprgn() == 1) {
			conflictErrors.put("germplasm.update.mutation.method.is.not.supported", new String[] {
				String.valueOf(germplasm.getGid())});
		} else if (gidsOfGermplasmWithDescendants.contains(germplasm.getGid())) {
			// Prevent update if the germplasm has existing pedigree tree.
			conflictErrors.put("germplasm.update.germplasm.has.existing.progeny", new String[] {
					String.valueOf(germplasm.getGid())});
		} else if (this.germplasmMethodValidator.isGenerative(breedingMethod.getMtype())) {
			this.assignProgenitorForGenerativeMethod(germplasm, femaleParentGid, maleParentGid, breedingMethod);
		} else if (this.germplasmMethodValidator.isMaintenanceOrDerivative(breedingMethod.getMtype())) {
			this.assignProgenitorForDerivativeOrMaintenanceMethod(germplasm, progenitorsMapByGid, conflictErrors,
					femaleParentGid, maleParentGid);
		}
	}

	private void assignProgenitorForGenerativeMethod(final Germplasm germplasm, final Integer femaleParentGid, final Integer maleParentGid,
		final Method newBreedingMethod) {

		if (femaleParentGid != null && maleParentGid != null) {
			if (femaleParentGid == 0 && maleParentGid == 0) {
				germplasm.setGnpgs(0);
			} else if (newBreedingMethod.getMprgn() != 1) {
				germplasm.setGnpgs(2);
			}
			germplasm.setGpid1(femaleParentGid);
			germplasm.setGpid2(maleParentGid);

		}
	}

	private void assignProgenitorForDerivativeOrMaintenanceMethod(final Germplasm germplasm,
		final Map<Integer, Germplasm> progenitorsMapByGid, final Multimap<String, Object[]> conflictErrors,
		final Integer femaleParentGid, final Integer maleParentGid) {

		if (femaleParentGid != null && maleParentGid != null) {
			// Gnpgs of derivative/maintenance germplasm is always -1
			germplasm.setGnpgs(-1);

			if (femaleParentGid != 0 && maleParentGid != 0
				&& progenitorsMapByGid.get(maleParentGid).getGpid1().intValue() != femaleParentGid) {
				// Prevent update if the specified immediate source (male parent) does not belong to the same group
				conflictErrors.put("germplasm.update.immediate.source.must.belong.to.the.same.group", new String[] {
					String.valueOf(germplasm.getGid())});
			} else if (femaleParentGid == 0 && maleParentGid != 0) {
				// For Unknown Group Source
				germplasm.setGpid1(progenitorsMapByGid.get(maleParentGid).getGpid1());
				germplasm.setGpid2(maleParentGid);
			} else {
				// For Terminal node (group source = 0 and immediate source = 0)
				// For Unknown Immediate Source (group source <> 0 and immediate source = 0)
				// Known Group/Immediate Source (group source <> 0 and immediate source <> 0 and immediate source belongs to the same group)
				germplasm.setGpid1(femaleParentGid);
				germplasm.setGpid2(maleParentGid);
			}
		}

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
				conflictErrors.put("germplasm.update.duplicate.names", new String[] {
					code, String.valueOf(germplasm.getGid())});
			} else if (namesByType.size() == 1) {
				// Update if name is existing
				final Name name = namesByType.get(0);
				name.setLocationId(germplasm.getLocationId());
				name.setNdate(germplasm.getGdate());
				name.setNval(value);
				this.daoFactory.getNameDao().update(name);
			} else {
				// Create new record if name not yet exists
				final Name name = new Name(null, germplasm.getGid(), nameTypeId, 0,
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
				conflictErrors.put("germplasm.update.duplicate.attributes", new String[] {
					code, String.valueOf(germplasm.getGid())});
			} else if (attributesByType.size() == 1) {
				final Attribute attribute = attributesByType.get(0);
				attribute.setLocationId(germplasm.getLocationId());
				attribute.setAdate(germplasm.getGdate());
				attribute.setAval(value);
				this.daoFactory.getAttributeDAO().update(attribute);
			} else {
				this.daoFactory.getAttributeDAO()
					.save(new Attribute(null, germplasm.getGid(), attributeTypeId, value,
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
			germplasmUpdateDTOList.stream().map(GermplasmUpdateDTO::getLocationAbbreviation).collect(Collectors.toSet());
		return
			this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbrs)).stream()
				.collect(Collectors.toMap(Location::getLabbr, Location::getLocid));
	}

	private Map<String, Method> getCodeBreedingMethodDTOMap(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> breedingMethodsAbbrs =
			germplasmUpdateDTOList.stream().map(GermplasmUpdateDTO::getBreedingMethodAbbr).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethodsAbbrs)).stream()
			.collect(Collectors.toMap(Method::getMcode, Function.identity()));
	}


	private Map<String, Integer> getLocationsMapByAbbr(final List<GermplasmImportDTO> germplasmImportDTOList) {
		final Set<String> locationAbbreviations =
			germplasmImportDTOList.stream().map(GermplasmImportDTO::getLocationAbbr).collect(Collectors.toSet());
		return this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbreviations)).stream()
			.collect(Collectors.toMap(l -> l.getLabbr().toUpperCase(), Location::getLocid));
	}

	private List<Germplasm> getGermplasmListByGIDorGermplasmUUID(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		// germplasm UUID should be the priority in getting germplasm
		final Set<String> germplasmUUIDs =
			germplasmUpdateDTOList.stream().map(GermplasmUpdateDTO::getGermplasmUUID).collect(Collectors.toSet());
		// If there's no UUID, use GID
		final Set<Integer> gids =
			germplasmUpdateDTOList.stream().map(o -> liquibase.util.StringUtils.isEmpty(o.getGermplasmUUID()) ? o.getGid() : null).filter(
				Objects::nonNull)
				.collect(Collectors.toSet());

		return this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(gids, germplasmUUIDs);

	}

	private Map<Integer, Germplasm> getGermplasmProgenitorsMapByGids(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<Integer> progenitorGids =
			germplasmUpdateDTOList.stream().map(dto -> dto.getProgenitors().values()).flatMap(Collection::stream)
				.filter(value -> value != null).collect(Collectors.toSet());
		return this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(progenitorGids, Collections.emptySet()).stream()
			.collect(Collectors.toMap(Germplasm::getGid, Function.identity()));
	}

	private Map<String, Method> getBreedingMethodsMapByAbbr(final List<GermplasmImportDTO> germplasmDtos) {
		final Set<String> breedingMethods =
			germplasmDtos.stream().map(GermplasmImportDTO::getBreedingMethodAbbr).collect(Collectors.toSet());
		return this.daoFactory.getMethodDAO().getByCode(new ArrayList<>(breedingMethods)).stream()
			.collect(Collectors.toMap(m -> m.getMcode().toUpperCase(), method -> method));
	}

	private Map<String, Integer> getNameTypesMapByCodes(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		// Get the names as well as the codes specified in the preferred name property.
		final Set<String> namesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> namesCode.addAll(g.getNames().keySet().stream().map(String::toUpperCase).collect(Collectors.toList())));
		namesCode
			.addAll(
				germplasmUpdateDTOList.stream().map(GermplasmUpdateDTO::getPreferredNameType).filter(Objects::nonNull)
					.collect(Collectors.toSet()));
		return this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), namesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private Map<String, Integer> getAttributesMapByCodes(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<String> attributesCode = new HashSet<>();
		germplasmUpdateDTOList.forEach(
			g -> attributesCode.addAll(g.getAttributes().keySet().stream().map(String::toUpperCase).collect(Collectors.toList())));

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
		return nameTypesUdfldList.stream().collect(Collectors.toMap(u -> u.getFcode().toUpperCase(), UserDefinedField::getFldno));
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
			return attributesUdfldList.stream().collect(Collectors.toMap(u -> u.getFcode().toUpperCase(), UserDefinedField::getFldno));
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
		final org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto.PedigreeConnectionType connectionType = germplasmImportRequestDto.getConnectUsing();
		if (connectionType != org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto.PedigreeConnectionType.NONE) {
			final Set<String> progenitor1Set = germplasmImportRequestDto.getGermplasmList().stream()
				.filter(g -> StringUtils.isNotEmpty(g.getProgenitor1()) && !"0".equals(g.getProgenitor1())).map(
					GermplasmImportDTO::getProgenitor1).collect(Collectors.toSet());
			final Set<String> progenitor2Set = germplasmImportRequestDto.getGermplasmList().stream()
				.filter(g -> StringUtils.isNotEmpty(g.getProgenitor2()) && !"0".equals(g.getProgenitor2())).map(
					GermplasmImportDTO::getProgenitor2).collect(Collectors.toSet());
			final Set<String> allProgenitors = new HashSet<>(progenitor1Set);
			allProgenitors.addAll(progenitor2Set);
			final List<Germplasm> germplasmList;
			if (connectionType == org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto.PedigreeConnectionType.GID) {
				final List<Integer> gids = allProgenitors.stream().map(g -> Integer.valueOf(g)).collect(Collectors.toList());
				germplasmList = this.daoFactory.getGermplasmDao().getByGIDList(gids);
			} else {
				germplasmList = this.daoFactory.getGermplasmDao().getGermplasmByGUIDs(new ArrayList<>(allProgenitors));
			}
			if (germplasmList.size() != allProgenitors.size()) {
				throw new MiddlewareRequestException("", "import.germplasm.invalid.progenitors");
			}
			return germplasmList.stream().collect(Collectors.toMap(
				g -> (connectionType == org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto.PedigreeConnectionType.GID) ? String.valueOf(g.getGid()) :
					g.getGermplasmUUID(), gm -> gm));
		} else {
			return new HashMap<>();
		}
	}

	private List<GermplasmDto> loadGermplasmMatches(final GermplasmImportRequestDto germplasmImportRequestDto) {
		if (germplasmImportRequestDto.isSkipIfExists()) {
			final List<String> guids =
				germplasmImportRequestDto.getGermplasmList().stream().filter(g -> StringUtils.isNotEmpty(g.getGermplasmUUID()))
					.map(GermplasmImportDTO::getGermplasmUUID).collect(Collectors.toList());
			final Set<String> names = new HashSet<>();
			germplasmImportRequestDto.getGermplasmList().forEach(g -> names.addAll(g.getNames().values()));
			final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
			germplasmMatchRequestDto.setNames(new ArrayList<>(names));
			germplasmMatchRequestDto.setGermplasmUUIDs(guids);
			return this.findGermplasmMatches(germplasmMatchRequestDto, null);
		} else {
			return new ArrayList<>();
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
			germplasm.setGpid1(this.resolveGpid(progenitor1, progenitorsMap));
			germplasm.setGpid2(this.resolveGpid(progenitor2, progenitorsMap));
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
				germplasm.setGpid1(this.resolveGpid(progenitor1, progenitorsMap));
			}
			germplasm.setGpid2(this.resolveGpid(progenitor2, progenitorsMap));
		}
	}

	private Integer resolveGpid(final String progenitor, final Map<String, Germplasm> progenitorsMap) {
		return ("0".equals(progenitor) || StringUtils.isEmpty(progenitor)) ? 0 : progenitorsMap.get(progenitor).getGid();
	}

	private Map<String, Integer> getLocationsMapByLocAbbr(final List<GermplasmImportRequest> germplasmImportRequestList) {
		final Set<String> locationAbbreviations =
			germplasmImportRequestList.stream().filter(g -> StringUtils.isNotEmpty(g.getCountryOfOriginCode()))
				.map(GermplasmImportRequest::getCountryOfOriginCode).collect(Collectors.toSet());
		return this.daoFactory.getLocationDAO().getByAbbreviations(new ArrayList<>(locationAbbreviations)).stream()
			.collect(Collectors.toMap(l -> l.getLabbr().toUpperCase(), Location::getLocid));
	}

	private Map<String, Integer> getAttributesMapByAttrCode(final List<GermplasmImportRequest> germplasmImportRequestList) {
		final Set<String> attributes = new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES);
		germplasmImportRequestList.forEach(g -> {
			if (!CollectionUtils.isEmpty(g.getAdditionalInfo())) {
				attributes.addAll(g.getAdditionalInfo().keySet());
			}
		});
		final List<UserDefinedField> attributesUdfldList = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType())),
				attributes);
		return attributesUdfldList.stream().collect(Collectors.toMap(u -> u.getFcode().toUpperCase(), UserDefinedField::getFldno));
	}

	private Map<String, Integer> getNameTypesMapByNameTypeCode(final List<GermplasmImportRequest> germplasmImportRequestList) {
		final Set<String> namesCode = new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES);
		germplasmImportRequestList.forEach(
			g -> namesCode.addAll(g.getSynonyms().stream().map(s -> s.getType().toUpperCase()).collect(Collectors.toList())));
		return this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), namesCode).stream().collect(Collectors.toMap(
				UserDefinedField::getFcode, UserDefinedField::getFldno));
	}


	@Override
	public List<GermplasmDTO> createGermplasm(final Integer userId, final String cropname,
		final List<GermplasmImportRequest> germplasmImportRequestList) {
		final Map<String, Integer> locationsMap = this.getLocationsMapByLocAbbr(germplasmImportRequestList);
		final Map<String, Integer> attributesMap = this.getAttributesMapByAttrCode(germplasmImportRequestList);
		final Map<String, Integer> nameTypesMap = this.getNameTypesMapByNameTypeCode(germplasmImportRequestList);
		final CropType cropType = this.workbenchDataManager.getCropTypeByName(cropname);

		//Set Unknown derivative method as default when no breeding method is specified
		Method unknownDerivativeMethod = null;
		if (germplasmImportRequestList.stream().anyMatch(g -> StringUtils.isEmpty(g.getBreedingMethodDbId()))) {
			final List<Method> unknownDerivativeMethods = this.daoFactory.getMethodDAO().getByCode(Arrays.asList(DEFAULT_METHOD));
			if (unknownDerivativeMethods.isEmpty()) {
				throw new MiddlewareRequestException("", "brapi.import.germplasm.no.default.method.found");
			}
			unknownDerivativeMethod = unknownDerivativeMethods.get(0);
		}

		final List<String> createdGermplasmUUIDs = new ArrayList<>();
		for (final GermplasmImportRequest germplasmDto : germplasmImportRequestList) {

			final Germplasm germplasm = new Germplasm();
			final Integer methodId =
				(StringUtils.isNotEmpty(germplasmDto.getBreedingMethodDbId())) ? Integer.parseInt(germplasmDto.getBreedingMethodDbId()) :
					unknownDerivativeMethod.getMid();
			germplasm.setMethodId(methodId);

			germplasm.setGrplce(0);
			germplasm.setMgid(0);
			germplasm.setLgid(0);
			germplasm.setGnpgs(0);
			germplasm.setGpid1(0);
			germplasm.setGpid2(0);
			germplasm.setLocationId(locationsMap.get(germplasmDto.getCountryOfOriginCode()));

			germplasm.setDeleted(Boolean.FALSE);
			germplasm.setGdate(Util.convertDateToIntegerValue(Util.tryParseDate(germplasmDto.getAcquisitionDate(), Util.FRONTEND_DATE_FORMAT)));
			germplasm.setReferenceId(0);

			GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(germplasm));
			this.daoFactory.getGermplasmDao().save(germplasm);

			this.addCustomNameFieldsToSynonyms(germplasmDto);
			germplasmDto.getSynonyms().forEach(synonym -> {
				final Integer typeId = nameTypesMap.get(synonym.getType().toUpperCase());
				if (typeId != null) {
					final Name name = new Name(null, germplasm.getGid(), typeId,
						0, synonym.getSynonym(), germplasm.getLocationId(), Util.getCurrentDateAsIntegerValue(), 0);
					if (GermplasmImportRequest.LNAME.equals(synonym.getType())) {
						name.setNstat(1);
					}
					this.daoFactory.getNameDao().save(name);
				}
			});

			if (germplasmDto.getExternalReferences() != null) {
				final List<ExternalReference> references = new ArrayList<>();
				germplasmDto.getExternalReferences().forEach(reference -> {
					final ExternalReference externalReference =
						new ExternalReference(germplasm, reference.getReferenceID(), reference.getReferenceSource());
					references.add(externalReference);
				});
				germplasm.setExternalReferences(references);
			}

			this.addCustomAttributeFieldsToAdditionalInfo(germplasmDto);
			germplasmDto.getAdditionalInfo().forEach((k, v) -> {
				final Integer typeId = attributesMap.get(k.toUpperCase());
				if (typeId != null) {
					final Attribute attribute = new Attribute(null, germplasm.getGid(), typeId, v,
						germplasm.getLocationId(),
						0, Util.getCurrentDateAsIntegerValue());
					this.daoFactory.getAttributeDAO().save(attribute);
				}
			});

			createdGermplasmUUIDs.add(germplasm.getGermplasmUUID());
		}
		if (!createdGermplasmUUIDs.isEmpty()) {
			final GermplasmSearchRequestDto searchRequestDto = new GermplasmSearchRequestDto();
			searchRequestDto.setGermplasmDbIds(createdGermplasmUUIDs);
			return this.searchFilteredGermplasm(searchRequestDto, null);
		}
		return Collections.emptyList();
	}

	@Override
	public GermplasmDTO updateGermplasm(final Integer userId, final String germplasmDbId, final GermplasmUpdateRequest germplasmUpdateRequest) {
		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		final GermplasmDAO germplasmDao = this.daoFactory.getGermplasmDao();
		final List<Germplasm> germplasmByGUIDs =
			germplasmDao.getGermplasmByGUIDs(Collections.singletonList(germplasmDbId));
		if (CollectionUtils.isEmpty(germplasmByGUIDs)) {
			throw new MiddlewareRequestException("", "germplasm.invalid.guid");
		}
		final Germplasm germplasm = germplasmByGUIDs.get(0);
		// Update breeding method if it is present
		if (!StringUtils.isEmpty(germplasmUpdateRequest.getBreedingMethodDbId())) {
			final Integer newBreedingMethodId = Integer.parseInt(germplasmUpdateRequest.getBreedingMethodDbId());
			final Integer oldBreedingMethodId = germplasm.getMethodId();
			if (newBreedingMethodId != oldBreedingMethodId) {
				final Map<Integer, Method> methodMap =
					this.daoFactory.getMethodDAO().getMethodsByIds(Arrays.asList(oldBreedingMethodId, newBreedingMethodId)).stream()
						.collect(Collectors.toMap(Method::getMid, Function.identity()));
				final Method newBreedingMethod = methodMap.get(newBreedingMethodId);
				final Method oldBreedingMethod = methodMap.get(oldBreedingMethodId);
				if (this.germplasmMethodValidator.isNewBreedingMethodValid(oldBreedingMethod, newBreedingMethod, germplasmDbId, conflictErrors)) {
					germplasm.setMethodId(newBreedingMethodId);
				}
			}
		}
		// Update germplasm location if it is present
		if (!StringUtils.isEmpty(germplasmUpdateRequest.getCountryOfOriginCode())) {
			final List<Location> locationList =
				this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(germplasmUpdateRequest.getCountryOfOriginCode()));
			if (!CollectionUtils.isEmpty(locationList)) {
				germplasm.setLocationId(locationList.get(0).getLocid());
			}
		}
		if (!StringUtils.isEmpty(germplasmUpdateRequest.getAcquisitionDate())) {
			germplasm.setGdate(Util.convertDateToIntegerValue(Util.tryParseDate(germplasmUpdateRequest.getAcquisitionDate(), Util.FRONTEND_DATE_FORMAT)));
		}
		if (!conflictErrors.isEmpty()) {
			throw new MiddlewareRequestException(null, conflictErrors);
		}
		germplasmDao.update(germplasm);

		// Update germplasm names
		final NameDAO nameDao = this.daoFactory.getNameDao();
		final Map<Integer, Name> existingNamesByType =
			nameDao.getNamesByGids(Collections.singletonList(germplasm.getGid())).stream()
				.collect(Collectors.toMap(Name::getTypeId,
					Function.identity()));
		final Map<String, Integer> nameTypesMap = this.getNameTypesMapByNameTypeCode(Collections.singletonList(germplasmUpdateRequest));
		this.addCustomNameFieldsToSynonyms(germplasmUpdateRequest);
		germplasmUpdateRequest.getSynonyms().forEach(synonym -> {
			final Integer typeId = nameTypesMap.get(synonym.getType().toUpperCase());
			if (typeId != null) {
				// Create new name if none of that type exists, otherwise update name value of existing one
				if (existingNamesByType.containsKey(typeId)) {
					final Name existingName = existingNamesByType.get(typeId);
					existingName.setNval(synonym.getSynonym());
					nameDao.update(existingName);
				} else {
					final Name name = new Name(null, germplasm.getGid(), typeId,
						0, synonym.getSynonym(), germplasm.getLocationId(), Util.getCurrentDateAsIntegerValue(), 0);
					if (GermplasmImportRequest.LNAME.equals(synonym.getType())) {
						name.setNstat(1);
					}
					nameDao.save(name);
				}
			}
		});

		// Update germplasm attributes
		final AttributeDAO attributeDAO = this.daoFactory.getAttributeDAO();
		final Map<Integer, Attribute> existingAttributesByType = attributeDAO.getByGID(germplasm.getGid()).stream()
			.collect(Collectors.toMap(Attribute::getTypeId,
				Function.identity()));
		final Map<String, Integer> attributesMap = this.getAttributesMapByAttrCode(Collections.singletonList(germplasmUpdateRequest));
		this.addCustomAttributeFieldsToAdditionalInfo(germplasmUpdateRequest);
		germplasmUpdateRequest.getAdditionalInfo().forEach((k, v) -> {
			final Integer typeId = attributesMap.get(k.toUpperCase());
			if (typeId != null) {
				// Create new attribute if none of that type exists, otherwise update value of existing one
				if (existingAttributesByType.containsKey(typeId)) {
					final Attribute existingAttribute = existingAttributesByType.get(typeId);
					existingAttribute.setAval(v);
					attributeDAO.update(existingAttribute);
				} else {
					final Attribute attribute = new Attribute(null, germplasm.getGid(), typeId, v,
						germplasm.getLocationId(),
						0, Util.getCurrentDateAsIntegerValue());
					attributeDAO.save(attribute);
				}
			}
		});

		// Unless the session is flushed, the latest changes to germplasm,names and attributes are not reflected in object returned by method
		this.sessionProvider.getSession().flush();

		return this.getGermplasmDTOByGUID(germplasmDbId).get();
	}

	// Add to attributes map to be saved the custom attribute fields in import request dto
	private void addCustomAttributeFieldsToAdditionalInfo(final GermplasmImportRequest germplasmDto) {
		final Map<String, String> customAttributeFieldsMap = germplasmDto.getCustomAttributeFieldsMap();
		for (final String attributeKey : GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES) {
			final boolean isAttributeSpecified = germplasmDto.getAdditionalInfo().containsKey(attributeKey);
			if (!StringUtils.isEmpty(customAttributeFieldsMap.get(attributeKey)) && !isAttributeSpecified) {
				germplasmDto.getAdditionalInfo().put(attributeKey, customAttributeFieldsMap.get(attributeKey));
			}
		}
	}

	// Add to names list to be saved the custom name fields in import request dto
	private void addCustomNameFieldsToSynonyms(final GermplasmImportRequest germplasmDto) {
		final Map<String, String> customNamesFieldsMap = germplasmDto.getCustomNamesFieldsMap();
		for (final String nameType : GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES) {
			final Optional<Synonym> synonymOptional =
				germplasmDto.getSynonyms().stream().filter(s -> nameType.equals(s.getType().toUpperCase())).findAny();
			if (!StringUtils.isEmpty(customNamesFieldsMap.get(nameType)) && !synonymOptional.isPresent()) {
				germplasmDto.getSynonyms().add(new Synonym(customNamesFieldsMap.get(nameType), nameType));
			}
		}

	}

	@Override
	public long countFilteredGermplasm(final GermplasmSearchRequestDto germplasmSearchRequestDTO) {
		return this.daoFactory.getGermplasmDao().countGermplasmDTOs(germplasmSearchRequestDTO);
	}

	@Override
	public List<GermplasmDTO> searchFilteredGermplasm(
		final GermplasmSearchRequestDto germplasmSearchRequestDTO, final Pageable pageable) {
		final List<GermplasmDTO> germplasmDTOList =
			this.daoFactory.getGermplasmDao().getGermplasmDTOList(germplasmSearchRequestDTO, pageable);
		this.populateExternalReferences(germplasmDTOList);
		this.populateSynonymsAndAttributes(germplasmDTOList);
		return germplasmDTOList;
	}

	@Override
	public Optional<GermplasmDTO> getGermplasmDTOByGUID(final String germplasmUUID) {
		final GermplasmSearchRequestDto searchDto = new GermplasmSearchRequestDto();
		searchDto.setGermplasmDbIds(Collections.singletonList(germplasmUUID));
		final List<GermplasmDTO> germplasmDTOS = this.searchFilteredGermplasm(searchDto,  new PageRequest(0, 1));
		if (!CollectionUtils.isEmpty(germplasmDTOS)) {
			return Optional.of(germplasmDTOS.get(0));
		}
		return Optional.empty();
	}

	@Override
	public List<GermplasmDTO> getGermplasmByStudy(final Integer studyDbId, final Pageable pageable) {
		final List<GermplasmDTO> germplasmByStudy = this.daoFactory.getGermplasmDao().getGermplasmByStudy(studyDbId, pageable);
		this.populateSynonymsAndAttributes(germplasmByStudy);
		return germplasmByStudy;
	}

	@Override
	public long countGermplasmByStudy(final Integer studyDbId) {
		return this.daoFactory.getGermplasmDao().countGermplasmByStudy(studyDbId);
	}

	@Override
	public GermplasmDto getGermplasmDtoById(final Integer gid) {
		final GermplasmDto germplasmDto = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (germplasmDto != null) {
			germplasmDto.setNames(this.daoFactory.getNameDao().getGermplasmNamesByGids(Collections.singletonList(gid)));
			germplasmDto.setGermplasmOrigin(this.daoFactory.getGermplasmStudySourceDAO().getGermplasmOrigin(gid));
			final List<Progenitor> progenitors = this.daoFactory.getProgenitorDao().getByGID(gid);
			germplasmDto.setOtherProgenitors(progenitors.stream().map(p -> p.getProgenitorGid()).collect(Collectors.toList()));
		}
		return germplasmDto;
	}

	@Override
	public ProgenitorsDetailsDto getGermplasmProgenitorDetails(final Integer gid) {
		final GermplasmDto germplasmDto = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (germplasmDto != null) {
			final List<Progenitor> progenitors = this.daoFactory.getProgenitorDao().getByGID(gid);
			germplasmDto.setOtherProgenitors(progenitors.stream().map(p -> p.getProgenitorGid()).collect(Collectors.toList()));
			final Method method = this.daoFactory.getMethodDAO().getById(germplasmDto.getBreedingMethodId());

			final ProgenitorsDetailsDto progenitorsDetailsDto = new ProgenitorsDetailsDto();
			progenitorsDetailsDto.setBreedingMethodId(germplasmDto.getBreedingMethodId());
			progenitorsDetailsDto.setBreedingMethodName(germplasmDto.getBreedingMethod());
			progenitorsDetailsDto.setBreedingMethodCode(method.getMcode());
			progenitorsDetailsDto.setBreedingMethodType(method.getMtype());

			final List<Integer> maleParentsGids = new ArrayList<>();
			maleParentsGids.add(germplasmDto.getGpid2());
			maleParentsGids.addAll(germplasmDto.getOtherProgenitors());

			final List<Integer> allParentsGids = new ArrayList<>();
			allParentsGids.add(germplasmDto.getGpid1());
			allParentsGids.addAll(maleParentsGids);

			final Map<Integer, GermplasmDto> germplasmDtoMap =
				this.daoFactory.getGermplasmDao().getGermplasmDtoByGids(allParentsGids).stream()
					.collect(Collectors.toMap(GermplasmDto::getGid, g -> g));
			final GermplasmDto femaleParent = germplasmDtoMap.get(germplasmDto.getGpid1());
			final List<GermplasmDto> maleParents = new ArrayList<>();
			maleParentsGids.forEach(m -> {
				if (germplasmDtoMap.containsKey(m)) {
					maleParents.add(germplasmDtoMap.get(m));
				}
			});

			if (MethodType.GENERATIVE.getCode().equals(method.getMtype())) {
				progenitorsDetailsDto.setFemaleParent(femaleParent);
				progenitorsDetailsDto.setMaleParents(maleParents);
			} else {
				progenitorsDetailsDto.setGroupSource(femaleParent);
				progenitorsDetailsDto.setImmediateSource(maleParents.isEmpty() ? null : maleParents.get(0));
			}
			return progenitorsDetailsDto;
		}
		return null;
	}

	@Override
	public void updateGermplasmBasicDetails(final Integer gid, final GermplasmBasicDetailsDto germplasmBasicDetailsDto) {
		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);
		Optional.ofNullable(germplasmBasicDetailsDto.getBreedingLocationId()).ifPresent(germplasm::setLocationId);
		Optional.ofNullable(germplasmBasicDetailsDto.getCreationDate()).ifPresent(g -> germplasm.setGdate(Integer.valueOf(g)));
		final Optional<String> referenceOptional = Optional.ofNullable(germplasmBasicDetailsDto.getReference());
		this.saveOrUpdateReference(germplasm, referenceOptional);
		this.daoFactory.getGermplasmDao().save(germplasm);
	}

	private void populateExternalReferences(final List<GermplasmDTO> germplasmDTOList) {
		final List<Integer> gids = germplasmDTOList.stream().map(g -> Integer.valueOf(g.getGid())).collect(Collectors.toList());
		if (!gids.isEmpty()) {
			final List<ExternalReferenceDTO> referenceDTOS = this.daoFactory.getExternalReferenceDAO().getExternalReferencesByGids(gids);
			final Map<String, List<ExternalReferenceDTO>> referencesByGidMap = referenceDTOS.stream()
				.collect(groupingBy(ExternalReferenceDTO::getGid));
			for (final GermplasmDTO germplasmDTO : germplasmDTOList) {
				if (referencesByGidMap.containsKey(germplasmDTO.getGid())) {
					germplasmDTO.setExternalReferences(referencesByGidMap.get(germplasmDTO.getGid()));
				}
			}
		}
	}

	@Override
	public List<GermplasmNameTypeDTO> filterGermplasmNameTypes(final Set<String> codes){
		return this.daoFactory.getUserDefinedFieldDAO().getByCodes(UDTableType.NAMES_NAME.getTable(),
			Collections.singleton(UDTableType.NAMES_NAME.getType()), codes)
			.stream()
			.map(userDefinedField -> {
				final GermplasmNameTypeDTO germplasmNameTypeDTO = new GermplasmNameTypeDTO();
				germplasmNameTypeDTO.setId(userDefinedField.getFldno());
				germplasmNameTypeDTO.setName(userDefinedField.getFname());
				germplasmNameTypeDTO.setCode(userDefinedField.getFcode());
				return germplasmNameTypeDTO;
			})
			.collect(Collectors.toList());
	}

	private void populateSynonymsAndAttributes(final List<GermplasmDTO> germplasmDTOList) {
		final List<Integer> gids = germplasmDTOList.stream().map(germplasmDTO -> Integer.valueOf(germplasmDTO.getGid()))
			.collect(Collectors.toList());
		final Map<Integer, String> nameTypesMap = this.daoFactory.getUserDefinedFieldDAO().getNameTypesByGIDList(gids).stream()
			.collect(Collectors.toMap(UserDefinedField::getFldno, UserDefinedField::getFcode));
		final Map<Integer, List<Name>> gidNamesMap = this.daoFactory.getNameDao().getNamesByGidsAndNTypeIdsInMap(new ArrayList<>(gids), Collections.emptyList());
		final Map<Integer, Map<String, String>> gidAttributesMap = this.getAttributesNameAndValuesMapForGids(new ArrayList<>(gids));
		// Populate synonyms and attributes per germplasm DTO
		for (final GermplasmDTO germplasmDTO : germplasmDTOList) {
			final Integer gid = Integer.valueOf(germplasmDTO.getGid());
			// Set as synonyms other names, other than the preferred name, found for germplasm
			final String defaultName = germplasmDTO.getGermplasmName();
			final List<Name> names = gidNamesMap.get(gid);
			if (!CollectionUtils.isEmpty(names)) {
				final Map<String, String> synonymsMap = new HashMap<>();
				final List<Name> synonyms =
					names.stream().filter(n -> !n.getNval().equalsIgnoreCase(defaultName)).collect(Collectors.toList());
				for (final Name name : synonyms) {
					synonymsMap.put(nameTypesMap.get(name.getTypeId()), name.getNval());
				}
				germplasmDTO.setSynonyms(synonymsMap);
			}
			germplasmDTO.setAdditionalInfo(gidAttributesMap.get(gid));
		}
	}

	private Map<Integer, Map<String, String>> getAttributesNameAndValuesMapForGids(final List<Integer> gidList) {
		final Map<Integer, Map<String, String>> attributeMap = new HashMap<>();

		// retrieve attribute values
		final List<Attribute> attributeList = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(gidList);
		final Map<Integer, String> attributeTypeMap = this.daoFactory.getUserDefinedFieldDAO().getAttributeTypesByGIDList(gidList).stream()
			.collect(Collectors.toMap(UserDefinedField::getFldno, UserDefinedField::getFcode));
		for (final Attribute attribute : attributeList) {
			Map<String, String> attrByType = attributeMap.get(attribute.getGermplasmId());
			if (attrByType == null) {
				attrByType = new HashMap<>();
			}
			final String attributeType = attributeTypeMap.get(attribute.getTypeId());
			attrByType.put(attributeType, attribute.getAval());
			attributeMap.put(attribute.getGermplasmId(), attrByType);
		}

		return attributeMap;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}
