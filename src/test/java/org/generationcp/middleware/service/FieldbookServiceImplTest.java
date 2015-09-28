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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.T;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.DatabaseBroker;
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

@RunWith(MockitoJUnitRunner.class)
public class FieldbookServiceImplTest {
	
	@Mock
	Germplasm germplasm;
	
	@Mock
	Name name;
	
	@Mock
	GermplasmListData listData;
	
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
	GermplasmDataManager germplasmDataManager;
	
	@Mock
	GermplasmList germplasmList;
	
	private List<Pair<Germplasm, List<Name>>> germplasms;
	
	private List<Pair<Germplasm, GermplasmListData>> listDataItems;
	
	@InjectMocks
	private FieldbookServiceImpl fieldbookServiceImpl;
	
	@Before
	public void setUp(){
		Mockito.doReturn(this.name).when(this.germplasm).getPreferredName();
		Mockito.doReturn(this.session).when(this.sessionProvider).getSession();
		Mockito.doReturn(this.query).when(this.session).createSQLQuery(Matchers.anyString());
		this.dbBroker.setSessionProvider(this.sessionProvider);
		this.germplasms = this.createGermplasms();
		listDataItems = this.createListDataItems();	
	}
	@Test
	public void testSaveNurseryAdvanceGermplasmListSuccess(){
		Integer out = this.fieldbookServiceImpl.saveNurseryAdvanceGermplasmList(germplasms, listDataItems, germplasmList);
		Assert.assertEquals("List Id should be 0", (Integer) 0, out);
	}
	
	@Test
	public void testSaveGermplasmListSuccess(){
		Integer out = this.fieldbookServiceImpl.saveGermplasmList(listDataItems, germplasmList);
		Assert.assertEquals("List Id should be 0", (Integer) 0, out);
	}
	
	private List<Pair<Germplasm, List<Name>>> createGermplasms(){
		List<Pair<Germplasm, List<Name>>> germplasms = new ArrayList<>();
		List<Name> names= Arrays.asList(name);
		germplasms.add(new ImmutablePair<Germplasm, List<Name>>(germplasm, names));
		return germplasms;
	}
	
	private List<Pair<Germplasm, GermplasmListData>> createListDataItems(){
		List<Pair<Germplasm, GermplasmListData>> listDataItems = new ArrayList<>();
		listDataItems.add(new ImmutablePair<Germplasm, GermplasmListData>(this.germplasm, this.listData));
		return listDataItems;
	}
}
