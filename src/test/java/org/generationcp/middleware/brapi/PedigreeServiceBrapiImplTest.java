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
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
	public void testSearchPedigreeNodes() {
		final Method derMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);
		final Method crossMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Germplasm germplasm_C = this.createGermplasm("C", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_B = this.createGermplasm("B", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_A = this.createGermplasm("A", crossMethod, null, 0, germplasm_B.getGid(), germplasm_C.getGid());
		final GermplasmExternalReference externalReference = this.createGermplasmExternalReference(germplasm_A);

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		// Include full pedigree tree
		pedigreeNodeSearchRequest.setIncludeFullTree(false);
		pedigreeNodeSearchRequest.setIncludeParents(true);
		pedigreeNodeSearchRequest.setGermplasmDbIds(Arrays.asList(germplasm_A.getGermplasmUUID()));
		final List<PedigreeNodeDTO> result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		Assert.assertEquals(1, result.size());

		final PedigreeNodeDTO pedigreeNodeDTO = result.get(0);
		Assert.assertEquals(germplasm_A.getGermplasmUUID(), pedigreeNodeDTO.getGermplasmDbId());
		Assert.assertEquals("A", pedigreeNodeDTO.getGermplasmName());
		Assert.assertEquals("A", pedigreeNodeDTO.getDefaultDisplayName());
		Assert.assertNotNull(pedigreeNodeDTO.getGermplasmPUI());
		Assert.assertEquals(String.valueOf(crossMethod.getMid()), pedigreeNodeDTO.getBreedingMethodDbId());
		Assert.assertEquals(crossMethod.getMname(), pedigreeNodeDTO.getBreedingMethodName());
		Assert.assertEquals("A", pedigreeNodeDTO.getPedigreeString());
		Assert.assertEquals(2020, pedigreeNodeDTO.getCrossingYear().intValue());
		Assert.assertFalse(pedigreeNodeDTO.getParents().isEmpty());
		Assert.assertEquals(externalReference.getReferenceId(), pedigreeNodeDTO.getExternalReferences().get(0).getReferenceID());
		Assert.assertEquals(externalReference.getSource(), pedigreeNodeDTO.getExternalReferences().get(0).getReferenceSource());

	}

	@Test
	public void testSearchPedigreeNodes_IncludeFullTree() {
		final Method polyCrossMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 0);
		final Method crossMethod = this.createBreedingMethod(MethodType.GENERATIVE.getCode(), 2);
		final Method derMethod = this.createBreedingMethod(MethodType.DERIVATIVE.getCode(), -1);

		final Germplasm germplasm_L = this.createGermplasm("L", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_K = this.createGermplasm("K", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_J = this.createGermplasm("J", derMethod, null, -1, germplasm_K.getGid(), germplasm_K.getGid());
		final Germplasm germplasm_I = this.createGermplasm("I", derMethod, null, -1, germplasm_K.getGid(), germplasm_J.getGid());
		final Germplasm germplasm_H = this.createGermplasm("H", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_G = this.createGermplasm("G", crossMethod, null, 0, 0, 0);
		final Germplasm germplasm_F = this.createGermplasm("F", crossMethod, null, 0, 0, 0);
		final Germplasm germplasm_E = this.createGermplasm("E", crossMethod, null, 0, 0, 0);
		final Germplasm germplasm_D = this.createGermplasm("D", crossMethod, null, 0, 0, 0);
		final Germplasm germplasm_C = this.createGermplasm("C", crossMethod, null, 4, germplasm_F.getGid(), germplasm_G.getGid());
		this.addProgenitors(germplasm_C, Arrays.asList(germplasm_H, germplasm_I));
		final Germplasm germplasm_B = this.createGermplasm("B", crossMethod, null, 2, germplasm_D.getGid(), germplasm_E.getGid());
		// Root Germplasm 1
		final Germplasm germplasm_A = this.createGermplasm("A", crossMethod, null, 0, germplasm_B.getGid(), germplasm_C.getGid());

		// Set the female parent of D to its own grandchild (loop)
		germplasm_D.setGpid1(germplasm_A.getGid());
		germplasm_D.setGpid2(germplasm_L.getGid());
		this.daoFactory.getGermplasmDao().update(germplasm_D);
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasm_D);

		final Germplasm germplasm_Z = this.createGermplasm("Z", crossMethod, null, 2, germplasm_B.getGid(), germplasm_C.getGid());
		final Germplasm germplasm_Y = this.createGermplasm("Y", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_X = this.createGermplasm("X", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_W = this.createGermplasm("W", derMethod, null, -1, 0, 0);
		final Germplasm germplasm_V = this.createGermplasm("V", crossMethod, null, 2, germplasm_Y.getGid(), germplasm_Z.getGid());
		final Germplasm germplasm_U = this.createGermplasm("U", crossMethod, null, 2, germplasm_W.getGid(), germplasm_X.getGid());
		// Root Germplasm 2
		final Germplasm germplasm_T = this.createGermplasm("T", crossMethod, null, 0, germplasm_U.getGid(), germplasm_V.getGid());

		List<PedigreeNodeDTO> result;
		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		// Include full pedigree tree
		pedigreeNodeSearchRequest.setIncludeFullTree(true);
		pedigreeNodeSearchRequest.setIncludeParents(true);
		pedigreeNodeSearchRequest.setGermplasmDbIds(Arrays.asList(germplasm_A.getGermplasmUUID(), germplasm_T.getGermplasmUUID()));
		/**
		 * Level 1
		 * A (root1)
		 * T (root2)
		 */
		pedigreeNodeSearchRequest.setPedigreeDepth(1);
		result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		assertEquals(2, result.size());

		/**
		 * Level 2
		 * A (root1)
		 * ├── B (female)
		 * └── C (male)
		 * T (root2)
		 * ├── U (female)
		 * └── V (male)
		 */
		pedigreeNodeSearchRequest.setPedigreeDepth(2);
		result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		assertEquals(6, result.size());

		/**
		 * Level 3
		 * A (root1)
		 * ├── B (female)
		 * │   ├── D (female)
		 * │   └── E (male)
		 * └── C (male)
		 *     ├── F (female)
		 *     ├── G (male)
		 *     ├── H (male)
		 *     └── I (male)
		 * T (root2)
		 * ├── U (female)
		 * │   ├── W (female)
		 * │   └── X (male)
		 * └── V (male)
		 *     ├── Y (female)
		 * 	   └── Z (male)
		 */
		pedigreeNodeSearchRequest.setPedigreeDepth(3);
		result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		assertEquals(16, result.size());

		/**
		 * Level 4
		 * A (root1)
		 * ├── B (female)
		 * │   ├── D (female)
		 * │   │   ├── A (female) *duplicate*
		 * │   │   └── L (male)
		 * │   └── E (male)
		 * └── C
		 *     ├── F (female)
		 *     ├── G (male)
		 *     ├── H (male)
		 *     └── I (male)
		 *         └── J (group K)
		 * T (root2)
		 * ├── U (female)
		 * │   ├── W (female)
		 * │   └── X (male)
		 * └── V (male)
		 *     ├── Y (female)
		 *     └── Z (male)
		 *         ├── B (female) *duplicate*
		 *     	   └── C (male) *duplicate*
		 */
		pedigreeNodeSearchRequest.setPedigreeDepth(4);
		result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		assertEquals(19, result.size());

		/**
		 * Level 5
		 * A (root1)
		 * ├── B (female)
		 * │   ├── D (female)
		 * │   │   ├── A (female) *duplicate*
		 * │   │   └── L (male)
		 * │   └── E (male)
		 * └── C (female)
		 *     ├── F (female)
		 * 	   ├── G (male)
		 * 	   ├── H (male)
		 * 	   └── I (male)
		 *         └── J (group K)
		 *             └── K
		 * T (root2)
		 * ├── U (female)
		 * │   ├── W (female)
		 * │   └── X (male)
		 * └── V (male)
		 *     ├── Y (female)
		 *     └── Z (male)
		 *         ├── B (female) *duplicate*
		 *         │   ├── D (female) *duplicate*
		 *         │   └── E (male) *duplicate*
		 *     	   └── C (male)
		 *     	       ├── F (female) *duplicate*
		 * 		   	   ├── G (male) *duplicate*
		 * 		  	   ├── H (male) *duplicate*
		 * 		  	   └── I (male) *duplicate*
		 *
		 */
		pedigreeNodeSearchRequest.setPedigreeDepth(5);
		result = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		assertEquals(19, result.size());

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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE.name(), femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE.name(), maleReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);
		final PedigreeNodeReferenceDTO otherParentReference = updatedPedigreeNodeDTO.getParents().get(2);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE.name(), femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE.name(), maleReference.getParentType());
		assertEquals(germplasmOtherParent.getGermplasmUUID(), otherParentReference.getGermplasmDbId());
		assertEquals(ParentType.MALE.name(), maleReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertNull(femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE.name(), femaleReference.getParentType());
		assertEquals(germplasmMale.getGermplasmUUID(), maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE.name(), maleReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO femaleReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO maleReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmFemale.getGermplasmUUID(), femaleReference.getGermplasmDbId());
		assertEquals(ParentType.FEMALE.name(), femaleReference.getParentType());
		assertNull(maleReference.getGermplasmDbId());
		assertEquals(ParentType.MALE.name(), maleReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertNull(groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertNull(immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmWithDescendants.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(germplasmDescendant2.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmGroupSource.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertNull(immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmWithDescendants.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(germplasmWithDescendants.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmParent.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(germplasmParent.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(germplasmParent.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(germplasmParent.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(parentGermplasm.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(parentGermplasm.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
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
		pedigreeNodeSearchRequest.setIncludeParents(true);
		final List<PedigreeNodeDTO> results = this.pedigreeServiceBrapi.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
		final PedigreeNodeDTO updatedPedigreeNodeDTO = results.get(0);
		assertEquals(String.valueOf(method.getMid()), updatedPedigreeNodeDTO.getBreedingMethodDbId());

		final PedigreeNodeReferenceDTO groupSourceReference = updatedPedigreeNodeDTO.getParents().get(0);
		final PedigreeNodeReferenceDTO immediateSourceReference = updatedPedigreeNodeDTO.getParents().get(1);

		assertEquals(parentGermplasm.getGermplasmUUID(), groupSourceReference.getGermplasmDbId());
		assertEquals(ParentType.POPULATION.name(), groupSourceReference.getParentType());
		assertEquals(parentGermplasm.getGermplasmUUID(), immediateSourceReference.getGermplasmDbId());
		assertEquals(ParentType.SELF.name(), immediateSourceReference.getParentType());
		assertEquals(-1, germplasm.getGnpgs().intValue());

	}

	private Germplasm createGermplasm(final String preferredName, final Method method, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2) {
		final Germplasm germplasm = this.createGermplasm(method, location, gnpgs, gpid1, gpid2, null);
		this.addName(germplasm, 1, preferredName, 0, Util.getCurrentDateAsIntegerValue(), 1);
		this.addName(germplasm, 40, RandomStringUtils.randomAlphabetic(10), 0, Util.getCurrentDateAsIntegerValue(), 0);
		this.sessionProvder.getSession().refresh(germplasm);
		return germplasm;
	}

	private Germplasm createGermplasm(final Method method, final Location location, final Integer gnpgs,
		final Integer gpid1, final Integer gpid2, final Bibref reference) {
		final Germplasm germplasm = new Germplasm(null, method.getMid(), gnpgs, gpid1, gpid2,
			0, (location == null) ? 0 : location.getLocid(), Integer.parseInt(this.CREATION_DATE), 0,
			0, 0, null, null, method);

		germplasm.setGermplasmUUID(UUID.randomUUID().toString());
		germplasm.setBibref(reference);
		this.daoFactory.getGermplasmDao().save(germplasm);
		this.daoFactory.getGermplasmDao().refresh(germplasm);
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

	private Name addName(final Germplasm germplasm, final Integer nameId, final String nameVal, final Integer locId, final Integer date,
		final int preferred) {
		final Name name = new Name(null, germplasm, nameId, preferred, nameVal, locId, date, 0);
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
		parents.add(new PedigreeNodeReferenceDTO(parent1GermplasmDbId, "", ParentType.FEMALE.name()));
		parents.add(new PedigreeNodeReferenceDTO(parent2GermplasmDbId, "", ParentType.MALE.name()));
		for (final String otherParentGermplasmDbId : otherParentGermplasmDbIds) {
			parents.add(new PedigreeNodeReferenceDTO(otherParentGermplasmDbId, "", ParentType.MALE.name()));
		}
		pedigreeNodeDTO.setParents(parents);
		return pedigreeNodeDTO;
	}

	private PedigreeNodeDTO createPedigreeNodeDTOForDerivative(final String germplasmDbId, final String parent1GermplasmDbId,
		final String parent2GermplasmDbId) {
		final PedigreeNodeDTO pedigreeNodeDTO = new PedigreeNodeDTO();
		pedigreeNodeDTO.setGermplasmDbId(germplasmDbId);

		final List<PedigreeNodeReferenceDTO> parents = new ArrayList<>();
		parents.add(new PedigreeNodeReferenceDTO(parent1GermplasmDbId, "", ParentType.POPULATION.name()));
		parents.add(new PedigreeNodeReferenceDTO(parent2GermplasmDbId, "", ParentType.SELF.name()));
		pedigreeNodeDTO.setParents(parents);
		return pedigreeNodeDTO;
	}

	private void addProgenitors(final Germplasm germplasm, final List<Germplasm> otherProgenitors) {
		if (!CollectionUtils.isEmpty(otherProgenitors)) {
			int progenitorNumber = 1;
			for (final Germplasm otherProgenitor : otherProgenitors) {
				this.addProgenitor(germplasm, otherProgenitor, progenitorNumber++);
			}
		}
	}

	private void addProgenitor(final Germplasm son, final Germplasm parent, final int progenitorNumber) {
		final Progenitor progenitor = new Progenitor(son, progenitorNumber, parent.getGid());
		this.daoFactory.getProgenitorDao().save(progenitor);
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(progenitor);
	}

	private GermplasmExternalReference createGermplasmExternalReference(final Germplasm germplasm) {
		final GermplasmExternalReference germplasmExternalReference = new GermplasmExternalReference();
		germplasmExternalReference.setGermplasm(germplasm);
		germplasmExternalReference.setSource(RandomStringUtils.randomAlphabetic(200));
		germplasmExternalReference.setReferenceId(RandomStringUtils.randomAlphabetic(500));
		this.daoFactory.getGermplasmExternalReferenceDAO().save(germplasmExternalReference);
		this.sessionProvder.getSession().flush();
		this.sessionProvder.getSession().refresh(germplasmExternalReference);
		return germplasmExternalReference;
	}

}
