/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.germplasm.GermplasmGuidGenerator;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergedDto;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequest;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

public class GermplasmDAOTest extends IntegrationTestBase {

	private static final String DUMMY_STOCK_ID = "USER-1-1";
	private static final Integer TEST_PROJECT_ID = 1;
	private static final String NOTE_ATTRIBUTE = "NOTE_AA_text";

	private static final Integer GROUP_ID = 10;

	private boolean testDataSetup = false;
	private CropType cropType;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmDataManager germplasmDataDM;

	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private IntegrationTestDataInitializer testDataInitializer;

	private static final int UNKNOWN_GENERATIVE_METHOD_ID = 1;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.daoFactory);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		if (!this.testDataSetup) {
			this.updateInventory();
			this.testDataSetup = true;
		}

		this.cropType = new CropType();
		this.cropType.setUseUUID(false);
		this.initializeGermplasms();
	}

	private void updateInventory() {
		final List<Transaction> transactions = this.daoFactory.getTransactionDAO().getAll(0, 1);
		if (transactions != null && !transactions.isEmpty()) {
			final Transaction transaction = transactions.get(0);
			transaction.getLot().setStockId(GermplasmDAOTest.DUMMY_STOCK_ID);
			this.daoFactory.getTransactionDAO().saveOrUpdate(transaction);
		}
	}

	@Test
	public void testGetDerivativeChildren() {
		final Germplasm parentGermplsm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 1, -1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(parentGermplsm, parentGermplsm.getPreferredName(), this.cropType);

		final Germplasm childDerivativeGermplsm = GermplasmTestDataInitializer
			.createGermplasm(20150101, 1, parentGermplsm.getGid(), -1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(childDerivativeGermplsm, childDerivativeGermplsm.getPreferredName(), this.cropType);

		final List<Germplasm> results = this.daoFactory.getGermplasmDao().getDescendants(parentGermplsm.getGid(), 'D');
		Assert.assertNotNull(results);
		Assert.assertEquals(childDerivativeGermplsm.getGid(), results.get(0).getGid());
	}

	@Test
	public void testCountGermplasmDerivativeProgeny() {
		final Germplasm parentGermplsm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 1, -1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(parentGermplsm, parentGermplsm.getPreferredName(), this.cropType);

		final Germplasm childDerivativeGermplsm = GermplasmTestDataInitializer
			.createGermplasm(20150101, 1, parentGermplsm.getGid(), -1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(childDerivativeGermplsm, childDerivativeGermplsm.getPreferredName(), this.cropType);

		final long germplasmDerivativeProgenyCount =
			this.daoFactory.getGermplasmDao().countGermplasmDerivativeProgeny(parentGermplsm.getGid());
		Assert.assertEquals((long) 1, germplasmDerivativeProgenyCount);
	}

	@Test
	public void testGetMaintenanceChildren() {
		final Germplasm parentGermplsm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 1, -1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(parentGermplsm, parentGermplsm.getPreferredName(), this.cropType);

		final List<org.generationcp.middleware.pojos.Method> maintenanceMethods = this.daoFactory.getMethodDAO().getByType("MAN");

		final Germplasm maintenanceChildrenGermplsm = GermplasmTestDataInitializer
			.createGermplasm(20150101, 1, parentGermplsm.getGid(), -1, 0, 0, 1, maintenanceMethods.get(0).getMid(), 0, 1, 1,
				"MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(maintenanceChildrenGermplsm, maintenanceChildrenGermplsm.getPreferredName(),
			this.cropType);

		final List<Germplasm> results = this.daoFactory.getGermplasmDao().getDescendants(parentGermplsm.getGid(), 'M');
		Assert.assertNotNull(results);
		Assert.assertNotNull(results);
		Assert.assertEquals(maintenanceChildrenGermplsm.getGid(), results.get(0).getGid());

	}

	@Test
	public void testRetrieveStudyParentGIDsKnownValuesOnly() {

		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 12, 13, 1, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
		final StockModel stock = new StockModel();
		stock.setGermplasm(germplasm);
		stock.setUniqueName("1");
		stock.setIsObsolete(false);
		stock.setProject(new DmsProject(TEST_PROJECT_ID));
		stock.setTypeId(TermId.ENTRY_CODE.getId());
		this.daoFactory.getStockDao().save(stock);

		final List<Germplasm> germplasmEntries =
			this.daoFactory.getGermplasmDao().getGermplasmParentsForStudy(GermplasmDAOTest.TEST_PROJECT_ID);

		Assert.assertEquals(1, germplasmEntries.size());
		Assert.assertEquals(germplasm.getGid(), germplasmEntries.get(0).getGid());
		Assert.assertEquals(germplasm.getGpid1(), germplasmEntries.get(0).getGpid1());
		Assert.assertEquals(germplasm.getGpid2(), germplasmEntries.get(0).getGpid2());
		Assert.assertEquals(germplasm.getGrplce(), germplasmEntries.get(0).getGrplce());
	}

	@Test
	public void testGetAllChildren() {
		final Germplasm parentGermplsm =
			GermplasmTestDataInitializer
				.createGermplasm(20150101, 1, 1, -1, 0, 0, 1, GermplasmDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1, 1, "MethodName",
					"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(parentGermplsm, parentGermplsm.getPreferredName(), this.cropType);

		final Germplasm childDerivativeGermplsm = GermplasmTestDataInitializer
			.createGermplasm(20150101, 1, parentGermplsm.getGid(), -1, 0, 0, 1, GermplasmDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1, 1,
				"MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(childDerivativeGermplsm, childDerivativeGermplsm.getPreferredName(), this.cropType);

		final Germplasm maintenanceChildrenGermplsm = GermplasmTestDataInitializer
			.createGermplasm(20150101, 1, parentGermplsm.getGid(), -1, 0, 0, 1, GermplasmDAOTest.UNKNOWN_GENERATIVE_METHOD_ID, 0, 1, 1,
				"MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(maintenanceChildrenGermplsm, maintenanceChildrenGermplsm.getPreferredName(),
			this.cropType);

		final List<Germplasm> children = this.daoFactory.getGermplasmDao().getAllChildren(parentGermplsm.getGid());
		Assert.assertNotNull("getAllChildren() should never return null.", children);

		final List<Integer> resultChildGermplasmIds = Lists.newArrayList();

		for (final Germplasm germplasm : children) {
			resultChildGermplasmIds.add(germplasm.getGid());
		}

		Assert.assertTrue("Derivative child Germplasm should be included in search result",
			resultChildGermplasmIds.contains(childDerivativeGermplsm.getGid()));
		Assert.assertTrue("Maintenance child Germplasm should be included in search result",
			resultChildGermplasmIds.contains(maintenanceChildrenGermplsm.getGid()));
	}

	@Test
	public void testGetPreviousCrosses() {
		final Germplasm female =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(female, female.getPreferredName(), this.cropType);

		final Germplasm male =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(male, male.getPreferredName(), this.cropType);

		final Germplasm currentCross =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		currentCross.setGpid1(female.getGid());
		currentCross.setGpid2(male.getGid());
		this.germplasmTestDataGenerator.addGermplasm(currentCross, currentCross.getPreferredName(), this.cropType);

		final Germplasm previousCross =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		previousCross.setGpid1(female.getGid());
		previousCross.setGpid2(male.getGid());
		this.germplasmTestDataGenerator.addGermplasm(previousCross, previousCross.getPreferredName(), this.cropType);

		final List<Germplasm> previousCrosses = this.daoFactory.getGermplasmDao().getPreviousCrosses(currentCross, female, male);
		Assert.assertNotNull("getPreviousCrosses() should never return null.", previousCrosses);

		Assert.assertEquals("There should be only one previous cross", 1, previousCrosses.size());
		Assert.assertEquals(previousCross.getGid(), previousCrosses.get(0).getGid());
	}

	@Test
	public void testLoadEntityWithNameCollection() {
		final Germplasm germplasm = this.daoFactory.getGermplasmDao().getById(1);
		if (germplasm != null) {
			Assert.assertFalse("If germplasm exists, the name collection can not be empty.", germplasm.getNames().isEmpty());
		}
	}

	@Test
	public void testGetManagementGroupMembers() {
		List<Germplasm> groupMembers = this.daoFactory.getGermplasmDao().getManagementGroupMembers(1);
		Assert.assertNotNull("getManagementGroupMembers() should never return null when supplied with proper mgid.", groupMembers);

		groupMembers = this.daoFactory.getGermplasmDao().getManagementGroupMembers(null);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = null.", groupMembers.isEmpty());

		groupMembers = this.daoFactory.getGermplasmDao().getManagementGroupMembers(0);
		Assert.assertTrue("getManagementGroupMembers() should return empty collection when supplied mgid = 0.", groupMembers.isEmpty());
	}

	@Test
	public void testGetPedigree() throws ParseException {

		final Method generativeMethod = this.daoFactory.getMethodDAO().getByCode(Collections.singletonList("C2W")).get(0);
		final Method derivativeMethod = this.daoFactory.getMethodDAO().getByCode(Collections.singletonList("UDM")).get(0);
		final Method maintenanceMethod = this.daoFactory.getMethodDAO().getByCode(Collections.singletonList("SMP")).get(0);

		final Germplasm femaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm maleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(femaleParent);
		this.daoFactory.getGermplasmDao().save(maleParent);

		final Germplasm cross = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		cross.setGpid1(femaleParent.getGid());
		cross.setGpid2(maleParent.getGid());
		cross.setGnpgs(2);
		cross.setMethodId(generativeMethod.getMid());
		cross.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(cross);

		final Germplasm advance = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		advance.setGpid1(cross.getGid());
		advance.setGpid2(cross.getGid());
		advance.setGnpgs(-1);
		advance.setMethodId(derivativeMethod.getMid());
		advance.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(advance);

		final Germplasm advance2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		advance2.setGpid1(cross.getGid());
		advance2.setGpid2(cross.getGid());
		advance2.setGnpgs(-1);
		advance2.setMethodId(maintenanceMethod.getMid());
		advance2.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(advance2);

		final PedigreeDTO generativePedigree = this.daoFactory.getGermplasmDao().getPedigree(cross.getGid(), null, false);
		final PedigreeDTO derivativePedigree = this.daoFactory.getGermplasmDao().getPedigree(advance.getGid(), null, true);
		final PedigreeDTO maintenancePedigree = this.daoFactory.getGermplasmDao().getPedigree(advance2.getGid(), null, true);

		Assert.assertThat(generativePedigree.getGermplasmDbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(generativePedigree.getParent1DbId(), is(femaleParent.getGermplasmUUID()));
		Assert.assertThat(generativePedigree.getParent1Type(), is(ParentType.FEMALE.name()));
		Assert.assertThat(generativePedigree.getParent2DbId(), is(maleParent.getGermplasmUUID()));
		Assert.assertThat(generativePedigree.getParent2Type(), is(ParentType.MALE.name()));
		Assert.assertThat(generativePedigree.getCrossingPlan(),
			is(generativeMethod.getMcode() + "|" + generativeMethod.getMname() + "|" + generativeMethod.getMtype()));
		final Date gdate = Util.parseDate(String.valueOf(cross.getGdate()), Util.DATE_AS_NUMBER_FORMAT);
		final Integer year = Integer.valueOf(Util.getSimpleDateFormat("yyyy").format(gdate));
		Assert.assertThat(generativePedigree.getCrossingYear(), is(year));
		Assert.assertThat(generativePedigree.getSiblings(), nullValue());

		Assert.assertThat(derivativePedigree.getGermplasmDbId(), is(advance.getGermplasmUUID()));
		Assert.assertThat(derivativePedigree.getParent1DbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(derivativePedigree.getParent1Type(), is(ParentType.POPULATION.name()));
		Assert.assertThat(derivativePedigree.getParent2DbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(derivativePedigree.getParent2Type(), is(ParentType.SELF.name()));
		Assert.assertThat(derivativePedigree.getCrossingPlan(),
			is(derivativeMethod.getMcode() + "|" + derivativeMethod.getMname() + "|" + derivativeMethod.getMtype()));
		Assert.assertThat(derivativePedigree.getSiblings(), hasSize(1));
		Assert.assertThat(derivativePedigree.getSiblings().get(0).getGermplasmDbId(), is(advance2.getGermplasmUUID()));

		Assert.assertThat(maintenancePedigree.getGermplasmDbId(), is(advance2.getGermplasmUUID()));
		Assert.assertThat(maintenancePedigree.getParent1DbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(maintenancePedigree.getParent1Type(), is(ParentType.POPULATION.name()));
		Assert.assertThat(maintenancePedigree.getParent2DbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(maintenancePedigree.getParent2Type(), is(ParentType.SELF.name()));
		Assert.assertThat(maintenancePedigree.getCrossingPlan(),
			is(maintenanceMethod.getMcode() + "|" + maintenanceMethod.getMname() + "|" + maintenanceMethod.getMtype()));
		Assert.assertThat(maintenancePedigree.getSiblings(), hasSize(1));
		Assert.assertThat(maintenancePedigree.getSiblings().get(0).getGermplasmDbId(), is(advance.getGermplasmUUID()));
	}

	@Test
	public void testGetProgeny() {
		final Germplasm femaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm maleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(femaleParent);
		this.daoFactory.getGermplasmDao().save(maleParent);

		final Name maleParentPreferredName = maleParent.getPreferredName();
		maleParentPreferredName.setGermplasm(maleParent);
		this.daoFactory.getNameDao().save(maleParentPreferredName);

		final Germplasm cross = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		cross.setGpid1(femaleParent.getGid());
		cross.setGpid2(maleParent.getGid());
		cross.setGnpgs(2);
		cross.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(cross);

		final Name crossPreferredName = cross.getPreferredName();
		crossPreferredName.setGermplasm(cross);
		this.daoFactory.getNameDao().save(crossPreferredName);

		final Germplasm advance = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		advance.setGpid1(cross.getGid());
		advance.setGpid2(cross.getGid());
		advance.setGnpgs(-1);
		advance.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));

		this.daoFactory.getGermplasmDao().save(advance);

		final ProgenyDTO progeny = this.daoFactory.getGermplasmDao().getProgeny(maleParent.getGid());

		Assert.assertThat(progeny.getGermplasmDbId(), is(maleParent.getGermplasmUUID()));
		Assert.assertThat(progeny.getDefaultDisplayName(), is(maleParentPreferredName.getNval()));
		Assert.assertThat(progeny.getProgeny(), hasSize(1));
		Assert.assertThat(progeny.getProgeny().get(0).getParentType(), is(ParentType.MALE.name()));
		Assert.assertThat(progeny.getProgeny().get(0).getDefaultDisplayName(), is(crossPreferredName.getNval()));

		final ProgenyDTO crossProgeny = this.daoFactory.getGermplasmDao().getProgeny(cross.getGid());

		Assert.assertThat(crossProgeny.getGermplasmDbId(), is(cross.getGermplasmUUID()));
		Assert.assertThat(crossProgeny.getProgeny(), hasSize(1));
		Assert.assertThat(crossProgeny.getProgeny().get(0).getParentType(), is(ParentType.SELF.name()));
		Assert.assertThat(crossProgeny.getProgeny().get(0).getGermplasmDbId(), is(advance.getGermplasmUUID()));
	}

	@Test
	public void testSaveGermplasmNamesThroughHibernateCascade() {

		final Germplasm germplasm = new Germplasm();
		germplasm.setMethodId(1);
		germplasm.setGnpgs(-1);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setLgid(0);
		germplasm.setLocationId(1);
		germplasm.setGdate(20160101);
		germplasm.setReferenceId(0);
		germplasm.setGrplce(0);
		germplasm.setMgid(0);

		this.daoFactory.getGermplasmDao().save(germplasm);
		Assert.assertNotNull(germplasm.getGid());

		final Name name1 = new Name();
		name1.setTypeId(5);
		name1.setNstat(1);
		name1.setNval("Name1");
		name1.setLocationId(1);
		name1.setNdate(20160101);
		name1.setReferenceId(0);

		final Name name2 = new Name();
		name2.setTypeId(5);
		name2.setNstat(1);
		name2.setNval("Name2");
		name2.setLocationId(1);
		name2.setNdate(20160101);
		name2.setReferenceId(0);

		germplasm.getNames().add(name1);
		germplasm.getNames().add(name2);

		// Name collection mapping is uni-directional OneToMany right now, so the other side of the relationship has to be managed manually.
		for (final Name name : germplasm.getNames()) {
			name.setGermplasm(germplasm);
		}

		// In real app flush will happen automatically on tx commit. We don't commit tx in tests, so flush manually.
		this.sessionProvder.getSession().flush();

		for (final Name name : germplasm.getNames()) {
			// No explicit save of name entity anywhere but should still be saved through cascade on flush.
			Assert.assertNotNull(name.getNid());
			Assert.assertEquals(germplasm.getGid(), name.getGermplasm().getGid());
		}
	}

	@Test
	public void testGetGermplasmDescendantByGIDs() {
		final Germplasm fParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(fParent, fParent.getPreferredName(), this.cropType);

		final Germplasm mParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(mParent, mParent.getPreferredName(), this.cropType);

		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasm(20150101, fParent.getGid(), mParent.getGid(), 2, 0, 0, 1, 1, GermplasmDAOTest.GROUP_ID, 1, 1, "MethodName",
				"LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		Assert.assertTrue(
			this.daoFactory.getGermplasmDao().getGermplasmOffspringByGIDs(Collections.singletonList(mParent.getGid())).size() > 0);
		Assert.assertTrue(
			this.daoFactory.getGermplasmDao().getGermplasmOffspringByGIDs(Collections.singletonList(fParent.getGid())).size() > 0);
		Assert.assertFalse(
			this.daoFactory.getGermplasmDao().getGermplasmOffspringByGIDs(Collections.singletonList(germplasm.getGid())).size() > 0);
	}

	@Test
	public void testGetNextSequenceNumberString() {

		final String crossNamePrefix = "ABCDEFG";
		final String existingGermplasmNameWithPrefix = crossNamePrefix + "1";

		this.insertGermplasmWithName(existingGermplasmNameWithPrefix);

		final String result = this.germplasmDataDM.getNextSequenceNumberAsString(crossNamePrefix);
		Assert.assertEquals(
			"Germplasm with prefix " + existingGermplasmNameWithPrefix + " is existing so the next sequence number should be 2", "2",
			result);
	}

	@Test
	public void testGetNextSequenceNumber() {
		final Session mockSession = Mockito.mock(Session.class);
		this.daoFactory.getGermplasmDao().setSession(mockSession);
		this.daoFactory.getGermplasmDao().getNextSequenceNumber("");
		// Verify that no query was made if the prefix is empty
		Mockito.verify(mockSession, Mockito.never()).createSQLQuery(ArgumentMatchers.anyString());
	}

	@Test
	public void testGetNextSequenceNumberStringForMixedCasePrefix() {

		final String crossNamePrefix = "aBcDeFg";
		final int lastCodeForMixedCasePrefix = 29;
		final String nameWithMixedCasePrefix = crossNamePrefix + lastCodeForMixedCasePrefix;
		final int lastCodeForUppercasePrefix = 19;
		final String nameWithUppercasePrefix = crossNamePrefix.toUpperCase() + lastCodeForUppercasePrefix;

		this.insertGermplasmWithName(nameWithMixedCasePrefix);
		this.insertGermplasmWithName(nameWithUppercasePrefix);

		final String result = this.germplasmDataDM.getNextSequenceNumberAsString(crossNamePrefix);
		final int nextCodeForPrefix = lastCodeForMixedCasePrefix + 1;
		Assert.assertEquals("Germplasm with prefix " + nameWithMixedCasePrefix + " is existing so the next sequence number should be "
			+ nextCodeForPrefix, Integer.toString(nextCodeForPrefix), result);
	}

	@Test
	public void testGetNextSequenceNumberStringForLowerCasePrefix() {

		final String crossNamePrefix = "aBcDeFgHij";
		final int lastCodeForLowercasePrefix = 49;
		final String nameWithLowercasePrefix = crossNamePrefix.toLowerCase() + lastCodeForLowercasePrefix;
		final int lastCodeForUppercasePrefix = 39;
		final String nameWithUppercasePrefix = crossNamePrefix.toUpperCase() + lastCodeForUppercasePrefix;

		this.insertGermplasmWithName(nameWithLowercasePrefix);
		this.insertGermplasmWithName(nameWithUppercasePrefix);

		final String result = this.germplasmDataDM.getNextSequenceNumberAsString(crossNamePrefix);
		final int nextCodeForPrefix = lastCodeForLowercasePrefix + 1;
		Assert.assertEquals("Germplasm with prefix " + nameWithLowercasePrefix + " is existing so the next sequence number should be "
			+ nextCodeForPrefix, Integer.toString(nextCodeForPrefix), result);
	}

	@Test
	public void testGetNextSequenceNumberStringGermplasmIsDeleted() {

		final String crossNamePrefix = "ABCDEFG";
		final String existingGermplasmNameWithPrefix = crossNamePrefix + "1";

		// Flag the germplasm as deleted
		this.insertGermplasmWithName(existingGermplasmNameWithPrefix, true);

		final String result = this.germplasmDataDM.getNextSequenceNumberAsString(crossNamePrefix);
		Assert.assertEquals(
			"Germplasm with name" + existingGermplasmNameWithPrefix + " is deleted so the next sequence number should still be 1", "1",
			result);

	}

	@Test
	public void testGermplasmWithoutGroup() {

		// Create 2 germplasm without group
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");

		// Create 1 germplasm with group
		final Germplasm germplasm3 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 999, 1, 1, "MethodName", "LocationName");

		// Save them
		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final List<Germplasm> listOfGermplasm =
			this.daoFactory.getGermplasmDao()
				.getGermplasmWithoutGroup(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		Assert.assertEquals("Only 2 germplasm from the gid list which are without group", 2, listOfGermplasm.size());

	}

	@Test
	public void testResetGermplasmGroup() {

		// Create 2 germplasm with group
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 99, 1, 1, "MethodName", "LocationName");
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 100, 1, 1, "MethodName", "LocationName");

		// Save them
		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);

		// Reset the germplasm group
		this.daoFactory.getGermplasmDao().resetGermplasmGroup(Arrays.asList(germplasm1.getGid(), germplasm2.getGid()));

		this.daoFactory.getGermplasmDao().getSession().refresh(germplasm1);
		this.daoFactory.getGermplasmDao().getSession().refresh(germplasm2);

		Assert.assertEquals(0, germplasm1.getMgid().intValue());
		Assert.assertEquals(0, germplasm2.getMgid().intValue());

		Assert.assertThat(germplasm1.getModifiedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(germplasm1.getModifiedDate());

		Assert.assertThat(germplasm2.getModifiedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(germplasm2.getModifiedDate());
	}

	@Test
	public void testGetGermplasmDTOList() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		this.createExperiment(germplasm);

		final Map<String, String> fields = new HashMap<>();

		// atributs
		fields.put("PLOTCODE_AP_text", "");
		fields.put("SPNAM_AP_text", "");
		fields.put("SPAUTH_AP_text", "");
		fields.put("SUBTAX_AP_text", "");
		fields.put("STAUTH_AP_text", "");
		fields.put("INSTCODE_AP_text", "");
		fields.put("ORIGININST_AP_text", "");
		fields.put("CROPNM_AP_text", "");

		for (final Map.Entry<String, String> attributEntry : fields.entrySet()) {
			final Attribute attribute = this.saveAttribute(germplasm, attributEntry.getKey());
			fields.put(attributEntry.getKey(), attribute.getAval());
		}

		// names
		final Map<String, String> names = new HashMap<>();
		names.put("GENUS", "");

		for (final Map.Entry<String, String> nameEntry : names.entrySet()) {
			final Name name = this.saveGermplasmName(germplasm.getGid(), nameEntry.getKey());
			names.put(nameEntry.getKey(), name.getNval());
		}

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setGermplasmDbIds(Lists.newArrayList(germplasm.getGermplasmUUID()));
		final List<GermplasmDTO> result = this.daoFactory.getGermplasmDao().getGermplasmDTOList(request, null);

		final String displayName = germplasm.getPreferredName().getNval();
		final GermplasmDTO germplasmDTO = result.get(0);

		Assert.assertThat(germplasmDTO.getGermplasmDbId(), is(germplasm.getGermplasmUUID()));
		Assert.assertThat(germplasmDTO.getGermplasmPUI(), nullValue());
		Assert.assertThat(germplasmDTO.getBreedingMethodDbId(), is(germplasm.getMethodId().toString()));
		Assert.assertThat(germplasmDTO.getDefaultDisplayName(), is(displayName));
		// Preferred Name is ACCNO
		Assert.assertThat(germplasmDTO.getAccessionNumber(), is(displayName));
		Assert.assertThat(germplasmDTO.getGermplasmName(), is(displayName));
		Assert.assertThat(germplasmDTO.getPedigree(), nullValue());
		Assert.assertThat(germplasmDTO.getGermplasmSeedSource(), is(fields.get("PLOTCODE_AP_text")));
		Assert.assertThat(germplasmDTO.getInstituteCode(), is(fields.get("INSTCODE_AP_text")));
		Assert.assertThat(germplasmDTO.getInstituteName(), is(fields.get("ORIGININST_AP_text")));
		Assert.assertThat(germplasmDTO.getCommonCropName(), is(fields.get("CROPNM_AP_text")));
		Assert.assertThat(germplasmDTO.getGermplasmOrigin(),
			is("{\"geoCoordinates\":{\"geometry\":{\"type\":\"Polygon\",\"coordinates\":},\"type\":\"Feature\"}}"));
		Assert.assertThat(germplasmDTO.getBiologicalStatusOfAccessionCode(), nullValue());
		Assert.assertThat(germplasmDTO.getGenus(), is(names.get("GENUS")));
		Assert.assertThat(germplasmDTO.getSpecies(), is(fields.get("SPNAM_AP_text")));
		Assert.assertThat(germplasmDTO.getSpeciesAuthority(), is(fields.get("SPAUTH_AP_text")));
		Assert.assertThat(germplasmDTO.getSubtaxa(), is(fields.get("SUBTAX_AP_text")));
		Assert.assertThat(germplasmDTO.getSubtaxaAuthority(), is(fields.get("STAUTH_AP_text")));
		Assert.assertThat(new SimpleDateFormat("yyyy-MM-dd").format(germplasmDTO.getAcquisitionDate()), is("2015-01-01"));
	}

	private void createExperiment(final Germplasm germplasm) {

		final DmsProject study = new DmsProject();
		study.setName(RandomStringUtils.randomAlphabetic(20));
		study.setDescription(RandomStringUtils.randomAlphabetic(20));
		study.setProgramUUID(UUID.randomUUID().toString());
		study.setObjective(RandomStringUtils.randomAlphabetic(20));
		study.setStartDate("20190101");
		study.setEndDate("20190630");
		final StudyType studyType = new StudyType();
		studyType.setStudyTypeId(6);
		study.setStudyType(studyType);
		this.daoFactory.getDmsProjectDAO().save(study);
		this.daoFactory.getDmsProjectDAO().refresh(study);

		final DmsProject plot =
			this.createDataset(study.getName() + " - Plot Dataset", study.getProgramUUID(), DatasetTypeEnum.PLOT_DATA.getId(), study,
				study);

		final WorkbenchUser user = this.testDataInitializer.createUserForTesting();

		final ExperimentModel experimentModel = new ExperimentModel();
		final Geolocation geolocation = new Geolocation();
		this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);

		experimentModel.setGeoLocation(geolocation);
		experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
		experimentModel.setProject(plot);
		experimentModel.setObservationUnitNo(1);
		experimentModel.setJsonProps("{\"geoCoordinates\":{\"geometry\":{\"type\":\"Polygon\",\"coordinates\":},\"type\":\"Feature\"}}");
		this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);

		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setTypeId(TermId.PLOT_NO.getId());
		experimentProperty.setValue("1");
		experimentProperty.setRank(1);
		this.daoFactory.getExperimentPropertyDao().saveOrUpdate(experimentProperty);

		final StockModel stockModel = new StockModel();
		stockModel.setUniqueName("1");
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());
		stockModel.setName("Germplasm 1");
		stockModel.setIsObsolete(false);
		stockModel.setGermplasm(germplasm);
		stockModel.setProject(study);
		this.daoFactory.getStockDao().saveOrUpdate(stockModel);
		experimentModel.setStock(stockModel);
		this.daoFactory.getExperimentDao().saveOrUpdate(experimentModel);

		final GermplasmStudySource germplasmStudySource = new GermplasmStudySource();
		germplasmStudySource.setGermplasm(germplasm);
		germplasmStudySource.setExperimentModel(experimentModel);
		germplasmStudySource.setStudy(study);
		germplasmStudySource.setGermplasmStudySourceType(GermplasmStudySourceType.ADVANCE);
		this.daoFactory.getGermplasmStudySourceDAO().save(germplasmStudySource);
	}

	private DmsProject createDataset(final String name, final String programUUID, final int datasetType, final DmsProject parent,
		final DmsProject study) {
		final DmsProject dataset = new DmsProject();
		dataset.setName(name);
		dataset.setDescription(name);
		dataset.setProgramUUID(programUUID);
		dataset.setDatasetType(new DatasetType(datasetType));
		dataset.setParent(parent);
		dataset.setStudy(study);
		this.daoFactory.getDmsProjectDAO().save(dataset);
		return dataset;
	}

	@Test
	public void testCountGermplasmDTOs_FilterByPreferredName() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final String displayName = RandomStringUtils.randomAlphanumeric(255);
		germplasm.getPreferredName().setNval(displayName);
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setPreferredName(displayName);
		final long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count, is(1L));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByGenus() {
		final List<String> names = this.saveGermplasmWithNames(GermplasmImportRequest.GENUS_NAME_TYPE);
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setGenus(names);
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(names.size()));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByAccessionNumbers() {
		final List<String> names = this.saveGermplasmWithNames(GermplasmImportRequest.ACCNO_NAME_TYPE);
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setAccessionNumbers(names);
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(names.size()));
	}

	@Test
	public void testCountGermplasmDTOs_FilterBySpecies() {
		final List<String> attributes = this.saveGermplasmWithAttributes(GermplasmImportRequest.SPECIES_ATTR);
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setSpecies(attributes);
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(attributes.size()));
	}

	@Test
	public void testCountGermplasmDTOs_FilterBySynonyms() {
		final List<String> allNames = new ArrayList<>();
		allNames.addAll(this.saveGermplasmWithNames(GermplasmImportRequest.GENUS_NAME_TYPE));
		allNames.addAll(this.saveGermplasmWithNames(GermplasmImportRequest.ACCNO_NAME_TYPE));
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setSynonyms(allNames);
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(allNames.size()));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByGermplasmNames() {
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final String displayName1 = RandomStringUtils.randomAlphanumeric(255);
		germplasm1.getPreferredName().setNval(displayName1);
		this.germplasmTestDataGenerator.addGermplasm(germplasm1, germplasm1.getPreferredName(), this.cropType);

		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		final String displayName2 = RandomStringUtils.randomAlphanumeric(255);
		germplasm2.getPreferredName().setNval(displayName2);
		this.germplasmTestDataGenerator.addGermplasm(germplasm2, germplasm2.getPreferredName(), this.cropType);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setGermplasmNames(Lists.newArrayList(displayName1, displayName2));
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(2));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByCommonCropNames() {
		final List<String> attributes = this.saveGermplasmWithAttributes(GermplasmImportRequest.CROPNM_ATTR);
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setCommonCropNames(attributes);
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(attributes.size()));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByParentDbId() {
		final Germplasm femaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		GermplasmGuidGenerator.generateGermplasmGuids(this.cropType, Collections.singletonList(femaleParent));
		final Germplasm maleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		GermplasmGuidGenerator.generateGermplasmGuids(this.cropType, Collections.singletonList(maleParent));
		this.daoFactory.getGermplasmDao().save(femaleParent);
		this.daoFactory.getGermplasmDao().save(maleParent);

		final Germplasm cross = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		cross.setGpid1(femaleParent.getGid());
		cross.setGpid2(maleParent.getGid());
		cross.setGnpgs(2);
		cross.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(cross);

		// Set female parent
		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setParentDbIds(Lists.newArrayList(femaleParent.getGermplasmUUID()));
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(1));

		// Set male parent
		final GermplasmSearchRequest request2 = new GermplasmSearchRequest();
		request2.setParentDbIds(Lists.newArrayList(maleParent.getGermplasmUUID()));
		final Long count2 = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request2);
		Assert.assertThat(count2.intValue(), is(1));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByProgeny() {
		final Germplasm femaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm maleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(femaleParent);
		this.daoFactory.getGermplasmDao().save(maleParent);

		final Germplasm cross = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		GermplasmGuidGenerator.generateGermplasmGuids(this.cropType, Collections.singletonList(cross));
		cross.setGpid1(femaleParent.getGid());
		cross.setGpid2(maleParent.getGid());
		cross.setGnpgs(2);
		cross.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(cross);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setProgenyDbIds(Lists.newArrayList(cross.getGermplasmUUID()));
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(2));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByExternalReferenceId() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(germplasm);

		final GermplasmExternalReference germplasmExternalReference = new GermplasmExternalReference();
		germplasmExternalReference.setGermplasm(germplasm);
		germplasmExternalReference.setSource(RandomStringUtils.randomAlphabetic(200));
		germplasmExternalReference.setReferenceId(RandomStringUtils.randomAlphabetic(500));
		this.daoFactory.getGermplasmExternalReferenceDAO().save(germplasmExternalReference);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setExternalReferenceIDs(Lists.newArrayList(germplasmExternalReference.getReferenceId()));
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(1));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByExternalReferenceSource() {
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(germplasm);

		final GermplasmExternalReference germplasmExternalReference = new GermplasmExternalReference();
		germplasmExternalReference.setGermplasm(germplasm);
		germplasmExternalReference.setSource(RandomStringUtils.randomAlphabetic(200));
		germplasmExternalReference.setReferenceId(RandomStringUtils.randomAlphabetic(500));
		this.daoFactory.getGermplasmExternalReferenceDAO().save(germplasmExternalReference);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setExternalReferenceSources(Lists.newArrayList(germplasmExternalReference.getSource()));
		final Long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count.intValue(), is(1));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByTrialDbId() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final DmsProject study = new DmsProject(TEST_PROJECT_ID);
		study.setName(RandomStringUtils.randomAlphabetic(10));
		study.setDescription(RandomStringUtils.randomAlphabetic(10));
		this.daoFactory.getDmsProjectDAO().save(study);

		final StockModel stock = new StockModel();
		stock.setGermplasm(germplasm);
		stock.setUniqueName("1");
		stock.setIsObsolete(false);
		stock.setProject(study);
		stock.setTypeId(TermId.ENTRY_CODE.getId());
		this.daoFactory.getStockDao().save(stock);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setTrialDbIds(Arrays.asList(String.valueOf(study.getProjectId())));
		final long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count, is(1L));
	}

	@Test
	public void testCountGermplasmDTOs_FilterByTrialNames() {
		final Germplasm germplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);

		final DmsProject study = new DmsProject(TEST_PROJECT_ID);
		study.setName(RandomStringUtils.randomAlphabetic(10));
		study.setDescription(RandomStringUtils.randomAlphabetic(10));
		this.daoFactory.getDmsProjectDAO().save(study);

		final StockModel stock = new StockModel();
		stock.setGermplasm(germplasm);
		stock.setUniqueName("1");
		stock.setIsObsolete(false);
		stock.setProject(study);
		stock.setTypeId(TermId.ENTRY_CODE.getId());
		this.daoFactory.getStockDao().save(stock);

		final GermplasmSearchRequest request = new GermplasmSearchRequest();
		request.setTrialNames(Arrays.asList(String.valueOf(study.getName())));
		final long count = this.daoFactory.getGermplasmDao().countGermplasmDTOs(request);
		Assert.assertThat(count, is(1L));
	}

	@Test
	public void testGetProgenitorsByGIDWithPrefName() {
		final String crossName = RandomStringUtils.randomAlphabetic(20);
		final Integer crossId = this.insertGermplasmWithName(crossName);
		final Germplasm crossGermplasm = this.daoFactory.getGermplasmDao().getById(crossId);
		Assert.assertTrue(this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(crossId).isEmpty());

		final String progenitor1Name = RandomStringUtils.randomAlphabetic(20);
		final Integer progenitor1ID = this.insertGermplasmWithName(progenitor1Name);
		final String progenitor2Name = RandomStringUtils.randomAlphabetic(20);
		final Integer progenitor2ID = this.insertGermplasmWithName(progenitor2Name);
		this.daoFactory.getProgenitorDao().save(new Progenitor(crossGermplasm, 3, progenitor1ID));
		this.daoFactory.getProgenitorDao().save(new Progenitor(crossGermplasm, 4, progenitor2ID));

		final List<Germplasm> progenitors = this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(crossId);
		Assert.assertEquals(2, progenitors.size());
		final Germplasm progenitor1FromDB = progenitors.get(0);
		Assert.assertEquals(progenitor1ID, progenitor1FromDB.getGid());
		Assert.assertEquals(progenitor1Name, progenitor1FromDB.getPreferredName().getNval());
		final Germplasm progenitor2FromDB = progenitors.get(1);
		Assert.assertEquals(progenitor2ID, progenitor2FromDB.getGid());
		Assert.assertEquals(progenitor2Name, progenitor2FromDB.getPreferredName().getNval());
	}

	@Test
	public void testGetParentsFromProgenitorsForGIDsMap() {
		final Integer cross1ID = this.insertGermplasmWithName(RandomStringUtils.randomAlphabetic(20));
		final Germplasm cross1Germplasm = this.daoFactory.getGermplasmDao().getById(cross1ID);
		Assert.assertTrue(this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(cross1ID).isEmpty());

		final Integer cross2ID = this.insertGermplasmWithName(RandomStringUtils.randomAlphabetic(20));
		final Germplasm cross2Germplasm = this.daoFactory.getGermplasmDao().getById(cross2ID);
		Assert.assertTrue(this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(cross2ID).isEmpty());

		final Integer gidNoProgenitor = this.insertGermplasmWithName(RandomStringUtils.randomAlphabetic(20));
		Assert.assertTrue(this.daoFactory.getGermplasmDao().getProgenitorsByGIDWithPrefName(gidNoProgenitor).isEmpty());

		// TODO seed data for listdata and perform assertions on pedigree
		// Create 2 progenitor records for Gid1 = Cross1
		final String cross1progenitor1Name = RandomStringUtils.randomAlphabetic(20);
		final Integer cross1progenitor1ID = this.insertGermplasmWithName(cross1progenitor1Name);
		final String cross1progenitor2Name = RandomStringUtils.randomAlphabetic(20);
		final Integer cross1progenitor2ID = this.insertGermplasmWithName(cross1progenitor2Name);
		this.daoFactory.getProgenitorDao().save(new Progenitor(cross1Germplasm, 3, cross1progenitor1ID));
		this.daoFactory.getProgenitorDao().save(new Progenitor(cross1Germplasm, 4, cross1progenitor2ID));

		// Create 3 progenitor records for Gid2 = Cross2
		final String cross2progenitor1Name = RandomStringUtils.randomAlphabetic(20);
		final Integer cross2progenitor1ID = this.insertGermplasmWithName(cross2progenitor1Name);
		final String cross2progenitor2Name = RandomStringUtils.randomAlphabetic(20);
		final Integer cross2progenitor2ID = this.insertGermplasmWithName(cross2progenitor2Name);
		final String cross2progenitor3Name = RandomStringUtils.randomAlphabetic(20);
		final Integer cross2progenitor3ID = this.insertGermplasmWithName(cross2progenitor3Name);
		this.daoFactory.getProgenitorDao().save(new Progenitor(cross2Germplasm, 3, cross2progenitor1ID));
		this.daoFactory.getProgenitorDao().save(new Progenitor(cross2Germplasm, 4, cross2progenitor2ID));
		this.daoFactory.getProgenitorDao().save(new Progenitor(cross2Germplasm, 5, cross2progenitor3ID));

		final Map<Integer, List<GermplasmParent>> progenitorsMap =
			this.daoFactory.getGermplasmDao().getParentsFromProgenitorsForGIDsMap(Lists.newArrayList(cross1ID, cross2ID, gidNoProgenitor));
		Assert.assertEquals(2, progenitorsMap.size());
		Assert.assertNull(progenitorsMap.get(gidNoProgenitor));
		// Verify progenitors for Cross1
		final List<GermplasmParent> cross1Progenitors = progenitorsMap.get(cross1ID);
		Assert.assertNotNull(cross1Progenitors);
		Assert.assertEquals(2, cross1Progenitors.size());
		final GermplasmParent cross1progenitor1FromDB = cross1Progenitors.get(0);
		Assert.assertEquals(cross1progenitor1ID, cross1progenitor1FromDB.getGid());
		Assert.assertEquals(cross1progenitor1Name, cross1progenitor1FromDB.getDesignation());
		final GermplasmParent cross1progenitor2FromDB = cross1Progenitors.get(1);
		Assert.assertEquals(cross1progenitor2ID, cross1progenitor2FromDB.getGid());
		Assert.assertEquals(cross1progenitor2Name, cross1progenitor2FromDB.getDesignation());

		// Verify progenitors for Cross2
		final List<GermplasmParent> cross2Progenitors = progenitorsMap.get(cross2ID);
		Assert.assertNotNull(cross2Progenitors);
		Assert.assertEquals(3, cross2Progenitors.size());
		final GermplasmParent cross2progenitor1FromDB = cross2Progenitors.get(0);
		Assert.assertEquals(cross2progenitor1ID, cross2progenitor1FromDB.getGid());
		Assert.assertEquals(cross2progenitor1Name, cross2progenitor1FromDB.getDesignation());
		final GermplasmParent cross2progenitor2FromDB = cross2Progenitors.get(1);
		Assert.assertEquals(cross2progenitor2ID, cross2progenitor2FromDB.getGid());
		Assert.assertEquals(cross2progenitor2Name, cross2progenitor2FromDB.getDesignation());
		final GermplasmParent cross2progenitor3FromDB = cross2Progenitors.get(2);
		Assert.assertEquals(cross2progenitor3ID, cross2progenitor3FromDB.getGid());
		Assert.assertEquals(cross2progenitor3Name, cross2progenitor3FromDB.getDesignation());
	}

	@Test
	public void testReplacedGermplasm() {
		final Germplasm replacedGermplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(replacedGermplasm);
		final Germplasm validGermplasm =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(validGermplasm);
		Assert.assertNotNull(replacedGermplasm.getGid());
		Assert.assertNull("Replaced Germplasm will not be retrieve", this.daoFactory.getGermplasmDao().getById(replacedGermplasm.getGid()));
		Assert.assertNotNull(validGermplasm.getGid());
		Assert.assertNotNull("Valid Germplasm will be retrieve", this.daoFactory.getGermplasmDao().getById(validGermplasm.getGid()));
	}

	@Test
	public void testHasExistingCrossesWithSingleMaleParent() {
		final Germplasm femaleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(femaleParent);
		final Germplasm maleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent);
		final Germplasm existingCross = GermplasmTestDataInitializer
			.createGermplasm(20150101, femaleParent.getGid(), maleParent.getGid(), 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(existingCross);
		Assert.assertTrue(
			this.daoFactory.getGermplasmDao()
				.hasExistingCrosses(femaleParent.getGid(), Collections.singletonList(maleParent.getGid()), Optional.empty()));
		//Check if self is excluded
		Assert.assertFalse(
			this.daoFactory.getGermplasmDao().hasExistingCrosses(femaleParent.getGid(), Collections.singletonList(maleParent.getGid()),
				Optional.of(existingCross.getGid())));
	}

	@Test
	public void testHasExistingCrossesWithMultipleMaleParents() {
		final Germplasm femaleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(femaleParent);
		final Germplasm maleParent1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent1);
		final Germplasm existingCross = GermplasmTestDataInitializer
			.createGermplasm(20150101, femaleParent.getGid(), maleParent1.getGid(), 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(existingCross);
		final Germplasm maleParent2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent2);
		this.daoFactory.getProgenitorDao().save(new Progenitor(existingCross, 3, maleParent2.getGid()));
		Assert.assertTrue(this.daoFactory.getGermplasmDao()
			.hasExistingCrosses(femaleParent.getGid(), Arrays.asList(maleParent1.getGid(), maleParent2.getGid()), Optional.empty()));
		//Check if self is excluded
		Assert.assertFalse(this.daoFactory.getGermplasmDao()
			.hasExistingCrosses(femaleParent.getGid(), Arrays.asList(maleParent1.getGid(), maleParent2.getGid()),
				Optional.of(existingCross.getGid())));
	}

	@Test
	public void testGetExistingCrossesWithSingleMaleParent() {
		final Germplasm femaleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(femaleParent);
		final Germplasm maleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent);
		final Germplasm existingCross = GermplasmTestDataInitializer
			.createGermplasm(20150101, femaleParent.getGid(), maleParent.getGid(), 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(existingCross);
		List<Germplasm> existingCrosses =
			this.daoFactory.getGermplasmDao()
				.getExistingCrosses(femaleParent.getGid(), Collections.singletonList(maleParent.getGid()), Optional.empty());
		Assert.assertEquals(existingCross.getGid(), existingCrosses.get(0).getGid());
		//Check if self is excluded
		existingCrosses = this.daoFactory.getGermplasmDao()
			.getExistingCrosses(femaleParent.getGid(), Collections.singletonList(maleParent.getGid()), Optional.of(existingCross.getGid()));
		Assert.assertTrue(existingCrosses.isEmpty());
	}

	@Test
	public void testGetExistingCrossesWithMultipleMaleParents() {
		final Germplasm femaleParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 1, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(femaleParent);
		final Germplasm maleParent1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent1);
		final Germplasm existingCross = GermplasmTestDataInitializer
			.createGermplasm(20150101, femaleParent.getGid(), maleParent1.getGid(), 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(existingCross);
		final Germplasm maleParent2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.daoFactory.getGermplasmDao().save(maleParent2);
		this.daoFactory.getProgenitorDao().save(new Progenitor(existingCross, 3, maleParent2.getGid()));
		List<Germplasm> existingCrosses =
			this.daoFactory.getGermplasmDao()
				.getExistingCrosses(femaleParent.getGid(), Arrays.asList(maleParent1.getGid(), maleParent2.getGid()), Optional.empty());
		Assert.assertEquals(existingCross.getGid(), existingCrosses.get(0).getGid());
		//Check if self is excluded
		existingCrosses = this.daoFactory.getGermplasmDao()
			.getExistingCrosses(femaleParent.getGid(), Arrays.asList(maleParent1.getGid(), maleParent2.getGid()),
				Optional.of(existingCross.getGid()));
		Assert.assertTrue(existingCrosses.isEmpty());
	}

	@Test
	public void testBuildCountGermplasmDTOsQuery_NoFilterSpecified() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		Assert.assertThat(this.daoFactory.getGermplasmDao().buildCountGermplasmDTOsQuery(dto),
			is("SELECT LEAST(count(1), 5000) FROM germplsm "));
	}

	@Test
	public void testBuildCountGermplasmDTOsQuery_WithFilterSpecified() {
		final GermplasmSearchRequest dto = new GermplasmSearchRequest();
		dto.setGermplasmDbIds(Collections.singletonList(RandomStringUtils.randomAlphanumeric(10)));
		Assert.assertThat(this.daoFactory.getGermplasmDao().buildCountGermplasmDTOsQuery(dto),
			is("SELECT COUNT(1) FROM (  SELECT g.gid  FROM germplsm g  WHERE g.deleted = 0 AND g.grplce = 0  AND g.germplsm_uuid IN (:germplasmDbIds) ) as T "));
	}

	@Test
	public void testUpdateGroupSource() {
		final Method derivativeMethod = this.daoFactory.getMethodDAO().getByCode(Collections.singletonList("UDM")).get(0);

		final Germplasm femaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm maleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(femaleParent);
		this.daoFactory.getGermplasmDao().save(maleParent);

		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		germplasm.setGpid1(femaleParent.getGid());
		germplasm.setGpid2(maleParent.getGid());
		germplasm.setGnpgs(2);
		germplasm.setMethodId(derivativeMethod.getMid());
		germplasm.setGermplasmUUID(RandomStringUtils.randomAlphanumeric(10));
		this.daoFactory.getGermplasmDao().save(germplasm);

		final Germplasm actualGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		Assert.assertThat(actualGermplasm.getCreatedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(actualGermplasm.getCreatedDate());
		Assert.assertNull(actualGermplasm.getModifiedDate());
		Assert.assertNull(actualGermplasm.getModifiedBy());

		Assert.assertThat(actualGermplasm.getGpid1(), is(femaleParent.getGid()));
		Assert.assertThat(actualGermplasm.getGpid2(), is(maleParent.getGid()));

		final Germplasm newFemaleParent = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		newFemaleParent.setMethodId(derivativeMethod.getMid());
		this.daoFactory.getGermplasmDao().save(newFemaleParent);

		this.daoFactory.getGermplasmDao().updateGroupSource(femaleParent.getGid(), newFemaleParent.getGid());

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().clear();

		this.sessionProvder.getSession().refresh(germplasm);

		final Germplasm updatedGermplasm = this.daoFactory.getGermplasmDao().getById(germplasm.getGid());
		Assert.assertThat(updatedGermplasm.getGpid1(), is(newFemaleParent.getGid()));
		Assert.assertThat(updatedGermplasm.getGpid2(), is(maleParent.getGid()));
		Assert.assertThat(updatedGermplasm.getModifiedBy(), is(this.findAdminUser()));
		Assert.assertNotNull(updatedGermplasm.getModifiedDate());
	}

	@Test
	public void testGetGermplasmMergeDTOs() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		this.daoFactory.getGermplasmDao().save(germplasm1);
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		germplasm2.setGrplce(germplasm1.getGid());
		this.daoFactory.getGermplasmDao().save(germplasm2);
		germplasm3.setGrplce(germplasm1.getGid());
		this.daoFactory.getGermplasmDao().save(germplasm3);

		this.daoFactory.getGermplasmDao().deleteGermplasm(Arrays.asList(germplasm2.getGid(), germplasm3.getGid(), germplasm1.getGid()));
		this.sessionProvder.getSession().flush();
		final List<GermplasmMergedDto> result = this.daoFactory.getGermplasmDao().getGermplasmMerged(germplasm1.getGid());
		assertThat(result.size(), equalTo(2));
		assertThat(result.get(0).getGid(), equalTo(germplasm2.getGid()));
		assertThat(result.get(0).getDesignation(), equalTo(germplasm2.getPreferredName().getNval()));
		assertThat(result.get(1).getGid(), equalTo(germplasm3.getGid()));
		assertThat(result.get(1).getDesignation(), equalTo(germplasm3.getPreferredName().getNval()));

	}

	@Test
	public void testCountGermplasmMatches_FilterByGids() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByGids_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Collections.singletonList(new Random().nextInt()));
		Assert.assertEquals(0l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByGids_RestrictByLocationName() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList("DSS")).get(0);
		germplasm1.setLocationId(location.getLocid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setLocationName(new SqlTextFilter(location.getLname(), SqlTextFilter.Type.EXACTMATCH));
		Assert.assertEquals(1l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByGids_RestrictByLocationAbbreviation() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList("DSS")).get(0);
		germplasm1.setLocationId(location.getLocid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setLocationAbbreviation(new SqlTextFilter(location.getLabbr(), SqlTextFilter.Type.CONTAINS));
		Assert.assertEquals(1l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByGids_RestrictByMethod() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Method method = this.daoFactory.getMethodDAO().getByCode("UDM");
		germplasm1.setMethodId(method.getMid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setMethods(Collections.singletonList(method.getMcode()));
		Assert.assertEquals(1l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByPUI() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByPUI_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(RandomStringUtils.randomAlphabetic(30)));
		Assert.assertEquals(0l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByNames() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setNames(Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByNames_RestrictNameType() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiNameType = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final UserDefinedField lineNameType = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "LNAME");
		final Name name1 = new Name(null, germplasm1, puiNameType.getFldno(), 0, RandomStringUtils.randomAlphabetic(20), 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, lineNameType.getFldno(), 0, RandomStringUtils.randomAlphabetic(20), 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, lineNameType.getFldno(), 0, RandomStringUtils.randomAlphabetic(20), 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setNames(Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval()));
		germplasmMatchRequestDto.setNameTypes(Collections.singletonList(lineNameType.getFcode()));
		Assert.assertEquals(2l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));

		final GermplasmMatchRequestDto germplasmMatchRequest2 = new GermplasmMatchRequestDto();
		germplasmMatchRequest2.setNames(Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval()));
		germplasmMatchRequest2.setNameTypes(Collections.singletonList(puiNameType.getFcode()));
		Assert.assertEquals(1l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequest2));
	}

	@Test
	public void testCountGermplasmMatches_FilterByNames_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(30)));
		Assert.assertEquals(0l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByPUI_Names() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(name1.getNval()));
		germplasmMatchRequestDto.setNames(Arrays.asList(name2.getNval(), name3.getNval()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByUUID() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmUUIDs(
			Arrays.asList(germplasm1.getGermplasmUUID(), germplasm2.getGermplasmUUID(), germplasm3.getGermplasmUUID()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByUUID_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmUUIDs(Collections.singletonList(RandomStringUtils.randomAlphabetic(30)));
		Assert.assertEquals(0l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testCountGermplasmMatches_FilterByPUI_GermplasmUUID_GID() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(name1.getNval()));
		germplasmMatchRequestDto.setGermplasmUUIDs(Collections.singletonList(germplasm2.getGermplasmUUID()));
		germplasmMatchRequestDto.setGids(Lists.newArrayList(germplasm3.getGid()));
		Assert.assertEquals(3l, this.daoFactory.getGermplasmDao().countGermplasmMatches(germplasmMatchRequestDto));
	}

	@Test
	public void testFindGermplasmMatches_FilterByGids() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		final List<Integer> gids = Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid());
		germplasmMatchRequestDto.setGids(gids);

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(germplasmMatches.stream().map(GermplasmDto::getGid).collect(Collectors.toList()).containsAll(gids));
	}

	@Test
	public void testFindGermplasmMatches_FilterByGID_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Collections.singletonList(new Random().nextInt()));

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(0, germplasmMatches.size());
	}

	@Test
	public void testFindGermplasmMatches_FilterByGids_RestrictByLocationName() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList("DSS")).get(0);
		germplasm1.setLocationId(location.getLocid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setLocationName(new SqlTextFilter(location.getLname(), SqlTextFilter.Type.EXACTMATCH));
		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(1, germplasmMatches.size());
		Assert.assertEquals(germplasm1.getGid(), germplasmMatches.get(0).getGid());
	}

	@Test
	public void testFindGermplasmMatches_FilterByGids_RestrictByLocationAbbreviation() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Location location = this.daoFactory.getLocationDAO().getByAbbreviations(Collections.singletonList("DSS")).get(0);
		germplasm1.setLocationId(location.getLocid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setLocationAbbreviation(new SqlTextFilter(location.getLabbr(), SqlTextFilter.Type.CONTAINS));
		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(1, germplasmMatches.size());
		Assert.assertEquals(germplasm1.getGid(), germplasmMatches.get(0).getGid());
	}

	@Test
	public void testFindGermplasmMatches_FilterByGids_RestrictByMethod() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Method method = this.daoFactory.getMethodDAO().getByCode("UDM");
		germplasm1.setMethodId(method.getMid());
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGids(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid()));
		germplasmMatchRequestDto.setMethods(Collections.singletonList(method.getMcode()));
		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(1, germplasmMatches.size());
		Assert.assertEquals(germplasm1.getGid(), germplasmMatches.get(0).getGid());
	}

	@Test
	public void testFindGermplasmMatches_FilterByPUI() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		final List<String> germplasmPUIs = Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval());
		germplasmMatchRequestDto.setGermplasmPUIs(germplasmPUIs);

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(
			germplasmMatches.stream().map(GermplasmDto::getGermplasmPUI).collect(Collectors.toList()).containsAll(germplasmPUIs));
	}

	@Test
	public void testFindGermplasmMatches_FilterByPUI_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(0, germplasmMatches.size());
	}

	@Test
	public void testFindGermplasmMatches_FilterByNames() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		final List<String> germplasmPUIs = Arrays.asList(name1.getNval(), name2.getNval(), name3.getNval());
		germplasmMatchRequestDto.setNames(germplasmPUIs);

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(
			germplasmMatches.stream().map(GermplasmDto::getGermplasmPUI).collect(Collectors.toList()).containsAll(germplasmPUIs));
	}

	@Test
	public void testFindGermplasmMatches_FilterByNames_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setNames(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(0, germplasmMatches.size());
	}

	@Test
	public void testFindGermplasmMatches_FilterByPUI_Names() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(name1.getNval()));
		germplasmMatchRequestDto.setNames(Arrays.asList(name2.getNval(), name3.getNval()));
		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(germplasmMatches.stream().map(GermplasmDto::getGid).collect(Collectors.toList())
			.containsAll(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid())));
	}

	@Test
	public void testFindGermplasmMatches_FilterByUUID() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		final List<String> germplasmUUIDs =
			Arrays.asList(germplasm1.getGermplasmUUID(), germplasm2.getGermplasmUUID(), germplasm3.getGermplasmUUID());
		germplasmMatchRequestDto.setGermplasmUUIDs(
			germplasmUUIDs);

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(
			germplasmMatches.stream().map(GermplasmDto::getGermplasmUUID).collect(Collectors.toList()).containsAll(germplasmUUIDs));
	}

	@Test
	public void testFindGermplasmMatches_FilterByUUID_NoMatch() {
		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmUUIDs(Collections.singletonList(RandomStringUtils.randomAlphabetic(20)));

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(0, germplasmMatches.size());
	}

	@Test
	public void testFindGermplasmMatches_FilterByPUI_GermplasmUUID_GID() {
		final Germplasm germplasm1 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm2 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();
		final Germplasm germplasm3 = GermplasmTestDataInitializer.createGermplasmWithPreferredName();

		this.daoFactory.getGermplasmDao().save(germplasm1);
		this.daoFactory.getGermplasmDao().save(germplasm2);
		this.daoFactory.getGermplasmDao().save(germplasm3);

		final UserDefinedField puiUserDefinedField = this.daoFactory.getUserDefinedFieldDAO()
			.getByTableTypeAndCode(UDTableType.NAMES_NAME.getTable(), UDTableType.NAMES_NAME.getType(), "PUI");
		final Name name1 = new Name(null, germplasm1, puiUserDefinedField.getFldno(), 0, "PUI1", 0, 0, 0);
		final Name name2 = new Name(null, germplasm2, puiUserDefinedField.getFldno(), 0, "PUI2", 0, 0, 0);
		final Name name3 = new Name(null, germplasm3, puiUserDefinedField.getFldno(), 0, "PUI3", 0, 0, 0);

		this.daoFactory.getNameDao().save(name1);
		this.daoFactory.getNameDao().save(name2);
		this.daoFactory.getNameDao().save(name3);

		final GermplasmMatchRequestDto germplasmMatchRequestDto = new GermplasmMatchRequestDto();
		germplasmMatchRequestDto.setGermplasmPUIs(Collections.singletonList(name1.getNval()));
		germplasmMatchRequestDto.setGermplasmUUIDs(Collections.singletonList(germplasm2.getGermplasmUUID()));
		germplasmMatchRequestDto.setGids(Lists.newArrayList(germplasm3.getGid()));

		final List<GermplasmDto> germplasmMatches = this.daoFactory.getGermplasmDao().findGermplasmMatches(germplasmMatchRequestDto, null);
		Assert.assertEquals(3, germplasmMatches.size());
		Assert.assertTrue(germplasmMatches.stream().map(GermplasmDto::getGid).collect(Collectors.toList())
			.containsAll(Arrays.asList(germplasm1.getGid(), germplasm2.getGid(), germplasm3.getGid())));
	}

	private Name saveGermplasmName(final Integer germplasmGID, final String nameType) {
		UserDefinedField attributeField =
			this.daoFactory.getUserDefinedFieldDAO().getByTableTypeAndCode("NAMES", "NAME", nameType);

		if (attributeField == null) {
			attributeField = new UserDefinedField(null, "NAMES", "NAME", nameType, "", "", "", 0, 0, 0, 0);
			this.daoFactory.getUserDefinedFieldDAO().saveOrUpdate(attributeField);
		}

		final Name name = GermplasmTestDataInitializer.createGermplasmName(germplasmGID, RandomStringUtils.randomAlphanumeric(50));
		name.setTypeId(attributeField.getFldno());
		name.setNstat(0); // TODO Review
		this.daoFactory.getNameDao().save(name);
		return name;
	}

	private List<String> saveGermplasmWithNames(final String nameType) {
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm1, germplasm1.getPreferredName(), this.cropType);

		final Name name1 = this.saveGermplasmName(germplasm1.getGid(), nameType);
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm2, germplasm2.getPreferredName(), this.cropType);

		final Name name2 = this.saveGermplasmName(germplasm2.getGid(), nameType);
		return Arrays.asList(name1.getNval(), name2.getNval());
	}

	private List<String> saveGermplasmWithAttributes(final String attributeType) {
		final Germplasm germplasm1 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm1, germplasm1.getPreferredName(), this.cropType);

		final Attribute attribute1 = this.saveAttribute(germplasm1, attributeType);
		final Germplasm germplasm2 =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(germplasm2, germplasm2.getPreferredName(), this.cropType);

		final Attribute attribute2 = this.saveAttribute(germplasm2, attributeType);
		return Arrays.asList(attribute1.getAval(), attribute2.getAval());
	}

	private Attribute saveAttribute(final Germplasm germplasm, final String attributeType) {
		CVTerm cvTerm =
			this.daoFactory.getCvTermDao().getByNameAndCvId(attributeType, CvId.VARIABLES.getId());

		if (cvTerm == null) {
			cvTerm = new CVTerm(null, CvId.VARIABLES.getId(), attributeType, attributeType, null, 0, 0, false);
			this.daoFactory.getCvTermDao().save(cvTerm);
		}

		final Attribute attribute = new Attribute();
		attribute.setGermplasmId(germplasm.getGid());
		attribute.setTypeId(cvTerm.getCvTermId());
		attribute.setAval(RandomStringUtils.randomAlphanumeric(50));
		attribute.setAdate(germplasm.getGdate());

		this.daoFactory.getAttributeDAO().saveOrUpdate(attribute);
		return attribute;
	}

	private Integer insertGermplasmWithName(final String existingGermplasmNameWithPrefix, final boolean isDeleted) {
		final Germplasm germplasm = GermplasmTestDataInitializer
			.createGermplasmWithPreferredName(existingGermplasmNameWithPrefix);
		germplasm.setDeleted(isDeleted);
		return this.germplasmTestDataGenerator.addGermplasm(germplasm, germplasm.getPreferredName(), this.cropType);
	}

	private Integer insertGermplasmWithName(final String existingGermplasmNameWithPrefix) {
		return this.insertGermplasmWithName(existingGermplasmNameWithPrefix, false);
	}

	private void initializeGermplasms() {
		final Germplasm fParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(fParent, fParent.getPreferredName(), this.cropType);

		final Germplasm mParent =
			GermplasmTestDataInitializer.createGermplasm(20150101, 1, 2, 2, 0, 0, 1, 1, 0, 1, 1, "MethodName", "LocationName");
		this.germplasmTestDataGenerator.addGermplasm(mParent, mParent.getPreferredName(), this.cropType);

		final Germplasm mgMember = GermplasmTestDataInitializer
			.createGermplasm(20150101, fParent.getGid(), mParent.getGid(), 2, 0, 0, 1, 1, GermplasmDAOTest.GROUP_ID, 1, 1, "MethodName",
				"LocationName");

		this.germplasmTestDataGenerator.addGermplasm(mgMember, mgMember.getPreferredName(), this.cropType);
	}

}
