package org.generationcp.middleware.dao.audit.germplasm;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.service.impl.audit.GermplasmAttributeAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmBasicDetailsAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmNameAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmOtherProgenitorsAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmProgenitorDetailsAuditDTO;
import org.generationcp.middleware.service.impl.audit.GermplasmReferenceAuditDTO;
import org.generationcp.middleware.service.impl.audit.RevisionType;
import org.generationcp.middleware.utils.test.SQLQueryUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GermplasmAuditDAOTest extends IntegrationTestBase {

	private static final String AUDIT_TABLE_PREFIX = "_aud";
	private static final String ATTRIBUTES_AUDIT_TABLE = Attribute.class.getAnnotation(Table.class).name() + AUDIT_TABLE_PREFIX;
	private static final String GERMPLASMS_AUDIT_TABLE = Germplasm.class.getAnnotation(Table.class).name() + AUDIT_TABLE_PREFIX;
	private static final String NAMES_AUDIT_TABLE = Name.class.getAnnotation(Table.class).name() + AUDIT_TABLE_PREFIX;
	private static final String PROGENITORS_AUDIT_TABLE = Progenitor.class.getAnnotation(Table.class).name() + AUDIT_TABLE_PREFIX;
	private static final String REFERENCE_AUDIT_TABLE = Bibref.class.getAnnotation(Table.class).name() + AUDIT_TABLE_PREFIX;

	private static final Integer NAME_ID = new Random().nextInt();
	private static final Integer ATTRIBUTE_ID = new Random().nextInt();
	private static final Integer GID = new Random().nextInt();
	private static final Integer REFERENCE_ID = new Random().nextInt();

	private GermplasmAuditDAO germplasmAuditDAO;
	private LocationDAO locationDAO;
	private UserDefinedFieldDAO userDefinedFieldDAO;
	private MethodDAO methodDAO;
	private CVTermDao cvTermDao;
	private CvTermPropertyDao cvTermPropertyDao;

	@Before
	public void setUp() throws Exception {
		this.germplasmAuditDAO = new GermplasmAuditDAO(this.sessionProvder.getSession());
		this.locationDAO = new LocationDAO(this.sessionProvder.getSession());
		this.userDefinedFieldDAO = new UserDefinedFieldDAO(this.sessionProvder.getSession());

		this.methodDAO = new MethodDAO(this.sessionProvder.getSession());

		this.cvTermDao = new CVTermDao();
		this.cvTermDao.setSession(this.sessionProvder.getSession());

		this.cvTermPropertyDao = new CvTermPropertyDao();
		this.cvTermPropertyDao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void shouldGetAndCountNameChangesByNameId() {
		final String nameType1 = "nameType1";
		final String nameCode1 = "nameCode1";
		final String nameValue1 = "nameValue1";
		final UserDefinedField name1 = this.createUserDefinedField(nameType1, nameCode1);

		final String nameType2 = "nameType2";
		final String nameCode2 = "nameCode2";
		final String nameValue2 = "nameValue2";
		final UserDefinedField name2 = this.createUserDefinedField(nameType2, nameCode2);

		final Location location1 = this.createLocation("Location 1");
		final Location location2 = this.createLocation("Location 2");

		final Integer creationDate1 = 20050101;
		final Integer creationDate2 = 20200707;

		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.createNameAuditQueryParams(RevisionType.CREATION, name1.getFldno(), nameValue1, location1.getLocid(), creationDate1, true),
			// Change name type
			this.createNameAuditQueryParams(RevisionType.EDITION, name2.getFldno(), nameValue1, location1.getLocid(), creationDate1, true),
			// Change name value
			this.createNameAuditQueryParams(RevisionType.EDITION, name2.getFldno(), nameValue2, location1.getLocid(), creationDate1, true),
			// Change location
			this.createNameAuditQueryParams(RevisionType.EDITION, name2.getFldno(), nameValue2, location2.getLocid(), creationDate1, true),
			// Change creation date
			this.createNameAuditQueryParams(RevisionType.EDITION, name2.getFldno(), nameValue2, location2.getLocid(), creationDate2, true),
			// Change preferred name
			this.createNameAuditQueryParams(RevisionType.EDITION, name2.getFldno(), nameValue2, location2.getLocid(), creationDate2, false)
		);
		this.insertAuditRows(NAMES_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countNameChangesByNameId(NAME_ID), is(6L));

		final List<GermplasmNameAuditDTO> nameChanges =
			this.germplasmAuditDAO.getNameChangesByNameId(NAME_ID, new PageRequest(0, 50));
		assertThat(nameChanges, hasSize(6));
		// preferred should have changed
		this.assertNameAuditChanges(nameChanges.get(0), RevisionType.EDITION, false, true, creationDate2, false,
			location2.getLname(), false, nameValue2, false);
		// creation date should have changed
		this.assertNameAuditChanges(nameChanges.get(1), RevisionType.EDITION, true, false, creationDate2, true,
			location2.getLname(), false, nameValue2, false);
		// location should have changed
		this.assertNameAuditChanges(nameChanges.get(2), RevisionType.EDITION, true, false, creationDate1, false,
			location2.getLname(), true, nameValue2, false);
		// name value should have changed
		this.assertNameAuditChanges(nameChanges.get(3), RevisionType.EDITION, true, false, creationDate1, false,
			location1.getLname(), false, nameValue2, true);
		// name type should have changed
		this.assertNameAuditChanges(nameChanges.get(4), RevisionType.EDITION, true, false, creationDate1, false,
			location1.getLname(), false, nameValue1, false);
		this.assertNameAuditChanges(nameChanges.get(5), RevisionType.CREATION, true, false, creationDate1, false,
			location1.getLname(), false, nameValue1, false);
	}

	@Test
	public void shouldGetAndCountAttributeChangesByAttributeId() {

		final String attrType1 = "nameType1";
		final String attrName1 = "nameCode1";
		final String attrValue1 = "nameValue1";
		final CVTerm attribute1 = this.createAttribute(attrType1, attrName1);

		final String attrType2 = "nameType2";
		final String attrName2 = "nameCode2";
		final String attrValue2 = "nameValue2";
		final CVTerm attribute2 = this.createAttribute(attrType2, attrName2);

		final Location location1 = this.createLocation("Location 1");
		final Location location2 = this.createLocation("Location 2");

		final Integer creationDate1 = 20050101;
		final Integer creationDate2 = 20200707;

		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.createAttributeAuditQueryParams(RevisionType.CREATION, attribute1.getCvTermId(), attrValue1, location1.getLocid(), creationDate1),
			// Change name type
			this.createAttributeAuditQueryParams(RevisionType.EDITION, attribute2.getCvTermId(), attrValue1, location1.getLocid(), creationDate1),
			// Change name value
			this.createAttributeAuditQueryParams(RevisionType.EDITION, attribute2.getCvTermId(), attrValue2, location1.getLocid(), creationDate1),
			// Change location
			this.createAttributeAuditQueryParams(RevisionType.EDITION, attribute2.getCvTermId(), attrValue2, location2.getLocid(), creationDate1),
			// Change creation date
			this.createAttributeAuditQueryParams(RevisionType.EDITION, attribute2.getCvTermId(), attrValue2, location2.getLocid(), creationDate2)
		);
		this.insertAuditRows(ATTRIBUTES_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countAttributeChangesByAttributeId(ATTRIBUTE_ID), is(5L));

		final List<GermplasmAttributeAuditDTO> changes =
			this.germplasmAuditDAO.getAttributeChangesByAttributeId(ATTRIBUTE_ID, new PageRequest(0, 50));
		assertThat(changes, hasSize(5));
		// creation date should have changed
		this.assertAttributeAuditChanges(changes.get(0), RevisionType.EDITION, creationDate2, true,
			location2.getLname(), false, attrValue2, false);
		// location should have changed
		this.assertAttributeAuditChanges(changes.get(1), RevisionType.EDITION, creationDate1, false,
			location2.getLname(), true, attrValue2, false);
		// name value should have changed
		this.assertAttributeAuditChanges(changes.get(2), RevisionType.EDITION, creationDate1, false,
			location1.getLname(), false, attrValue2, true);
		// name type should have changed
		this.assertAttributeAuditChanges(changes.get(3), RevisionType.EDITION, creationDate1, false,
			location1.getLname(), false, attrValue1, false);
		this.assertAttributeAuditChanges(changes.get(4), RevisionType.CREATION, creationDate1, false,
			location1.getLname(), false, attrValue1, false);
	}

	@Test
	public void shouldGetAndCountGermplasmBasicDetailsChangesByGid() {
		final Location location1 = this.createLocation("Location 1");
		final Location location2 = this.createLocation("Location 2");

		final Integer creationDate1 = 20050101;
		final Integer creationDate2 = 20200707;
		final Integer method1 = new Random().nextInt();
		final Integer method2 = new Random().nextInt();

		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.creatGermplasmBasicDetailsAuditQueryParams(RevisionType.CREATION, location1.getLocid(), creationDate1, method1, 0),
			// Change breeding method
			this.creatGermplasmBasicDetailsAuditQueryParams(RevisionType.EDITION, location2.getLocid(), creationDate1, method2, 0),
			// Change location
			this.creatGermplasmBasicDetailsAuditQueryParams(RevisionType.EDITION, location2.getLocid(), creationDate1, method2, 0),
			// Change creation date
			this.creatGermplasmBasicDetailsAuditQueryParams(RevisionType.EDITION, location2.getLocid(), creationDate2, method2, 0),
			// Change group id
			this.creatGermplasmBasicDetailsAuditQueryParams(RevisionType.EDITION, location2.getLocid(), creationDate2, method2, 1)
		);
		this.insertAuditRows(GERMPLASMS_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countBasicDetailsChangesByGid(GID), is(4L));

		final List<GermplasmBasicDetailsAuditDTO> changes =
			this.germplasmAuditDAO.getBasicDetailsChangesByGid(GID, new PageRequest(0, 50));
		assertThat(changes, hasSize(4));
		// groupId should have changed
		this.assertGermplasmBasicDetailsAuditChanges(changes.get(0), RevisionType.EDITION, creationDate2, false,
			location2.getLname(), false, 1, true);
		// creation date should have changed
		this.assertGermplasmBasicDetailsAuditChanges(changes.get(1), RevisionType.EDITION, creationDate2, true,
			location2.getLname(), false, 0, false);
		// location should have changed
		this.assertGermplasmBasicDetailsAuditChanges(changes.get(2), RevisionType.EDITION, creationDate1, false,
			location2.getLname(), true, 0, false);
		this.assertGermplasmBasicDetailsAuditChanges(changes.get(3), RevisionType.CREATION, creationDate1, false,
			location1.getLname(), false, 0, false);
	}

	@Test
	public void shouldGetAndCountReferenceChangesByReferenceId() {
		final String value1 = "value1";
		final String value2 = "value2";

		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.createReferenceAuditQueryParams(RevisionType.CREATION, value1),
			// Change value
			this.createReferenceAuditQueryParams(RevisionType.EDITION, value2)
		);
		this.insertAuditRows(REFERENCE_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countReferenceChangesByReferenceId(REFERENCE_ID), is(2L));

		final List<GermplasmReferenceAuditDTO> changes =
			this.germplasmAuditDAO.getReferenceChangesByReferenceId(REFERENCE_ID, new PageRequest(0, 50));
		assertThat(changes, hasSize(2));
		// creation date should have changed
		this.assertReferenceAuditChanges(changes.get(0), RevisionType.EDITION, value2, true);
		// location should have changed
		this.assertReferenceAuditChanges(changes.get(1), RevisionType.CREATION, value1, false);
	}

	@Test
	public void shouldGetAndCountProgenitorDetailsByGid() {
		final String breedingMethodName1 = "name1";
		final String breedingMethodType1 = "DER";
		final Method method1 = this.createBreedingMethod(breedingMethodName1, breedingMethodType1, "BLE1");

		final String breedingMethodName2 = "name2";
		final String breedingMethodType2 = "GEN";
		final Method method2 = this.createBreedingMethod(breedingMethodName2, breedingMethodType2, "BLE2");

		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.createProgenitorDetailsAuditQueryParams(RevisionType.CREATION, method1.getMid(), 0, 0, -1),
			// Change breeding method type
			this.createProgenitorDetailsAuditQueryParams(RevisionType.EDITION, method2.getMid(), 0, 0, 0),
			// Change female and male
			this.createProgenitorDetailsAuditQueryParams(RevisionType.EDITION, method2.getMid(), 1, 2, 2)
		);
		this.insertAuditRows(GERMPLASMS_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countProgenitorDetailsChangesByGid(GID), is(3L));

		final List<GermplasmProgenitorDetailsAuditDTO> changes =
			this.germplasmAuditDAO.getProgenitorDetailsByGid(GID, new PageRequest(0, 50));
		assertThat(changes, hasSize(3));
		// female and male parent should have changed
		this.assertProgenitorDetailsAuditChanges(changes.get(0), RevisionType.EDITION, breedingMethodType2, breedingMethodName2, false, 1,
			true, 2, true, 2, true);
		// breeding method should have changed
		this.assertProgenitorDetailsAuditChanges(changes.get(1), RevisionType.EDITION, breedingMethodType2, breedingMethodName2, true, 0,
			false, 0, false, 0, true);
		this.assertProgenitorDetailsAuditChanges(changes.get(2), RevisionType.CREATION, breedingMethodType1, breedingMethodName1, false, 0,
			false, 0, false, 1, false);
	}

	@Test
	public void shouldGetAndCountOtherProgenitorsByGid() {
		List<Map<String, Object>> queriesParams = Arrays.asList(
			this.createOtherProgenitorsAuditQueryParams(RevisionType.CREATION, 3, 1),
			this.createOtherProgenitorsAuditQueryParams(RevisionType.CREATION, 4, 2)
		);
		this.insertAuditRows(PROGENITORS_AUDIT_TABLE, queriesParams);

		assertThat(this.germplasmAuditDAO.countOtherProgenitorsChangesByGid(GID), is(2L));

		final List<GermplasmOtherProgenitorsAuditDTO> changes =
			this.germplasmAuditDAO.getOtherProgenitorsByGid(GID, new PageRequest(0, 50));
		assertThat(changes, hasSize(2));
		this.assertProgenitorDetailsAuditChanges(changes.get(0), RevisionType.CREATION, 2, 4);
		this.assertProgenitorDetailsAuditChanges(changes.get(1), RevisionType.CREATION, 1, 3);
	}

	private Method createBreedingMethod(final String name, final String type, final String code) {
		final Method method =
			new Method(null, type, "S", code, name, "description", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(2), Integer.valueOf(19980610));
		this.methodDAO.save(method);
		return method;
	}

	private UserDefinedField createUserDefinedField(final String type, final String code) {
		final UserDefinedField
			userDefinedField = new UserDefinedField(null, RandomStringUtils.randomAlphabetic(10), type, code,
			RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(100),
			1, 1, 20180909, null);
		this.userDefinedFieldDAO.save(userDefinedField);
		return userDefinedField;
	}

	private CVTerm createAttribute(final String type, final String name) {
		CVTerm cvTerm = new CVTerm(null, CvId.VARIABLES.getId(), name, type, null, 0, 0, false);
		this.cvTermDao.save(cvTerm);

		this.cvTermPropertyDao.save(
			new CVTermProperty(TermId.VARIABLE_TYPE.getId(), VariableType.GERMPLASM_ATTRIBUTE.getName(), 0, cvTerm.getCvTermId()));

		return cvTerm;
	}

	private Location createLocation(final String name) {
		final Location location = LocationTestDataInitializer.createLocation(null, name, 123,
			RandomStringUtils.randomNumeric(8));
		this.locationDAO.saveOrUpdate(location);
		return location;
	}

	private void addCommonsQueryParams(final Map<String, Object> queryParams, final RevisionType revisionType) {
		queryParams.put("rev_type", revisionType.getValue());
		queryParams.put("created_by", this.findAdminUser());
		queryParams.put("modified_by", this.findAdminUser());
		queryParams.put("created_date", new Date());
		queryParams.put("modified_date", new Date());
	}

	private void insertAuditRows(final String auditTable, final List<Map<String, Object>> queriesParams) {
		queriesParams.forEach(queryParams -> {
			final String sql = SQLQueryUtil.generateInsertQuery(auditTable, queryParams);
			this.sessionProvder.getSession().createSQLQuery(sql).executeUpdate();
		});
	}

	// Name audit
	private Map<String, Object> createNameAuditQueryParams(final RevisionType revisionType, final Integer nameTypeId, final String value,
		final Integer locationId, final Integer creationDate, final boolean isPreferred) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("nid", NAME_ID);
		queryParams.put("gid", new Random().nextInt());
		queryParams.put("ntype", nameTypeId);
		queryParams.put("nstat", (isPreferred) ? 1 : 0);
		queryParams.put("nval", value);
		queryParams.put("nlocn", locationId);
		queryParams.put("ndate", creationDate);
		queryParams.put("nref", new Random().nextInt());

		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertNameAuditChanges(final GermplasmNameAuditDTO change,
		final RevisionType revisionType,
		final boolean preferred, final boolean preferredChanged,
		final Integer creationDate, final boolean creationDateChanged,
		final String locationName, final boolean locationChanged,
		final String value, final boolean valueChanged) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.isPreferred(), is(preferred));
		assertThat(change.isPreferredChanged(), is(preferredChanged));
		assertThat(change.getCreationDate(), is(creationDate.toString()));
		assertThat(change.isCreationDateChanged(), is(creationDateChanged));
		assertThat(change.getLocationName(), is(locationName));
		assertThat(change.isLocationChanged(), is(locationChanged));
		assertThat(change.getValue(), is(value));
		assertThat(change.isValueChanged(), is(valueChanged));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	// Attribute audit
	private Map<String, Object> createAttributeAuditQueryParams(final RevisionType revisionType, final Integer attrTypeId,
		final String value,
		final Integer locationId, final Integer creationDate) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", new Random().nextInt());
		queryParams.put("aid", ATTRIBUTE_ID);
		queryParams.put("atype", attrTypeId);
		queryParams.put("aval", value);
		queryParams.put("alocn", locationId);
		queryParams.put("aref", new Random().nextInt());
		queryParams.put("adate", creationDate);

		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertAttributeAuditChanges(final GermplasmAttributeAuditDTO change,
		final RevisionType revisionType,
		final Integer creationDate, final boolean creationDateChanged,
		final String locationName, final boolean locationChanged,
		final String value, final boolean valueChanged) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.getCreationDate(), is(creationDate.toString()));
		assertThat(change.isCreationDateChanged(), is(creationDateChanged));
		assertThat(change.getLocationName(), is(locationName));
		assertThat(change.isLocationChanged(), is(locationChanged));
		assertThat(change.getValue(), is(value));
		assertThat(change.isValueChanged(), is(valueChanged));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	// Germplasm basic details audit
	private Map<String, Object> creatGermplasmBasicDetailsAuditQueryParams(final RevisionType revisionType,
		final Integer locationId, final Integer creationDate, final Integer methodId, final Integer groupId) {

		final Map<String, Object> queryParams =
			this.createGermplasmQueryParams(locationId, creationDate, methodId, new Random().nextInt(),
				new Random().nextInt(), new Random().nextInt(), groupId);
		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertGermplasmBasicDetailsAuditChanges(final GermplasmBasicDetailsAuditDTO change,
		final RevisionType revisionType,
		final Integer creationDate, final boolean creationDateChanged,
		final String locationName, final boolean locationChanged,
		final Integer groupId, final boolean groupIdChanged) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.getCreationDate(), is(creationDate.toString()));
		assertThat(change.isCreationDateChanged(), is(creationDateChanged));
		assertThat(change.getLocationName(), is(locationName));
		assertThat(change.isLocationChanged(), is(locationChanged));
		assertThat(change.getGroupId(), is(groupId));
		assertThat(change.isGroupIdChanged(), is(groupIdChanged));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	// Attribute audit
	private Map<String, Object> createReferenceAuditQueryParams(final RevisionType revisionType, final String value) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("refid", REFERENCE_ID);
		queryParams.put("pubtype", new Random().nextInt());
		queryParams.put("pubdate", new Random().nextInt());
		queryParams.put("authors", UUID.randomUUID().toString());
		queryParams.put("editors", UUID.randomUUID().toString());
		queryParams.put("analyt", value);
		queryParams.put("monogr", UUID.randomUUID().toString());
		queryParams.put("series", UUID.randomUUID().toString());
		queryParams.put("volume", UUID.randomUUID().toString().substring(0, 10));
		queryParams.put("issue", UUID.randomUUID().toString().substring(0, 10));
		queryParams.put("pagecol", UUID.randomUUID().toString().substring(0, 25));
		queryParams.put("publish", UUID.randomUUID().toString());
		queryParams.put("pucity", UUID.randomUUID().toString().substring(0, 30));
		queryParams.put("pucntry", UUID.randomUUID().toString());
		queryParams.put("authorlist", new Random().nextInt());
		queryParams.put("editorlist", new Random().nextInt());
		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertReferenceAuditChanges(final GermplasmReferenceAuditDTO change,
		final RevisionType revisionType,
		final String value, final boolean valueChanged) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.getValue(), is(value));
		assertThat(change.isValueChanged(), is(valueChanged));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	// Progenitor details
	private Map<String, Object> createProgenitorDetailsAuditQueryParams(final RevisionType revisionType, final Integer methodId,
		final Integer femaleParentGID, final Integer maleParentGID, final Integer progenitorsNumber) {
		final Map<String, Object> queryParams =
			this.createGermplasmQueryParams(new Random().nextInt(), 20200101, methodId, femaleParentGID, maleParentGID, progenitorsNumber, 0);
		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertProgenitorDetailsAuditChanges(final GermplasmProgenitorDetailsAuditDTO change,
		final RevisionType revisionType, final String breedingMethodType,
		final String breedingMethodName, final boolean breedingMethodChanged,
		final Integer femaleParent, final boolean femaleParentChanged,
		final Integer maleParent, final boolean maleParentChanged,
		final Integer progenitorsNumber, final boolean progenitorsNumberChanged) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.getBreedingMethodType(), is(breedingMethodType));
		assertThat(change.getBreedingMethodName(), is(breedingMethodName));
		assertThat(change.isBreedingMethodChanged(), is(breedingMethodChanged));
		assertThat(change.getFemaleParent(), is(femaleParent));
		assertThat(change.isFemaleParentChanged(), is(femaleParentChanged));
		assertThat(change.getMaleParent(), is(maleParent));
		assertThat(change.isMaleParentChanged(), is(maleParentChanged));
		assertThat(change.getProgenitorsNumber(), is(progenitorsNumber));
		assertThat(change.isProgenitorsNumberChanged(), is(progenitorsNumberChanged));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	// Other progenitors
	private Map<String, Object> createOtherProgenitorsAuditQueryParams(final RevisionType revisionType, final Integer progenitorNumber,
		final Integer progenitorGid) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", GID);
		queryParams.put("pno", progenitorNumber);
		queryParams.put("pid", progenitorGid);
		queryParams.put("id", new Random().nextInt());
		this.addCommonsQueryParams(queryParams, revisionType);

		return queryParams;
	}

	private void assertProgenitorDetailsAuditChanges(final GermplasmOtherProgenitorsAuditDTO change,
		final RevisionType revisionType, final Integer progenitorGid, final Integer progenitorsNumber) {
		assertThat(change.getRevisionType(), is(revisionType));
		assertThat(change.getProgenitorGid(), is(progenitorGid));
		assertThat(change.getProgenitorsNumber(), is(progenitorsNumber));
		assertNotNull(change.getCreatedDate());
		assertNotNull(change.getModifiedDate());
		assertThat(change.getCreatedBy(), is(ADMIN_NAME));
		assertThat(change.getModifiedBy(), is(ADMIN_NAME));
	}

	private Map<String, Object> createGermplasmQueryParams(final Integer locationId, final Integer creationDate, final Integer methodId,
		final Integer femaleParentGID, final Integer maleParentGID, final Integer progenitorsNumber, final Integer groupId) {
		final Map<String, Object> queryParams = new LinkedHashMap<>();
		queryParams.put("gid", GID);
		queryParams.put("methn", methodId);
		queryParams.put("gnpgs", progenitorsNumber);
		queryParams.put("gpid1", femaleParentGID);
		queryParams.put("gpid2", maleParentGID);
		queryParams.put("glocn", locationId);
		queryParams.put("gdate", creationDate);
		queryParams.put("gref", new Random().nextInt());
		queryParams.put("grplce", new Random().nextInt());
		queryParams.put("mgid", groupId);
		queryParams.put("cid", new Random().nextInt());
		queryParams.put("sid", new Random().nextInt());
		queryParams.put("gchange", new Random().nextInt());
		queryParams.put("deleted", true);
		queryParams.put("germplsm_uuid", UUID.randomUUID().toString());
		return queryParams;
	}

}
