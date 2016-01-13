
package org.generationcp.middleware.service.pedigree;

import org.junit.Assert;
import org.junit.Test;

public class PedigreeFactoryTest {

	private static final String WHEAT_CROP = "wheat";
	private static final String MAIZE_CROP = "maize";

	@Test
	public void testisCimmytWheatReturnsTrueForCimmytProfileAndWheatCrop() {
		Assert.assertTrue("Expecting to return true when the the profile is cimmyt and crop is wheat.",
				PedigreeFactory.isCimmytWheat(PedigreeFactory.PROFILE_CIMMYT, WHEAT_CROP));
	}

	@Test
	public void testisCimmytWheatReturnsFalseForDefaultProfileAndWheatCrop() {
		Assert.assertFalse("Expecting to return false when the the profile is default and crop is wheat.",
				PedigreeFactory.isCimmytWheat(PedigreeFactory.PROFILE_DEFAULT, WHEAT_CROP));
	}

	@Test
	public void testisCimmytWheatReturnsFalseForCimmytProfileAndNonWheatCrop() {
		Assert.assertFalse("Expecting to return false when the the profile is cimmyt and crop is non wheat.",
				PedigreeFactory.isCimmytWheat(PedigreeFactory.PROFILE_CIMMYT, MAIZE_CROP));
	}

}
