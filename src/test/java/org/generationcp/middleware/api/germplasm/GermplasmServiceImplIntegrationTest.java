package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmUpdateRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.Synonym;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.util.Util;
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
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GermplasmServiceImplIntegrationTest extends IntegrationTestBase {

	public static final String DRVNM = "DRVNM";
	public static final String NOTE = "NOTE";
	public static final String NOLOC = "NOLOC";
	public static final String CROP_NAME = "maize";

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmService germplasmService;

	private Integer noLocationId, variableTypeId, attributeId, clientId, userId;
	private String creationDate, name, germplasmUUID, reference, note;
	private Map<String, String> names, attributes;
	private Method derivativeMethod, generativeMethod;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.noLocationId = this.daoFactory.getLocationDAO().getByAbbreviations(Arrays.asList(NOLOC)).get(0).getLocid();
		this.derivativeMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		this.generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		this.variableTypeId = this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", DRVNM).getFldno();
		this.attributeId = this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("ATRIBUTS", "ATTRIBUTE", NOTE).getFldno();
		this.creationDate = "20201212";
		this.name = RandomStringUtils.randomAlphabetic(10);
		this.germplasmUUID = RandomStringUtils.randomAlphabetic(10);
		this.clientId = 1;
		this.reference = RandomStringUtils.randomAlphabetic(20);
		this.note = RandomStringUtils.randomAlphabetic(10);
		this.names = new HashMap<>();
		this.names.put(DRVNM, this.name);
		this.attributes = new HashMap<>();
		this.attributes.put(NOTE, this.note);
		this.userId = this.findAdminUser();
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
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);
		final List<Name> names = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Arrays.asList(germplasm.getGid()));

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(newLocation.getLocid(), savedGermplasm.getLocationId());
		assertEquals(creationDate, savedGermplasm.getGdate().intValue());
		assertNotNull(savedGermplasm.getReferenceId());
		assertFalse(names.isEmpty());
		assertFalse(attributes.isEmpty());

		final Name savedName = names.get(0);
		assertEquals(newNameCode.getFldno(), savedName.getTypeId());
		assertEquals(1, savedName.getNstat().intValue());
		assertEquals(newLocation.getLocid(), savedName.getLocationId());
		assertEquals(creationDate, savedName.getNdate().intValue());
		assertEquals("Name for " + germplasm.getGid(), savedName.getNval());

		final Attribute savedAttribute = attributes.get(0);
		assertEquals(newAttributeCode.getFldno(), savedAttribute.getTypeId());
		assertEquals(newLocation.getLocid(), savedAttribute.getLocationId());
		assertEquals(creationDate, savedAttribute.getAdate().intValue());
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
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 0, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasm.getGid(), newAttributeCode.getFldno(), germplasm.getUserId(), "",
				germplasm.getLocationId(),
				0, germplasm.getGdate()));

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);
		final List<Name> names = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		final List<Attribute> attributes = this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Arrays.asList(germplasm.getGid()));

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(newLocation.getLocid(), savedGermplasm.getLocationId());
		assertEquals(creationDate, savedGermplasm.getGdate().intValue());
		assertNotNull(savedGermplasm.getReferenceId());
		assertFalse(names.isEmpty());
		assertFalse(attributes.isEmpty());

		final Name savedName = names.get(0);
		assertEquals(newNameCode.getFldno(), savedName.getTypeId());
		assertEquals(1, savedName.getNstat().intValue());
		assertEquals(newLocation.getLocid(), savedName.getLocationId());
		assertEquals(creationDate, savedName.getNdate().intValue());
		assertEquals("Name for " + germplasm.getGid(), savedName.getNval());

		final Attribute savedAttribute = attributes.get(0);
		assertEquals(newAttributeCode.getFldno(), savedAttribute.getTypeId());
		assertEquals(newLocation.getLocid(), savedAttribute.getLocationId());
		assertEquals(creationDate, savedAttribute.getAdate().intValue());
		assertEquals("Note for " + germplasm.getGid(), savedAttribute.getAval());

	}

	@Test
	public void testImportGermplasmUpdates_PreferredNameHasDuplicates() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Create Duplicate PreferredName assigned
		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 1, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.preferred.name.duplicate.names"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_PreferredNameDoesntExist() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		// Set invalid preferred name code.
		germplasmUpdateDTO.setPreferredNameType("Some Non Existing Code");

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.preferred.name.doesnt.exist"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_DuplicateNamesAndAttributes() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod),
				Optional.of(newLocation), creationDate);
		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		// Create duplicate names and attributes
		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 0, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));
		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 0, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasm.getGid(), newAttributeCode.getFldno(), germplasm.getUserId(), "",
				germplasm.getLocationId(),
				0, germplasm.getGdate()));
		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasm.getGid(), newAttributeCode.getFldno(), germplasm.getUserId(), "",
				germplasm.getLocationId(),
				0, germplasm.getGdate()));

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.duplicate.names"));
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.duplicate.attributes"));
		}

	}

	@Test
	public void testGetGermplasmByGIDs() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final List<Germplasm> germplasmByGIDs = this.germplasmService.getGermplasmByGIDs(Arrays.asList(germplasm.getGid()));
		assertThat(germplasmByGIDs, hasSize(1));

		final Germplasm actualGermplasm = germplasmByGIDs.get(0);
		assertNotNull(actualGermplasm);
		assertThat(actualGermplasm.getGid(), is(germplasm.getGid()));
	}

	@Test
	public void testGetAttributesByGID() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		assertThat(this.germplasmService.getAttributesByGID(germplasm.getGid()), hasSize(0));

		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasm.getGid());
		attribute.setTypeId(newAttributeCode.getFldno());
		attribute.setAval(RandomStringUtils.randomAlphanumeric(50));
		attribute.setUserId(0);
		attribute.setAdate(germplasm.getGdate());

		this.daoFactory.getAttributeDAO().save(attribute);

		final List<Attribute> attributes = this.germplasmService.getAttributesByGID(germplasm.getGid());
		assertThat(attributes, hasSize(1));
		final Attribute actualAttribute = attributes.get(0);
		assertNotNull(actualAttribute);
		assertThat(actualAttribute.getAid(), is(attribute.getAid()));
		assertThat(actualAttribute.getGermplasmId(), is(germplasm.getGid()));
	}

	@Test
	public void test_getPlotCodeField_OK() {
		final UserDefinedField plotCodeField = this.germplasmService.getPlotCodeField();
		// Should never return null no matter whether the plot code UDFLD is present in the target database or not.
		assertThat("GermplasmDataManager.getPlotCodeField() should never return null.", plotCodeField, is(notNullValue()));
		if (plotCodeField.getFldno() != 0) {
			// Non-zero fldno is a case where the UDFLD table has a record matching ftable=ATRIBUTS, ftype=PASSPORT, fcode=PLOTCODE
			// Usually the id of this record is 1552. Not asserting as we dont want tests to depend on primary key values to be exact.

			assertThat(plotCodeField.getFtable(), is(UDTableType.ATRIBUTS_PASSPORT.getTable()));
			assertThat(plotCodeField.getFtype(), is(UDTableType.ATRIBUTS_PASSPORT.getType()));
			assertThat(plotCodeField.getFcode(), is("PLOTCODE"));
		}
	}

	@Test
	public void test_getPlotCodeValue_OK() {
		final String plotCodeValue = UUID.randomUUID().toString();
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final UserDefinedField plotCodeAttr =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_PASSPORT.getTable(),
				UDTableType.ATRIBUTS_PASSPORT.getType(), GermplasmServiceImpl.PLOT_CODE);
		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasm.getGid(), plotCodeAttr.getFldno(), germplasm.getUserId(), plotCodeValue,
				germplasm.getLocationId(),
				0, germplasm.getGdate()));

		final String actualPlotCodeValue = this.germplasmService.getPlotCodeValue(germplasm.getGid());
		assertThat(actualPlotCodeValue, is(plotCodeValue));
	}

	@Test
	public void test_getPlotCodeValues_OK() {
		final String plotCodeValue = UUID.randomUUID().toString();

		final Germplasm germplasmWithoutPlotCode = this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0);
		final Germplasm germplasmWithPlotCode = this.createGermplasm(this.generativeMethod, null, null, 0, 0, 0);

		final UserDefinedField plotCodeAttr =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_PASSPORT.getTable(),
				UDTableType.ATRIBUTS_PASSPORT.getType(), GermplasmServiceImpl.PLOT_CODE);
		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasmWithPlotCode.getGid(), plotCodeAttr.getFldno(), germplasmWithPlotCode.getUserId(),
				plotCodeValue,
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
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Cretae GermplasmUpdateDTO with empty method.
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.empty(), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 1);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 2);

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(method.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(1, savedGermplasm.getGpid1().intValue());
		assertEquals(2, savedGermplasm.getGpid2().intValue());

	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodTypeMismatch() {

		// If the germplasm has a GENERATIVE type then the new breeding method has to be also GENERATIVE, if not, it should throw an error.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.breeding.method.mismatch"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_BreedingMethodNumberOfProgenitorsMismatch() {

		// If the germplasm has a GENERATIVE type then the new breeding ,ethod has to be also GENERATIVE, and the expected number of
		// progenitors should be the same. If not, it should throw an error.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
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
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.mutation.method.is.not.supported"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_Generative_UpdateCrossesKnownParents() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign known parents
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 1);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 2);

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(1, savedGermplasm.getGpid1().intValue());
		assertEquals(2, savedGermplasm.getGpid2().intValue());

	}

	@Test
	public void testImportGermplasmUpdates_Generative_UpdateCrossesUnnownMaleOrFemaleParent() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		// Assign unknown male parent
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 1);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 0);

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(1, savedGermplasm.getGpid1().intValue());
		assertEquals(0, savedGermplasm.getGpid2().intValue());

		// Assign unknown female parent
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 1);

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(2, savedGermplasm.getGnpgs().intValue());
		assertEquals(0, savedGermplasm.getGpid1().intValue());
		assertEquals(1, savedGermplasm.getGpid2().intValue());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_GermplasmHasExistingProgeny() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		// Create Germplasm With Descendant
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant.setGpid1(germplasm.getGid());
		germplasmDescendant.setGpid2(germplasm.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.germplasm.has.existing.progeny"));
		}
	}

	@Test
	public void testImportGermplasmUpdates_Derivative_ImmediateSourceShouldBelongToGroup() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmDescendant2.getGid());
		// Assign immediate source germplasm with a group source (female parent) different from current group source.
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant.getGid());

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
			fail("Method should throw an error");
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(
				e.getErrorCodeParamsMultiMap().containsKey("germplasm.update.immediate.source.must.belong.to.the.same.group"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UpdateImmediateAndGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, germplasmWithDescendants.getGid());
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant2.getGid());

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

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

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 1);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, 0);

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(1, savedGermplasm.getGpid1().intValue());
		assertEquals(0, savedGermplasm.getGpid2().intValue());

	}

	@Test
	public void testImportGermplasmUpdates_Derivative_UpdateUnknownGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method newMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_1, 0);
		germplasmUpdateDTO.getProgenitors().put(GermplasmServiceImpl.PROGENITOR_2, germplasmDescendant.getGid());

		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(-1, savedGermplasm.getGnpgs().intValue());
		assertEquals(germplasmWithDescendants.getGid(), savedGermplasm.getGpid1());
		assertEquals(germplasmDescendant.getGid(), savedGermplasm.getGpid2());

	}

	@Test
	public void testImportGermplasmUpdates_TerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method newMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), Optional.of(newMethod), Optional.empty(), null);
		this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));

		final Germplasm savedGermplasm =
			this.daoFactory.getGermplasmDao()
				.getByGIDsOrUUIDListWithMethodAndBibref(Collections.singleton(germplasm.getGid()), new HashSet<>()).get(0);

		assertEquals(newMethod.getMid(), savedGermplasm.getMethodId());
		assertEquals(0, savedGermplasm.getGnpgs().intValue());
		assertEquals(0, savedGermplasm.getGpid1().intValue());
		assertEquals(0, savedGermplasm.getGpid2().intValue());
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
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);
		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(0));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(0));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));

		final Bibref bibref = this.daoFactory.getBibrefDAO().getById(germplasm.getReferenceId());
		assertThat(bibref.getAnalyt(), equalTo(this.reference));

		final List<Name> savedNames = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		assertThat(savedNames.size(), equalTo(1));
		assertThat(savedNames.get(0).getNval(), equalTo(this.name));
		assertThat(savedNames.get(0).getNstat(), equalTo(1));
		assertThat(savedNames.get(0).getUserId(), equalTo(this.userId));
		assertThat(savedNames.get(0).getLocationId(), equalTo(this.noLocationId));
		assertThat(savedNames.get(0).getTypeId(), equalTo(this.variableTypeId));

		final List<Attribute> savedAttributes = this.daoFactory.getAttributeDAO().getByGID(germplasm.getGid());
		assertThat(savedAttributes.size(), equalTo(1));
		assertThat(savedAttributes.get(0).getAval(), equalTo(this.note));
		assertThat(savedAttributes.get(0).getTypeId(), equalTo(this.attributeId));
		assertThat(savedAttributes.get(0).getUserId(), equalTo(this.userId));
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
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);
		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(0));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));

		final Bibref bibref = this.daoFactory.getBibrefDAO().getById(germplasm.getReferenceId());
		assertThat(bibref.getAnalyt(), equalTo(this.reference));

		final List<Name> savedNames = this.daoFactory.getNameDao().getNamesByGids(Arrays.asList(germplasm.getGid()));
		assertThat(savedNames.size(), equalTo(1));
		assertThat(savedNames.get(0).getNval(), equalTo(this.name));
		assertThat(savedNames.get(0).getNstat(), equalTo(1));
		assertThat(savedNames.get(0).getUserId(), equalTo(this.userId));
		assertThat(savedNames.get(0).getLocationId(), equalTo(this.noLocationId));
		assertThat(savedNames.get(0).getTypeId(), equalTo(this.variableTypeId));

		final List<Attribute> savedAttributes = this.daoFactory.getAttributeDAO().getByGID(germplasm.getGid());
		assertThat(savedAttributes.size(), equalTo(1));
		assertThat(savedAttributes.get(0).getAval(), equalTo(this.note));
		assertThat(savedAttributes.get(0).getTypeId(), equalTo(this.attributeId));
		assertThat(savedAttributes.get(0).getUserId(), equalTo(this.userId));
		assertThat(savedAttributes.get(0).getLocationId(), equalTo(this.noLocationId));
	}

	@Test
	public void test_importGermplasm_matchFound_ok() {
		final Germplasm germplasm = this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();
		this.addName(germplasm.getGid(), this.variableTypeId, this.name, this.noLocationId, this.creationDate, 1);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.FOUND));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().get(0), is(germplasm.getGid()));

	}

	@Test
	public void test_importGermplasm_matchNotFound_ok() {
		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, null, null);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.NONE);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));
	}

	@Test
	public void test_importGermplasm_saveGenerativeWithOneProgenitorSpecified_Ok() {
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, "0", progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(2));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
	}

	@Test
	public void test_importGermplasm_saveGenerativeWithBothProgenitorsSpecified_Ok() {
		final String progenitor1FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1FemaleParent = this.createGermplasm(this.generativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.generativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor1GUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.generativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(2));
		assertThat(germplasm.getGpid1(), equalTo(progenitor1.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_importGermplasm_saveDerivativeWithBothProgenitorsSpecified_ThrowsException_WhenGroupIsNotValid() {
		final String progenitor1FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor1GUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor1FemaleParent = this.createGermplasm(this.derivativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		this.createGermplasm(this.derivativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0);
		this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor1GUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(false);

		this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);
	}

	@Test
	public void test_importGermplasm_saveDerivativeWithBothProgenitors_Ok() {
		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent = this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, progenitor2FemaleParentGUID, progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(progenitor2FemaleParent.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
	}

	@Test
	public void test_importGermplasm_saveDerivativeWithProgenitor2Specified_Ok() {

		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent = this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0);
		this.sessionProvder.getSession().flush();

		final GermplasmImportDTO germplasmImportDto =
			new GermplasmImportDTO(this.clientId, this.germplasmUUID, NOLOC, this.derivativeMethod.getMcode(),
				this.reference, DRVNM, this.names, this.attributes, this.creationDate
				, "0", progenitor2GUID);

		final GermplasmImportRequestDto germplasmImportRequestDto = new GermplasmImportRequestDto();
		germplasmImportRequestDto.setConnectUsing(GermplasmImportRequestDto.PedigreeConnectionType.GUID);
		germplasmImportRequestDto.setGermplasmList(Collections.singletonList(germplasmImportDto));
		germplasmImportRequestDto.setSkipIfExists(true);

		final Map<Integer, GermplasmImportResponseDto> response =
			this.germplasmService.importGermplasm(this.userId, CROP_NAME, germplasmImportRequestDto);

		assertThat(response.size(), is(1));
		assertThat(response.get(this.clientId).getStatus(), equalTo(GermplasmImportResponseDto.Status.CREATED));
		assertThat(response.get(this.clientId).getGids().size(), is(this.clientId));
		assertThat(response.get(this.clientId).getGids().size(), is(1));

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(response.get(this.clientId).getGids().get(0));
		assertThat(germplasm.getMethodId(), equalTo(this.derivativeMethod.getMid()));
		assertThat(germplasm.getGnpgs(), equalTo(-1));
		assertThat(germplasm.getGpid1(), equalTo(progenitor2FemaleParent.getGid()));
		assertThat(germplasm.getGpid2(), equalTo(progenitor2.getGid()));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
		assertThat(germplasm.getLocationId(), equalTo(this.noLocationId));
		assertThat(germplasm.getGdate(), equalTo(Integer.valueOf(this.creationDate)));
	}

	@Test
	public void test_getGermplasmDtoById_Ok() {

		final Germplasm progenitor1FemaleParent =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 2, progenitor1FemaleParent.getGid(),
				0);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Location location = this.createLocation();

		final Germplasm germplasm =
			this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), location, 3, progenitor1.getGid(),
				progenitor2.getGid());
		this.sessionProvder.getSession().flush();

		final Name preferredName =
			this.addName(germplasm.getGid(), this.variableTypeId, RandomStringUtils.randomAlphabetic(10), location.getLocid(),
				this.creationDate, 1);
		this.addName(germplasm.getGid(), this.variableTypeId, RandomStringUtils.randomAlphabetic(10), location.getLocid(),
			this.creationDate, 0);

		final Germplasm progenitor3 = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0);
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
	}

	@Test
	public void test_getGermplasmProgenitorDetails_whenMethodIsDerivative_Ok() {
		final String progenitor2FemaleParentGUID = RandomStringUtils.randomAlphabetic(10);
		final String progenitor2GUID = RandomStringUtils.randomAlphabetic(10);

		final Germplasm progenitor2FemaleParent = this.createGermplasm(this.derivativeMethod, progenitor2FemaleParentGUID, null, 0, 0, 0);
		final Germplasm progenitor2 =
			this.createGermplasm(this.derivativeMethod, progenitor2GUID, null, 0, progenitor2FemaleParent.getGid(), 0);
		this.sessionProvder.getSession().flush();

		final Germplasm germplasm =
			this.createGermplasm(this.derivativeMethod, this.germplasmUUID, null, -1, progenitor2FemaleParent.getGid(),
				progenitor2.getGid());
		this.sessionProvder.getSession().flush();

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

		final Germplasm progenitor1FemaleParent = this.createGermplasm(this.generativeMethod, progenitor1FemaleParentGUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm progenitor1 =
			this.createGermplasm(this.generativeMethod, progenitor1GUID, null, 2, progenitor1FemaleParent.getGid(), 0);
		final Germplasm progenitor2 = this.createGermplasm(this.generativeMethod, progenitor2GUID, null, 0, 0, 0);
		this.sessionProvder.getSession().flush();

		final Germplasm germplasm =
			this.createGermplasm(this.generativeMethod, this.germplasmUUID, null, 3, progenitor1.getGid(), progenitor2.getGid());
		this.sessionProvder.getSession().flush();
		final Germplasm progenitor3 = this.createGermplasm(this.generativeMethod, RandomStringUtils.randomAlphabetic(10), null, 0, 0, 0);
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
	public void test_createGermplasm_Ok() {
		final String creationDate = "2020-10-24";
		final GermplasmImportRequest request = new GermplasmImportRequest(RandomStringUtils.randomAlphabetic(20), creationDate,
			this.derivativeMethod.getMid().toString(), RandomStringUtils.randomAlphabetic(20), "UKN",
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		request.getSynonyms().add(new Synonym(RandomStringUtils.randomAlphabetic(20), DRVNM));
		request.getAdditionalInfo().put(NOTE, RandomStringUtils.randomAlphabetic(20));
		final List<GermplasmDTO> germplasmDTOList =
			this.germplasmService.createGermplasm(this.userId, CROP_NAME, Collections.singletonList(request));
		assertThat(germplasmDTOList.size(), is(1));

		final GermplasmDTO germplasmDTO = germplasmDTOList.get(0);
		assertThat(germplasmDTO.getGid(), notNullValue());
		final Integer gid = Integer.parseInt(germplasmDTO.getGid());
		assertThat(germplasmDTO.getBreedingMethodDbId(), equalTo(this.derivativeMethod.getMid().toString()));
		assertThat(germplasmDTO.getCountryOfOriginCode(), equalTo("UKN"));
		assertThat(germplasmDTO.getAcquisitionDate(), equalTo(Util.tryParseDate(creationDate, Util.FRONTEND_DATE_FORMAT)));
		assertThat(germplasmDTO.getGermplasmDbId(), notNullValue());
		assertThat(germplasmDTO.getGermplasmPUI(), nullValue());
		assertThat(germplasmDTO.getEntryNumber(), nullValue());
		assertThat(germplasmDTO.getDefaultDisplayName(), equalTo(request.getDefaultDisplayName()));
		assertThat(germplasmDTO.getGermplasmName(), equalTo(request.getDefaultDisplayName()));

		final Map<String, Integer> existingNameTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmNames = this.daoFactory.getNameDao().getNamesByGids(Collections.singletonList(gid)).stream()
			.collect(Collectors.toMap(Name::getTypeId, Name::getNval));
		if (existingNameTypes.containsKey(GermplasmImportRequest.ACCNO)) {
			assertThat(germplasmDTO.getAccessionNumber(), equalTo(request.getAccessionNumber()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.ACCNO)), equalTo(request.getAccessionNumber()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.ACCNO), equalTo(request.getAccessionNumber()));
		}
		if (existingNameTypes.containsKey(GermplasmImportRequest.GENUS)) {
			assertThat(germplasmDTO.getGenus(), equalTo(request.getGenus()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.GENUS)), equalTo(request.getGenus()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.GENUS), equalTo(request.getGenus()));
		}
		assertThat(germplasmDTO.getPedigree(), nullValue());
		if (existingNameTypes.containsValue(GermplasmImportRequest.PED)) {
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.PED)), equalTo(request.getPedigree()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.PED), equalTo(request.getPedigree()));
		}

		final Map<String, Integer> existingAttrTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType()))
				, new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmAttributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Collections.singletonList(gid)).stream()
				.collect(Collectors.toMap(Attribute::getTypeId, Attribute::getAval));
		if (existingAttrTypes.containsKey(GermplasmImportRequest.PLOTCODE)) {
			assertThat(germplasmDTO.getSeedSource(), equalTo(request.getSeedSource()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.PLOTCODE)), equalTo(request.getSeedSource()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.PLOTCODE), equalTo(request.getSeedSource()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.ORIGIN)) {
			assertThat(germplasmDTO.getGermplasmOrigin(), equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.ORIGIN)),
				equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.ORIGIN), equalTo(request.getGermplasmOrigin()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.CROPNM)) {
			assertThat(germplasmDTO.getCommonCropName(), equalTo(request.getCommonCropName()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.CROPNM)), equalTo(request.getCommonCropName()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.CROPNM), equalTo(request.getCommonCropName()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES)) {
			assertThat(germplasmDTO.getSpecies(), equalTo(request.getSpecies()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES)), equalTo(request.getSpecies()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES), equalTo(request.getSpecies()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES_AUTH)) {
			assertThat(germplasmDTO.getSpeciesAuthority(), equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES_AUTH)),
				equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES_AUTH), equalTo(request.getSpeciesAuthority()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX)) {
			assertThat(germplasmDTO.getSubtaxa(), equalTo(request.getSubtaxa()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX)), equalTo(request.getSubtaxa()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX), equalTo(request.getSubtaxa()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX_AUTH)) {
			assertThat(germplasmDTO.getSubtaxaAuthority(), equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX_AUTH)),
				equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX_AUTH), equalTo(request.getSubtaxaAuthority()));
		}
		assertTrue(germplasmDTO.getSynonyms().size() > 0);
		assertTrue(germplasmDTO.getAdditionalInfo().size() > 0);

		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(gid);
		assertThat(germplasm.getGnpgs(), equalTo(0));
		assertThat(germplasm.getGpid1(), equalTo(0));
		assertThat(germplasm.getGpid2(), equalTo(0));
		assertThat(germplasm.getUserId(), equalTo(this.userId));
	}

	@Test
	public void test_updateGermplasm_Ok() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		final String newCreationDate = "2020-10-24";
		final Method methodNew = this.createBreedingMethod("GEN", 2);
		final String location = "ARG";
		final GermplasmUpdateRequest request = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), newCreationDate,
			methodNew.getMid().toString(), RandomStringUtils.randomAlphabetic(20), location,
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		request.getSynonyms().add(new Synonym(RandomStringUtils.randomAlphabetic(20), DRVNM));
		request.getAdditionalInfo().put(NOTE, RandomStringUtils.randomAlphabetic(20));

		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, germplasm.getGermplasmUUID(), request);
		final Integer gid = germplasm.getGid();
		assertThat(germplasmDTO.getGid(), equalTo(gid.toString()));
		assertThat(germplasmDTO.getGermplasmDbId(), equalTo(germplasm.getGermplasmUUID()));
		assertThat(germplasmDTO.getBreedingMethodDbId(), equalTo(methodNew.getMid().toString()));
		assertThat(germplasmDTO.getCountryOfOriginCode(), equalTo(location));
		assertThat(germplasmDTO.getAcquisitionDate(), equalTo(Util.tryParseDate(newCreationDate, Util.FRONTEND_DATE_FORMAT)));
		assertThat(germplasmDTO.getGermplasmPUI(), nullValue());
		assertThat(germplasmDTO.getEntryNumber(), nullValue());
		assertThat(germplasmDTO.getDefaultDisplayName(), equalTo(request.getDefaultDisplayName()));
		assertThat(germplasmDTO.getGermplasmName(), equalTo(request.getDefaultDisplayName()));

		final Map<String, Integer> existingNameTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmNames = this.daoFactory.getNameDao().getNamesByGids(Collections.singletonList(
			gid)).stream()
			.collect(Collectors.toMap(Name::getTypeId, Name::getNval));
		if (existingNameTypes.containsKey(GermplasmImportRequest.ACCNO)) {
			assertThat(germplasmDTO.getAccessionNumber(), equalTo(request.getAccessionNumber()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.ACCNO)), equalTo(request.getAccessionNumber()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.ACCNO), equalTo(request.getAccessionNumber()));
		}
		if (existingNameTypes.containsKey(GermplasmImportRequest.GENUS)) {
			assertThat(germplasmDTO.getGenus(), equalTo(request.getGenus()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.GENUS)), equalTo(request.getGenus()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.GENUS), equalTo(request.getGenus()));
		}
		assertThat(germplasmDTO.getPedigree(), nullValue());
		if (existingNameTypes.containsValue(GermplasmImportRequest.PED)) {
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.PED)), equalTo(request.getPedigree()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.PED), equalTo(request.getPedigree()));
		}

		final Map<String, Integer> existingAttrTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType()))
				, new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmAttributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Collections.singletonList(gid)).stream()
				.collect(Collectors.toMap(Attribute::getTypeId, Attribute::getAval));
		if (existingAttrTypes.containsKey(GermplasmImportRequest.PLOTCODE)) {
			assertThat(germplasmDTO.getSeedSource(), equalTo(request.getSeedSource()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.PLOTCODE)), equalTo(request.getSeedSource()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.PLOTCODE), equalTo(request.getSeedSource()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.ORIGIN)) {
			assertThat(germplasmDTO.getGermplasmOrigin(), equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.ORIGIN)),
				equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.ORIGIN), equalTo(request.getGermplasmOrigin()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.CROPNM)) {
			assertThat(germplasmDTO.getCommonCropName(), equalTo(request.getCommonCropName()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.CROPNM)), equalTo(request.getCommonCropName()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.CROPNM), equalTo(request.getCommonCropName()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES)) {
			assertThat(germplasmDTO.getSpecies(), equalTo(request.getSpecies()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES)), equalTo(request.getSpecies()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES), equalTo(request.getSpecies()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES_AUTH)) {
			assertThat(germplasmDTO.getSpeciesAuthority(), equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES_AUTH)),
				equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES_AUTH), equalTo(request.getSpeciesAuthority()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX)) {
			assertThat(germplasmDTO.getSubtaxa(), equalTo(request.getSubtaxa()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX)), equalTo(request.getSubtaxa()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX), equalTo(request.getSubtaxa()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX_AUTH)) {
			assertThat(germplasmDTO.getSubtaxaAuthority(), equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX_AUTH)),
				equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX_AUTH), equalTo(request.getSubtaxaAuthority()));
		}
		assertTrue(germplasmDTO.getSynonyms().size() > 0);
		assertTrue(germplasmDTO.getAdditionalInfo().size() > 0);

		// Verify that originally saved field values are unmodified
		final Germplasm germplasmLatest = this.daoFactory.getGermplasmDao().getById(gid);
		assertThat(germplasmLatest.getGnpgs(), equalTo(germplasm.getGnpgs()));
		assertThat(germplasmLatest.getGpid1(), equalTo(germplasm.getGpid1()));
		assertThat(germplasmLatest.getGpid2(), equalTo(germplasm.getGpid2()));
	}

	@Test
	public void test_updateGermplasm_OnlyAddSynonymsAndAttributes_Ok() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		// Specify null for those fields we don't want to update like breeding method, location, germplasm date
		final GermplasmUpdateRequest request = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), null,
			null, RandomStringUtils.randomAlphabetic(20), null,
			null,
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		request.getSynonyms().add(new Synonym(RandomStringUtils.randomAlphabetic(20), DRVNM));
		request.getAdditionalInfo().put(NOTE, RandomStringUtils.randomAlphabetic(20));

		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, germplasm.getGermplasmUUID(), request);
		final Integer gid = germplasm.getGid();
		assertThat(germplasmDTO.getGid(), equalTo(gid.toString()));
		// Germplasm details remain unchanged
		assertThat(germplasmDTO.getGermplasmDbId(), equalTo(germplasm.getGermplasmUUID()));
		assertThat(germplasmDTO.getBreedingMethodDbId(), equalTo(germplasm.getMethodId().toString()));
		assertThat(germplasmDTO.getCountryOfOriginCode(), equalTo("UKN"));
		assertThat(germplasmDTO.getAcquisitionDate(), equalTo(Util.tryParseDate(this.creationDate, Util.DATE_AS_NUMBER_FORMAT)));
		assertThat(germplasmDTO.getDefaultDisplayName(), equalTo(request.getDefaultDisplayName()));
		assertThat(germplasmDTO.getGermplasmName(), equalTo(request.getDefaultDisplayName()));

		final Map<String, Integer> existingNameTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmNames = this.daoFactory.getNameDao().getNamesByGids(Collections.singletonList(
			gid)).stream()
			.collect(Collectors.toMap(Name::getTypeId, Name::getNval));
		if (existingNameTypes.containsKey(GermplasmImportRequest.ACCNO)) {
			assertThat(germplasmDTO.getAccessionNumber(), equalTo(request.getAccessionNumber()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.ACCNO)), equalTo(request.getAccessionNumber()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.ACCNO), equalTo(request.getAccessionNumber()));
		}
		if (existingNameTypes.containsKey(GermplasmImportRequest.GENUS)) {
			assertThat(germplasmDTO.getGenus(), equalTo(request.getGenus()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.GENUS)), equalTo(request.getGenus()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.GENUS), equalTo(request.getGenus()));
		}
		assertThat(germplasmDTO.getPedigree(), nullValue());
		if (existingNameTypes.containsValue(GermplasmImportRequest.PED)) {
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.PED)), equalTo(request.getPedigree()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.PED), equalTo(request.getPedigree()));
		}

		final Map<String, Integer> existingAttrTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType()))
				, new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmAttributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Collections.singletonList(gid)).stream()
				.collect(Collectors.toMap(Attribute::getTypeId, Attribute::getAval));
		if (existingAttrTypes.containsKey(GermplasmImportRequest.PLOTCODE)) {
			assertThat(germplasmDTO.getSeedSource(), equalTo(request.getSeedSource()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.PLOTCODE)), equalTo(request.getSeedSource()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.PLOTCODE), equalTo(request.getSeedSource()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.ORIGIN)) {
			assertThat(germplasmDTO.getGermplasmOrigin(), equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.ORIGIN)),
				equalTo(request.getGermplasmOrigin()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.ORIGIN), equalTo(request.getGermplasmOrigin()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.CROPNM)) {
			assertThat(germplasmDTO.getCommonCropName(), equalTo(request.getCommonCropName()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.CROPNM)), equalTo(request.getCommonCropName()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.CROPNM), equalTo(request.getCommonCropName()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES)) {
			assertThat(germplasmDTO.getSpecies(), equalTo(request.getSpecies()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES)), equalTo(request.getSpecies()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES), equalTo(request.getSpecies()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES_AUTH)) {
			assertThat(germplasmDTO.getSpeciesAuthority(), equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES_AUTH)),
				equalTo(request.getSpeciesAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES_AUTH), equalTo(request.getSpeciesAuthority()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX)) {
			assertThat(germplasmDTO.getSubtaxa(), equalTo(request.getSubtaxa()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX)), equalTo(request.getSubtaxa()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX), equalTo(request.getSubtaxa()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX_AUTH)) {
			assertThat(germplasmDTO.getSubtaxaAuthority(), equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX_AUTH)),
				equalTo(request.getSubtaxaAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX_AUTH), equalTo(request.getSubtaxaAuthority()));
		}
		assertTrue(germplasmDTO.getSynonyms().size() > 0);
		assertTrue(germplasmDTO.getAdditionalInfo().size() > 0);

		// Verify that originally saved field values are unmodified
		final Germplasm germplasmLatest = this.daoFactory.getGermplasmDao().getById(gid);
		assertThat(germplasmLatest.getGnpgs(), equalTo(germplasm.getGnpgs()));
		assertThat(germplasmLatest.getGpid1(), equalTo(germplasm.getGpid1()));
		assertThat(germplasmLatest.getGpid2(), equalTo(germplasm.getGpid2()));
	}

	@Test
	public void test_updateGermplasm_UpdateSynonymsAndAttributes_Ok() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		// Specify null for those fields we don't want to update like breeding method, location, germplasm date
		// Save the first version of names and attributes
		final GermplasmUpdateRequest request1 = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), null,
			null, RandomStringUtils.randomAlphabetic(20), null,
			null,
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		request1.getSynonyms().add(new Synonym(RandomStringUtils.randomAlphabetic(20), DRVNM));
		request1.getAdditionalInfo().put(NOTE, RandomStringUtils.randomAlphabetic(20));
		this.germplasmService.updateGermplasm(this.userId, germplasm.getGermplasmUUID(), request1);
		
		// Update the names and attributes with new values
		final GermplasmUpdateRequest request2 = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), null,
			null, RandomStringUtils.randomAlphabetic(20), null,
			null,
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));
		request2.getSynonyms().add(new Synonym(RandomStringUtils.randomAlphabetic(20), DRVNM));
		request2.getAdditionalInfo().put(NOTE, RandomStringUtils.randomAlphabetic(20));
		
		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, germplasm.getGermplasmUUID(), request2);
		final Integer gid = germplasm.getGid();
		assertThat(germplasmDTO.getGid(), equalTo(gid.toString()));
		// Germplasm details remain unchanged
		assertThat(germplasmDTO.getGermplasmDbId(), equalTo(germplasm.getGermplasmUUID()));
		assertThat(germplasmDTO.getBreedingMethodDbId(), equalTo(germplasm.getMethodId().toString()));
		assertThat(germplasmDTO.getCountryOfOriginCode(), equalTo("UKN"));
		assertThat(germplasmDTO.getAcquisitionDate(), equalTo(Util.tryParseDate(this.creationDate, Util.DATE_AS_NUMBER_FORMAT)));
		assertThat(germplasmDTO.getDefaultDisplayName(), equalTo(request2.getDefaultDisplayName()));
		assertThat(germplasmDTO.getGermplasmName(), equalTo(request2.getDefaultDisplayName()));

		final Map<String, Integer> existingNameTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.NAMES_NAME.getTable(),
				Collections.singleton(UDTableType.NAMES_NAME.getType()), new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_NAMETYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmNames = this.daoFactory.getNameDao().getNamesByGids(Collections.singletonList(
			gid)).stream()
			.collect(Collectors.toMap(Name::getTypeId, Name::getNval));
		if (existingNameTypes.containsKey(GermplasmImportRequest.ACCNO)) {
			assertThat(germplasmDTO.getAccessionNumber(), equalTo(request2.getAccessionNumber()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.ACCNO)), equalTo(request2.getAccessionNumber()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.ACCNO), equalTo(request2.getAccessionNumber()));
		}
		if (existingNameTypes.containsKey(GermplasmImportRequest.GENUS)) {
			assertThat(germplasmDTO.getGenus(), equalTo(request2.getGenus()));
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.GENUS)), equalTo(request2.getGenus()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.GENUS), equalTo(request2.getGenus()));
		}
		assertThat(germplasmDTO.getPedigree(), nullValue());
		if (existingNameTypes.containsValue(GermplasmImportRequest.PED)) {
			assertThat(germplasmNames.get(existingNameTypes.get(GermplasmImportRequest.PED)), equalTo(request2.getPedigree()));
			assertThat(germplasmDTO.getSynonyms().get(GermplasmImportRequest.PED), equalTo(request2.getPedigree()));
		}

		final Map<String, Integer> existingAttrTypes = this.daoFactory.getUserDefinedFieldDAO()
			.getByCodes(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				new HashSet<>(Arrays.asList(UDTableType.ATRIBUTS_ATTRIBUTE.getType(), UDTableType.ATRIBUTS_PASSPORT.getType()))
				, new HashSet<>(GermplasmImportRequest.BRAPI_SPECIFIABLE_ATTRTYPES))
			.stream().collect(Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
		final Map<Integer, String> germplasmAttributes =
			this.daoFactory.getAttributeDAO().getAttributeValuesGIDList(Collections.singletonList(gid)).stream()
				.collect(Collectors.toMap(Attribute::getTypeId, Attribute::getAval));
		if (existingAttrTypes.containsKey(GermplasmImportRequest.PLOTCODE)) {
			assertThat(germplasmDTO.getSeedSource(), equalTo(request2.getSeedSource()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.PLOTCODE)), equalTo(request2.getSeedSource()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.PLOTCODE), equalTo(request2.getSeedSource()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.ORIGIN)) {
			assertThat(germplasmDTO.getGermplasmOrigin(), equalTo(request2.getGermplasmOrigin()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.ORIGIN)),
				equalTo(request2.getGermplasmOrigin()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.ORIGIN), equalTo(request2.getGermplasmOrigin()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.CROPNM)) {
			assertThat(germplasmDTO.getCommonCropName(), equalTo(request2.getCommonCropName()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.CROPNM)), equalTo(request2.getCommonCropName()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.CROPNM), equalTo(request2.getCommonCropName()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES)) {
			assertThat(germplasmDTO.getSpecies(), equalTo(request2.getSpecies()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES)), equalTo(request2.getSpecies()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES), equalTo(request2.getSpecies()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SPECIES_AUTH)) {
			assertThat(germplasmDTO.getSpeciesAuthority(), equalTo(request2.getSpeciesAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SPECIES_AUTH)),
				equalTo(request2.getSpeciesAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SPECIES_AUTH), equalTo(request2.getSpeciesAuthority()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX)) {
			assertThat(germplasmDTO.getSubtaxa(), equalTo(request2.getSubtaxa()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX)), equalTo(request2.getSubtaxa()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX), equalTo(request2.getSubtaxa()));
		}
		if (existingAttrTypes.containsKey(GermplasmImportRequest.SUBTAX_AUTH)) {
			assertThat(germplasmDTO.getSubtaxaAuthority(), equalTo(request2.getSubtaxaAuthority()));
			assertThat(germplasmAttributes.get(existingAttrTypes.get(GermplasmImportRequest.SUBTAX_AUTH)),
				equalTo(request2.getSubtaxaAuthority()));
			assertThat(germplasmDTO.getAdditionalInfo().get(GermplasmImportRequest.SUBTAX_AUTH), equalTo(request2.getSubtaxaAuthority()));
		}
		assertTrue(germplasmDTO.getSynonyms().size() > 0);
		assertTrue(germplasmDTO.getAdditionalInfo().size() > 0);

		// Verify that originally saved field values are unmodified
		final Germplasm germplasmLatest = this.daoFactory.getGermplasmDao().getById(gid);
		assertThat(germplasmLatest.getGnpgs(), equalTo(germplasm.getGnpgs()));
		assertThat(germplasmLatest.getGpid1(), equalTo(germplasm.getGpid1()));
		assertThat(germplasmLatest.getGpid2(), equalTo(germplasm.getGpid2()));
	}


	@Test(expected = MiddlewareRequestException.class)
	public void test_updateGermplasm_InvalidGUID() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		final String creationDate = "2020-10-24";
		final GermplasmUpdateRequest request = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), creationDate, null,
			RandomStringUtils.randomAlphabetic(20), "UKN",
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));

		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, UUID.randomUUID().toString(), request);
		Assert.fail("Expected to throw exception that germplasm with GUID does not exist but did not");
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_updateGermplasm_MethodMutation() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		final String creationDate = "2020-10-24";
		// New method is "DER" type while old one is "GEN" type. Expecting to cause method mutation validation error
		final Method methodNew = this.createBreedingMethod("DER", 2);
		final GermplasmUpdateRequest request = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), creationDate,
			methodNew.getMid().toString(), RandomStringUtils.randomAlphabetic(20), "UKN",
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));

		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, UUID.randomUUID().toString(), request);
		Assert.fail("Expected to throw exception that breeding method type is invalid but did not");
	}

	@Test(expected = MiddlewareRequestException.class)
	public void test_updateGermplasm_MethodProgenitorsError() {
		final Method method = this.createBreedingMethod("GEN", 2);
		final String germplasmUUID = UUID.randomUUID().toString();
		final Germplasm germplasm = this.createGermplasm(method, germplasmUUID, null, 2, 0, 0);

		final String creationDate = "2020-10-24";
		// New method has mpgrn = 1 while old one has mpgrn = 2. Expecting to cause validation error that mpgrn should be the same
		final Method methodNew = this.createBreedingMethod("GEN", 1);
		final GermplasmUpdateRequest request = new GermplasmUpdateRequest(RandomStringUtils.randomAlphabetic(20), creationDate,
			methodNew.getMid().toString(), RandomStringUtils.randomAlphabetic(20), "UKN",
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20),
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(20));

		final GermplasmDTO germplasmDTO = this.germplasmService.updateGermplasm(this.userId, UUID.randomUUID().toString(), request);
		Assert.fail("Expected to throw exception that breeding method.mpgrn should be the same");
	}

	@Test
	public void testDeleteGermplasm() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		this.germplasmService.deleteGermplasm(Lists.newArrayList(germplasm.getGid()));

		assertThat(this.germplasmService.getGermplasmByGIDs(Lists.newArrayList(germplasm.getGid())), iterableWithSize(0));
	}

	@Test
	public void testGetCodeFixedGidsByGidList() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Set the germplasm as code fixed (mgid > 0)
		germplasm.setMgid(1);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasm);

		assertThat(this.germplasmService.getGermplasmByGIDs(Lists.newArrayList(germplasm.getGid())), iterableWithSize(1));
	}

	@Test
	public void testGetGidsWithOpenLots() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

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
	public void testGetGidsOfGermplasmWithDescendants() {
		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, null, 0, 0, 0);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, null, 0, 0, 0);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		this.sessionProvder.getSession().flush();

		final Set<Integer> gids =
			this.germplasmService.getGidsOfGermplasmWithDescendants(Lists.newArrayList(germplasmWithDescendants.getGid()));

		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testGetGermplasmUsedInOneOrMoreList() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

		// Create germplasm list with listdata associated with germplasm
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(null);
		this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);

		final GermplasmListData germplasmListData =
			new GermplasmListData(null, germplasmList, germplasm.getGid(), 1, "EntryCode", "SeedSource", "Germplasm Name 5", "GroupName", 0,
				1);
		this.daoFactory.getGermplasmListDataDAO().saveOrUpdate(germplasmListData);

		final Set<Integer> gids =
			this.germplasmService.getGermplasmUsedInOneOrMoreList(Lists.newArrayList(germplasm.getGid()));

		Assert.assertEquals(1, gids.size());

	}

	@Test
	public void testGetGermplasmUsedInStudies() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, null, 0, 0, 0);

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

	private Germplasm createGermplasm(final Method method, final String germplasmUUID, final Location location, final Integer gnpgs,
		final Integer gpid1,
		final Integer gpid2) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, 0, (location == null) ? 0 : location.getLocid(), Integer.parseInt(this.creationDate), 0,
			0, 0, null, null, method);
		if (StringUtils.isNotEmpty(germplasmUUID)) {
			germplasm.setGermplasmUUID(germplasmUUID);
		}
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708, "");
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

	private Location createLocation() {
		final Location location = new Location(null, 1,
			1, RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(8),
			1, 1, 1,
			1, 1);
		location.setLdefault(false);
		this.daoFactory.getLocationDAO().saveOrUpdate(location);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getLocationDAO().refresh(location);
		return location;
	}

	private Name addName(final Integer gid, final Integer nameId, final String nameVal, final Integer locId, final String date,
		final int preferred) {
		final Name name = new Name(null, gid, nameId, preferred, this.userId, nameVal, locId, Integer.valueOf(date), 0);
		this.daoFactory.getNameDao().save(name);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getNameDao().refresh(name);
		return name;
	}

	private Progenitor addProgenitor(final Germplasm son, final Germplasm parent) {
		final Progenitor progenitor = new Progenitor(son, 3, parent.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getProgenitorDao().refresh(progenitor);
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

}
