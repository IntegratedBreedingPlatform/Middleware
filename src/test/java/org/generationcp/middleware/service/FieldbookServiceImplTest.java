/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementRowTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.operation.saver.ExperimentPropertySaver;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.pojos.*;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldbookServiceImplTest {

	public static final String PROGRAM_UUID = "9f9c606e-03c1-4073-bf0c-2ffa58c36037";
	@Mock
	Session session;

	@Mock
	HibernateSessionProvider sessionProvider;

	@Mock
	DatabaseBroker dbBroker;

	@Mock
	SQLQuery query;

	@Mock
	LocationDataManager locationDataManager;

	@Mock
	GermplasmListManager germplasmListManager;

	@Mock
	private CrossExpansionProperties crossExpansionProperties;

	@Mock
	private GermplasmGroupingService germplasmGroupingService;

	@Mock
	private WorkbookSaver workbookSaver;

	private List<Pair<Germplasm, List<Name>>> germplasms;

	private List<Pair<Germplasm, GermplasmListData>> listDataItems;

	private List<Pair<Germplasm, List<Attribute>>> germplasmAttributes;

	@InjectMocks
	private FieldbookServiceImpl fieldbookServiceImpl;

	@Before
	public void setUp() {
		this.fieldbookServiceImpl.setCrossExpansionProperties(this.crossExpansionProperties);
		this.fieldbookServiceImpl.setGermplasmGroupingService(this.germplasmGroupingService);
		this.fieldbookServiceImpl.setLocationDataManager(this.locationDataManager);
		this.fieldbookServiceImpl.setGermplasmListManager(this.germplasmListManager);
		this.fieldbookServiceImpl.setWorkbookSaver(this.workbookSaver);
		Mockito.doReturn(this.session).when(this.sessionProvider).getSession();
		Mockito.doReturn(this.query).when(this.session).createSQLQuery(Matchers.anyString());
		this.dbBroker.setSessionProvider(this.sessionProvider);
		this.germplasms = this.createGermplasms();
		this.listDataItems = this.createListDataItems();
		this.germplasmAttributes = this.createGermplasmAttributes();
		when(this.locationDataManager.getLocationsByUniqueID(FieldbookServiceImplTest.PROGRAM_UUID))
			.thenReturn(new ArrayList<>());
	}

	@Test
	public void testSaveNurseryAdvanceGermplasmListSuccess() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(1);
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		final Integer out = this.fieldbookServiceImpl
			.saveNurseryAdvanceGermplasmList(this.germplasms, this.listDataItems, germplasmList, this.germplasmAttributes, cropType);
		Assert.assertEquals("List Id should be 1", (Integer) 1, out);

		// Make sure a call to save various things occur.
		Mockito.verify(this.session).save(germplasmList);
		Mockito.verify(this.session).save(this.listDataItems.get(0).getLeft());
		Mockito.verify(this.session).save(this.germplasms.get(0).getLeft());
		Mockito.verify(this.session).save(this.germplasmAttributes.get(0).getLeft());
	}

	@Test
	public void testSaveGermplasmListSuccess() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(1);
		final Integer out = this.fieldbookServiceImpl.saveGermplasmList(this.listDataItems, germplasmList, false);
		Assert.assertEquals("List Id should be 1", (Integer) 1, out);
	}

	@Test
	public void testSaveMeasurementsTrue() {
		final Measurements measurements = Mockito.mock(Measurements.class);
		final List<MeasurementVariable> variates = MeasurementVariableTestDataInitializer.createMeasurementVariableList();
		final List<MeasurementRow> observations =
			MeasurementRowTestDataInitializer.createMeasurementRowList(1, "Test Name", "Test Value", new MeasurementVariable());
		this.fieldbookServiceImpl.saveMeasurements(true, variates, observations, measurements);
		// Verify that the method is called
		Mockito.verify(measurements).saveMeasurements(observations);
	}

	@Test
	public void testSaveMeasurementsFalse() {
		final Measurements measurements = Mockito.mock(Measurements.class);
		final List<MeasurementVariable> variates = MeasurementVariableTestDataInitializer.createMeasurementVariableList();
		final List<MeasurementRow> observations =
			MeasurementRowTestDataInitializer.createMeasurementRowList(1, "Test Name", "Test Value", new MeasurementVariable());
		this.fieldbookServiceImpl.saveMeasurements(false, variates, observations, measurements);
		// Verify that the method is never called
		Mockito.verify(measurements, Mockito.times(0)).saveMeasurements(observations);
	}

	@Test
	public void getLocationsByProgramUUID() {
		final List<Location> locations = this.fieldbookServiceImpl.getLocationsByProgramUUID(FieldbookServiceImplTest.PROGRAM_UUID);

		Mockito.verify(this.locationDataManager, Mockito.times(1)).getLocationsByUniqueID(FieldbookServiceImplTest.PROGRAM_UUID);
		Assert.assertNotNull("The return locations list should not be null", locations);
	}

	@Test
	public void testSaveOrUpdateStudyDesignDataFactorIsCategorical() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataTypeId(TermId.CATEGORICAL_VARIABLE.getId());

		final ExperimentPropertySaver experimentPropertySaver = Mockito.mock(ExperimentPropertySaver.class);
		final int termId = 234;
		final String cValueId = "749793";
		final String value = "My Value";
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setcValueId(cValueId);
		measurementData.setValue(value);
		measurementData.setMeasurementVariable(measurementVariable);

		this.fieldbookServiceImpl.saveOrUpdateTrialDesignData(experimentPropertySaver, new ExperimentModel(), measurementData, termId);

		Mockito.verify(experimentPropertySaver)
			.saveOrUpdateProperty(ArgumentMatchers.any(ExperimentModel.class), ArgumentMatchers.eq(termId), Matchers.eq(cValueId));

	}

	@Test
	public void testSaveOrUpdateStudyDesignDataFactorIsNotCategorical() {

		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setDataTypeId(TermId.CHARACTER_VARIABLE.getId());

		final ExperimentPropertySaver experimentPropertySaver = Mockito.mock(ExperimentPropertySaver.class);
		final int termId = 234;
		final String cValueId = "749793";
		final String value = "My Value";
		final MeasurementData measurementData = new MeasurementData();
		measurementData.setcValueId(cValueId);
		measurementData.setValue(value);
		measurementData.setMeasurementVariable(measurementVariable);

		this.fieldbookServiceImpl.saveOrUpdateTrialDesignData(experimentPropertySaver, new ExperimentModel(), measurementData, termId);

		Mockito.verify(experimentPropertySaver)
			.saveOrUpdateProperty(ArgumentMatchers.any(ExperimentModel.class), ArgumentMatchers.eq(termId), Matchers.eq(value));

	}

	@Test
	public void testAddLocation() {
		Mockito.when(this.locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode()))
			.thenReturn(1001);
		Mockito.when(this.locationDataManager.getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode()))
			.thenReturn(1002);

		this.fieldbookServiceImpl.addLocation("LOCNAME", 1, 101, LocationType.BLOCK.getCode(), LocdesType.BLOCK_PARENT.getCode());

		Mockito.verify(this.locationDataManager).getUserDefinedFieldIdOfCode(UDTableType.LOCATION_LTYPE, LocationType.BLOCK.getCode());
		Mockito.verify(this.locationDataManager).getUserDefinedFieldIdOfCode(UDTableType.LOCDES_DTYPE, LocdesType.BLOCK_PARENT.getCode());
		final ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
		final ArgumentCaptor<Locdes> locdesCaptor = ArgumentCaptor.forClass(Locdes.class);
		Mockito.verify(this.locationDataManager).addLocationAndLocdes(locationCaptor.capture(), locdesCaptor.capture());
		final Location location = locationCaptor.getValue();
		final Locdes locdes = locdesCaptor.getValue();
		Assert.assertEquals("1001", location.getLtype().toString());
		Assert.assertEquals("LOCNAME", location.getLname());
		Assert.assertFalse(location.getLdefault());
		Assert.assertNull(location.getLabbr());
		Assert.assertEquals("1002", locdes.getTypeId().toString());
		Assert.assertEquals("101", locdes.getUserId().toString());
		Assert.assertEquals("1", locdes.getDval());

	}

	@Test
	public void testSaveWorkbookVariablesAndObservations() throws ParseException {
		final Workbook workbook = new Workbook();
		this.fieldbookServiceImpl.saveWorkbookVariablesAndObservations(workbook);
		Mockito.verify(this.workbookSaver).saveWorkbookVariables(workbook);
		Mockito.verify(this.workbookSaver).removeDeletedVariablesAndObservations(workbook);
	}

	private List<Pair<Germplasm, List<Name>>> createGermplasms() {
		final List<Pair<Germplasm, List<Name>>> germplasms = new ArrayList<>();
		final Name name = new Name();
		name.setNid(1);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final List<Name> names = Arrays.asList(name);
		germplasms.add(new ImmutablePair<>(germplasm, names));
		return germplasms;
	}

	private List<Pair<Germplasm, GermplasmListData>> createListDataItems() {
		final List<Pair<Germplasm, GermplasmListData>> listDataItems = new ArrayList<>();
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final GermplasmListData listData = new GermplasmListData();
		listDataItems.add(new ImmutablePair<>(germplasm, listData));
		return listDataItems;
	}

	private List<Pair<Germplasm, List<Attribute>>> createGermplasmAttributes() {
		final List<Pair<Germplasm, List<Attribute>>> attrs = new ArrayList<>();
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final Attribute attribute = new Attribute();
		attribute.setAval("Plot Code");
		attribute.setTypeId(1552);
		attrs.add(new ImmutablePair<>(germplasm, Lists.newArrayList(attribute)));
		return attrs;
	}
}
