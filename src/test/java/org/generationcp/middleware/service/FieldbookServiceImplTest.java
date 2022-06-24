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
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmTestDataInitializer;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.operation.saver.WorkbookSaver;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationType;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FieldbookServiceImplTest {

	public static final String PROGRAM_UUID = "9f9c606e-03c1-4073-bf0c-2ffa58c36037";
	@Mock
	Session session;

	@Mock
	HibernateSessionProvider sessionProvider;

	@Mock
	SQLQuery query;

	@Mock
	LocationDataManager locationDataManager;

	@Mock
	GermplasmListManager germplasmListManager;

	@Mock
	private GermplasmListDataService germplasmListDataService;

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
		this.fieldbookServiceImpl.setGermplasmListDataService(this.germplasmListDataService);
		this.fieldbookServiceImpl.setWorkbookSaver(this.workbookSaver);
		Mockito.doReturn(this.session).when(this.sessionProvider).getSession();
		Mockito.doReturn(this.query).when(this.session).createSQLQuery(ArgumentMatchers.anyString());
		this.germplasms = this.createGermplasms();
		this.listDataItems = this.createListDataItems();
		this.germplasmAttributes = this.createGermplasmAttributes();
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
		final Integer out = this.fieldbookServiceImpl.saveGermplasmList("maize", this.listDataItems, germplasmList, false);
		Assert.assertEquals("List Id should be 1", (Integer) 1, out);
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
