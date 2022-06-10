package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.file.FileMetadataFilterRequest;
import org.generationcp.middleware.constant.SystemNameTypes;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.germplasm.GermplasmBasicDetailsDto;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergeRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergedDto;
import org.generationcp.middleware.domain.germplasm.GermplasmProgenyDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.ProgenitorsUpdateRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.pojos.ims.EntityType;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.util.Util;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GermplasmServiceImplIntegrationTest extends IntegrationTestBase {

	public static final String DRVNM = "DRVNM";
	public static final String NOTE = "NOTE_AA_text";
	public static final String NOLOC = "NOLOC";

	private static final String DEFAULT_BIBREF_FIELD = "-";

	private final String programUUID = RandomStringUtils.randomAlphabetic(16);

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmService germplasmService;

	@Autowired
	private StudyEntryService studyEntryService;

	@Autowired
	private LocationDataManager locationDataManager;

	private Integer noLocationId, variableTypeId, attributeId, clientId, userId, puiNameTypeId;
	private String creationDate, name, germplasmPUI, germplasmUUID, reference, note;
	private Map<String, String> names, attributes;
	private Method derivativeMethod, generativeMethod;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.noLocationId = this.daoFactory.getLocationDAO().getByAbbreviations(Arrays.asList(NOLOC)).get(0).getLocid();
		this.derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		this.generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		this.variableTypeId = this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", DRVNM).getFldno();
		this.attributeId = this.daoFactory.getCvTermDao().getByNameAndCvId(NOTE, CvId.VARIABLES.getId()).getCvTermId();
		this.creationDate = "20201212";
		this.name = RandomStringUtils.randomAlphabetic(10);
		this.germplasmPUI = RandomStringUtils.randomAlphabetic(20);
		this.germplasmUUID = RandomStringUtils.randomAlphabetic(10);
		this.clientId = 1;
		this.reference = RandomStringUtils.randomAlphabetic(20);
		this.note = RandomStringUtils.randomAlphabetic(10);
		this.names = new HashMap<>();
		this.names.put(DRVNM, this.name);
		this.attributes = new HashMap<>();
		this.attributes.put(NOTE, this.note);
		this.userId = this.findAdminUser();
		this.puiNameTypeId = this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", "PUI").getFldno();

	}

	@Test
	public void testImportGermplasmUpdates_NewNamesAndAttributes() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		final String germplasmPUI = RandomStringUtils.randomAlphabetic(40);
		germplasmUpdateDTO.getNames().put(GermplasmServiceImpl.PUI, germplasmPUI);
		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		final Map<Integer, Name> namesMap = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid())).stream()
			.collect(Collectors.toMap(Name::getTypeId, Function
				.identity()));
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Arrays.asList(germplasm.getGid()));

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(newLocation.getLocid(), savedGermplasm.getLocationId());
		assertEquals(creationDate, savedGermplasm.getGdate().intValue());
		assertNotNull(savedGermplasm.getReferenceId());
		assertFalse(namesMap.isEmpty());
		assertFalse(attributes.isEmpty());

		final Name savedDerivativeName = namesMap.get(newNameCode.getFldno());
		assertEquals(newNameCode.getFldno(), savedDerivativeName.getTypeId());
		assertEquals(1, savedDerivativeName.getNstat().intValue());
		assertEquals(newLocation.getLocid(), savedDerivativeName.getLocationId());
		assertEquals(Util.getCurrentDateAsIntegerValue(), savedDerivativeName.getNdate());
		assertEquals("Name for " + germplasm.getGid(), savedDerivativeName.getNval());

		final Name saveGermplasmPUI = namesMap.get(this.puiNameTypeId);
		assertEquals(this.puiNameTypeId, saveGermplasmPUI.getTypeId());
		assertEquals(0, saveGermplasmPUI.getNstat().intValue());
		assertEquals(newLocation.getLocid(), saveGermplasmPUI.getLocationId());
		assertEquals(Util.getCurrentDateAsIntegerValue(), saveGermplasmPUI.getNdate());
		assertEquals(germplasmPUI, saveGermplasmPUI.getNval());

		final Attribute savedAttribute = attributes.get(0);
		assertEquals(this.attributeId, savedAttribute.getTypeId());
		assertEquals(newLocation.getLocid(), savedAttribute.getLocationId());
		assertEquals(Util.getCurrentDateAsIntegerValue(), savedAttribute.getAdate());
		assertEquals("Note for " + germplasm.getGid(), savedAttribute.getAval());

	}

	@Test
	public void testImportGermplasmUpdates_UpdateNamesAndAttributes() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Save previous values for DRVNM and PUI names
		this.daoFactory.getNameDao().save(new Name(null, germplasm, newNameCode.getFldno(), 0,
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));
		this.daoFactory.getNameDao().save(new Name(null, germplasm, this.puiNameTypeId, 0,
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasm.getGid(), this.attributeId, "", null,
				germplasm.getLocationId(),
				0, germplasm.getGdate()));

		final String newGermplasmPUI = RandomStringUtils.randomAlphabetic(40);
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		germplasmUpdateDTO.getNames().put(GermplasmServiceImpl.PUI, newGermplasmPUI);
		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));
		this.sessionProvder.getSession().flush();

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);
		final Map<Integer, Name> namesMap = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid())).stream()
			.collect(Collectors.toMap(Name::getTypeId, Function
				.identity()));
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Arrays.asList(germplasm.getGid()));

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(newLocation.getLocid(), savedGermplasm.getLocationId());
		assertEquals(creationDate, savedGermplasm.getGdate().intValue());
		assertNotNull(savedGermplasm.getReferenceId());
		assertFalse(namesMap.isEmpty());
		assertFalse(attributes.isEmpty());

		final Name savedDerivativeName = namesMap.get(newNameCode.getFldno());
		assertEquals(newNameCode.getFldno(), savedDerivativeName.getTypeId());
		assertEquals(1, savedDerivativeName.getNstat().intValue());
		assertEquals(newLocation.getLocid(), savedDerivativeName.getLocationId());
		assertEquals(this.creationDate, savedDerivativeName.getNdate().toString());
		assertEquals("Name for " + germplasm.getGid(), savedDerivativeName.getNval());

		final Name saveGermplasmPUI = namesMap.get(this.puiNameTypeId);
		assertEquals(this.puiNameTypeId, saveGermplasmPUI.getTypeId());
		assertEquals(0, saveGermplasmPUI.getNstat().intValue());
		assertEquals(newLocation.getLocid(), saveGermplasmPUI.getLocationId());
		assertEquals(this.creationDate, saveGermplasmPUI.getNdate().toString());
		assertEquals(newGermplasmPUI, saveGermplasmPUI.getNval());

		final Attribute savedAttribute = attributes.get(0);
		assertEquals(this.attributeId, savedAttribute.getTypeId());
		assertEquals(newLocation.getLocid(), savedAttribute.getLocationId());
		assertEquals(this.creationDate, savedAttribute.getAdate().toString());
		assertEquals("Note for " + germplasm.getGid(), savedAttribute.getAval());

	}

	@Test
	public void testImportGermplasmUpdates_ThrowException_PreferredNameHasDuplicates() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create Duplicate PreferredName assigned
		this.daoFactory.getNameDao().save(new Name(null, germplasm, newNameCode.getFldno(), 1,
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);

		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.preferred.name.duplicate.names"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_ThrowException_PreferredNameDoesntExist() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		// Set invalid preferred name code.
		germplasmUpdateDTO.setPreferredNameType("Some Non Existing Code");

		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.preferred.name.doesnt.exist"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_ThrowException_DuplicateNames() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		// Create duplicate names and attributes
		this.daoFactory.getNameDao().save(new Name(null, germplasm, newNameCode.getFldno(), 0,
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));
		this.daoFactory.getNameDao().save(new Name(null, germplasm, newNameCode.getFldno(), 0,
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.duplicate.names"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_ThrowException_PUIAlreadyExists() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm2 = this.createGermplasm(method, null, null, 0, 0, 0, this.germplasmPUI);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		germplasmUpdateDTO.getNames().put(GermplasmServiceImpl.PUI, this.germplasmPUI);

		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.germplasm.pui.exists"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_ShouldBeOk_WhenUsingSamePUI() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, this.germplasmPUI);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.empty(),
				Optional.of(newLocation), creationDate);
		germplasmUpdateDTO.getNames().put(GermplasmServiceImpl.PUI, this.germplasmPUI);

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		this.sessionProvder.getSession().flush();

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);
		final Map<Integer, Name> namesMap = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid())).stream()
			.collect(Collectors.toMap(Name::getTypeId, Function
				.identity()));
		assertFalse(namesMap.isEmpty());

		final Name saveGermplasmPUI = namesMap.get(this.puiNameTypeId);
		assertEquals(this.puiNameTypeId, saveGermplasmPUI.getTypeId());
		assertEquals(0, saveGermplasmPUI.getNstat().intValue());
		assertEquals(newLocation.getLocid(), saveGermplasmPUI.getLocationId());
		assertEquals(this.creationDate, saveGermplasmPUI.getNdate().toString());
		assertEquals(this.germplasmPUI, saveGermplasmPUI.getNval());
	}

	@Test
	public void testGetGermplasmByGIDs() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final List<Germplasm> germplasmByGIDs = this.germplasmService.getGermplasmByGIDs(Arrays.asList(germplasm.getGid()));
		assertThat(germplasmByGIDs, hasSize(1));

		final Germplasm actualGermplasm = germplasmByGIDs.get(0);
		assertNotNull(actualGermplasm);
		assertThat(actualGermplasm.getGid(), is(germplasm.getGid()));
	}

	@Test
	public void test_getPlotCodeValues_OK() {
		final String plotCodeValue = UUID.randomUUID().toString();

		final Germplasm germplasmWithoutPlotCode = this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0, null);
		final Germplasm germplasmWithPlotCode = this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0, null);

		final CVTerm plotCodeVariable =
			this.daoFactory.getCvTermDao().getByNameAndCvId("PLOTCODE_AP_text", CvId.VARIABLES.getId());

		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasmWithPlotCode.getGid(), plotCodeVariable.getCvTermId(),
				plotCodeValue, null,
				germplasmWithPlotCode.getLocationId(),
				0, germplasmWithPlotCode.getGdate()));

		final Map<Integer, String> actualPlotCodeValues = this.germplasmService.getPlotCodeValues(
			ImmutableSet.of(germplasmWithoutPlotCode.getGid(), germplasmWithPlotCode.getGid()));
		assertThat(actualPlotCodeValues.size(), is(2));
		assertTrue(actualPlotCodeValues.containsKey(germplasmWithoutPlotCode.getGid()));
		assertThat(actualPlotCodeValues.get(germplasmWithoutPlotCode.getGid()), is(GermplasmListDataDAO.SOURCE_UNKNOWN));

		assertTrue(actualPlotCodeValues.containsKey(germplasmWithPlotCode.getGid()));
		assertThat(actualPlotCodeValues.get(germplasmWithPlotCode.getGid()), is(plotCodeValue));
	}

	@Test
	public void testImportGermplasmUpdates_UpdateProgenitors_BreedingMethodNotSpecified() {

		final Method method = this.createBreedingMethod("GEN", 2);
		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Cretae GermplasmUpdateDTO with empty method.
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.empty(), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmMale.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(method.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmMale.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodTypeMismatch_HasProgeny() {

		// If the germplasm has a GENERATIVE type then the new breeding method has to be also GENERATIVE, if not, it should throw an error.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasmWithDescendants(method);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		final List<GermplasmUpdateDTO> germplasmUpdateDTOList = Collections.singletonList(germplasmUpdateDTO);
		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, germplasmUpdateDTOList);
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.breeding.method.mismatch"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodTypeMismatch_NoProgeny() {

		// If the germplasm has a GENERATIVE type then the new breeding method has to be also GENERATIVE, if not, it should throw an error.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		final List<GermplasmUpdateDTO> germplasmUpdateDTOList = Collections.singletonList(germplasmUpdateDTO);
		assertThat(this.germplasmService.importGermplasmUpdates(this.programUUID, germplasmUpdateDTOList), hasSize(1));
	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodNumberOfProgenitorsMismatch() {

		// If the germplasm has a GENERATIVE type then the new breeding method has to be also GENERATIVE, and the expected number of
		// progenitors should be the same. If not, it should throw an error.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);
		final Germplasm germplasmWithDescendants = this.createGermplasmWithDescendants(method);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasmWithDescendants.getGid(), germplasmWithDescendants.getGermplasmUUID(),
				Optional.of(newMethod), Optional.empty(), null);

		final List<GermplasmUpdateDTO> germplasmUpdateDTOList = Collections.singletonList(germplasmUpdateDTO);
		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, germplasmUpdateDTOList);
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.number.of.progenitors.mismatch"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodMutationIsNotAllowed() {

		// Breeding method with numberOfPregenitors = 1 is a mutation method. This is not yet supported.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 1);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		final List<GermplasmUpdateDTO> germplasmUpdateDTOList = Collections.singletonList(germplasmUpdateDTO);
		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, germplasmUpdateDTOList);
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.mutation.method.is.not.supported"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_Generative_KnownParents() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign known parents
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmMale.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmMale.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_Generative_KnownParents_WithOtherProgenitors() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmOtherProgenitors = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign known parents
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmMale.getGid());
		germplasmUpdateDTO.getProgenitors().put("PROGENITOR 3", germplasmOtherProgenitors.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(3, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmMale.getGid(), savedGermplasm.getGpid2());
		assertFalse(savedGermplasm.getOtherProgenitors().isEmpty());

	}

	@Test
	public void testImportGermplasmUpdates_Generative_KnownParents_WithoutOtherProgenitors() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasm);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign known parents
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmMale.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmMale.getGid(), savedGermplasm.getGpid2());
		assertTrue(savedGermplasm.getOtherProgenitors().isEmpty());

	}

	@Test
	public void testImportGermplasmUpdates_Generative_UpdateCrossesUnnownMaleOrFemaleParent() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign unknown male parent
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 0);

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(0, savedGermplasm.getGpid2().intValue());

		// Assign unknown female parent
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmMale.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(0, savedGermplasm.getGpid1().intValue());
		assertEquals(germplasmMale.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_ImmediateSourceShouldBelongToGroup() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmDescendant2.getGid());
		// Assign immediate source germplasm with a group source (female parent) different from current group source.
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant.getGid());

		final List<GermplasmUpdateDTO> germplasmUpdateDTOList = Collections.singletonList(germplasmUpdateDTO);
		try {
			this.germplasmService.importGermplasmUpdates(this.programUUID, germplasmUpdateDTOList);
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(
				e.getErrorCodeParamsMultiMap().containsKey("import.germplasm.invalid.immediate.source.group"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UpdateImmediateAndGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmWithDescendants.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant2.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmWithDescendants.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmDescendant2.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UpdateUnknownImmediateSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmFemale.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 0);

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmFemale.getGid(), savedGermplasm.getGpid1());
		assertEquals(0, savedGermplasm.getGpid2().intValue());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UpdateUnknownGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmWithDescendants.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmDescendant.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UnknownGroupSource_ImmediateSourceIsGenerative() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmParent = this.createGermplasm(generativeMethod, null, null, 0, 0, 0, null);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmParent);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmParent.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmParent.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmParent.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UnknownGroupSource_ImmediateSourceIsTerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmParent = this.createGermplasm(method, null, null, 0, 0, 0, null);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmParent);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmParent.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmParent.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmParent.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_TerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(0, savedGermplasm.getGnpgs().intValue());
		assertEquals(0, savedGermplasm.getGpid1().intValue());
		assertEquals(0, savedGermplasm.getGpid2().intValue());
	}

	@Test
	public void testImportGermplasmUpdates_FemaleAndMaleParentsAreSame_ParentIsTerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create a terminal node germplasm
		final Germplasm parentGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, parentGermplasm.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, parentGermplasm.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(parentGermplasm.getGid(), savedGermplasm.getGpid1());
		assertEquals(parentGermplasm.getGid(), savedGermplasm.getGpid2());
	}

	@Test
	public void testImportGermplasmUpdates_FemaleAndMaleParentsAreSame_ParentIsGenerative() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create a generative germplasm
		final Germplasm parentGermplasm = this.createGermplasm(generativeMethod, null, null, 0, 0, 0, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, parentGermplasm.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, parentGermplasm.getGid());

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(parentGermplasm.getGid(), savedGermplasm.getGpid1());
		assertEquals(parentGermplasm.getGid(), savedGermplasm.getGpid2());
	}

	@Test
	public void testImportGermplasmUpdates_FemaleAndMaleParentsAreNull() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm femaleParent = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm maleParent = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 2, femaleParent.getGid(), maleParent.getGid(), null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(method), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, null);

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(method.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasm.getGpid1(), savedGermplasm.getGpid1());
		assertEquals(germplasm.getGpid2(), savedGermplasm.getGpid2());
	}

	@Test
	public void testImportGermplasmUpdates_ReferenceIsEmptyString() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Bibref reference = this.createReference(UUID.randomUUID().toString());
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, reference, null);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(method), Optional.empty(), null);
		germplasmUpdateDTO.setReference("");

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertNotNull(savedGermplasm.getBibref());
		assertThat(savedGermplasm.getBibref().getRefid(), is(reference.getRefid()));
		assertThat(savedGermplasm.getBibref().getAnalyt(), is(reference.getAnalyt()));

	}

	@Test
	public void test_importGermplasm_saveGenerativeWithNoProgenitors_Ok() {

		final GermplasmImportDTO germplasmImportDto = new GermplasmImportDTO(this.clientId, null, NOLOC, this.generativeMethod.getMcode(),
			this.reference, DRVNM, this.names, this.attributes, this.creationDate
			, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);
		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(0));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(0));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));

		final Bibref bibref = this.daoFactory.getBibrefDAO().getById(germplasm.getReferenceId());
		assertThat(bibref.getAnalyt(), equalTo(this.reference));

		final List<Name> savedNames = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		assertThat(savedNames.size(), equalTo(1));
		assertThat(savedNames.get(0).getNval(), equalTo(this.name));
		assertThat(savedNames.get(0).getNstat(), equalTo(1));
		assertThat(savedNames.get(0).getCreatedBy(), equalTo(this.userId));
		assertThat(savedNames.get(0).getLocationId(), equalTo(this.noLocationId));
		assertThat(savedNames.get(0).getTypeId(), equalTo(this.variableTypeId));

		final List<Attribute> savedAttributes = this.daoFactory.getAttributeDAO().getByGID(germplasm.getGid());
		assertThat(savedAttributes.size(), equalTo(1));
		assertThat(savedAttributes.get(0).getAval(), equalTo(this.note));
		assertThat(savedAttributes.get(0).getTypeId(), equalTo(this.attributeId));
		assertThat(savedAttributes.get(0).getCreatedBy(), equalTo(this.userId));
		assertThat(savedAttributes.get(0).getLocationId(), equalTo(this.noLocationId));
	}

	@Test
	public void test_importGermplasm_saveDerivativeWithNoProgenitors_Ok() {
		final GermplasmImportDTO germplasmImportDto = new GermplasmImportDTO(this.clientId, null, NOLOC, this.derivativeMethod.getMcode(),
			this.reference, DRVNM, this.names, this.attributes, this.creationDate
			, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);
		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(0));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));

		final Bibref bibref = this.daoFactory.getBibrefDAO().getById(germplasm.getReferenceId());
		assertThat(bibref.getAnalyt(), equalTo(this.reference));

		final List<Name> savedNames = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		assertThat(savedNames.size(), equalTo(1));
		assertThat(savedNames.get(0).getNval(), equalTo(this.name));
		assertThat(savedNames.get(0).getNstat(), equalTo(1));
		assertThat(savedNames.get(0).getCreatedBy(), equalTo(this.userId));
		assertThat(savedNames.get(0).getLocationId(), equalTo(this.noLocationId));
		assertThat(savedNames.get(0).getTypeId(), equalTo(this.variableTypeId));

		final List<Attribute> savedAttributes = this.daoFactory.getAttributeDAO().getByGID(germplasm.getGid());
		assertThat(savedAttributes.size(), equalTo(1));
		assertThat(savedAttributes.get(0).getAval(), equalTo(this.note));
		assertThat(savedAttributes.get(0).getTypeId(), equalTo(this.attributeId));
		assertThat(savedAttributes.get(0).getCreatedBy(), equalTo(this.userId));
		assertThat(savedAttributes.get(0).getLocationId(), equalTo(this.noLocationId));
	}

	@Test
	public void test_importGermplasm_matchFound_ok() {
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 0, 0, 0, this.germplasmPUI);
		this.addName(germplasm, this.variableTypeId, this.name, this.noLocationId, this.creationDate, 1);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.FOUND));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().get(0), is(germplasm.getGid()));

	}

	@Test
	public void test_importGermplasm_matchNotFound_ok() {
		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));
		this.assertGermplasmPuiWasSaved(response.get(this.clientId).getGids().get(0));
	}

	@Test
	public void test_importGermplasm_saveGenerativeWithOneProgenitorSpecified_Ok() {
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0, null);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, "0", progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(2));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
		this.assertGermplasmPuiWasSaved(germplasm.getGid());
	}

	@Test
	public void test_importGermplasm_saveGenerativeWithBothProgenitorsSpecified_Ok() {
		final String progenitor1FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1FemaleParent =
			this.createGermplasm(this.generativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0, null);

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0, null);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0, null);

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor1GUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(2));
		assertThat(germplasm.getGpid1(), equalTo(progenitor1.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
		this.assertGermplasmPuiWasSaved(germplasm.getGid());
	}

	private void assertGermplasmPuiWasSaved(final Integer gid) {
		final GermplasmMatchRequestDto requestDto = new GermplasmMatchRequestDto();
		requestDto.setGermplasmPUIs(Collections.singletonList(this.germplasmPUI));
		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(requestDto, null);
		Assert.assertEquals(1, germplasmMatches.size());
		Assert.assertEquals(this.germplasmPUI, germplasmMatches.get(0).getGermplasmPUI());
		Assert.assertEquals(gid, germplasmMatches.get(0).getGid());
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_importGermplasm_saveDerivativeWithBothProgenitorsSpecified_ThrowsException_WhenGroupIsNotValid() {
		final String progenitor1FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1FemaleParent =
			this.createGermplasm(this.derivativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0, null);

		this.createGermplasm(this.derivativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0, null);
		this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, 0, 0, null);

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor1GUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);
	}

	@Test
	public void test_importGermplasm_saveDerivativeWithBothProgenitors_Ok() {
		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent =
			this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0, null);

		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0, null);

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor2FemaleParentGUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(progenitor2FemaleParent.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
		this.assertGermplasmPuiWasSaved(germplasm.getGid());
	}

	@Test
	public void test_importGermplasm_saveDerivativeWithProgenitor2Specified_Ok() {

		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent =
			this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0, null);

		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0, null);

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, "0", progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(progenitor2FemaleParent.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getCreatedBy(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
		this.assertGermplasmPuiWasSaved(germplasm.getGid());
	}

	@Test
	public void test_importGermplasm_ThrowsException_WhenGermplasmPUIExistsAlready() {

		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, this.germplasmUUID, null, -1, 0, 0, this.germplasmPUI);

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmPUI, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		try {
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);
			Assert.fail("Expected to throw exception for existing PUI but did not.");
		} catch (final MiddlewareRequestException exception) {
			assertTrue(exception.getErrorCodeParamsMultiMap().containsKey("import.germplasm.pui.exists"));
		}
	}

	@Test
	public void test_importGermplasm_ThrowsException_WhenGermplasmPUIInNamesExistsAlready() {

		final Germplasm germplasm = this.createGermplasm(this.derivativeMethod, this.germplasmPUI, null, -1, 0, 0, this.germplasmPUI);

		this.names.put(GermplasmImportRequest.PUI_NAME_TYPE, this.germplasmPUI);
		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, null, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		try {
			this.germplasmService.importGermplasm(ContextHolder.getCurrentCrop(), this.programUUID, germplasmImportRequestDto);
			Assert.fail("Expected to throw exception for existing PUI but did not.");
		} catch (final MiddlewareRequestException exception) {
			assertTrue(exception.getErrorCodeParamsMultiMap().containsKey("import.germplasm.pui.exists"));
		}
	}

	@Test
	public void test_getGermplasmDtoById_Ok() {

		final Germplasm progenitor1FemaleParent =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 2, progenitor1FemaleParent.getGid(),
				0, null);
		final Germplasm progenitor2 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		final Location location = this.createLocation();

		final Germplasm germplasm =
			this.createGermplasm(this.generativeMethod, null, location, 3, progenitor1.getGid(),
				progenitor2.getGid(), null);

		final Name preferredName =
			this.addName(germplasm, this.variableTypeId, RandomStringUtils.randomAlphabetic(10), location.getLocid(),
				this.creationDate, 1);
		this.addName(germplasm, this.puiNameTypeId, RandomStringUtils.randomAlphabetic(10), location.getLocid(),
			this.creationDate, 0);

		final Germplasm progenitor3 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);
		final Progenitor progenitor = this.addProgenitor(germplasm, progenitor3);

		final GermplasmDto germplasmDto = this.germplasmService.getGermplasmDtoById(germplasm.getGid());
		assertThat(germplasmDto.getBreedingMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasmDto.getGpid1(), equalTo(progenitor1.getGid()));
		assertThat(germplasmDto.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasmDto.getBreedingLocationId(), equalTo(location.getLocid()));
		assertThat(germplasmDto.getCreationDate(), equalTo(this.creationDate));
		assertThat(germplasmDto.getPreferredName(), equalTo(preferredName.getNval()));
		assertThat(germplasmDto.getNames(), hasSize(2));
		assertThat(germplasmDto.getOtherProgenitors(), hasSize(1));
		assertThat(germplasmDto.getOtherProgenitors().get(0), equalTo(progenitor.getProgenitorGid()));
		assertThat(germplasmDto.getCreatedBy(), equalToIgnoringCase("admin"));
		assertThat(germplasmDto.getCreatedByUserId(), equalTo(1));
	}

	@Test
	public void test_getGermplasmProgenitorDetails_whenMethodIsDerivative_Ok() {
		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent =
			this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0, null);
		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0, null);
		final Germplasm germplasm =
			this.createGermplasm(this.derivativeMethod, this.germplasmUUID, null, -1, progenitor2FemaleParent.getGid(),
				progenitor2.getGid(), null);

		final ProgenitorsDetailsDto progenitorsDetailsDto = this.germplasmService.getGermplasmProgenitorDetails(germplasm.getGid());
		assertThat(progenitorsDetailsDto.getBreedingMethodCode(), equalTo(this.derivativeMethod.getMcode()));
		assertThat(progenitorsDetailsDto.getBreedingMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(progenitorsDetailsDto.getBreedingMethodName(), equalTo(this.derivativeMethod.getMname()));
		assertThat(progenitorsDetailsDto.getBreedingMethodType(), equalTo(this.derivativeMethod.getMtype()));
		assertThat(progenitorsDetailsDto.getFemaleParent(), is(nullValue()));
		assertThat(progenitorsDetailsDto.getMaleParents(), is(nullValue()));
		assertThat(progenitorsDetailsDto.getGroupSource().getGid(), equalTo(progenitor2FemaleParent.getGid()));
		assertThat(progenitorsDetailsDto.getImmediateSource().getGid(), equalTo(progenitor2.getGid()));
	}

	@Test
	public void test_getGermplasmProgenitorDetails_whenMethodIsGenerative_Ok() {
		final String progenitor1FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1FemaleParent =
			this.createGermplasm(this.generativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0, null);

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0, null);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0, null);

		final Germplasm germplasm =
			this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 3, progenitor1.getGid(), progenitor2.getGid(), null);
		final Germplasm progenitor3 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);
		this.addProgenitor(germplasm, progenitor3);
		this.sessionProvder.getSession().flush();

		final ProgenitorsDetailsDto progenitorsDetailsDto = this.germplasmService.getGermplasmProgenitorDetails(germplasm.getGid());
		final List<Integer> maleParentsGids =
			progenitorsDetailsDto.getMaleParents().stream().map(GermplasmDto::getGid).collect(Collectors.toList());
		assertThat(progenitorsDetailsDto.getBreedingMethodCode(), equalTo(this.generativeMethod.getMcode()));
		assertThat(progenitorsDetailsDto.getBreedingMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(progenitorsDetailsDto.getBreedingMethodName(), equalTo(this.generativeMethod.getMname()));
		assertThat(progenitorsDetailsDto.getBreedingMethodType(), equalTo(this.generativeMethod.getMtype()));
		assertThat(progenitorsDetailsDto.getGroupSource(), is(nullValue()));
		assertThat(progenitorsDetailsDto.getImmediateSource(), is(nullValue()));
		assertThat(progenitorsDetailsDto.getFemaleParent().getGid(), equalTo(progenitor1.getGid()));
		assertThat(progenitorsDetailsDto.getMaleParents(), hasSize(2));
		assertThat(maleParentsGids, contains(progenitor2.getGid(), progenitor3.getGid()));
	}

	@Test
	public void test_updateGermplasmPedigree_noChangeIsDetected_Ok() {
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, progenitor1GUID, null, 2, 0, 0, null);

		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0, null);

		final Germplasm germplasm =
			this.createGermplasm(this.generativeMethod, this.germplasmPUI, null, 3, progenitor1.getGid(), progenitor2.getGid(), null);

		final Germplasm progenitor3 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		this.addProgenitor(germplasm, progenitor3);

		final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto =
			new ProgenitorsUpdateRequestDto(this.generativeMethod.getMid(), null, null, null);

		this.germplasmService.updateGermplasmPedigree(germplasm.getGid(), progenitorsUpdateRequestDto);

		this.daoFactory.getGermplasmDao().refresh(germplasm);

		assertThat(germplasm.getGpid1(), equalTo(progenitor1.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getOtherProgenitors(), hasSize(1));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
	}

	@Test
	public void test_updateGermplasmPedigree_directUpdate_Ok() {

		final Germplasm nodeA =
			this.createGermplasm(this.generativeMethod, null, null, 2, 0, 0, null);
		final Germplasm nodeB = this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0, null);

		final Germplasm nodeC =
			this.createGermplasm(this.generativeMethod, null, null, 3, nodeA.getGid(), nodeB.getGid(), null);
		final Germplasm otherProgenitor =
			this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0, null);
		this.addProgenitor(nodeC, otherProgenitor);
		final Germplasm nodeD =
			this.createGermplasm(this.derivativeMethod, null, null, -1, nodeC.getGid(), nodeC.getGid(), null);
		final Germplasm nodeE =
			this.createGermplasm(this.derivativeMethod, null, null, -1, nodeC.getGid(), nodeD.getGid(), null);

		final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto =
			new ProgenitorsUpdateRequestDto(this.derivativeMethod.getMid(), nodeA.getGid(), nodeA.getGid(), null);

		this.germplasmService.updateGermplasmPedigree(nodeC.getGid(), progenitorsUpdateRequestDto);
		this.sessionProvder.getSession().flush();

		this.daoFactory.getGermplasmDao().refresh(nodeC);
		this.daoFactory.getGermplasmDao().refresh(nodeD);
		this.daoFactory.getGermplasmDao().refresh(nodeE);

		assertThat(nodeC.getGpid1(), equalTo(nodeA.getGid()));
		assertThat(nodeC.getGpid2(), equalTo(nodeA.getGid()));
		assertThat(nodeC.getOtherProgenitors(), hasSize(0));
		assertThat(nodeC.getMethodId(), equalTo(this.derivativeMethod.getMid()));

		assertThat(nodeD.getGpid1(), equalTo(nodeA.getGid()));
		assertThat(nodeD.getGpid2(), equalTo(nodeC.getGid()));
		assertThat(nodeD.getOtherProgenitors(), hasSize(0));
		assertThat(nodeD.getMethodId(), equalTo(this.derivativeMethod.getMid()));

		assertThat(nodeE.getGpid1(), equalTo(nodeA.getGid()));
		assertThat(nodeE.getGpid2(), equalTo(nodeD.getGid()));
		assertThat(nodeE.getOtherProgenitors(), hasSize(0));
		assertThat(nodeE.getMethodId(), equalTo(this.derivativeMethod.getMid()));
	}

	@Test
	public void test_updateGermplasmPedigree_recursiveUpdate_Ok() {
		final Germplasm nodeA =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 2, 0, 0, null);
		final Germplasm nodeB = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		final Germplasm nodeC =
			this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 3, nodeA.getGid(), nodeB.getGid(), null);
		final Germplasm otherProgenitor =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);
		this.addProgenitor(nodeC, otherProgenitor);
		final Germplasm nodeD =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeC.getGid(),
				null);
		final Germplasm nodeE =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeD.getGid(),
				null);
		final Germplasm nodeF =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeE.getGid(),
				null);

		final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto =
			new ProgenitorsUpdateRequestDto(this.derivativeMethod.getMid(), 0, 0, null);

		this.germplasmService.updateGermplasmPedigree(nodeD.getGid(), progenitorsUpdateRequestDto);
		this.sessionProvder.getSession().flush();

		this.daoFactory.getGermplasmDao().refresh(nodeD);
		this.daoFactory.getGermplasmDao().refresh(nodeE);
		this.daoFactory.getGermplasmDao().refresh(nodeF);

		assertThat(nodeD.getGpid1(), equalTo(0));
		assertThat(nodeD.getGpid2(), equalTo(0));
		assertThat(nodeD.getOtherProgenitors(), hasSize(0));
		assertThat(nodeD.getMethodId(), equalTo(this.derivativeMethod.getMid()));

		assertThat(nodeE.getGpid1(), equalTo(nodeD.getGid()));
		assertThat(nodeE.getGpid2(), equalTo(nodeD.getGid()));
		assertThat(nodeE.getOtherProgenitors(), hasSize(0));
		assertThat(nodeE.getMethodId(), equalTo(this.derivativeMethod.getMid()));

		assertThat(nodeF.getGpid1(), equalTo(nodeD.getGid()));
		assertThat(nodeF.getGpid2(), equalTo(nodeE.getGid()));
		assertThat(nodeF.getOtherProgenitors(), hasSize(0));
		assertThat(nodeF.getMethodId(), equalTo(this.derivativeMethod.getMid()));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_updateGermplasmPedigree_recursiveUpdate_maxRecursionLevelReached_throwsException() {
		final Germplasm nodeA =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 2, 0, 0, null);
		final Germplasm nodeB = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		final Germplasm nodeC =
			this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 3, nodeA.getGid(), nodeB.getGid(), null);
		final Germplasm otherProgenitor =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);
		this.addProgenitor(nodeC, otherProgenitor);

		final Germplasm nodeD =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeC.getGid(),
				null);
		final Germplasm nodeE =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeD.getGid(),
				null);
		final Germplasm nodeF =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeE.getGid(),
				null);
		final Germplasm nodeG =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeF.getGid(),
				null);
		final Germplasm nodeH =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeG.getGid(),
				null);
		final Germplasm nodeI =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeH.getGid(),
				null);
		this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeI.getGid(), null);

		final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto =
			new ProgenitorsUpdateRequestDto(this.derivativeMethod.getMid(), 0, 0, null);

		this.germplasmService.updateGermplasmPedigree(nodeD.getGid(), progenitorsUpdateRequestDto);
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_updateGermplasmPedigree_setParentsToAChild_throwsException() {
		final Germplasm nodeA =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 2, 0, 0, null);
		final Germplasm nodeB = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);

		final Germplasm nodeC =
			this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 3, nodeA.getGid(), nodeB.getGid(), null);

		final Germplasm otherProgenitor =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null);
		this.addProgenitor(nodeC, otherProgenitor);

		final Germplasm nodeD =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeC.getGid(),
				null);
		final Germplasm nodeE =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeD.getGid(),
				null);
		final Germplasm nodeF =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeE.getGid(),
				null);
		final Germplasm nodeG =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeF.getGid(),
				null);
		final Germplasm nodeH =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeG.getGid(),
				null);
		final Germplasm nodeI =
			this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeH.getGid(),
				null);
		this.createGermplasm(this.derivativeMethod, RandomStringUtils.randomAlphabetic(10), null, -1, nodeC.getGid(), nodeI.getGid(), null);

		final ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto =
			new ProgenitorsUpdateRequestDto(this.derivativeMethod.getMid(), nodeC.getGid(), nodeD.getGid(), null);

		this.germplasmService.updateGermplasmPedigree(nodeD.getGid(), progenitorsUpdateRequestDto);
	}

	@Test
	public void testDeleteGermplasm() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		this.germplasmService.deleteGermplasm(Lists.newArrayList(germplasm.getGid()));

		assertThat(this.germplasmService.getGermplasmByGIDs(Lists.newArrayList(germplasm.getGid())), iterableWithSize(0));

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		this.sessionProvder.getSession().refresh(germplasm);

		final Germplasm deletedGermplasm = (Germplasm) this.sessionProvder.getSession().createQuery(
			String.format("select G from %s G where gid=%s",
				Germplasm.class.getCanonicalName(),
				germplasm.getGid())).uniqueResult();
		assertNotNull(deletedGermplasm);
		assertThat(deletedGermplasm.getModifiedBy(), is(this.userId));
		assertNotNull(deletedGermplasm.getModifiedDate());
	}

	@Test
	public void testGetCodeFixedGidsByGidList() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Set the germplasm as code fixed (mgid > 0)
		germplasm.setMgid(1);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasm);

		assertThat(this.germplasmService.getGermplasmByGIDs(Lists.newArrayList(germplasm.getGid())), iterableWithSize(1));
	}

	@Test
	public void testGetGidsWithOpenLots() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create germplasm with open lots
		final Lot lot =
			InventoryDetailsTestDataInitializer.createLot(1, "GERMPLSM", germplasm.getGid(), 1, 8264, 0, 1, "Comments", "InventoryId");
		this.daoFactory.getLotDao().saveOrUpdate(lot);

		final Transaction transaction =
			InventoryDetailsTestDataInitializer
				.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST",
					TransactionType.DEPOSIT.getId());
		InventoryDetailsTestDataInitializer
			.createTransaction(2.0, 0, TransactionType.DEPOSIT.getValue(), lot, 1, 1, 1, "LIST", TransactionType.DEPOSIT.getId());
		this.daoFactory.getTransactionDAO().saveOrUpdate(transaction);

		final Set<Integer> gids = this.germplasmService.getGidsWithOpenLots(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testGetGidsOfGermplasm_WithDescendants() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasmWithDescendants(method);

		this.sessionProvder.getSession().flush();

		final Set<Integer> gids =
			this.germplasmService.getGidsOfGermplasmWithDescendants(Lists.newArrayList(germplasmWithDescendants.getGid()));

		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testGetGidsOfGermplasm_WhereDescendantIsDeleted() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		// Create germplasm with deleted descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		germplasmDescendant.setDeleted(true);
		this.daoFactory.getGermplasmDao().save(germplasmDescendant);
		this.sessionProvder.getSession().flush();

		final Set<Integer> gids =
			this.germplasmService.getGidsOfGermplasmWithDescendants(Lists.newArrayList(germplasmWithDescendants.getGid()));

		Assert.assertEquals(0, gids.size());

	}

	@Test
	public void testGetGidsOfGermplasm_WithPolyCrossDescendant() {
		final Method derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendant = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm femaleParent = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm maleParent = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(generativeMethod, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(femaleParent.getGid());
		germplasmDescendant.setGpid2(maleParent.getGid());
		this.daoFactory.getGermplasmDao().save(germplasmDescendant);

		final Progenitor progenitor = new Progenitor(germplasmDescendant, 1, germplasmWithDescendant.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();

		final Set<Integer> gids =
			this.germplasmService.getGidsOfGermplasmWithDescendants(Lists.newArrayList(germplasmWithDescendant.getGid()));
		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testGetGidsOfGermplasm_WherePolyCrossDescendantIsDeleted() {
		final Method derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		// Create germplasm with deleted descendants
		final Germplasm germplasmWithDescendant = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm femaleParent = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm maleParent = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(generativeMethod, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(femaleParent.getGid());
		germplasmDescendant.setGpid2(maleParent.getGid());
		germplasmDescendant.setDeleted(true);
		this.daoFactory.getGermplasmDao().save(germplasmDescendant);

		final Progenitor progenitor = new Progenitor(germplasmDescendant, 1, germplasmWithDescendant.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();

		final Set<Integer> gids =
			this.germplasmService.getGidsOfGermplasmWithDescendants(Lists.newArrayList(germplasmWithDescendant.getGid()));
		Assert.assertEquals(0, gids.size());

	}

	@Test
	public void testGetGidsOfGermplasmWithDescendants() {
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), -1);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasmWithDescendants(method);

		final Germplasm germplasmWithoutDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);

		this.sessionProvder.getSession().flush();

		final List<Integer> lists = Arrays.asList(germplasmWithDescendants.getGid(), germplasmWithoutDescendants.getGid());
		final Set<Integer> gids =
			this.daoFactory.getGermplasmDao().getGidsOfGermplasmWithDescendants(Sets.newHashSet(lists));

		Assert.assertEquals(1, gids.size());
		Assert.assertTrue(gids.contains(germplasmWithDescendants.getGid()));
	}

	@Test
	public void testGetGermplasmUsedInStudies() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);

		// Create study with stocks associated with germplasm
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setName(RandomStringUtils.randomAlphanumeric(10));
		dmsProject.setDescription(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getDmsProjectDAO().save(dmsProject);

		final StockModel stockModel =
			new StockModel(null, null, RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5),
				RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5),
				SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId(), false);
		stockModel.setGermplasm(germplasm);
		stockModel.setProject(dmsProject);
		this.daoFactory.getStockDao().save(stockModel);

		final Set<Integer> gids =
			this.germplasmService.getGermplasmUsedInStudies(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testUpdateGermplasmBasicDetails_Ok() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final String reference = RandomStringUtils.randomAlphabetic(25);
		final Location location = this.createLocation();
		final String date = "20201010";
		final GermplasmBasicDetailsDto germplasmBasicDetailsDto = new GermplasmBasicDetailsDto();
		germplasmBasicDetailsDto.setBreedingLocationId(location.getLocid());
		germplasmBasicDetailsDto.setCreationDate(date);
		germplasmBasicDetailsDto.setReference(reference);

		this.germplasmService.updateGermplasmBasicDetails(germplasm.getGid(), germplasmBasicDetailsDto);
		final Germplasm afterSave = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		Assert.assertEquals(location.getLocid(), afterSave.getLocationId());
		Assert.assertEquals(date, String.valueOf(afterSave.getGdate()));
		final Bibref bibref = this.daoFactory.getBibrefDAO().getById(afterSave.getReferenceId());
		Assert.assertEquals(reference, bibref.getAnalyt());
	}

	@Test
	public void testImportGermplasmUpdates_AuditFields_ForReferenceAndAttrAndNames() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final Bibref reference = this.createReference(UUID.randomUUID().toString());
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0, reference, null);
		this.addAttribute(germplasm, this.attributeId, RandomStringUtils.randomAlphabetic(10));

		final Location location = this.createLocation();
		final Name name =
			this.addName(germplasm, this.variableTypeId, RandomStringUtils.randomAlphabetic(10), location.getLocid(),
				this.creationDate, 0);

		final Germplasm createdGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		assertThat(createdGermplasm.getGid(), is(germplasm.getGid()));
		assertNotNull(createdGermplasm.getBibref());
		assertThat(createdGermplasm.getBibref().getRefid(), is(reference.getRefid()));

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);

		this.germplasmService.importGermplasmUpdates(this.programUUID, Collections.singletonList(germplasmUpdateDTO));

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();
		this.sessionProvder.getSession().refresh(germplasm);

		final Germplasm updatedGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		assertThat(updatedGermplasm.getGid(), is(germplasm.getGid()));
		assertThat(updatedGermplasm.getModifiedBy(), is(this.userId));
		assertNotNull(updatedGermplasm.getModifiedDate());
		assertThat(updatedGermplasm.getNames(), hasSize(1));

		final Name actualName = this.daoFactory.getNameDao().getNameByNameId(name.getNid());
		assertThat(actualName.getModifiedBy(), is(this.userId));
		assertNotNull(actualName.getModifiedDate());

		final Bibref updatedReference = this.daoFactory.getBibrefDAO().getById(reference.getRefid());
		assertNotNull(updatedReference);
		assertThat(updatedReference.getRefid(), is(reference.getRefid()));
		assertThat(updatedReference.getModifiedBy(), is(this.userId));
		assertNotNull(updatedReference.getModifiedDate());

		final List<Attribute> attributeValuesGIDList =
			this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Arrays.asList(germplasm.getGid()));
		assertThat(attributeValuesGIDList, hasSize(1));
		final Attribute attribute = attributeValuesGIDList.get(0);
		assertThat(attribute.getModifiedBy(), is(this.userId));
		assertNotNull(attribute.getModifiedDate());
	}

	@Test
	public void testMergeGermplasm_OK() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, true)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasmToMerge1);
		this.sessionProvder.getSession().refresh(germplasmToMerge2);

		assertThat(germplasmToMerge1.getDeleted(), equalTo(true));
		// germplasmToMerge2 is omitted, so it should not be merged and deleted.
		assertThat(germplasmToMerge2.getDeleted(), equalTo(false));

	}

	@Test
	public void testMergeGermplasm_ReplaceGermplasmListEntries() {

		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null, false);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);

		final GermplasmListData germplasmListData1 = this.createTestListDataForList(germplasmToMerge1, germplasmList, 1);
		final GermplasmListData germplasmListData2 = this.createTestListDataForList(germplasmToMerge2, germplasmList, 2);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasmListData1);
		this.sessionProvder.getSession().refresh(germplasmListData2);

		assertThat(germplasmListData1.getGermplasmId(), equalTo(targetGermplasm.getGid()));
		assertThat(germplasmListData2.getGermplasmId(), equalTo(targetGermplasm.getGid()));

	}

	@Test
	public void testMergeGermplasm_ReplaceStudyEntries() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);

		final DmsProject study = this.createStudy();
		this.daoFactory.getStockDao().save(new StockModel(study.getProjectId(), this.createTestStudyEntry(1, germplasmToMerge1.getGid())));
		this.daoFactory.getStockDao().save(new StockModel(study.getProjectId(), this.createTestStudyEntry(2, germplasmToMerge2.getGid())));

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();

		// Check if the study entries are replaced with the target germplasm id for the first study
		final List<StudyEntryDto> studyEntries = this.studyEntryService.getStudyEntries(study.getProjectId());
		assertThat(studyEntries.get(0).getGid(), equalTo(targetGermplasm.getGid()));
		assertThat(studyEntries.get(1).getGid(), equalTo(targetGermplasm.getGid()));

	}

	@Test
	public void testMergeGermplasm_MigrateNames() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add LINE name to the target germplasm
		final Name lineName =
			this.addName(targetGermplasm, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);

		final Germplasm germplasmToMerge = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add MANAGEMENT_NAME to the germplasm to be merged
		final Name managementName =
			this.addName(germplasmToMerge, GermplasmNameType.MANAGEMENT_NAME.getUserDefinedFieldID(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		// If migrateNameTypes is false, migration of names should not execute
		germplasmMergeRequestDto.getMergeOptions().setMigrateNameTypes(false);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		Assert.assertEquals(1, this.germplasmService.getGermplasmDtoById(targetGermplasm.getGid()).getNames().size());

		germplasmMergeRequestDto.getMergeOptions().setMigrateNameTypes(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		final GermplasmDto targetGermplasmDto = this.germplasmService.getGermplasmDtoById(targetGermplasm.getGid());
		final GermplasmDto germplasmToMergeDto = this.germplasmService.getGermplasmDtoById(germplasmToMerge.getGid());

		Assert.assertNotNull(targetGermplasmDto);
		Assert.assertNull(germplasmToMergeDto);

		// Both LINE_NAME and MANAGEMENT_NAME should be present in the target germplasm's names.
		Assert.assertEquals(2, targetGermplasmDto.getNames().size());
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getNameTypeId().equals(lineName.getTypeId()) && n.getName().equals(lineName.getNval())));
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getNameTypeId().equals(managementName.getTypeId()) && n.getName()
				.equals(managementName.getNval())));
		Assert.assertFalse(targetGermplasmDto.getNames().stream().filter(
			n -> n.getNameTypeId().equals(managementName.getTypeId()) && n.getName()
				.equals(managementName.getNval())).findFirst().get().isPreferred());

	}

	@Test
	public void testMergeGermplasm_MigrateNames_WithDuplicateNameTypes() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), SystemNameTypes.PUI.getType());

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add ALTERNATE_CROSS_NAME name to the target germplasm
		final Name altCrossName1 =
			this.addName(targetGermplasm, GermplasmNameType.ALTERNATE_CROSS_NAME.getUserDefinedFieldID(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		// Add PUI name to the target germplasm
		final Name pui1 =
			this.addName(targetGermplasm, puiUserDefinedField.getFldno(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 0);

		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add ALTERNATE_CROSS_NAME to the germplasm to be merged
		final Name altCrossName2 =
			this.addName(germplasmToMerge1, GermplasmNameType.ALTERNATE_CROSS_NAME.getUserDefinedFieldID(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		// Add PUI name to the germplasm to be merged
		final Name pui2 =
			this.addName(germplasmToMerge1, puiUserDefinedField.getFldno(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 0);

		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add ALTERNATE_CROSS_NAME to the germplasm to be merged
		final Name altCrossName3 =
			this.addName(germplasmToMerge2, GermplasmNameType.ALTERNATE_CROSS_NAME.getUserDefinedFieldID(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		// Add PUI name to the germplasm to be merged
		final Name pui3 =
			this.addName(germplasmToMerge2, puiUserDefinedField.getFldno(),
				RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 0);

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		germplasmMergeRequestDto.getMergeOptions().setMigrateNameTypes(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		final GermplasmDto targetGermplasmDto = this.germplasmService.getGermplasmDtoById(targetGermplasm.getGid());
		final GermplasmDto germplasmToMergeDto = this.germplasmService.getGermplasmDtoById(germplasmToMerge1.getGid());

		Assert.assertNotNull(targetGermplasmDto);
		Assert.assertNull(germplasmToMergeDto);

		Assert.assertEquals(4, targetGermplasmDto.getNames().size());
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getName().equals(altCrossName1.getNval())));
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getName().equals(altCrossName2.getNval())));
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getName().equals(altCrossName3.getNval())));
		Assert.assertTrue(targetGermplasmDto.getNames().stream().anyMatch(
			n -> n.getName().equals(pui1.getNval())));

	}

	@Test
	public void testMergeGermplasm_MigrateAttributesAndTheirFiles() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);
		// Add  NOTE attribute to the target germplasm
		final CVTerm note = this.daoFactory.getCvTermDao().getByNameAndCvId(NOTE, CvId.VARIABLES.getId());
		final Attribute noteAttribute =
			this.addAttribute(targetGermplasm, note.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);
		// Add ACQ_DATE_AA_text attribute to the germplasm to be merged
		final CVTerm acqDate = this.daoFactory.getCvTermDao().getByNameAndCvId("ACQ_DATE_AA_text", CvId.VARIABLES.getId());
		final Attribute acqDateAttribute =
			this.addAttribute(germplasmToMerge, acqDate.getCvTermId(), RandomStringUtils.randomAlphabetic(10));
		// Add two files associated to variables and one file detached
		final FileMetadata fileMetadata1 = this.createFileMetadata(germplasmToMerge, note);
		final FileMetadata fileMetadata2 = this.createFileMetadata(germplasmToMerge, acqDate);
		final FileMetadata fileMetadata3 = this.createFileMetadata(germplasmToMerge, null);

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final FileMetadataFilterRequest fileMetadataFilterRequestByTargetGermplasmUUID = new FileMetadataFilterRequest();
		fileMetadataFilterRequestByTargetGermplasmUUID.setGermplasmUUID(targetGermplasm.getGermplasmUUID());
		assertThat(this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null),
			hasSize(0));

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		// If migrateAttributesData is false, migration of attributes should not execute
		germplasmMergeRequestDto.getMergeOptions().setMigrateAttributesData(false);
		germplasmMergeRequestDto.getMergeOptions().setMigrateFiles(false);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid()).size());
		assertThat(this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null),
			hasSize(0));

		germplasmMergeRequestDto.getMergeOptions().setMigrateAttributesData(true);
		germplasmMergeRequestDto.getMergeOptions().setMigrateFiles(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();

		// Both NOTE and ACQ_DATE attributes should be present in the target germplasm.
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid());
		Assert.assertEquals(2, attributes.size());
		Assert.assertTrue(attributes.stream().anyMatch(
			a -> a.getTypeId().equals(noteAttribute.getTypeId()) && a.getAval().equals(noteAttribute.getAval())));
		Assert.assertTrue(attributes.stream().anyMatch(
			n -> n.getTypeId().equals(acqDateAttribute.getTypeId()) && n.getAval()
				.equals(acqDateAttribute.getAval())));

		final List<FileMetadata> fileMetadataSearchResponse =
			this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null);
		assertThat(fileMetadataSearchResponse, hasSize(3));
		assertThat(fileMetadataSearchResponse, CoreMatchers.hasItems(
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata1.getFileUUID())),
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata2.getFileUUID())),
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata3.getFileUUID()))));
	}

	@Test
	public void testMergeGermplasm_MigrateAttributes_WithDuplicateAttributesType() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add  NOTE attribute to the target germplasm
		final CVTerm note = this.daoFactory.getCvTermDao().getByNameAndCvId(NOTE, CvId.VARIABLES.getId());
		final Attribute noteAttribute1 =
			this.addAttribute(targetGermplasm, note.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add same NOTE attribute to the first germplasm to be merged
		final Attribute noteAttribute2 =
			this.addAttribute(germplasmToMerge1, note.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add same NOTE attribute to the second germplasm to be merged
		final Attribute noteAttribute3 =
			this.addAttribute(germplasmToMerge2, note.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		germplasmMergeRequestDto.getMergeOptions().setMigrateAttributesData(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();

		// The attributes from first and second germplasm that were merged should not be migrated because they have
		// the same type of attribute that already exists in the target germplasm.
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid());
		Assert.assertEquals(1, attributes.size());
		Assert.assertTrue(attributes.stream().anyMatch(
			a -> a.getTypeId().equals(noteAttribute1.getTypeId()) && a.getAval().equals(noteAttribute1.getAval())));

	}

	@Test
	public void testMergeGermplasm_MigratePassportAndTheirFiles() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);
		// Add ANCEST_AP_text attribute to the target germplasm
		final CVTerm ancest_ap_text = this.daoFactory.getCvTermDao().getByNameAndCvId("ANCEST_AP_text", CvId.VARIABLES.getId());
		final Attribute ancestApTextAttribute =
			this.addAttribute(targetGermplasm, ancest_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);
		// Add COLL_DAT_AP_text attribute to the germplasm to be merged
		final CVTerm coll_dat_ap_text = this.daoFactory.getCvTermDao().getByNameAndCvId("COLL_DAT_AP_text", CvId.VARIABLES.getId());
		final Attribute collDatApTextAttribute =
			this.addAttribute(germplasmToMerge, coll_dat_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));
		// Add two files associated to variables and one file detached
		final FileMetadata fileMetadata1 = this.createFileMetadata(germplasmToMerge, ancest_ap_text);
		final FileMetadata fileMetadata2 = this.createFileMetadata(germplasmToMerge, coll_dat_ap_text);
		final FileMetadata fileMetadata3 = this.createFileMetadata(germplasmToMerge, null);

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final FileMetadataFilterRequest fileMetadataFilterRequestByTargetGermplasmUUID = new FileMetadataFilterRequest();
		fileMetadataFilterRequestByTargetGermplasmUUID.setGermplasmUUID(targetGermplasm.getGermplasmUUID());
		assertThat(this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null),
			hasSize(0));

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		// If migratePassportData is false, migration of attributes should not execute
		germplasmMergeRequestDto.getMergeOptions().setMigratePassportData(false);
		germplasmMergeRequestDto.getMergeOptions().setMigrateFiles(false);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		Assert.assertEquals(1, this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid()).size());
		assertThat(this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null),
			hasSize(0));

		germplasmMergeRequestDto.getMergeOptions().setMigratePassportData(true);
		germplasmMergeRequestDto.getMergeOptions().setMigrateFiles(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		final GermplasmDto targetGermplasmDto = this.germplasmService.getGermplasmDtoById(targetGermplasm.getGid());
		final GermplasmDto germplasmToMergeDto = this.germplasmService.getGermplasmDtoById(germplasmToMerge.getGid());

		Assert.assertNotNull(targetGermplasmDto);
		Assert.assertNull(germplasmToMergeDto);

		// Both ANCEST_AP_text and COLL_DAT_AP_text attributes should be present in the target germplasm.
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid());
		Assert.assertEquals(2, attributes.size());
		Assert.assertTrue(attributes.stream().anyMatch(
			a -> a.getTypeId().equals(ancestApTextAttribute.getTypeId()) && a.getAval().equals(ancestApTextAttribute.getAval())));
		Assert.assertTrue(attributes.stream().anyMatch(
			n -> n.getTypeId().equals(collDatApTextAttribute.getTypeId()) && n.getAval()
				.equals(collDatApTextAttribute.getAval())));

		final List<FileMetadata> fileMetadataSearchResponse =
			this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null);
		assertThat(fileMetadataSearchResponse, hasSize(3));
		assertThat(fileMetadataSearchResponse, CoreMatchers.hasItems(
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata1.getFileUUID())),
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata2.getFileUUID())),
			Matchers.hasProperty("fileUUID", Matchers.is(fileMetadata3.getFileUUID()))));
	}

	@Test
	public void testMergeGermplasm_MigratePassport_WithDuplicatePassportType() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add ANCEST_AP_text attribute to the target germplasm
		final CVTerm ancest_ap_text = this.daoFactory.getCvTermDao().getByNameAndCvId("ANCEST_AP_text", CvId.VARIABLES.getId());
		final Attribute ancestApTextAttribute1 =
			this.addAttribute(targetGermplasm, ancest_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add same ANCEST_AP_text attribute to the first germplasm to be merged
		final Attribute ancestApTextAttribute2 =
			this.addAttribute(germplasmToMerge1, ancest_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		// Add same ANCEST_AP_text attribute to the second germplasm to be merged
		final Attribute ancestApTextAttribute3 =
			this.addAttribute(germplasmToMerge2, ancest_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		germplasmMergeRequestDto.getMergeOptions().setMigrateAttributesData(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();

		// The passport attributes from first and second germplasm that were merged should not be migrated because they have
		// the same type of passport attribute that already exists in the target germplasm.
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid());
		Assert.assertEquals(1, attributes.size());
		Assert.assertTrue(attributes.stream().anyMatch(
			a -> a.getTypeId().equals(ancestApTextAttribute1.getTypeId()) && a.getAval().equals(ancestApTextAttribute1.getAval())));

	}

	@Test
	public void testMergeGermplasm_MigrateLots() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);

		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		this.addTransaction(this.addLot(germplasmToMerge1.getGid()));
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		this.addTransaction(this.addLot(germplasmToMerge2.getGid()));

		this.sessionProvder.getSession().flush();

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		// Set the migrateLots to true
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), true, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), true, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();

		final List<Lot> lotsOfTargetGermplasm = this.daoFactory.getLotDao().getByGids(Arrays.asList(targetGermplasm.getGid()));
		assertThat(lotsOfTargetGermplasm.size(), equalTo(2));

		final List<Lot> lotsOfGermplasmMerged =
			this.daoFactory.getLotDao().getByGids(Arrays.asList(germplasmToMerge1.getGid(), germplasmToMerge2.getGid()));
		assertThat(lotsOfGermplasmMerged.size(), equalTo(0));

	}

	@Test
	public void testMergeGermplasm_CloseLots() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);

		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Lot lot1 = this.addLot(germplasmToMerge1.getGid());
		this.addTransaction(lot1);
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Lot lot2 = this.addLot(germplasmToMerge2.getGid());
		this.addTransaction(lot2);

		this.sessionProvder.getSession().flush();

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		// Set the closeLots to true
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(lot1);
		this.sessionProvder.getSession().refresh(lot2);

		final List<Lot> lotsOfTargetGermplasm = this.daoFactory.getLotDao().getByGids(Arrays.asList(targetGermplasm.getGid()));
		assertThat(lotsOfTargetGermplasm.size(), equalTo(0));

		final List<Lot> lotsOfGermplasmMerged =
			this.daoFactory.getLotDao().getByGids(Arrays.asList(germplasmToMerge1.getGid(), germplasmToMerge2.getGid()));
		assertThat(lotsOfGermplasmMerged.size(), equalTo(2));
		assertThat(lotsOfGermplasmMerged.get(0).getStatus(), equalTo(LotStatus.CLOSED.getIntValue()));
		assertThat(lotsOfGermplasmMerged.get(1).getStatus(), equalTo(LotStatus.CLOSED.getIntValue()));

	}

	@Test
	public void testGetGermplasmMergeDTOs() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm targetGermplasm = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge1 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Name lineName1 =
			this.addName(germplasmToMerge1, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		final Germplasm germplasmToMerge2 = this.createGermplasm(method, null, null, 0, 0, 0, null, null);
		final Name lineName2 =
			this.addName(germplasmToMerge2, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);

		this.sessionProvder.getSession().flush();

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge1.getGid(), false, false),
				new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge2.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());
		germplasmMergeRequestDto.getMergeOptions().setMigrateNameTypes(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));

		this.sessionProvder.getSession().flush();

		final List<GermplasmMergedDto> result = this.germplasmService.getGermplasmMerged(targetGermplasm.getGid());
		assertThat(result.size(), equalTo(2));
		assertThat(result.get(0).getGid(), equalTo(germplasmToMerge1.getGid()));
		assertThat(result.get(1).getGid(), equalTo(germplasmToMerge2.getGid()));

	}

	@Test
	public void testGermplasmProgenies() {
		final Method derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method generativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm parentGermplasm = this.createGermplasm(derivativeMethod, null, null, 0, 0, 0, null, null);

		final Germplasm child1 = this.createGermplasm(generativeMethod, null, null, 2, parentGermplasm.getGid(), 1, null, null);
		final Name name1 =
			this.addName(child1, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		final Germplasm child2 = this.createGermplasm(generativeMethod, null, null, 2, 1, parentGermplasm.getGid(), null, null);
		final Name name2 =
			this.addName(child2, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		final Germplasm child3 = this.createGermplasm(derivativeMethod, null, null, -1, 1, parentGermplasm.getGid(), null, null);
		final Name name3 =
			this.addName(child3, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		final Germplasm child4 = this.createGermplasm(derivativeMethod, null, null, -1, parentGermplasm.getGid(), 1, null, null);
		final Name name4 =
			this.addName(child4, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);
		final Germplasm child5 = this.createGermplasm(generativeMethod, null, null, 3, 1, 2, null, null);
		this.addProgenitor(child5, parentGermplasm);
		final Name name5 =
			this.addName(child5, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(), RandomStringUtils.randomAlphabetic(10), 0,
				this.creationDate, 1);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		final Map<Integer, GermplasmProgenyDto> progenies =
			this.germplasmService.getGermplasmProgenies(parentGermplasm.getGid()).stream().collect(
				Collectors.toMap(GermplasmProgenyDto::getGid, Function.identity()));
		assertThat(progenies.size(), equalTo(5));
		assertThat(progenies.get(child1.getGid()).getDesignation(), equalTo(name1.getNval()));
		assertThat(progenies.get(child2.getGid()).getDesignation(), equalTo(name2.getNval()));
		assertThat(progenies.get(child3.getGid()).getDesignation(), equalTo(name3.getNval()));
		assertThat(progenies.get(child4.getGid()).getDesignation(), equalTo(name4.getNval()));
		assertThat(progenies.get(child5.getGid()).getDesignation(), equalTo(name5.getNval()));
	}

	@Test
	public void testMergeGermplasm_TransferOnlyDetachedFilesToTarget() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm targetGermplasm = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);
		final Germplasm germplasmToMerge = this.createGermplasm(method, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0, null, null);

		// Add ANCEST_AP_text attribute to the target germplasm
		final CVTerm ancest_ap_text = this.daoFactory.getCvTermDao().getByNameAndCvId("ANCEST_AP_text", CvId.VARIABLES.getId());
		final Attribute ancestApTextAttribute =
			this.addAttribute(germplasmToMerge, ancest_ap_text.getCvTermId(), RandomStringUtils.randomAlphabetic(10));

		// Add  NOTE attribute to the target germplasm
		final CVTerm note = this.daoFactory.getCvTermDao().getByNameAndCvId(NOTE, CvId.VARIABLES.getId());
		final Attribute noteAttribute =
			this.addAttribute(germplasmToMerge, note.getCvTermId(), RandomStringUtils.randomAlphabetic(10));
		// Add two files associated to variables and one file detached
		final FileMetadata fileMetadata1 = this.createFileMetadata(germplasmToMerge, ancest_ap_text);
		final FileMetadata fileMetadata2 = this.createFileMetadata(germplasmToMerge, note);
		final FileMetadata fileMetadata3 = this.createFileMetadata(germplasmToMerge, null);

		this.sessionProvder.getSession().refresh(targetGermplasm);

		final FileMetadataFilterRequest fileMetadataFilterRequestByTargetGermplasmUUID = new FileMetadataFilterRequest();
		fileMetadataFilterRequestByTargetGermplasmUUID.setGermplasmUUID(targetGermplasm.getGermplasmUUID());
		assertThat(this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null),
			hasSize(0));

		final GermplasmMergeRequestDto germplasmMergeRequestDto = new GermplasmMergeRequestDto();
		germplasmMergeRequestDto.setTargetGermplasmId(targetGermplasm.getGid());
		germplasmMergeRequestDto.setNonSelectedGermplasm(
			Arrays.asList(new GermplasmMergeRequestDto.NonSelectedGermplasm(germplasmToMerge.getGid(), false, false)));
		germplasmMergeRequestDto.setMergeOptions(new GermplasmMergeRequestDto.MergeOptions());

		// If migratePassportData and migrateAttributesData is false, migration of attributes should not execute
		germplasmMergeRequestDto.getMergeOptions().setMigratePassportData(false);
		germplasmMergeRequestDto.getMergeOptions().setMigrateAttributesData(false);
		germplasmMergeRequestDto.getMergeOptions().setMigrateFiles(true);
		this.germplasmService.mergeGermplasm(germplasmMergeRequestDto, RandomStringUtils.randomAlphabetic(10));
		this.sessionProvder.getSession().flush();
		Assert.assertEquals(0, this.daoFactory.getAttributeDAO().getByGID(targetGermplasm.getGid()).size());

		final List<FileMetadata> fileMetadataSearchResponse =
			this.daoFactory.getFileMetadataDAO().search(fileMetadataFilterRequestByTargetGermplasmUUID, this.programUUID, null);
		assertThat(fileMetadataSearchResponse, hasSize(1));
		assertThat(fileMetadataSearchResponse.get(0).getFileUUID(), is(fileMetadata3.getFileUUID()));
	}

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final String germplasmPUI) {
		return this.createGermplasm(method, germplasmUUID, location, gnpgs, gpid1, gpid2, null, germplasmPUI);
	}

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Bibref reference, final String germplasmPUI) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, (location == null) ? 0 : location.getLocid(), Integer.parseInt(this.creationDate), 0,
			0, 0, null, null, method);
		if (StringUtils.isNotEmpty(germplasmUUID)) {
			germplasm.setGermplasmUUID(germplasmUUID);
		}
		germplasm.setBibref(reference);
		this.daoFactory.getGermplasmDao().save(germplasm);
		if (StringUtils.isNotEmpty(germplasmPUI)) {
			this.addName(germplasm, this.puiNameTypeId, germplasmPUI, this.noLocationId, this.creationDate, 0);
		}
		this.sessionProvder.getSession().flush();

		assertThat(germplasm.getCreatedBy(), is(this.userId));
		assertNotNull(germplasm.getCreatedBy());
		assertNull(germplasm.getModifiedBy());
		assertNull(germplasm.getModifiedDate());

		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708);
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

	private Location createLocation() {
		final Location province = this.daoFactory.getLocationDAO().getById(1);
		final Country country = this.daoFactory.getCountryDao().getById(1);
		final Location location = new Location(null, 1,
			1, RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(8),
			1, 1, province,
			country, 1);
		this.daoFactory.getLocationDAO().saveOrUpdate(location);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getLocationDAO().refresh(location);
		return location;
	}

	private Name addName(final Germplasm germplasm, final Integer nameId, final String nameVal, final Integer locId, final String date,
		final int preferred) {
		final Name name = new Name(null, germplasm, nameId, preferred, nameVal, locId, Integer.valueOf(date), 0);
		this.daoFactory.getNameDao().save(name);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getNameDao().refresh(name);

		assertNotNull(name.getCreatedDate());
		assertThat(name.getCreatedBy(), is(this.userId));
		assertNull(name.getModifiedDate());
		assertNull(name.getModifiedBy());

		return name;
	}

	private Attribute addAttribute(final Germplasm germplasm, final Integer attributeId, final String value) {

		final Attribute attribute = new Attribute(null, germplasm.getGid(), attributeId, value, null,
			germplasm.getLocationId(),
			0, germplasm.getGdate());
		this.daoFactory.getAttributeDAO()
			.save(attribute);
		this.daoFactory.getAttributeDAO().refresh(attribute);

		assertNotNull(attribute.getCreatedDate());
		assertThat(attribute.getCreatedBy(), is(this.userId));
		assertNull(attribute.getModifiedDate());
		assertNull(attribute.getModifiedBy());

		return attribute;
	}

	private Progenitor addProgenitor(final Germplasm child, final Germplasm parent) {
		final Progenitor progenitor = new Progenitor(child, 3, parent.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getProgenitorDao().refresh(progenitor);
		this.daoFactory.getGermplasmDao().refresh(child);

		assertNotNull(progenitor.getCreatedDate());
		assertThat(progenitor.getCreatedBy(), is(this.userId));
		assertNull(progenitor.getModifiedDate());
		assertNull(progenitor.getModifiedBy());

		return progenitor;
	}

	private GermplasmUpdateDTO createGermplasmUpdateDto(final Integer gid, final String uuid, final Optional<Method> method,
		final Optional<Location> location, final Integer creationDate) {
		final GermplasmUpdateDTO germplasmUpdateDTO = new GermplasmUpdateDTO();
		germplasmUpdateDTO.setGid(gid);
		germplasmUpdateDTO.setGermplasmUUID(uuid);
		germplasmUpdateDTO.setLocationAbbreviation(location.isPresent() ? location.get().getLabbr() : null);
		germplasmUpdateDTO.setBreedingMethodAbbr(method.isPresent() ? method.get().getMcode() : null);
		germplasmUpdateDTO.setPreferredNameType(DRVNM);
		germplasmUpdateDTO.setCreationDate(creationDate != null ? String.valueOf(creationDate) : null);
		germplasmUpdateDTO.setReference("Reference gid " + gid);
		germplasmUpdateDTO.getAttributes().put(NOTE, "Note for " + gid);
		germplasmUpdateDTO.getNames().put(DRVNM, "Name for " + gid);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 0);
		return germplasmUpdateDTO;
	}

	private Bibref createReference(final String reference) {
		final Bibref bibref = new Bibref(null, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, reference, DEFAULT_BIBREF_FIELD,
			DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD,
			DEFAULT_BIBREF_FIELD,
			DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD, DEFAULT_BIBREF_FIELD);
		this.daoFactory.getBibrefDAO().save(bibref);
		assertThat(bibref.getCreatedBy(), is(this.userId));
		assertNotNull(bibref.getCreatedBy());
		assertNull(bibref.getModifiedDate());
		assertNull(bibref.getModifiedBy());
		return bibref;
	}

	private Lot addLot(final int gid) {
		final Integer id = this.locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.SSTORE.name());
		final Lot lot =
			new Lot(null, this.userId, EntityType.GERMPLSM.name(), gid, 6000, TermId.SEED_AMOUNT_G.getId(),
				LotStatus.ACTIVE.getIntValue(), 0,
				"Lot", RandomStringUtils.randomAlphabetic(35));
		lot.setLotUuId(RandomStringUtils.randomAlphabetic(35));
		return this.daoFactory.getLotDao().save(lot);
	}

	private Transaction addTransaction(final Lot lot) {
		final Transaction confirmedDeposit =
			new Transaction(null, this.userId, lot, Util.getCurrentDate(), TransactionStatus.CONFIRMED.getIntValue(),
				20D, "Transaction 1", Util.getCurrentDateAsIntegerValue(), null, null, null, this.userId, TransactionType.DEPOSIT.getId());

		return this.daoFactory.getTransactionDAO().save(confirmedDeposit);

	}

	private GermplasmListData createTestListDataForList(final Germplasm germplasm,
		final GermplasmList germplasmList, final Integer entryId) {
		final GermplasmListData listData = GermplasmListDataTestDataInitializer.createGermplasmListData(
			germplasmList, germplasm.getGid(), entryId);
		listData.setFemaleParent(new GermplasmParent(germplasm.getGpid1(), "", ""));
		listData.addMaleParent(new GermplasmParent(germplasm.getGpid2(), "", ""));
		this.daoFactory.getGermplasmListDataDAO().save(listData);
		return listData;
	}

	private StudyEntryDto createTestStudyEntry(final int i, final Integer gid) {
		final StudyEntryDto studyEntryDto = new StudyEntryDto();
		studyEntryDto.setGid(gid);
		studyEntryDto.setEntryNumber(i);
		studyEntryDto.setDesignation("DESIGNATION" + i);
		studyEntryDto.setEntryCode("ENTRY_CODE" + gid);

		studyEntryDto.getProperties()
			.put(TermId.CROSS.getId(), new StudyEntryPropertyData(null, TermId.CROSS.getId(), "CROSS" + i));
		studyEntryDto.getProperties()
			.put(TermId.ENTRY_TYPE.getId(), new StudyEntryPropertyData(null, TermId.ENTRY_TYPE.getId(),
				String.valueOf(SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId())));
		studyEntryDto.getProperties()
			.put(TermId.SEED_SOURCE.getId(), new StudyEntryPropertyData(null, TermId.SEED_SOURCE.getId(),
				"SEED_SOURCE" + i));

		return studyEntryDto;
	}

	private DmsProject createStudy() {
		final DmsProject study = new DmsProject(
			"TEST STUDY " + RandomStringUtils.randomAlphanumeric(10), "TEST DESCRIPTION", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		this.daoFactory.getDmsProjectDAO().save(study);
		final DmsProject plotDataset = new DmsProject(
			"TEST DATASET", "TEST DATASET DESC", null, Collections.emptyList(),
			false,
			false, new StudyType(6), "20200606", null, null,
			null, "1");
		plotDataset.setDatasetType(new DatasetType(DatasetTypeEnum.PLOT_DATA.getId()));
		plotDataset.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(plotDataset);

		final ProjectProperty gidProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 1, TermId.GID.getId(), "GID");
		final ProjectProperty desigProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 2, TermId.DESIG.getId(), "DESIG");
		final ProjectProperty entryNoProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 3, TermId.ENTRY_NO.getId(), "ENTRY_NO");
		final ProjectProperty seedSourceProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 4, TermId.SEED_SOURCE.getId(),
				"SEED_SOURCE");
		final ProjectProperty crossProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 5, TermId.CROSS.getId(), "CROSS");
		final ProjectProperty entryTypeProp =
			new ProjectProperty(plotDataset, VariableType.GERMPLASM_DESCRIPTOR.getId(), "", 6, TermId.ENTRY_TYPE.getId(),
				"ENTRY_TYPE");
		this.daoFactory.getProjectPropertyDAO().save(gidProp);
		this.daoFactory.getProjectPropertyDAO().save(desigProp);
		this.daoFactory.getProjectPropertyDAO().save(entryNoProp);
		this.daoFactory.getProjectPropertyDAO().save(seedSourceProp);
		this.daoFactory.getProjectPropertyDAO().save(crossProp);
		this.daoFactory.getProjectPropertyDAO().save(entryTypeProp);

		return study;
	}

	private FileMetadata createFileMetadata(final Germplasm germplasm, final CVTerm variable) {
		final FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setGermplasm(germplasm);
		fileMetadata.setFileUUID(UUID.randomUUID().toString());
		if (variable != null) {
			fileMetadata.setVariables(Arrays.asList(variable));
		}
		return this.daoFactory.getFileMetadataDAO().save(fileMetadata);
	}

	private Germplasm createGermplasmWithDescendants(final Method method) {
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0, null);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		return germplasmWithDescendants;
	}
}
