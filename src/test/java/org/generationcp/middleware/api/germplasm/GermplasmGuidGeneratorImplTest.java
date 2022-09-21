package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class GermplasmGuidGeneratorImplTest {

	@Test
	public void testGenerateGermplasmUUIDUseUUIDTrue() {
		final Germplasm germplasm = this.createGermplasm();
		final CropType cropType = new CropType();
		cropType.setUseUUID(true);
		GermplasmGuidGenerator.generateGermplasmGuids(cropType, Arrays.asList(germplasm));
		Assert.assertEquals(36, germplasm.getGermplasmUUID().length());
	}

	@Test
	public void testGenerateGermplasmUUIDUseUUIDFalse() {
		final Germplasm germplasm = this.createGermplasm();
		final CropType cropType = new CropType();
		cropType.setUseUUID(false);
		cropType.setPlotCodePrefix("AXDG");
		GermplasmGuidGenerator.generateGermplasmGuids(cropType, Arrays.asList(germplasm));
		Assert.assertTrue(germplasm.getGermplasmUUID().startsWith(cropType.getPlotCodePrefix() + "G"));
		Assert.assertEquals(13, germplasm.getGermplasmUUID().length());
	}

	private Germplasm createGermplasm() {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(1166066);
		germplasm.setMethod(new Method(31));
		germplasm.setGnpgs(-1);
		germplasm.setGrplce(0);
		germplasm.setGpid1(0);
		germplasm.setGpid2(0);
		germplasm.setLgid(0);
		germplasm.setLocationId(0);
		germplasm.setGdate(20180206);
		germplasm.setReferenceId(0);
		return germplasm;
	}

}
