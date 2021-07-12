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
	private GermplasmNameTypeService germplasmNameTypeService;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateNameType_Ok(){
		final String name = "TEST" + RandomStringUtils.randomAlphabetic(10);
		final String code = "TEST" + RandomStringUtils.randomAlphabetic(10).toUpperCase();
		final String description = "Test Description" + RandomStringUtils.randomAlphabetic(10);

		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = new GermplasmNameTypeRequestDTO();
		germplasmNameTypeRequestDTO.setName(name);
		germplasmNameTypeRequestDTO.setCode(code);
		germplasmNameTypeRequestDTO.setDescription(description);
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);
		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));
		final List<GermplasmNameTypeDTO> nameTypeRequestDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
		Assert.assertThat(1, equalTo(nameTypeRequestDTOs.size()));
		Assert.assertThat(nameTypeId, equalTo(nameTypeRequestDTOs.get(0).getId()));
		Assert.assertThat(name, equalTo(nameTypeRequestDTOs.get(0).getName()));
		Assert.assertThat(code, equalTo(nameTypeRequestDTOs.get(0).getCode()));
		Assert.assertThat(description, equalTo(nameTypeRequestDTOs.get(0).getDescription()));
	}

	@Test
	public void testGetNameTypes_Pagination() {
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.getNameTypes(new PageRequest(0, 10));
		Assert.assertThat(10, equalTo(germplasmNameTypeDTOs.size()));
	}
}
