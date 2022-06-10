package org.generationcp.middleware.brapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.brapi.PedigreeServiceBrapi;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PedigreeServiceBrapiImplTest extends IntegrationTestBase {

	public static final String NOLOC = "NOLOC";
	private static final String CREATION_DATE = "20201212";

	private DaoFactory daoFactory;

	@Autowired
	private PedigreeServiceBrapi pedigreeServiceBrapi;

	private Integer userId;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.userId = this.findAdminUser();
	}

	@Test
	public void testUpdatePedigreeNodes_Generative_AssignKnownParents() {

		final Method method = this.createBreedingMethod("GEN", 2);
		final Germplasm germplasmFemale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), germplasmFemale.getGermplasmUUID(),
				germplasmMale.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE, femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE, maleReference.getParentType());
		assertEquals(2, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Generative_AssignKnownParents_WithOtherProgenitors() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmOtherParent = this.createGermplasm(method, null, 0, 0, 0, null);

		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), germplasmFemale.getGermplasmUUID(),
				germplasmMale.getGermplasmUUID(), germplasmOtherParent.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);
		final PedigreeNodeReferenceDTO otherParentReference = updatedPedigreeNodeDTO.getParents().get(2);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE, femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE, maleReference.getParentType());
		assertEquals(germplasmOtherParent.getGermplasmUUID(), otherParentReference.getGermplasmDbId());
		assertEquals(ParentType.MALE, maleReference.getParentType());
		assertEquals(3, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Generative_BreedingMethodMutationIsNotAllowed() {

		// Breeding method with numberOfPregenitors = 1 is a mutation method. This is not yet supported.
		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 1);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), null, null));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		assertFalse(conflictErrors.isEmpty());
		assertTrue(conflictErrors.containsKey("germplasm.update.mutation.method.is.not.supported"));

	}

	@Test
	public void testUpdatePedigreeNodes_Generative_UnknownFemaleParent() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Assign unknown female parent (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), null,
				germplasmMale.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertNull(femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE, femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE, maleReference.getParentType());
		assertEquals(2, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Generative_UnknownMaleParent() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasmFemale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmMale = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Assign unknown male parent (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), germplasmFemale.getGermplasmUUID(),
				null));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE, femaleReference.getParentType());
		assertNull(maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE, maleReference.getParentType());
		assertEquals(2, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Generative_TerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Set Unknown female and Unknown male (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForGenerative(germplasm.getGermplasmUUID(), null, null));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertNull(groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertNull(immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(0, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_ImmediateSourceShouldBelongToGroup() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, 0, 0, 0, null);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), germplasmDescendant2.getGermplasmUUID(),
				germplasmDescendant.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		Assert.assertTrue(conflictErrors.containsKey("import.germplasm.invalid.immediate.source.group"));

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_UpdateImmediateAndGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);
		final Germplasm germplasmDescendant2 = this.createGermplasm(method, null, 0, 0, 0, null);
		germplasmDescendant2.setGpid1(germplasmDescendant.getGpid1());
		germplasmDescendant2.setGpid2(germplasmDescendant.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant2);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), germplasmWithDescendants.getGermplasmUUID(),
				germplasmDescendant2.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmWithDescendants.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(germplasmDescendant2.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_UnknownImmediateSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasmGroupSource = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Set unknown immediate source (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), germplasmGroupSource.getGermplasmUUID(),
				null));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmGroupSource.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertNull(immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_UnknownGroupSource() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Create germplasm with descendants
		final Germplasm germplasmWithDescendants = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmDescendant = this.createGermplasm(method, null, 0, 0, 0, null);
		germplasmDescendant.setGpid1(germplasmWithDescendants.getGid());
		germplasmDescendant.setGpid2(germplasmWithDescendants.getGid());
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmDescendant);

		// Set Unknown group source (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), null,
				germplasmWithDescendants.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmWithDescendants.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(germplasmWithDescendants.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_UnknownGroupSource_ImmediateSourceIsGenerative() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);

		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);
		final Germplasm germplasmParent = this.createGermplasm(generativeMethod, null, 0, 0, 0, null);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmParent);

		// Set Unknown group source (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), null,
				germplasmParent.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmParent.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(germplasmParent.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_UnknownGroupSource_ImmediateSourceIsTerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		final Germplasm germplasmParent = this.createGermplasm(method, null, 0, 0, 0, null);
		this.daoFactory.getGermplasmDao().saveOrUpdate(germplasmParent);

		// Set Unknown group source (null germplasmDbId = unknown)
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), null,
				germplasmParent.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmParent.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(germplasmParent.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_GroupAndImmediateSourceAreSame_ParentIsTerminalNode() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Create a terminal node germplasm
		final Germplasm parentGermplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), parentGermplasm.getGermplasmUUID(),
				parentGermplasm.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(parentGermplasm.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(parentGermplasm.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());
	}

	@Test
	public void testUpdatePedigreeNodes_Derivative_GroupAndImmediateSourceAreSame_ParentIsGenerative() {

		final Method method = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), 2);
		final Method generativeMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm = this.createGermplasm(method, null, 0, 0, 0, null);

		// Create a generative germplasm
		final Germplasm parentGermplasm = this.createGermplasm(generativeMethod, null, 0, 0, 0, null);

		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap = new HashMap<>();
		pedigreeNodeDTOMap.put(germplasm.getGermplasmUUID(),
			this.createPedigreeNodeDTOForDerivative(germplasm.getGermplasmUUID(), parentGermplasm.getGermplasmUUID(),
				parentGermplasm.getGermplasmUUID()));

		final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
		this.pedigreeServiceBrapi.updatePedigreeNodes(pedigreeNodeDTOMap, conflictErrors);

		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm);

		assertTrue(conflictErrors.isEmpty());

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(parentGermplasm.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION, groupSourceReference.getParentType());
		assertEquals(parentGermplasm.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF, immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	private Germplasm createGermplasm(final Method method, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2) {
		return this.createGermplasm(method, location, gnpgs, gpid1, gpid2, null);
	}

	private Germplasm createGermplasm(final Method method, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Bibref reference) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, (location == null) ? 0 : location.getLocid(), Integer.parseInt(this.CREATION_DATE), 0,
			0, 0, null, null, method);

		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		germplasm.setBibref(reference);
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.sessionProvder.getSession().flush();

		assertThat(germplasm.getCreatedBy(), is(this.userId));
		assertNotNull(germplasm.getCreatedBy());
		assertNull(germplasm.getModifiedBy());
		assertNull(germplasm.getModifiedDate());

		return germplasm;
	}

	private Method createBreedingMethod(final String breedingMethodType, final int numberOfProgenitors) {
		final Method method =
			new Method(null, breedingMethodType, "G", RandomStringUtils.randomAlphanumeric(4).toUpperCase(),
				RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10), 0, numberOfProgenitors, 1, 0, 1490, 1, 0, 19980708);
		this.daoFactory.getMethodDAO().save(method);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getMethodDAO().refresh(method);
		return method;
	}

	private Name addName(final Germplasm germplasm, final Integer nameId, final String nameVal, final Integer locId, final String date,
		final int preferred) {
		final Name name = new Name(null, germplasm, nameId, preferred, nameVal, locId, Integer.valueOf(date), 0);
		this.daoFactory.getNameDao().save(name);
		this.sessionProvder.getSession().flush();
		this.daoFactory.getNameDao().refresh(name);

		assertNotNull(name.getCreatedDate());
		assertThat(name.getCreatedBy(), is(this.userId));
		assertNull(name.getModifiedDate());
		assertNull(name.getModifiedBy());

		return name;
	}

	private PedigreeNodeDTO createPedigreeNodeDTOForGenerative(final String germplasmDbId, final String parent1GermplasmDbId,
		final String parent2GermplasmDbId, final String... otherParentGermplasmDbIds) {
		final PedigreeNodeDTO pedigreeNodeDTO = new PedigreeNodeDTO();
		pedigreeNodeDTO.setGermplasmDbId(germplasmDbId);

		final List<PedigreeNodeReferenceDTO> parents = new ArrayList<>();
		parents.add(new PedigreeNodeReferenceDTO(parent1GermplasmDbId, "", ParentType.FEMALE));
		parents.add(new PedigreeNodeReferenceDTO(parent2GermplasmDbId, "", ParentType.MALE));
		for (final String otherParentGermplasmDbId : otherParentGermplasmDbIds) {
			parents.add(new PedigreeNodeReferenceDTO(otherParentGermplasmDbId, "", ParentType.MALE));
		}
		pedigreeNodeDTO.setParents(parents);
		return pedigreeNodeDTO;
	}

	private PedigreeNodeDTO createPedigreeNodeDTOForDerivative(final String germplasmDbId, final String parent1GermplasmDbId,
		final String parent2GermplasmDbId) {
		final PedigreeNodeDTO pedigreeNodeDTO = new PedigreeNodeDTO();
		pedigreeNodeDTO.setGermplasmDbId(germplasmDbId);

		final List<PedigreeNodeReferenceDTO> parents = new ArrayList<>();
		parents.add(new PedigreeNodeReferenceDTO(parent1GermplasmDbId, "", ParentType.POPULATION));
		parents.add(new PedigreeNodeReferenceDTO(parent2GermplasmDbId, "", ParentType.SELF));
		pedigreeNodeDTO.setParents(parents);
		return pedigreeNodeDTO;
	}

}
