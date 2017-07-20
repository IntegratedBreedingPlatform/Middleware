/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementRowTestDataInitializer;
import org.generationcp.middleware.data.initializer.MeasurementVariableTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class FieldbookServiceImplTest {

	public static final String PROGRAM_UUID = "9f9c606e-03c1-4073-bf0c-2ffa58c36037";
	@Mock
	Session session;

	@Mock
	GermplasmListDAO germplasmListDao;

	@Mock
	HibernateSessionProvider sessionProvider;

	@Mock
	DatabaseBroker dbBroker;

	@Mock
	SQLQuery query;

	@Mock
	Criteria criteria;

	@Mock
	GermplasmDataManager germplasmDataManager;

	@Mock
	LocationDAO locationDAO;

	@Mock
	LocationDataManager locationDataManager;

	@Mock
	private CrossExpansionProperties crossExpansionProperties;

	@Mock
	private GermplasmGroupingService germplasmGroupingService;

	private List<Pair<Germplasm, List<Name>>> germplasms;

	private List<Pair<Germplasm, GermplasmListData>> listDataItems;

	private List<Pair<Germplasm, List<Attribute>>> germplasmAttributes;

	private MeasurementVariableTestDataInitializer measurementVariableTestDataInitializer;

	private MeasurementRowTestDataInitializer measurementRowTestDataInitializer;

	@InjectMocks
	private FieldbookServiceImpl fieldbookServiceImpl;

	@Before
	public void setUp() {
		this.measurementVariableTestDataInitializer = new MeasurementVariableTestDataInitializer();
		this.measurementRowTestDataInitializer = new MeasurementRowTestDataInitializer();
		this.fieldbookServiceImpl.setCrossExpansionProperties(this.crossExpansionProperties);
		this.fieldbookServiceImpl.setGermplasmGroupingService(this.germplasmGroupingService);
		this.fieldbookServiceImpl.setLocationDataManager(this.locationDataManager);
		Mockito.doReturn(this.session).when(this.sessionProvider).getSession();
		Mockito.doReturn(this.query).when(this.session).createSQLQuery(Matchers.anyString());
		Mockito.doReturn(this.criteria).when(this.session).createCriteria(UserDefinedField.class);
		this.dbBroker.setSessionProvider(this.sessionProvider);
		this.germplasms = this.createGermplasms();
		this.listDataItems = this.createListDataItems();
		this.germplasmAttributes = this.createGermplasmAttributes();
		Mockito.when(this.dbBroker.getLocationDAO()).thenReturn(this.locationDAO);
		Mockito.when(this.locationDataManager.getLocationsByUniqueID(FieldbookServiceImplTest.PROGRAM_UUID))
				.thenReturn(new ArrayList<Location>());
	}

	@Test
	public void testSaveNurseryAdvanceGermplasmListSuccess() {
		final GermplasmList germplasmList = GermplasmListTestDataInitializer.createGermplasmList(1);
		final Integer out = this.fieldbookServiceImpl.saveNurseryAdvanceGermplasmList(this.germplasms,
				this.listDataItems, germplasmList, this.germplasmAttributes);
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
		final List<MeasurementVariable> variates = this.measurementVariableTestDataInitializer
				.createMeasurementVariableList();
		final List<MeasurementRow> observations = this.measurementRowTestDataInitializer.createMeasurementRowList(1,
				"Test Name", "Test Value", new MeasurementVariable());
		this.fieldbookServiceImpl.saveMeasurements(true, variates, observations, measurements);
		// Verify that the method is called
		Mockito.verify(measurements).saveMeasurements(observations);
	}

	@Test
	public void testSaveMeasurementsFalse() {
		final Measurements measurements = Mockito.mock(Measurements.class);
		final List<MeasurementVariable> variates = this.measurementVariableTestDataInitializer
				.createMeasurementVariableList();
		final List<MeasurementRow> observations = this.measurementRowTestDataInitializer.createMeasurementRowList(1,
				"Test Name", "Test Value", new MeasurementVariable());
		this.fieldbookServiceImpl.saveMeasurements(false, variates, observations, measurements);
		// Verify that the method is never called
		Mockito.verify(measurements, Mockito.times(0)).saveMeasurements(observations);
	}

	@Test
	public void getLocationsByProgramUUID() {
		final List<Location> locations = this.fieldbookServiceImpl
				.getLocationsByProgramUUID(FieldbookServiceImplTest.PROGRAM_UUID);

		Mockito.verify(this.locationDataManager, Mockito.times(1))
				.getLocationsByUniqueID(FieldbookServiceImplTest.PROGRAM_UUID);
		Assert.assertNotNull("The return locations list should not be null", locations);
	}

	private List<Pair<Germplasm, List<Name>>> createGermplasms() {
		final List<Pair<Germplasm, List<Name>>> germplasms = new ArrayList<>();
		final Name name = new Name();
		name.setNid(1);
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final List<Name> names = Arrays.asList(name);
		germplasms.add(new ImmutablePair<Germplasm, List<Name>>(germplasm, names));
		return germplasms;
	}

	private List<Pair<Germplasm, GermplasmListData>> createListDataItems() {
		final List<Pair<Germplasm, GermplasmListData>> listDataItems = new ArrayList<>();
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final GermplasmListData listData = new GermplasmListData();
		listDataItems.add(new ImmutablePair<Germplasm, GermplasmListData>(germplasm, listData));
		return listDataItems;
	}

	private List<Pair<Germplasm, List<Attribute>>> createGermplasmAttributes() {
		final List<Pair<Germplasm, List<Attribute>>> attrs = new ArrayList<>();
		final Germplasm germplasm = GermplasmTestDataInitializer.createGermplasm(1);
		final Attribute attribute = new Attribute();
		attribute.setAval("Plot Code");
		attribute.setTypeId(1552);
		attrs.add(new ImmutablePair<Germplasm, List<Attribute>>(germplasm, Lists.newArrayList(attribute)));
		return attrs;
	}
}
