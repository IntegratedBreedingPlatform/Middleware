package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
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

	private DaoFactory daoFactory;

	private GermplasmService germplasmService;

	@Before
	public void setUp() {

		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.germplasmService = new GermplasmServiceImpl(this.sessionProvder);

	}

	@Test
	public void testImportGermplasmUpdates_NewNamesAndAttributes() {
		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);
		final int creationDate = 20200101;

		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final Germplasm germplasm = this.createGermplasm(method);
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
		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final int creationDate = 20200101;
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Germplasm germplasm = this.createGermplasm(method);

		final List<Germplasm> germplasmByGIDs = this.germplasmService.getGermplasmByGIDs(Arrays.asList(germplasm.getGid()));
		assertThat(germplasmByGIDs, hasSize(1));

		final Germplasm actualGermplasm = germplasmByGIDs.get(0);
		assertNotNull(actualGermplasm);
		assertThat(actualGermplasm.getGid(), is(germplasm.getGid()));
	}

	@Test
	public void testGetAttributesByGID() {
		final Method method = this.createBreedingMethod("DER", -1);
		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("DER", -1);
		final Germplasm germplasmWithoutPlotCode = this.createGermplasm(method);
		final Germplasm germplasmWithPlotCode = this.createGermplasm(method);

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
	public void testImportGermplasmUpdates_BreedingMethodTypeMismatch() {

		// If the germplasm has a GENERATIVE type then the new breeding method has to be also GENERATIVE, if not, it should throw an error.
		final Method method = this.createBreedingMethod("GEN", 2);
		final Method newMethod = this.createBreedingMethod("DER", -1);
		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("GEN", 2);
		final Method newMethod = this.createBreedingMethod("GEN", 0);
		final Germplasm germplasm = this.createGermplasm(method);

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
		final Method method = this.createBreedingMethod("GEN", 1);
		final Method newMethod = this.createBreedingMethod("GEN", 1);
		final Germplasm germplasm = this.createGermplasm(method);

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

		final Method method = this.createBreedingMethod("GEN", 2);
		final Method newMethod = this.createBreedingMethod("GEN", 2);

		final Germplasm germplasm = this.createGermplasm(method);

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

		final Method method = this.createBreedingMethod("GEN", 2);
		final Method newMethod = this.createBreedingMethod("GEN", 2);

		final Germplasm germplasm = this.createGermplasm(method);

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

		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		// Create Germplasm With Descendant
		final Germplasm germplasm = this.createGermplasm(method);
		final Germplasm germplasmDescendant = this.createGermplasm(method);
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

		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final Germplasm germplasm = this.createGermplasm(method);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method);
		final Germplasm germplasmDescendant = this.createGermplasm(method);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method);
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

		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final Germplasm germplasm = this.createGermplasm(method);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method);
		final Germplasm germplasmDescendant = this.createGermplasm(method);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method);
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

		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final Germplasm germplasm = this.createGermplasm(method);

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

		final Method method = this.createBreedingMethod("DER", -1);
		final Method newMethod = this.createBreedingMethod("DER", -1);

		final Germplasm germplasm = this.createGermplasm(method);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method);
		final Germplasm germplasmDescendant = this.createGermplasm(method);
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

		final Method method = this.createBreedingMethod("GEN", 2);
		final Method newMethod = this.createBreedingMethod("GEN", 2);
		final Germplasm germplasm = this.createGermplasm(method);

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

	private Germplasm createGermplasm(final Method method) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, null, null, method);
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(10),
				"Selfing a Single Plant or population and bulk seed", 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708, "");
		return this.daoFactory.getMethodDAO().save(method);
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
