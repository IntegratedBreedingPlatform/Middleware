package org.generationcp.middleware.api.nametype;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.DaoFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;

public class GermplasmNameTypeServiceImplIntegrationTest extends IntegrationTestBase {

	private DaoFactory daoFactory;

	@Autowired
	GermplasmNameTypeService germplasmNameTypeService;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateNameType(){
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = new GermplasmNameTypeRequestDTO();
		germplasmNameTypeRequestDTO.setName("TEST" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setCode("TEST" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setDescription("Test Description" + RandomStringUtils.randomAlphabetic(10));
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);
		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));
		final List<GermplasmNameTypeDTO> nameTypeRequestDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
		Assert.assertThat(1, equalTo(nameTypeRequestDTOs.size()));
		Assert.assertThat(nameTypeId, equalTo(nameTypeRequestDTOs.get(0).getId()));
	}

	@Test
	public void testGetNameTypes() {
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.getNameTypes(new PageRequest(0, 10));
		Assert.assertThat(10, equalTo(germplasmNameTypeDTOs.size()));
	}
}
