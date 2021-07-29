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
	public void testCreateNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO();
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);

		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));
		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, germplasmNameTypeRequestDTO, codes);
	}

	@Test
	public void testUpdateNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO();
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);
		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));

		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, germplasmNameTypeRequestDTO, codes);

		germplasmNameTypeRequestDTO.setCode("TEST1" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setName("TEST1" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setDescription("Test Description1" + RandomStringUtils.randomAlphabetic(10));
		codes.clear();
		codes.add(germplasmNameTypeRequestDTO.getCode());

		this.germplasmNameTypeService.updateNameType(nameTypeId, germplasmNameTypeRequestDTO);
		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, germplasmNameTypeRequestDTO, codes);

	}

	@Test
	public void testDeleteNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO();
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);
		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));

		final List<GermplasmNameTypeDTO> nameTypeRequestDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
		Assert.assertThat(1, equalTo(nameTypeRequestDTOs.size()));

		this.germplasmNameTypeService.deleteNameType(nameTypeId);
		final List<GermplasmNameTypeDTO> nameTypeDelete = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
		Assert.assertThat(0, equalTo(nameTypeDelete.size()));
	}

	@Test
	public void testGetNameTypes_Pagination_Ok() {
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.getNameTypes(new PageRequest(0, 10));
		Assert.assertThat(10, equalTo(germplasmNameTypeDTOs.size()));
	}

	private void verifyAssertGermplasmNameTypeRequestDTO(final Integer nameTypeId, final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO,
		final Set<String> codes) {
		final List<GermplasmNameTypeDTO> nameTypeRequestDTOs = this.germplasmNameTypeService.filterGermplasmNameTypes(codes);
		Assert.assertThat(1, equalTo(nameTypeRequestDTOs.size()));
		Assert.assertThat(nameTypeId, equalTo(nameTypeRequestDTOs.get(0).getId()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getName(), equalTo(nameTypeRequestDTOs.get(0).getName()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getCode(), equalTo(nameTypeRequestDTOs.get(0).getCode()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getDescription(), equalTo(nameTypeRequestDTOs.get(0).getDescription()));
	}

	private GermplasmNameTypeRequestDTO buildGermplasmNameTypeRequestDTO() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = new GermplasmNameTypeRequestDTO();
		germplasmNameTypeRequestDTO.setCode("TEST" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setName("TEST" + RandomStringUtils.randomAlphabetic(10));
		germplasmNameTypeRequestDTO.setDescription("Test Description" + RandomStringUtils.randomAlphabetic(10));
		return germplasmNameTypeRequestDTO;
	}
}
