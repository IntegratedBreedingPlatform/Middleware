package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.IntegrationTestBase;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GermplasmServiceImplTest extends IntegrationTestBase {

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
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(UKN)).get(0);
		final int creationDate = 20200101;

		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);

		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, location.getLocid());

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
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(UKN)).get(0);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, location.getLocid());

		this.daoFactory.getNameDao().save(new Name(null, germplasm.getGid(), newNameCode.getFldno(), 0, germplasm.getUserId(),
			"", germplasm.getLocationId(), germplasm.getGdate(), 0));

		final Attribute attribute = new Attribute();
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
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(UKN)).get(0);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, location.getLocid());

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
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(UKN)).get(0);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, location.getLocid());
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
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(UKN)).get(0);

		final int creationDate = 20200101;
		final Method newMethod = this.daoFactory.getMethodDAO().getByCode(BDU, null);
		final Location newLocation = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList(NOLOC)).get(0);
		final UserDefinedField newNameCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(),
				UDTableType.NAMES_NAME.getType(), DRVNM);
		final UserDefinedField newAttributeCode =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode(UDTableType.ATRIBUTS_ATTRIBUTE.getTable(),
				UDTableType.ATRIBUTS_ATTRIBUTE.getType(), NOTE);

		final Germplasm germplasm = this.createGermplasm(method, location.getLocid());

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

	private Germplasm createGermplasm(final Method method, final int locationId) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), 0, 0, 0,
			0, 0, 0, 0, 0,
			0, 0, null, null, method);
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}

	private Germplasm createGermplasmWithNamesAndAttributes(final Method method, final int locationId,
		final UserDefinedField nameUserDefinedField, final UserDefinedField attributeUserDefinedField) {
		final Germplasm germplasm = this.createGermplasm(method, locationId);
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
