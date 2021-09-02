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

	private static final String CODE = "CODE" + RandomStringUtils.randomAlphabetic(10);
	private static final String NAME = "NAME" + RandomStringUtils.randomAlphabetic(10);
	private static final String DESCRIPTION = "DESCRIPTION" + RandomStringUtils.randomAlphabetic(10);

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmNameTypeService germplasmNameTypeService;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
	}

	@Test
	public void testCreateNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //

		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);

		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));
		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, germplasmNameTypeRequestDTO, codes);
	}

	@Test
	public void testUpdateNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //

		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);
		final Set<String> codes = new HashSet<>(Arrays.asList(germplasmNameTypeRequestDTO.getCode()));

		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, germplasmNameTypeRequestDTO, codes);

		final GermplasmNameTypeRequestDTO nameTypeUpdate = this.buildGermplasmNameTypeRequestDTO(
			"TEST1" + RandomStringUtils.randomAlphabetic(10), "TEST1" + RandomStringUtils.randomAlphabetic(10), //
			"Test Description1" + RandomStringUtils.randomAlphabetic(10));

		codes.clear();
		codes.add(nameTypeUpdate.getCode());

		this.germplasmNameTypeService.updateNameType(nameTypeId, nameTypeUpdate);
		this.verifyAssertGermplasmNameTypeRequestDTO(nameTypeId, nameTypeUpdate, codes);

	}

	@Test
	public void testDeleteNameType_Ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //

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
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.searchNameTypes(new NameTypeMetadataFilterRequest(), new PageRequest(0, 10));
		Assert.assertThat(10, equalTo(germplasmNameTypeDTOs.size()));
	}

	@Test
	public void testSearchNameType_FilteredByName_ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);

		final  NameTypeMetadataFilterRequest nameTypeMetadataFilterRequest =new NameTypeMetadataFilterRequest();
		nameTypeMetadataFilterRequest.setName(germplasmNameTypeRequestDTO.getName());
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.searchNameTypes(nameTypeMetadataFilterRequest, new PageRequest(0, 10));
		Assert.assertThat(1, equalTo(germplasmNameTypeDTOs.size()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getName(), equalTo(germplasmNameTypeDTOs.get(0).getName()));
	}

	@Test
	public void testSearchNameType_FilteredByCode_ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);

		final  NameTypeMetadataFilterRequest nameTypeMetadataFilterRequest =new NameTypeMetadataFilterRequest();
		nameTypeMetadataFilterRequest.setCode(germplasmNameTypeRequestDTO.getCode());
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.searchNameTypes(nameTypeMetadataFilterRequest, new PageRequest(0, 10));
		Assert.assertThat(1, equalTo(germplasmNameTypeDTOs.size()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getCode(), equalTo(germplasmNameTypeDTOs.get(0).getCode()));
	}

	@Test
	public void testSearchNameType_FilteredByDescription_ok() {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = this.buildGermplasmNameTypeRequestDTO(
			GermplasmNameTypeServiceImplIntegrationTest.CODE, GermplasmNameTypeServiceImplIntegrationTest.NAME, //
			GermplasmNameTypeServiceImplIntegrationTest.DESCRIPTION); //
		final Integer nameTypeId = this.germplasmNameTypeService.createNameType(germplasmNameTypeRequestDTO);

		final  NameTypeMetadataFilterRequest nameTypeMetadataFilterRequest =new NameTypeMetadataFilterRequest();
		nameTypeMetadataFilterRequest.setDescription(germplasmNameTypeRequestDTO.getDescription());
		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.germplasmNameTypeService.searchNameTypes(nameTypeMetadataFilterRequest, new PageRequest(0, 10));
		Assert.assertThat(1, equalTo(germplasmNameTypeDTOs.size()));
		Assert.assertThat(germplasmNameTypeRequestDTO.getDescription(), equalTo(germplasmNameTypeDTOs.get(0).getDescription()));
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

	private GermplasmNameTypeRequestDTO buildGermplasmNameTypeRequestDTO(final String code, final String name, final String description) {
		final GermplasmNameTypeRequestDTO germplasmNameTypeRequestDTO = new GermplasmNameTypeRequestDTO();
		germplasmNameTypeRequestDTO.setCode(code);
		germplasmNameTypeRequestDTO.setName(name);
		germplasmNameTypeRequestDTO.setDescription(description);
		return germplasmNameTypeRequestDTO;
	}
}
