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
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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

	private List<Pair<Germplasm, List<Name>>> germplasms;

	private List<Pair<Germplasm, GermplasmListData>> listDataItems;

	private List<Pair<Germplasm, List<Attribute>>> germplasmAttributes;

	@InjectMocks
	private FieldbookServiceImpl fieldbookServiceImpl;

	@Before
	public void setUp() {
		Mockito.doReturn(this.session).when(this.sessionProvider).getSession();
		Mockito.doReturn(this.query).when(this.session).createSQLQuery(Matchers.anyString());
		this.dbBroker.setSessionProvider(this.sessionProvider);
		this.germplasms = this.createGermplasms();
		this.listDataItems = this.createListDataItems();
		this.germplasmAttributes = this.createGermplasmAttributes();
	}

	@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
	@Test
	public void testSaveNurseryAdvanceGermplasmListSuccess() {
		final GermplasmList germplasmList = this.createGermplasmlist();
		final Integer out =
				this.fieldbookServiceImpl.saveNurseryAdvanceGermplasmList(this.germplasms, this.listDataItems, germplasmList,
						this.germplasmAttributes);
		Assert.assertEquals("List Id should be 1", (Integer) 1, out);

		// Make sure a call to save various things occur.
		Mockito.verify(this.session).save(germplasmList);
		Mockito.verify(this.session).save(this.listDataItems.get(0).getLeft());
		Mockito.verify(this.session).save(this.germplasms.get(0).getLeft());
		Mockito.verify(this.session).save(this.germplasms.get(0).getRight().get(0));
		Mockito.verify(this.session).save(this.germplasmAttributes.get(0).getLeft());
	}

	@Test
	public void testSaveGermplasmListSuccess() {
		final GermplasmList germplasmList = this.createGermplasmlist();
		final Integer out = this.fieldbookServiceImpl.saveGermplasmList(this.listDataItems, germplasmList);
		Assert.assertEquals("List Id should be 1", (Integer) 1, out);
	}

	private List<Pair<Germplasm, List<Name>>> createGermplasms() {
		final List<Pair<Germplasm, List<Name>>> germplasms = new ArrayList<>();
		final Name name = new Name();
		name.setNid(1);
		final Germplasm germplasm = this.createGermplasm();
		final List<Name> names = Arrays.asList(name);
		germplasms.add(new ImmutablePair<Germplasm, List<Name>>(germplasm, names));
		return germplasms;
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(1);
		final Name preferredName = new Name();
		preferredName.setNval("1005");
		germplasm.setPreferredName(preferredName);
		return germplasm;
	}

	private List<Pair<Germplasm, GermplasmListData>> createListDataItems() {
		final List<Pair<Germplasm, GermplasmListData>> listDataItems = new ArrayList<>();
		final Germplasm germplasm = this.createGermplasm();
		final GermplasmListData listData = new GermplasmListData();
		listDataItems.add(new ImmutablePair<Germplasm, GermplasmListData>(germplasm, listData));
		return listDataItems;
	}

	private List<Pair<Germplasm, List<Attribute>>> createGermplasmAttributes() {
		List<Pair<Germplasm, List<Attribute>>> attrs = new ArrayList<>();
		final Germplasm germplasm = this.createGermplasm();
		final Attribute attribute = new Attribute();
		attribute.setAval("Plot Code");
		attribute.setTypeId(1552);
		attrs.add(new ImmutablePair<Germplasm, List<Attribute>>(germplasm, Lists.newArrayList(attribute)));
		return attrs;
	}

	private GermplasmList createGermplasmlist() {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(1);
		return germplasmList;
	}
}
