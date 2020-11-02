package org.generationcp.middleware.api.breedingmethod;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.ProgramFavoriteTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BreedingMethodServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private BreedingMethodServiceImpl breedingMethodService;

	private ProgramFavoriteTestDataInitializer programFavoriteTestDataInitializer;

	@Before
	public void setUp() {
		this.breedingMethodService = new BreedingMethodServiceImpl(this.sessionProvder);
		this.programFavoriteTestDataInitializer = new ProgramFavoriteTestDataInitializer();
	}

	@Test
	public void testGetBreedingMethodsWithoutProgram_Ok() {
		final List<Method> methodsWithoutProgram = this.germplasmDataManager.getMethodsByUniqueID(null);

		//Should get all breeding methods without program
		final List<BreedingMethodDTO> actualMethodsWithoutProgram = this.breedingMethodService.getBreedingMethods(null, null, false);
		assertNotNull(actualMethodsWithoutProgram);
		assertThat(actualMethodsWithoutProgram, hasSize(methodsWithoutProgram.size()));
	}

	@Test
	public void testGetBreedingMethodsFilteredByFavorites_Ok() {

		final List<Method> allMethods = this.germplasmDataManager.getAllMethods();
		assertNotNull(allMethods);
		assertTrue(allMethods.size() > 1);

		//Create a favorite breeding method
		final Method favoriteBreedingMethod = allMethods.get(0);
		final String programUUID = UUID.randomUUID().toString();
		final ProgramFavorite
			programFavorite = this.programFavoriteTestDataInitializer.createProgramFavorite(favoriteBreedingMethod.getMid(), programUUID);
		this.germplasmDataManager.saveProgramFavorite(programFavorite);

		//Should get only the favorite breeding method
		final List<BreedingMethodDTO> favoriteBreedingMethods = this.breedingMethodService.getBreedingMethods(programUUID, null, true);
		assertNotNull(favoriteBreedingMethods);
		assertThat(favoriteBreedingMethods, hasSize(1));
		assertThat(favoriteBreedingMethods.get(0).getCode(), is(favoriteBreedingMethod.getMcode()));
	}

	@Test
	public void testGetBreedingMethodsByProgramUUID_Ok() {
		final String newMethodCode = "NEWMETHO";
		final String programUUID = UUID.randomUUID().toString();
		final List<Method> methodsByUniqueID = this.germplasmDataManager.getMethodsByUniqueID(programUUID);
		assertNotNull(methodsByUniqueID);
		assertTrue(methodsByUniqueID.size() > 1);
		assertThat(methodsByUniqueID, not(hasItem(hasProperty("code", is(newMethodCode)))));

		final Method newMethod = new Method(null, "NEW", "S", newMethodCode, "New Method", "New Method", 0, 0, 0, 0, 0, 0, 0, 0, programUUID);
		this.germplasmDataManager.addMethod(newMethod);

		//Should get all methods without program and also the method previously created
		final List<BreedingMethodDTO> favoriteBreedingMethods = this.breedingMethodService.getBreedingMethods(programUUID, null, false);
		assertNotNull(favoriteBreedingMethods);
		assertThat(favoriteBreedingMethods.size(), is(methodsByUniqueID.size() + 1));
		assertThat(favoriteBreedingMethods, hasItem(hasProperty("code", is(newMethodCode))));
	}

	//TODO Add test for codes

}
