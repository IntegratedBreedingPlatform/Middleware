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
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GermplasmServiceImplIntegrationTest extends IntegrationTestBase {

	public static final String DRVNM = "DRVNM";
	public static final String NOTE = "NOTE";
	public static final String UGM = "UGM";
	public static final String BDU = "BDU";
	public static final String UKN = "UKN";
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
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);
		final int creationDate = 20200101;

		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), newMethod, newLocation, creationDate);
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
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
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
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), newMethod, newLocation, creationDate);
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
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final Germplasm germplasm = this.createGermplasm(method);

		// Create Duplicate PreferredName assigned
		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 1, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), newMethod, newLocation, creationDate);

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("import.germplasm.update.preferred.name.duplicate.names"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_PreferredNameDoesntExist() {
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final Germplasm germplasm = this.createGermplasm(method);
		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), newMethod, newLocation, creationDate);
		// Set invalid preferred name code.
		germplasmUpdateDTO.setPreferredNameType("Some Non Existing Code");

		try {
			this.germplasmService.importGermplasmUpdates(1, Collections.singletonList(germplasmUpdateDTO));
		} catch (final MiddlewareRequestException e) {
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("import.germplasm.update.preferred.name.doesnt.exist"));
		}

	}

	@Test
	public void testImportGermplasmUpdates_DuplicateNamesAndAttributes() {
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method);

		final GermplasmUpdateDTO germplasmUpdateDTO =
			this.createGermplasmUpdateDto(germplasm.getGid(), germplasm.getGermplasmUUID(), newMethod, newLocation, creationDate);
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
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("import.germplasm.update.duplicate.names"));
			Assert.assertTrue(e.getErrorCodeParamsMultiMap().containsKey("import.germplasm.update.duplicate.attributes"));
		}

	}

	@Test
	public void testGetGermplasmByGIDs() {
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);
		final Germplasm germplasm = this.createGermplasm(method);

		final List<Germplasm> germplasmByGIDs = this.germplasmService.getGermplasmByGIDs(Arrays.asList(germplasm.getGid()));
		assertThat(germplasmByGIDs, hasSize(1));

		final Germplasm actualGermplasm = germplasmByGIDs.get(0);
		assertNotNull(actualGermplasm);
		assertThat(actualGermplasm.getGid(), is(germplasm.getGid()));
	}

	@Test
	public void testGetAttributesByGID() {
		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);
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

		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);
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

		final Method method = this.daoFactory.getMethodDAO().getByCode(UGM, null);
		final Germplasm germplasmWithoutPlotCode = this.createGermplasm(method);
		final Germplasm germplasmWithPlotCode = this.createGermplasm(method);

		final UserDefinedField plotCodeAttr =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_PASSPORT.getTable(),
				UDTableType.ATRIBUTS_PASSPORT.getType(), GermplasmServiceImpl.PLOT_CODE);
		this.daoFactory.getAttributeDAO()
			.save(new Attribute(null, germplasmWithPlotCode.getGid(), plotCodeAttr.getFldno(), germplasmWithPlotCode.getUserId(), plotCodeValue,
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

	private Germplasm createGermplasm(final Method method) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, null, null, method);
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private GermplasmUpdateDTO createGermplasmUpdateDto(final Integer gid, final String uuid, final Method method,
		final Location location, final Integer creationDate) {
		final GermplasmUpdateDTO germplasmUpdateDTO = new GermplasmUpdateDTO();
		germplasmUpdateDTO.setGid(gid);
		germplasmUpdateDTO.setGermplasmUUID(uuid);
		germplasmUpdateDTO.setLocationAbbreviation(location.getLabbr());
		germplasmUpdateDTO.setBreedingMethodAbbr(method.getMcode());
		germplasmUpdateDTO.setPreferredNameType(DRVNM);
		germplasmUpdateDTO.setCreationDate(String.valueOf(creationDate));
		germplasmUpdateDTO.setReference("Reference gid " + gid);
		germplasmUpdateDTO.getAttributes().put(NOTE, "Note for " + gid);
		germplasmUpdateDTO.getNames().put(DRVNM, "Name for " + gid);
		return germplasmUpdateDTO;
	}

}
