package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.file.FileMetadataService;
import org.generationcp.middleware.api.germplasmlist.GermplasmListService;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeService;
import org.generationcp.middleware.constant.SystemNameTypes;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmBasicDetailsDto;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergeRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergedDto;
import org.generationcp.middleware.domain.germplasm.GermplasmNameDto;
import org.generationcp.middleware.domain.germplasm.GermplasmProgenyDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.ProgenitorsUpdateRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.PedigreeUtil;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.util.VariableValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class GermplasmServiceImpl implements GermplasmService {

	public static final String PUI = "PUI";
	@Value("${germplasm.edition.max.recursion}")
	public int maxRecursiveQueries;


	// This enum is used to define the required action given a germplasm pedigree change
	private enum UpdateGroupSourceAction {
		NONE,
		//Update the old group source by the new group source using a simple query
		DIRECT,
		//A recursive function to get all the derivative germplasm to be changed is required
		RECURSIVE
	}


	public static final String PLOT_CODE = "PLOTCODE_AP_text";

	private static final String DEFAULT_BIBREF_FIELD = "-";
	public static final String PROGENITOR_1 = "PROGENITOR 1";
	public static final String PROGENITOR_2 = "PROGENITOR 2";
	public static final List<VariableType> ATTRIBUTE_TYPES =
		Arrays.asList(VariableType.GERMPLASM_ATTRIBUTE, VariableType.GERMPLASM_PASSPORT);

	private final DaoFactory daoFactory;

	@Autowired
	private GermplasmListService germplasmListService;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private UserService userService;

	@Autowired
	private GermplasmNameTypeService germplasmNameTypeService;

	@Autowired
	private StudyEntryService studyEntryService;

	@Autowired
	private FileMetadataService fileMetadataService;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private CropService cropService;

	private final GermplasmMethodValidator germplasmMethodValidator;

	public GermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
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
	public Map<Integer, String> getPlotCodeValues(final Set<Integer> gids) {
		final Term plotCodeVariable = this.getPlotCodeField();
		final Map<Integer, String> plotCodeValuesByGids =
			this.daoFactory.getAttributeDAO().getAttributeValuesByTypeAndGIDList(plotCodeVariable.getId(), new ArrayList<>(gids))
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
	public Term getPlotCodeField() {
		return this.ontologyDataManager.findTermByName(PLOT_CODE, CvId.VARIABLES.getId());
	}

	@Override
	public Map<Integer, GermplasmImportResponseDto> importGermplasm(final String cropName, final String programUUID,
		final GermplasmImportRequestDto germplasmImportRequestDto) {
		final Map<Integer, GermplasmImportResponseDto> results = new HashMap<>();
		final List<GermplasmImportDTO> germplasmDtoList = germplasmImportRequestDto.getGermplasmList();
		final Map<String, Method> methodsMapByAbbr = this.getBreedingMethodsMapByAbbr(germplasmDtoList);
		final Map<String, Integer> locationsMapByAbbr = this.getLocationsMapByAbbr(germplasmDtoList);
		final Set<String> attributesKeys = new HashSet<>();
		final List<String> germplasmPUIs = new ArrayList<>();
		germplasmDtoList.forEach(g -> {
			if (g.getAttributes() != null && !g.getAttributes().isEmpty()) {
				attributesKeys.addAll(g.getAttributes().keySet());
			}
			germplasmPUIs.addAll(g.collectGermplasmPUIs());
		});
		final Map<String, Variable> attributesMapByName = this.getAttributesMap(programUUID, attributesKeys);
		final Map<String, Integer> nameTypesMapByName = this.getNameTypesMapByName(germplasmDtoList);
		final CropType cropType = this.cropService.getCropTypeByName(cropName);

		final Map<String, Germplasm> progenitorsMap = this.loadProgenitors(germplasmImportRequestDto);
		final List<GermplasmDto> germplasmMatches = this.loadGermplasmMatches(germplasmImportRequestDto, germplasmPUIs);
		final Map<String, List<Integer>> gidMatchByPUI =
			germplasmMatches.stream().collect(Collectors.toMap(GermplasmDto::getGermplasmPUI, g -> Collections.singletonList(g.getGid())));
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
		final List<String> existingGermplasmPUIs = this.daoFactory.getNameDao().getExistingGermplasmPUIs(germplasmPUIs);

		for (final GermplasmImportDTO germplasmDto : germplasmDtoList) {

			if (germplasmImportRequestDto.isSkipIfExists()) {
				if (gidMatchByPUI.containsKey(germplasmDto.getGermplasmPUI())) {
					results.put(germplasmDto.getClientId(),
						new GermplasmImportResponseDto(GermplasmImportResponseDto.Status.FOUND,
							gidMatchByPUI.get(germplasmDto.getGermplasmPUI())));
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
			} else if (germplasmDto.isGermplasmPUIExisting(existingGermplasmPUIs)) {
				throw new MiddlewareRequestException("", "import.germplasm.pui.exists",
					!StringUtils.isEmpty(germplasmDto.getGermplasmPUI()) ? germplasmDto.getGermplasmPUI() :
						germplasmDto.getGermplasmPUIFromNames().orElse(""));
			}

			final Germplasm germplasm =
				this.saveGermplasmFromGermplasmImportDto(methodsMapByAbbr, locationsMapByAbbr, attributesMapByName, nameTypesMapByName,
					cropType,
					progenitorsMap, germplasmDto);
			results.put(germplasmDto.getClientId(),
				new GermplasmImportResponseDto(GermplasmImportResponseDto.Status.CREATED, Collections.singletonList(germplasm.getGid())));
		}

		return results;
	}

	private Germplasm saveGermplasmFromGermplasmImportDto(final Map<String, Method> methodsMapByAbbr,
		final Map<String, Integer> locationsMapByAbbr, final Map<String, Variable> attributesMapByName,
		final Map<String, Integer> nameTypesMapByName, final CropType cropType, final Map<String, Germplasm> progenitorsMap,
		final GermplasmImportDTO germplasmDto) {
		final Germplasm germplasm = new Germplasm();

		final Method method = methodsMapByAbbr.get(germplasmDto.getBreedingMethodAbbr().toUpperCase());
		germplasm.setMethod(method);

		germplasm.setGnpgs(PedigreeUtil.calculateGnpgs(method, germplasmDto.getProgenitor1(), germplasmDto.getProgenitor2(), null));
		final Multimap<String, Object[]> progenitorsErrors = ArrayListMultimap.create();
		PedigreeUtil.setProgenitors(germplasm, method, germplasmDto.getProgenitor1(), germplasmDto.getProgenitor2(), progenitorsMap,
			progenitorsErrors);
		if (!progenitorsErrors.isEmpty()) {
			final Map.Entry<String, Object[]> error = progenitorsErrors.entries().iterator().next();
			throw new MiddlewareRequestException("", error.getKey(), error.getValue());
		}
		germplasm.setGrplce(0);
		germplasm.setMgid(0);
		germplasm.setLocationId(locationsMapByAbbr.get(germplasmDto.getLocationAbbr().toUpperCase()));
		germplasm.setDeleted(Boolean.FALSE);
		germplasm.setGdate(Integer.valueOf(germplasmDto.getCreationDate()));
		GermplasmGuidGenerator.generateGermplasmGuids(cropType, Collections.singletonList(germplasm));

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

		if (!StringUtils.isEmpty(germplasmDto.getGermplasmPUI())) {
			germplasmDto.getNames().put(GermplasmServiceImpl.PUI, germplasmDto.getGermplasmPUI());
		}
		germplasmDto.getNames().forEach((k, v) -> {
			final Name name = new Name(null, germplasm, nameTypesMapByName.get(k.toUpperCase()),
				(k.equalsIgnoreCase(germplasmDto.getPreferredName())) ? 1 : 0, v, germplasm.getLocationId(),
				Util.getCurrentDateAsIntegerValue(), 0);
			this.daoFactory.getNameDao().save(name);
		});

		if (germplasmDto.getAttributes() != null) {
			germplasmDto.getAttributes().forEach((k, v) -> {
				final Variable variable = attributesMapByName.get(k.toUpperCase());
				final boolean isValidValue = VariableValueUtil.isValidAttributeValue(variable, v);
				if (isValidValue) {
					final Integer cValueId = VariableValueUtil.resolveCategoricalValueId(variable, v);
					final Attribute attribute =
						new Attribute(null, germplasm.getGid(), variable.getId(), v, cValueId,
							germplasm.getLocationId(),
							0, Util.getCurrentDateAsIntegerValue());
					this.daoFactory.getAttributeDAO().save(attribute);
				}
			});
		}
		return germplasm;
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

			final Map<Integer, String> crossExpansionsBulk =
				this.pedigreeService.getCrossExpansionsBulk(new HashSet<>(gids), null, this.crossExpansionProperties);

			germplasmDtos.forEach(g -> {
				g.setNames(namesByGid.get(g.getGid()));
				g.setPedigreeString(crossExpansionsBulk.get(g.getGid()));
			});
		}

		return germplasmDtos;
	}

	@Override
	public Set<Integer> importGermplasmUpdates(final String programUUID, final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();

		final List<Germplasm> germplasmList = this.getGermplasmListByGIDorGermplasmUUID(germplasmUpdateDTOList);
		final Map<String, Integer> nameCodesFieldNoMap = this.getNameTypesMapByCodes(germplasmUpdateDTOList);
		final Set<String> attributeKeys = new HashSet<>();
		final List<String> germplasmPUIs = new ArrayList<>();
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap = new HashMap<>();
		germplasmUpdateDTOList.forEach(g -> {
			germplasmUpdateDTOMap
				.put(StringUtils.isNotEmpty(g.getGermplasmUUID()) ? g.getGermplasmUUID() :
					String.valueOf(g.getGid()), g);
			attributeKeys.addAll(g.getAttributes().keySet().stream().map(String::toUpperCase).collect(Collectors.toList()));
			germplasmPUIs.addAll(
				g.getNames().entrySet().stream().filter(n -> PUI.equals(n.getKey())).map(Map.Entry::getValue).collect(Collectors.toList()));
		});
		final Map<String, Variable> attributeVariablesNameMap = this.getAttributesMap(programUUID, attributeKeys);
		final Map<String, Germplasm> progenitorsMapByGid = this.getGermplasmProgenitorsMapByGids(germplasmUpdateDTOList);

		// Retrieve location and method IDs in one go
		final Map<String, Integer> locationAbbreviationIdMap = this.getLocationAbbreviationIdMap(germplasmUpdateDTOList);
		final Map<String, Method> codeBreedingMethodDTOMap =
			this.getCodeBreedingMethodDTOMap(germplasmUpdateDTOList);

		// Retrieve the names and attributes associated to GIDs in one go.
		final List<Integer> gids = germplasmList.stream().map(Germplasm::getGid).collect(Collectors.toList());
		final Map<Integer, List<Name>> namesMap =
			this.daoFactory.getNameDao().getNamesByGidsInMap(gids);
		final List<String> existingGermplasmPUIs = this.daoFactory.getNameDao().getExistingGermplasmPUIs(germplasmPUIs);
		final List<Attribute> attributes =
			this.daoFactory.getAttributeDAO()
				.getAttributeValuesGIDList(gids);
		final Map<Integer, List<Attribute>> attributesMap =
			attributes.stream().collect(groupingBy(Attribute::getGermplasmId, LinkedHashMap::new, Collectors.toList()));
		final Set<Integer> gidsWithProgeny = this.daoFactory.getGermplasmDao().getGidsOfGermplasmWithDescendants(new HashSet<>(gids));

		for (final Germplasm germplasm : germplasmList) {
			this.saveGermplasmUpdateDTO(attributeVariablesNameMap, nameCodesFieldNoMap,
				germplasmUpdateDTOMap,
				locationAbbreviationIdMap, codeBreedingMethodDTOMap, namesMap, attributesMap, germplasm,
				progenitorsMapByGid, existingGermplasmPUIs, gidsWithProgeny, conflictErrors);
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
		return new HashSet<>(this.daoFactory.getStockDao().getGermplasmUsedInStudies(gids, false));
	}

	@Override
	public Set<Integer> getGermplasmUsedInLockedStudies(final List<Integer> gids) {
		return new HashSet<>(this.daoFactory.getStockDao().getGermplasmUsedInStudies(gids, true));
	}

	private void saveGermplasmUpdateDTO(final Map<String, Variable> attributeVariablesMap,
		final Map<String, Integer> nameCodes,
		final Map<String, GermplasmUpdateDTO> germplasmUpdateDTOMap, final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap, final Map<Integer, List<Name>> namesMap,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final Map<String, Germplasm> progenitorsMapByGid, final List<String> existingGermplasmPUIs,
		final Set<Integer> gidsWithProgeny,
		final Multimap<String, Object[]> conflictErrors) {
		final Optional<GermplasmUpdateDTO> optionalGermplasmUpdateDTO =
			this.getGermplasmUpdateDTOByGidOrUUID(germplasm, germplasmUpdateDTOMap);
		if (optionalGermplasmUpdateDTO.isPresent()) {
			final GermplasmUpdateDTO germplasmUpdateDTO = optionalGermplasmUpdateDTO.get();
			this.updateGermplasm(germplasm, germplasmUpdateDTO, locationAbbreviationIdMap, codeBreedingMethodDTOMap, progenitorsMapByGid,
				gidsWithProgeny, conflictErrors);
			this.saveAttributesAndNames(attributeVariablesMap, nameCodes, namesMap, attributesMap, existingGermplasmPUIs, germplasm,
				conflictErrors, germplasmUpdateDTO);
			this.updatePreferredName(nameCodes, namesMap, germplasm, germplasmUpdateDTO, conflictErrors);
		}
	}

	private void saveAttributesAndNames(final Map<String, Variable> attributeVariablesMap,
		final Map<String, Integer> nameCodes, final Map<Integer, List<Name>> namesMap, final Map<Integer, List<Attribute>> attributesMap,
		final List<String> existingGermplasmPUIs, final Germplasm germplasm, final Multimap<String, Object[]> conflictErrors,
		final GermplasmUpdateDTO germplasmUpdateDTO) {
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getNames().entrySet()) {
			final String code = codeValuesEntry.getKey();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateName(nameCodes, namesMap, germplasm, code, value, existingGermplasmPUIs,
				conflictErrors);
		}
		for (final Map.Entry<String, String> codeValuesEntry : germplasmUpdateDTO.getAttributes().entrySet()) {
			final String variableNameOrAlias = codeValuesEntry.getKey().toUpperCase();
			final String value = codeValuesEntry.getValue();
			this.saveOrUpdateAttribute(attributeVariablesMap, attributesMap, germplasm,
				variableNameOrAlias, value, conflictErrors);
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

	private void updateGermplasm(final Germplasm germplasm, final GermplasmUpdateDTO germplasmUpdateDTO,
		final Map<String, Integer> locationAbbreviationIdMap,
		final Map<String, Method> codeBreedingMethodDTOMap,
		final Map<String, Germplasm> progenitorsMapByGid,
		final Set<Integer> gidsWithProgeny,
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
			gidsWithProgeny, conflictErrors);

		this.daoFactory.getGermplasmDao().update(germplasm);
	}

	private void updateBreedingMethodAndProgenitors(final GermplasmUpdateDTO germplasmUpdateDTO, final Germplasm germplasm,
		final Optional<Method> breedingMethodOptional,
		final Map<String, Germplasm> progenitorsMapByGid,
		final Set<Integer> gidsWithProgeny,
		final Multimap<String, Object[]> conflictErrors) {

		final Integer femaleParentGid = germplasmUpdateDTO.getProgenitors().get(PROGENITOR_1);
		final Integer maleParentGid = germplasmUpdateDTO.getProgenitors().get(PROGENITOR_2);
		final List<Integer> otherProgenitors = germplasmUpdateDTO.getProgenitors().entrySet().stream()
			.filter(entry -> !entry.getKey().equals(PROGENITOR_1) && !entry.getKey().equals(PROGENITOR_2))
			.map(Map.Entry::getValue).collect(
				Collectors.toList());

		if (!breedingMethodOptional.isPresent()) {
			// If breeding method is not specified, update the progenitors based on existing method
			PedigreeUtil.assignProgenitors(germplasm, progenitorsMapByGid, conflictErrors, femaleParentGid,
				maleParentGid,
				germplasm.getMethod(), otherProgenitors);

		} else if (!gidsWithProgeny.contains(germplasmUpdateDTO.getGid()) || this.germplasmMethodValidator
			.isNewBreedingMethodValid(germplasm.getMethod(), breedingMethodOptional.get(), String.valueOf(germplasm.getGid()),
				conflictErrors)) {

			final Method breedingMethod = breedingMethodOptional.get();

			// Only update the method if the new method has the same type as the old method.
			germplasm.setMethod(breedingMethod);

			// Update the progenitors based on the new method
			PedigreeUtil.assignProgenitors(germplasm, progenitorsMapByGid, conflictErrors, femaleParentGid,
				maleParentGid, breedingMethod, otherProgenitors);
		}

	}

	private void saveOrUpdateReference(final Germplasm germplasm, final Optional<String> referenceOptional) {
		if (referenceOptional.isPresent() && !referenceOptional.get().isEmpty()) {
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
		final Map<Integer, List<Name>> namesMap, final Germplasm germplasm, final String code, final String value,
		final List<String> existingGermplasmPUIs, final Multimap<String, Object[]> conflictErrors) {

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

			} else if (this.isPuiUniquenessEnforced(code, value, germplasm.getGid(), namesByType, existingGermplasmPUIs, conflictErrors)) {
				if (namesByType.size() == 1) {
					// Update if name is existing
					final Name name = namesByType.get(0);
					name.setLocationId(germplasm.getLocationId());
					name.setNval(value);
					this.daoFactory.getNameDao().update(name);
				} else {
					// Create new record if name not yet exists
					final Name name = new Name(null, germplasm, nameTypeId, 0,
						value, germplasm.getLocationId(), Util.getCurrentDateAsIntegerValue(), 0);
					this.daoFactory.getNameDao().save(name);
					germplasmNames.add(name);
					namesMap.putIfAbsent(germplasm.getGid(), germplasmNames);
				}
			}

		}
	}

	private boolean isPuiUniquenessEnforced(final String nameType, final String value, final Integer gid, final List<Name> namesByType,
		final List<String> existingGermplasmPUIs,
		final Multimap<String, Object[]> conflictErrors) {
		if (PUI.equalsIgnoreCase(nameType) && existingGermplasmPUIs.contains(value) && (CollectionUtils.isEmpty(namesByType)
			|| !namesByType.get(0).getNval().equalsIgnoreCase(value))) {
			conflictErrors.put("germplasm.update.germplasm.pui.exists", new String[] {value, String.valueOf(gid)});
			return false;
		}
		return true;
	}

	private void saveOrUpdateAttribute(final Map<String, Variable> attributeVariables,
		final Map<Integer, List<Attribute>> attributesMap, final Germplasm germplasm,
		final String variableNameOrAlias, final String value, final Multimap<String, Object[]> conflictErrors) {
		// Check first if the code to save is a valid Attribute
		if (attributeVariables.containsKey(variableNameOrAlias) && StringUtils.isNotEmpty(value)) {
			final Variable variable = attributeVariables.get(variableNameOrAlias);
			final List<Attribute> germplasmAttributes = attributesMap.getOrDefault(germplasm.getGid(), new ArrayList<>());
			final List<Attribute> attributesByType =
				germplasmAttributes.stream().filter(n -> n.getTypeId().equals(variable.getId())).collect(Collectors.toList());

			// Check if there are multiple attributes with same type
			if (attributesByType.size() > 1) {
				conflictErrors.put("germplasm.update.duplicate.attributes", new String[] {
					variableNameOrAlias, String.valueOf(germplasm.getGid())});
			} else {
				final boolean isValidValue = VariableValueUtil.isValidAttributeValue(variable, value);
				if (isValidValue) {
					final Integer cValueId = VariableValueUtil.resolveCategoricalValueId(variable, value);
					if (attributesByType.size() == 1) {
						final Attribute attribute = attributesByType.get(0);
						attribute.setLocationId(germplasm.getLocationId());
						attribute.setAval(value);
						attribute.setcValueId(cValueId);
						this.daoFactory.getAttributeDAO().update(attribute);
					} else {
						if(variable.isObsolete()) {
							conflictErrors.put("germplasm.update.obsolete.attributes", new String[] {
								variableNameOrAlias, String.valueOf(germplasm.getGid())});
						} else {
							this.daoFactory.getAttributeDAO()
								.save(new Attribute(null, germplasm.getGid(), variable.getId(), value, cValueId,
									germplasm.getLocationId(),
									0, Util.getCurrentDateAsIntegerValue()));
						}
					}
				}
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

	private Map<String, Germplasm> getGermplasmProgenitorsMapByGids(final List<GermplasmUpdateDTO> germplasmUpdateDTOList) {
		final Set<Integer> progenitorGids =
			germplasmUpdateDTOList.stream().map(dto -> dto.getProgenitors().values()).flatMap(Collection::stream)
				.filter(Objects::nonNull).collect(Collectors.toSet());
		return this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(progenitorGids, Collections.emptySet()).stream()
			.collect(Collectors.toMap(g -> String.valueOf(g.getGid()), Function.identity()));
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
		return this.germplasmNameTypeService.filterGermplasmNameTypes(namesCode).stream().collect(Collectors.toMap(nameType ->
			nameType.getCode().toUpperCase(), GermplasmNameTypeDTO::getId));
	}

	private Map<String, Integer> getNameTypesMapByName(final List<GermplasmImportDTO> germplasmDtos) {
		final Set<String> nameTypes = new HashSet<>();
		nameTypes.add(GermplasmServiceImpl.PUI);
		germplasmDtos.forEach(g -> nameTypes.addAll(g.getNames().keySet()));
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOS = this.germplasmNameTypeService.filterGermplasmNameTypes(nameTypes);
		return germplasmNameTypeDTOS.stream()
			.collect(Collectors.toMap(germplasmNameTypeDTO -> germplasmNameTypeDTO.getCode().toUpperCase(), GermplasmNameTypeDTO::getId));
	}

	private Map<String, Variable> getAttributesMap(final String programUUID, final Set<String> variableNamesOrAlias) {
		if (!variableNamesOrAlias.isEmpty()) {
			final VariableFilter variableFilter = new VariableFilter();
			variableFilter.setProgramUuid(programUUID);
			ATTRIBUTE_TYPES.forEach(variableFilter::addVariableType);
			variableNamesOrAlias.forEach(variableFilter::addName);

			final List<Variable> existingAttributeVariables =
				this.ontologyVariableDataManager.getWithFilter(variableFilter);

			final Map<String, Variable> map = new HashMap<>();
			existingAttributeVariables.forEach(a -> {
				map.put(a.getName().toUpperCase(), a);
				if (StringUtils.isNotEmpty(a.getAlias())) {
					map.put(a.getAlias().toUpperCase(), a);
				}
			});
			return map;
		} else {
			return new HashMap<>();
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
			final List<Germplasm> germplasmList;
			if (connectionType == GermplasmImportRequestDto.PedigreeConnectionType.GID) {
				final List<Integer> gids = allProgenitors.stream().map(Integer::parseInt).collect(Collectors.toList());
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

	private List<GermplasmDto> loadGermplasmMatches(final GermplasmImportRequestDto germplasmImportRequestDto,
		final List<String> germplasmPUIs) {
		if (germplasmImportRequestDto.isSkipIfExists()) {
			final Set<String> names = new HashSet<>();
			germplasmImportRequestDto.getGermplasmList().forEach(g -> names.addAll(g.getNames().values()));
			final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
			germplasmMatchRequestDto.setNames(new ArrayList<>(names));
			germplasmMatchRequestDto.setGermplasmPUIs(germplasmPUIs);
			return this.findGermplasmMatches(germplasmMatchRequestDto, null);
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public GermplasmDto getGermplasmDtoById(final Integer gid) {
		final GermplasmDto germplasmDto = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (germplasmDto != null) {
			germplasmDto.setNames(this.daoFactory.getNameDao().getGermplasmNamesByGids(Collections.singletonList(gid)));
			germplasmDto.setGermplasmOrigin(this.daoFactory.getGermplasmStudySourceDAO().getGermplasmOrigin(gid));
			germplasmDto.setExternalReferences(
				this.daoFactory.getGermplasmExternalReferenceDAO().getExternalReferences(Collections.singletonList(gid)));
			final List<Progenitor> progenitors = this.daoFactory.getProgenitorDao().getByGID(gid);
			germplasmDto.setOtherProgenitors(progenitors.stream().map(Progenitor::getProgenitorGid).collect(Collectors.toList()));
			this.getCreatedByWorkbenchUserName(germplasmDto.getCreatedByUserId()).ifPresent(germplasmDto::setCreatedBy);
		}
		return germplasmDto;
	}

	@Override
	public ProgenitorsDetailsDto getGermplasmProgenitorDetails(final Integer gid) {
		final GermplasmDto germplasmDto = this.daoFactory.getGermplasmDao().getGermplasmDtoByGid(gid);
		if (germplasmDto != null) {
			final List<Progenitor> progenitors = this.daoFactory.getProgenitorDao().getByGID(gid);
			germplasmDto.setOtherProgenitors(progenitors.stream().map(Progenitor::getProgenitorGid).collect(Collectors.toList()));
			final Method method = this.daoFactory.getMethodDAO().getById(germplasmDto.getBreedingMethodId());

			final ProgenitorsDetailsDto progenitorsDetailsDto = new ProgenitorsDetailsDto();
			progenitorsDetailsDto.setBreedingMethodId(germplasmDto.getBreedingMethodId());
			progenitorsDetailsDto.setBreedingMethodName(germplasmDto.getBreedingMethod());
			progenitorsDetailsDto.setBreedingMethodCode(method.getMcode());
			progenitorsDetailsDto.setBreedingMethodType(method.getMtype());
			progenitorsDetailsDto.setNumberOfDerivativeProgeny(
				this.daoFactory.getGermplasmDao().countGermplasmDerivativeProgeny(Sets.newHashSet(gid)).getOrDefault(gid, 0));

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

	@Override
	public void updateGermplasmPedigree(final Integer gid, final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto) {
		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);

		//Request values
		final Optional<Integer> newBreedingMethodIdOptional = Optional.ofNullable(progenitorsUpdateRequestDto.getBreedingMethodId());
		final Optional<Integer> newGpid1Optional = Optional.ofNullable(progenitorsUpdateRequestDto.getGpid1());
		final Optional<Integer> newGpid2Optional = Optional.ofNullable(progenitorsUpdateRequestDto.getGpid2());
		final Optional<List<Integer>> newOtherProgenitorsOptional = Optional.ofNullable(progenitorsUpdateRequestDto.getOtherProgenitors());

		//Old germplasm values.
		final Germplasm germplasmBeforeUpdate = germplasm.clone();

		//Final values after combining request and existing germplasm data
		final Method methodFinal =
			(newBreedingMethodIdOptional.isPresent() && !newBreedingMethodIdOptional.get()
				.equals(germplasmBeforeUpdate.getMethod().getMid())) ?
				this.daoFactory.getMethodDAO().getById(newBreedingMethodIdOptional.get()) :
				germplasmBeforeUpdate.getMethod();

		final Integer gpid1Final = newGpid1Optional.orElseGet(germplasmBeforeUpdate::getGpid1);

		//For mutations, if gpid1 is set and gpid2 is null, then new gpid2 is 0
		final Integer gpid2Final = newGpid2Optional
			.orElseGet(() -> ((Integer.valueOf(1).equals(methodFinal.getMprgn()) && newGpid1Optional.isPresent()) ? 0 :
				germplasmBeforeUpdate.getGpid2()));

		final List<Integer> otherProgenitorsFinal =
			this.getOtherProgenitorsFinal(germplasmBeforeUpdate, newGpid2Optional.orElse(null), newOtherProgenitorsOptional.orElse(null));

		if (this.isPedigreeUpdateDetected(germplasmBeforeUpdate, methodFinal, gpid1Final, gpid2Final, otherProgenitorsFinal)) {
			final Multimap<String, Object[]> progenitorsErrors = ArrayListMultimap.create();
			germplasm.setMethod(methodFinal);
			final Map<String, Germplasm> progenitorsMap = this.loadProgenitors(gpid1Final, gpid2Final);
			PedigreeUtil.setProgenitors(germplasm, methodFinal, String.valueOf(gpid1Final), String.valueOf(gpid2Final), progenitorsMap,
				progenitorsErrors);
			PedigreeUtil.setOtherProgenitors(germplasm, methodFinal, otherProgenitorsFinal, progenitorsErrors);

			if (!progenitorsErrors.isEmpty()) {
				final Map.Entry<String, Object[]> error = progenitorsErrors.entries().iterator().next();
				throw new MiddlewareRequestException("", error.getKey(), error.getValue());
			}

			germplasm.setGnpgs(PedigreeUtil.calculateGnpgs(methodFinal, String.valueOf(gpid1Final), String.valueOf(gpid2Final),
				otherProgenitorsFinal.stream().map(String::valueOf).collect(Collectors.toList())));

			final boolean gpidsWillBeChanged = !germplasm.getGpid1().equals(germplasmBeforeUpdate.getGpid1()) || !germplasm.getGpid2()
				.equals(germplasmBeforeUpdate.getGpid2());
			final boolean hasProgeny = !this.daoFactory.getGermplasmDao().getChildren(germplasm.getGid()).isEmpty();
			if (gpidsWillBeChanged && hasProgeny) {
				final Set<Integer> gpids = new HashSet<>();
				gpids.add(germplasm.getGpid1());
				gpids.add(germplasm.getGpid2());

				final boolean isNewParentANodeChildren =
					this.daoFactory.getGermplasmDao().isNewParentANodeDescendant(gpids, germplasm.getGid(), this.maxRecursiveQueries);
				if (isNewParentANodeChildren) {
					throw new MiddlewareRequestException("", "germplasm.update.germplasm.new.parents.are.children", "");
				}
			}

			this.daoFactory.getGermplasmDao().save(germplasm);
			this.updateGroupSource(germplasmBeforeUpdate, germplasm);
		}

	}

	@Override
	public void mergeGermplasm(final GermplasmMergeRequestDto germplasmMergeRequestDto, final String crossExpansion) {

		final Germplasm targetGermplasm = this.daoFactory.getGermplasmDao().getById(germplasmMergeRequestDto.getTargetGermplasmId());

		final List<GermplasmMergeRequestDto.NonSelectedGermplasm> nonSelectedGermplasmList =
			germplasmMergeRequestDto.getNonSelectedGermplasm().stream().filter(
				nonSelectedGermplasm -> !nonSelectedGermplasm.isOmit()).collect(Collectors.toList());

		final List<Integer> gidsNonSelectedGermplasm =
			nonSelectedGermplasmList.stream().map(GermplasmMergeRequestDto.NonSelectedGermplasm::getGermplasmId).collect(
				Collectors.toList());

		// Replace the non-selected germplasm in Germplasm List entries with the target germplasm.
		this.daoFactory.getGermplasmListDataDAO()
			.replaceGermplasm(gidsNonSelectedGermplasm, targetGermplasm, crossExpansion);

		// Replace non-selected germplasm used as entries in any study with the target germplasm.
		this.studyEntryService.replaceStudyEntries(gidsNonSelectedGermplasm, germplasmMergeRequestDto.getTargetGermplasmId(),
			crossExpansion);

		if (germplasmMergeRequestDto.getMergeOptions().isMigrateNameTypes()) {
			// Migrate names from non-selected germplasm to the target germplasm
			this.migrateNames(gidsNonSelectedGermplasm, targetGermplasm);
		}

		final List<Integer> nonSelectedGermplasmDescriptorTypeIds = new ArrayList<>();
		if (germplasmMergeRequestDto.getMergeOptions().isMigrateAttributesData()) {
			// Migrate attributes from non-selected germplasm to the target germplasm
			final Set<Integer> attributesTypeIds =
				this.migrateAttributes(gidsNonSelectedGermplasm, germplasmMergeRequestDto.getTargetGermplasmId(),
					VariableType.GERMPLASM_ATTRIBUTE.getId());
			nonSelectedGermplasmDescriptorTypeIds.addAll(attributesTypeIds);
		}

		if (germplasmMergeRequestDto.getMergeOptions().isMigratePassportData()) {
			// Migrate passport from non-selected germplasm to the target germplasm
			final Set<Integer> passportTypeIds =
				this.migrateAttributes(gidsNonSelectedGermplasm, germplasmMergeRequestDto.getTargetGermplasmId(),
					VariableType.GERMPLASM_PASSPORT.getId());
			nonSelectedGermplasmDescriptorTypeIds.addAll(passportTypeIds);
		}

		// Migrate lots from non-selected germplasm to the target germplasm
		this.migrateLots(nonSelectedGermplasmList, germplasmMergeRequestDto.getTargetGermplasmId());
		this.closeLots(nonSelectedGermplasmList);

		// Delete all non-selected germplasm that were merged
		this.daoFactory.getGermplasmDao().deleteGermplasm(gidsNonSelectedGermplasm, germplasmMergeRequestDto.getTargetGermplasmId());

		if (germplasmMergeRequestDto.getMergeOptions().isMigrateFiles()) {
			final List<FileMetadata> files = this.fileMetadataService.getByGids(gidsNonSelectedGermplasm);
			final List<String> fileUUIDs = files.stream()
				.filter(fileMetadataDTO -> CollectionUtils.isEmpty(fileMetadataDTO.getVariables()) ||
					(!CollectionUtils.isEmpty(nonSelectedGermplasmDescriptorTypeIds) &&
						fileMetadataDTO.getVariables().stream()
							.anyMatch(cvTerm -> nonSelectedGermplasmDescriptorTypeIds.contains(cvTerm.getCvTermId()))))
				.map(FileMetadata::getFileUUID)
				.collect(Collectors.toList());
			if (!CollectionUtils.isEmpty(fileUUIDs)) {
				this.fileMetadataService.updateGid(germplasmMergeRequestDto.getTargetGermplasmId(), fileUUIDs);
			}
		}
	}

	@Override
	public List<GermplasmMergedDto> getGermplasmMerged(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getGermplasmMerged(gid);
	}

	@Override
	public List<GermplasmProgenyDto> getGermplasmProgenies(final Integer gid) {
		return this.daoFactory.getGermplasmDao().getGermplasmDescendantByGID(gid, 0, Integer.MAX_VALUE).stream().map((germplasm) -> {
			return new GermplasmProgenyDto(germplasm.getGid(),
				germplasm.getPreferredName() != null ? germplasm.getPreferredName().getNval() : StringUtils.EMPTY);
		}).collect(Collectors.toList());
	}

	@Override
	public Optional<Germplasm> findOneByMethodId(final Integer breedingMethodDbId) {
		return this.daoFactory.getGermplasmDao().findOneByMethodId(breedingMethodDbId);
	}

	@Override
	public boolean isLocationUsedInGermplasm(final Integer locationId) {
		return this.daoFactory.getGermplasmDao().isLocationUsedInGermplasm(locationId);
	}

	@Override
	public Map<Integer, Pair<String, String>> getDerivativeParentsMapByGids(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getDerivativeParentsMapByGids(gids);
	}

	@Override
	public Map<Integer, Integer> countGermplasmDerivativeProgeny(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().countGermplasmDerivativeProgeny(gids);
	}

	@Override
	public List<BasicGermplasmDTO> getBasicGermplasmByGids(final Set<Integer> gids) {
		return this.daoFactory.getGermplasmDao().getBasicGermplasmByGids(gids);
	}

	private void migrateNames(final List<Integer> gidsNonSelectedGermplasm, final Germplasm targetGermplasm) {
		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), SystemNameTypes.PUI.getType());
		final NameDAO nameDAO = this.daoFactory.getNameDao();
		final List<Name> names = nameDAO.getNamesByGids(gidsNonSelectedGermplasm);
		final List<Integer> existingNameTypeIds = targetGermplasm.getNames().stream().map(Name::getTypeId).collect(Collectors.toList());
		for (final Name name : names) {
			// If name is a PUI do not migrate if it's already present
			if (!(puiUserDefinedField.getFldno().equals(name.getTypeId()) && existingNameTypeIds.contains(name.getTypeId()))) {
				final Name nameToSave =
					new Name(null, targetGermplasm, name.getTypeId(), 0, name.getNval(), name.getLocationId(), name.getNdate(),
						name.getReferenceId());
				nameDAO.save(nameToSave);
				existingNameTypeIds.add(nameToSave.getTypeId());
			}
			// Delete non-preferred names of merged germplasm (we need to retain the preferred name to be able to show in audit history)
			if (name.getNstat() != 1) {
				nameDAO.makeTransient(name);
			}
		}
	}

	/**
	 * @param gidsNonSelectedGermplasm
	 * @param targetGermplasmId
	 * @param variableTypeId
	 * @return {@link Set} with all the attributes that the target and the non selected germplasms has
	 */
	private Set<Integer> migrateAttributes(final List<Integer> gidsNonSelectedGermplasm, final Integer targetGermplasmId,
		final Integer variableTypeId) {
		final AttributeDAO attributeDAO = this.daoFactory.getAttributeDAO();
		final List<Attribute> attributesOfTargetGermplasm =
			attributeDAO.getByGIDsAndVariableType(Arrays.asList(targetGermplasmId), variableTypeId);
		final List<Attribute> attributesOfNonSelectedGermplasm =
			attributeDAO.getByGIDsAndVariableType(gidsNonSelectedGermplasm, variableTypeId);
		final List<Integer> existingAttributeTypeIds =
			attributesOfTargetGermplasm.stream().map(Attribute::getTypeId).collect(Collectors.toList());
		final List<Integer> nonSelectedMergeableGermplasmAttributesTypeIds = new ArrayList<>();
		for (final Attribute attribute : attributesOfNonSelectedGermplasm) {
			if (!existingAttributeTypeIds.contains(attribute.getTypeId())) {
				final Attribute attributeToSave =
					new Attribute(null, targetGermplasmId, attribute.getTypeId(), attribute.getAval(), attribute.getcValueId(),
						attribute.getLocationId(), attribute.getReferenceId(), attribute.getAdate());
				attributeDAO.save(attributeToSave);
				attributeDAO.makeTransient(attribute);
				existingAttributeTypeIds.add(attributeToSave.getTypeId());
			}

			nonSelectedMergeableGermplasmAttributesTypeIds.add(attribute.getTypeId());
		}
		return Stream.concat(existingAttributeTypeIds.stream(), nonSelectedMergeableGermplasmAttributesTypeIds.stream())
			.collect(Collectors.toSet());
	}

	private void migrateLots(final List<GermplasmMergeRequestDto.NonSelectedGermplasm> nonSelectedGermplasmList,
		final Integer targetGermplasmId) {
		final List<Integer> migrateLotsGids =
			nonSelectedGermplasmList.stream().filter(o -> !o.isOmit() && BooleanUtils.isTrue(o.isMigrateLots()))
				.map(GermplasmMergeRequestDto.NonSelectedGermplasm::getGermplasmId).collect(
					Collectors.toList());
		this.daoFactory.getLotDao().replaceGermplasm(migrateLotsGids, targetGermplasmId);
	}

	private void closeLots(final List<GermplasmMergeRequestDto.NonSelectedGermplasm> nonSelectedGermplasmList) {
		final List<Integer> closeLotsGids =
			nonSelectedGermplasmList.stream().filter(o -> !o.isOmit() && BooleanUtils.isNotTrue(o.isMigrateLots()))
				.map(GermplasmMergeRequestDto.NonSelectedGermplasm::getGermplasmId).collect(
					Collectors.toList());
		final List<Integer> lotsToClose =
			this.daoFactory.getLotDao().getByGids(closeLotsGids).stream().map(Lot::getId).collect(Collectors.toList());
		this.daoFactory.getLotDao().closeLots(lotsToClose);

	}

	private void updateGroupSource(final Germplasm oldGermplasm, final Germplasm newGermplasm) {
		final UpdateGroupSourceAction updateGroupSourceAction = this.getUpdateGroupSourceAction(oldGermplasm, newGermplasm);
		if (updateGroupSourceAction == UpdateGroupSourceAction.NONE) {
			return;
		}
		final Integer newGroupSource = PedigreeUtil.getProgenyGroupSource(newGermplasm);
		if (updateGroupSourceAction == UpdateGroupSourceAction.DIRECT) {
			this.daoFactory.getGermplasmDao().updateGroupSource(newGermplasm.getGid(), newGroupSource);
			return;
		}

		if (updateGroupSourceAction == UpdateGroupSourceAction.RECURSIVE) {
			this.daoFactory.getGermplasmDao()
				.updateGroupSourceTraversingProgeny(newGermplasm.getGid(), newGroupSource, this.maxRecursiveQueries);
		}
	}

	/**
	 * With this function we avoid to traverse the tree whenever is possible.
	 * Do not update this function if you are not clear enough about BMS germplasm tree management
	 *
	 * @param oldGermplasm Germplasm before any pedigree update
	 * @param newGermplasm Germplasm after being modified
	 * @return tree update action to be taken
	 */
	private UpdateGroupSourceAction getUpdateGroupSourceAction(final Germplasm oldGermplasm, final Germplasm newGermplasm) {
		final Method oldMethod = oldGermplasm.getMethod();
		final Method newMethod = newGermplasm.getMethod();
		if (oldMethod.isGenerative() && newMethod.isDerivativeOrMaintenance() && !newGermplasm.isTerminalAncestor()) {
			return UpdateGroupSourceAction.DIRECT;
		}
		if (oldMethod.isDerivativeOrMaintenance() && newMethod.isGenerative() && !oldGermplasm.isTerminalAncestor()) {
			return UpdateGroupSourceAction.RECURSIVE;
		}
		final boolean isGpidUpdateDetected = this.isGpidUpdateDetected(oldGermplasm, newGermplasm.getGpid1(), newGermplasm.getGpid2());
		if (oldMethod.isDerivativeOrMaintenance() && newMethod.isDerivativeOrMaintenance() && isGpidUpdateDetected) {
			if (oldGermplasm.isTerminalAncestor()) {
				return UpdateGroupSourceAction.DIRECT;
			}
			return UpdateGroupSourceAction.RECURSIVE;
		}
		return UpdateGroupSourceAction.NONE;
	}

	private boolean isPedigreeUpdateDetected(final Germplasm germplasmBeforeUpdate, final Method newMethod, final Integer newGpid1,
		final Integer newGpid2,
		final List<Integer> newOtherProgenitors) {
		return !newMethod.getMid().equals(germplasmBeforeUpdate.getMethod().getMid()) || this.isGpidUpdateDetected(germplasmBeforeUpdate,
			newGpid1, newGpid2)
			|| !germplasmBeforeUpdate.otherProgenitorsGidsEquals(newOtherProgenitors);
	}

	private boolean isGpidUpdateDetected(final Germplasm germplasmBeforeUpdate, final Integer newGpid1, final Integer newGpid2) {
		return !(germplasmBeforeUpdate.getGpid1().equals(newGpid1) && germplasmBeforeUpdate.getGpid2().equals(newGpid2));
	}

	private List<Integer> getOtherProgenitorsFinal(final Germplasm germplasmBeforeUpdate, final Integer newGpid2,
		final List<Integer> newOtherProgenitors) {
		if (Objects.nonNull(newGpid2)) {
			return (CollectionUtils.isEmpty(newOtherProgenitors)) ? new ArrayList<>() : newOtherProgenitors;
		}
		return germplasmBeforeUpdate.getOtherProgenitors().stream().map(Progenitor::getProgenitorGid).collect(Collectors.toList());
	}

	private Map<String, Germplasm> loadProgenitors(final Integer gpid1, final Integer gpid2) {
		final Map<String, Germplasm> progenitorsMap = new HashMap<>();
		if (!gpid1.equals(0)) {
			progenitorsMap.put(String.valueOf(gpid1), this.daoFactory.getGermplasmDao().getById(gpid1));
		}
		if (!gpid2.equals(0) && !progenitorsMap.containsKey(String.valueOf(gpid2))) {
			progenitorsMap.put(String.valueOf(gpid2), this.daoFactory.getGermplasmDao().getById(gpid2));
		}
		return progenitorsMap;
	}

	private Optional<String> getCreatedByWorkbenchUserName(final Integer userId) {
		final WorkbenchUser workbenchUser = this.userService.getUserById(userId);
		if (workbenchUser != null) {
			return Optional.of(workbenchUser.getName());
		}
		return Optional.empty();
	}

	public void setCropService(final CropService cropService) {
		this.cropService = cropService;
	}

	public void setOntologyDataManager(final OntologyDataManager ontologyDataManager) {
		this.ontologyDataManager = ontologyDataManager;
	}

	public void setOntologyVariableDataManager(
		final OntologyVariableDataManager ontologyVariableDataManager) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}

	public void setGermplasmNameTypeService(final GermplasmNameTypeService germplasmNameTypeService) {
		this.germplasmNameTypeService = germplasmNameTypeService;
	}

	public void setFileMetadataService(final FileMetadataService fileMetadataService) {
		this.fileMetadataService = fileMetadataService;
	}

}
