
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.NameDAO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

public class GermplasmNamingServiceImplTest {

	@Mock
	private GermplasmDAO germplasmDAO;

	@Mock
	private NameDAO nameDAO;

	@Mock
	private KeySequenceRegisterService keySequenceRegisterService;

	@InjectMocks
	private GermplasmNamingServiceImpl service = new GermplasmNamingServiceImpl();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testWhenGermplasmIsNotFixed() {
		final UserDefinedField nameType = new UserDefinedField();
		nameType.setFldno(41);
		nameType.setFcode("CODE1");

		Germplasm g1 = new Germplasm();
		g1.setGid(1);

		Mockito.when(this.germplasmDAO.getById(g1.getGid())).thenReturn(g1);

		GermplasmGroupNamingResult result = this.service.applyGroupName(g1.getGid(), "ABH05", nameType, 0, 0);
		Assert.assertEquals("Expected service to return with one validation message regarding germplasm not being fixed.", 1,
				result.getMessages().size());
		Assert.assertTrue("Expected service to return with validation regarding germplasm not being fixed.",
				result.getMessages().contains("Germplasm (gid: 1) is not part of a management group. Can not assign group name."));
	}

	@Test
	public void testWhenGermplasmIsFixedAndHasGroupMembers() {
		final UserDefinedField codedNameType = new UserDefinedField();
		codedNameType.setFldno(41);
		codedNameType.setFcode("CODE1");

		Integer mgid = 1;

		Germplasm g1 = new Germplasm();
		g1.setGid(1);
		g1.setMgid(mgid);

		// Setup existing preferred name
		Name g1Name = new Name();
		g1Name.setNval("g1Name");
		g1Name.setNstat(1);
		g1.getNames().add(g1Name);

		Mockito.when(this.germplasmDAO.getById(g1.getGid())).thenReturn(g1);

		Germplasm g2 = new Germplasm();
		g2.setGid(2);
		g2.setMgid(mgid);

		Germplasm g3 = new Germplasm();
		g3.setGid(3);
		g3.setMgid(mgid);

		Mockito.when(this.germplasmDAO.getManagementGroupMembers(mgid)).thenReturn(Lists.newArrayList(g1, g2, g3));

		String groupName = "ABH05";
		int nextSequence = 100;
		Mockito.when(this.keySequenceRegisterService.incrementAndGetNextSequence(groupName)).thenReturn(nextSequence);

		String expectedCodedName = groupName + nextSequence;

		GermplasmGroupNamingResult result = this.service.applyGroupName(g1.getGid(), groupName, codedNameType, 0, 0);
		Assert.assertEquals("Expected service to return with 3 messages, one per group member.", 3, result.getMessages().size());

		Assert.assertEquals("Expected germplasm g1 to have a coded name assigned as preferred name.", expectedCodedName,
				g1.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g1 to have a coded name with coded name type.", codedNameType.getFldno(),
				g1.findPreferredName().getTypeId());
		Assert.assertEquals("Expected existing preferred name of germplasm g1 to be set as non-preferred.", new Integer(0),
				g1Name.getNstat());

		Assert.assertEquals("Expected germplasm g2 to have a coded name assigned.", expectedCodedName, g2.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g2 to have a coded name with coded name type.", codedNameType.getFldno(),
				g2.findPreferredName().getTypeId());

		Assert.assertEquals("Expected germplasm g3 to have a coded name assigned.", expectedCodedName, g3.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g3 to have a coded name with coded name type.", codedNameType.getFldno(),
				g3.findPreferredName().getTypeId());
	}

	@Test
	public void testWhenGermplasmIsFixedAndHasGroupMembersWithExistingCodedNames() {
		final UserDefinedField codedNameType = new UserDefinedField();
		codedNameType.setFldno(41);
		codedNameType.setFcode("CODE1");

		Integer mgid = 1;

		Germplasm g1 = new Germplasm();
		g1.setGid(1);
		g1.setMgid(mgid);

		Mockito.when(this.germplasmDAO.getById(g1.getGid())).thenReturn(g1);

		Germplasm g2 = new Germplasm();
		g2.setGid(2);
		g2.setMgid(mgid);

		Germplasm g3 = new Germplasm();
		g3.setGid(3);
		g3.setMgid(mgid);

		// Lets setup the third member with existing coded name.
		Name g3CodedName = new Name();
		// same name type
		g3CodedName.setTypeId(codedNameType.getFldno());
		// but different name
		String existingCodedNameOfG3 = "ExistingCodedNameOfG3";
		g3CodedName.setNval(existingCodedNameOfG3);
		g3CodedName.setNstat(1);
		g3.getNames().add(g3CodedName);

		Mockito.when(this.germplasmDAO.getManagementGroupMembers(mgid)).thenReturn(Lists.newArrayList(g1, g2, g3));

		String groupName = "ABH05";
		int nextSequence = 100;
		Mockito.when(this.keySequenceRegisterService.incrementAndGetNextSequence(groupName)).thenReturn(nextSequence);

		String expectedCodedName = groupName + nextSequence;

		GermplasmGroupNamingResult result = this.service.applyGroupName(g1.getGid(), groupName, codedNameType, 0, 0);
		Assert.assertEquals("Expected service to return with 3 messages, one per group member.", 3, result.getMessages().size());

		Assert.assertEquals("Expected germplasm g1 to have a coded name assigned as preferred name.", expectedCodedName,
				g1.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g1 to have a coded name with coded name type.", codedNameType.getFldno(),
				g1.findPreferredName().getTypeId());

		Assert.assertEquals("Expected germplasm g2 to have a coded name assigned.", expectedCodedName, g2.findPreferredName().getNval());
		Assert.assertEquals("Expected germplasm g2 to have a coded name with coded name type.", codedNameType.getFldno(),
				g2.findPreferredName().getTypeId());

		Assert.assertEquals("Expected existing coded name of g3 to be retained.", existingCodedNameOfG3, g3.findPreferredName().getNval());
		Assert.assertTrue(
				"Expected service to return with validation regarding germplasm g3 not assigned given name because it already has one with same type.",
				result.getMessages().contains(
						"Germplasm (gid: 3) already has existing name: ExistingCodedNameOfG3 of type: CODE1. Supplied name ABH05100 was not added."));
	}

}
