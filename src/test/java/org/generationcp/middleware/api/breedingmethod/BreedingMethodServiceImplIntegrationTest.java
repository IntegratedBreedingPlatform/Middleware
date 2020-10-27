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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
	public void testGetBreedingMethods_Ok() {
		long allMethodsCount = this.germplasmDataManager.countAllMethods();

		//Should get all breeding methods
		final List<BreedingMethodDTO> allBreedingMethods = this.breedingMethodService.getBreedingMethods(null, false);
		assertNotNull(allBreedingMethods);
		assertThat(allBreedingMethods, hasSize((int) allMethodsCount));

		//Create a favorite breeding method
		BreedingMethodDTO favoriteBreedingMethodDTO = allBreedingMethods.get(0);

		final Method c2WMethod = this.germplasmDataManager.getMethodByCode(favoriteBreedingMethodDTO.getCode());
		assertNotNull(c2WMethod);
		assertThat(c2WMethod.getMcode(), is(favoriteBreedingMethodDTO.getCode()));

		final String programUUID = UUID.randomUUID().toString();
		final ProgramFavorite
			programFavorite = this.programFavoriteTestDataInitializer.createProgramFavorite(c2WMethod.getMid(), programUUID);
		this.germplasmDataManager.saveProgramFavorite(programFavorite);

		//Should get only the favorite breeding method
		final List<BreedingMethodDTO> favoriteBreedingMethods = this.breedingMethodService.getBreedingMethods(programUUID, true);
		assertNotNull(favoriteBreedingMethods);
		assertThat(favoriteBreedingMethods, hasSize(1));
		assertThat(favoriteBreedingMethods.get(0).getCode(), is(favoriteBreedingMethodDTO.getCode()));
	}

}
