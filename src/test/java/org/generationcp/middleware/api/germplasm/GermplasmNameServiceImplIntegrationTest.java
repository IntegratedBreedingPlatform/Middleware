package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.germplasm.GermplasmNameRequestDto;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmNameServiceImplIntegrationTest extends IntegrationTestBase {

	private static final String NAME = RandomStringUtils.randomAlphanumeric(5);
	private static final String DATE = "20210326";
	private static final Integer LOCATION_ID = 0;
	private static final String NAME_TYPE_CODE = "ACCNO";
	private static final Integer NAME_TYPE_ID = 1;

	private DaoFactory daoFactory;

	@Autowired
	private GermplasmNameService germplasmNameService;

	@Before
	public void setUp() {
		this.daoFactory = new DaoFactory(this.sessionProvder);

	}

	@Test
	public void testCreateGermplasmName() {
		final Germplasm germplasm = this.createGermplasm();
		final GermplasmNameRequestDto germplasmNameRequestDto = createGermplasmNameRequestDto(Boolean.TRUE);
		final Integer createNameId = this.createName(germplasm.getGid(), germplasmNameRequestDto);
		final Name name = this.daoFactory.getNameDao().getById(createNameId);
		Assert.assertEquals(createNameId, name.getNid());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME, name.getNval());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME_TYPE_ID, name.getTypeId());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.LOCATION_ID, name.getLocationId());
		Assert.assertEquals(new Integer(1), name.getNstat());
	}

	@Test
	public void testUpdateGermplasmName() {

		final Germplasm germplasm = this.createGermplasm();
		final GermplasmNameRequestDto germplasmNameRequestDto = createGermplasmNameRequestDto(Boolean.TRUE);

		final Integer createNameId = this.createName(germplasm.getGid(), germplasmNameRequestDto);
		final Name name = this.daoFactory.getNameDao().getById(createNameId);
		Assert.assertEquals(createNameId, name.getNid());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME, name.getNval());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME_TYPE_ID, name.getTypeId());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.LOCATION_ID, name.getLocationId());
		Assert.assertEquals(new Integer(1), name.getNstat());

		germplasmNameRequestDto.setName("TEST");
		this.germplasmNameService.updateName(germplasmNameRequestDto, germplasm.getGid(),name.getNid());
		Assert.assertEquals("TEST", name.getNval());
	}


	@Test
	public void testDeleteGermplasmName() {
		final Germplasm germplasm = this.createGermplasm();
		final GermplasmNameRequestDto germplasmNameRequestDto = createGermplasmNameRequestDto(Boolean.FALSE);
		final Integer createNameId = this.createName(germplasm.getGid(), germplasmNameRequestDto);
		final Name name = this.daoFactory.getNameDao().getById(createNameId);
		Assert.assertEquals(createNameId, name.getNid());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME, name.getNval());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.NAME_TYPE_ID, name.getTypeId());
		Assert.assertEquals(GermplasmNameServiceImplIntegrationTest.LOCATION_ID, name.getLocationId());
		Assert.assertEquals(new Integer(0), name.getNstat());
		this.germplasmNameService.deleteName(createNameId);
		final Name nameDeleted = this.daoFactory.getNameDao().getById(createNameId);
		Assert.assertNull(nameDeleted);
	}

	private Integer createName(final Integer gid, final GermplasmNameRequestDto germplasmNameRequestDto) {
		return this.germplasmNameService.createName(germplasmNameRequestDto, gid);
	}

	private GermplasmNameRequestDto createGermplasmNameRequestDto(final Boolean preferred) {
		final GermplasmNameRequestDto germplasmNameRequestDto = new GermplasmNameRequestDto();
		germplasmNameRequestDto.setName(GermplasmNameServiceImplIntegrationTest.NAME);
		germplasmNameRequestDto.setDate(GermplasmNameServiceImplIntegrationTest.DATE);
		germplasmNameRequestDto.setLocationId(GermplasmNameServiceImplIntegrationTest.LOCATION_ID);
		germplasmNameRequestDto.setNameTypeCode(GermplasmNameServiceImplIntegrationTest.NAME_TYPE_CODE);
		germplasmNameRequestDto.setPreferredName(preferred);
		return germplasmNameRequestDto;
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm(null, -1, 0, 0, 0, 0, 0, 0,
			0, 0, null, null, new Method(1));
		this.daoFactory.getGermplasmDao().save(germplasm);
		return germplasm;
	}
}
