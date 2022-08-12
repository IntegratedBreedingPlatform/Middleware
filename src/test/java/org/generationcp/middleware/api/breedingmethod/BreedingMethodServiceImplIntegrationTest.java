package org.generationcp.middleware.api.breedingmethod;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.api.program.ProgramFavoriteService;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodClass;
import org.generationcp.middleware.pojos.MethodGroup;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BreedingMethodServiceImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private ProgramFavoriteService programFavoriteService;

	private BreedingMethodServiceImpl breedingMethodService;
	private MethodDAO methodDAO;
	private UserDefinedFieldDAO userDefinedFieldDAO;

	@Before
	public void setUp() {
		this.breedingMethodService = new BreedingMethodServiceImpl(this.sessionProvder);
		this.methodDAO = new MethodDAO(this.sessionProvder.getSession());
		this.userDefinedFieldDAO = new UserDefinedFieldDAO(this.sessionProvder.getSession());
	}

	@Test
	public void testSearchBreedingMethodsFilteredByFavorites_Ok() {

		final List<Method> allMethods = this.germplasmDataManager.getAllMethods();
		assertNotNull(allMethods);
		assertTrue(allMethods.size() > 1);

		//Create a favorite breeding method
		final Method favoriteBreedingMethod = allMethods.get(0);
		final String programUUID = UUID.randomUUID().toString();
		this.programFavoriteService.addProgramFavorites(programUUID, ProgramFavorite.FavoriteType.METHODS, ImmutableSet.of(favoriteBreedingMethod.getMid()));

		//Should get only the favorite breeding method
		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setFavoriteProgramUUID(programUUID);
		searchRequest.setFilterFavoriteProgramUUID(true);
		final List<BreedingMethodDTO> favoriteBreedingMethods = this.breedingMethodService.searchBreedingMethods(searchRequest, null, programUUID);
		assertNotNull(favoriteBreedingMethods);
		assertThat(favoriteBreedingMethods, hasSize(1));
		assertThat(favoriteBreedingMethods.get(0).getCode(), is(favoriteBreedingMethod.getMcode()));
	}

	@Test
	public void testSearchBreedingMethodsByCodes_Ok() {
		final String newMethodCode = "NEWMETHO";

		final Method newMethod = new Method(null, "NEW", "S", newMethodCode, "New Method", "New Method", 0, 0, 0, 0, 0, 0, 0, 0);
		this.methodDAO.save(newMethod);

		//Should get all methods without program and also the method previously created
		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setMethodAbbreviations(Collections.singletonList(newMethodCode));
		final List<BreedingMethodDTO> breedingMethods = this.breedingMethodService.searchBreedingMethods(searchRequest, null, null);
		assertNotNull(breedingMethods);
		assertThat(breedingMethods.size(), is(1));
		assertThat(breedingMethods, hasItem(hasProperty("code", is(newMethodCode))));
	}

	@Test
	public void testSearchBreedingMethodsByCodes_FilterBySnameTypeCode_Ok() {
		final String newMethodCode = RandomStringUtils.randomAlphabetic(5);

		final UserDefinedField nameField = new UserDefinedField();
		nameField.setFtable("NAMES");
		nameField.setFtype("NAME");
		nameField.setFcode(RandomStringUtils.randomAlphabetic(5));
		nameField.setFname("Test");
		nameField.setFfmt("-");
		nameField.setFdesc("-");
		nameField.setLfldno(0);
		nameField.setLfldno(0);
		nameField.setFuid(0);
		nameField.setFdate(20221212);
		nameField.setScaleid(0);

		this.userDefinedFieldDAO.save(nameField);

		final Method newMethod = new Method(null, "NEW", "S", newMethodCode, "New Method", "New Method", 0, 0, 0, 0, 0, 0, 0, 0);
		newMethod.setSnametype(nameField.getFldno());
		this.methodDAO.save(newMethod);

		//Should get all methods without program and also the method previously created
		final BreedingMethodSearchRequest searchRequest = new BreedingMethodSearchRequest();
		searchRequest.setSnameTypeCodes(Collections.singletonList(nameField.getFcode()));
		final List<BreedingMethodDTO> breedingMethods = this.breedingMethodService.searchBreedingMethods(searchRequest, null, null);
		assertNotNull(breedingMethods);
		assertThat(breedingMethods.size(), is(1));
		assertThat(breedingMethods, hasItem(hasProperty("code", is(newMethodCode))));
	}

	@Test
	public void testCreate_Ok() {
		final BreedingMethodNewRequest request = new BreedingMethodNewRequest();
		request.setCode(randomAlphanumeric(8));
		request.setName(randomAlphanumeric(50));
		request.setDescription(randomAlphanumeric(255));
		request.setType(MethodType.DERIVATIVE.getCode());
		request.setGroup(MethodGroup.ALL_SYSTEM.getCode());
		request.setMethodClass(MethodClass.SEED_ACQUISITION.getId());
		request.setNumberOfProgenitors(-1);
		final BreedingMethodDTO breedingMethodDTO = this.breedingMethodService.create(request);
		assertThat(breedingMethodDTO.getMid(), notNullValue());
	}

}
