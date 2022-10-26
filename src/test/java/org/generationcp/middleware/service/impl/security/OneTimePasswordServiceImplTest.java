package org.generationcp.middleware.service.impl.security;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.service.api.security.OneTimePasswordDto;
import org.generationcp.middleware.service.api.security.OneTimePasswordService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OneTimePasswordServiceImplTest extends IntegrationTestBase {

	@Autowired
	private OneTimePasswordService oneTimePasswordService;

	@Test
	public void testCreateOneTimePassword() {
		final OneTimePasswordDto oneTimePasswordDto = this.oneTimePasswordService.createOneTimePassword();
		Assert.assertNotNull(oneTimePasswordDto.getOtpCode());
		Assert.assertNotNull(oneTimePasswordDto.getExpires());
	}

	@Test
	public void testIsOneTimePasswordValid_TokenNotYetExpired() {
		final OneTimePasswordDto oneTimePasswordDto = this.oneTimePasswordService.createOneTimePassword();
		Assert.assertTrue(this.oneTimePasswordService.isOneTimePasswordValid(oneTimePasswordDto.getOtpCode()));
	}

	@Test
	public void testIsOneTimePasswordValid_TokenAlreadyExpired() {
		final OneTimePasswordServiceImpl oneTimePasswordServiceTemp = new OneTimePasswordServiceImpl(this.workbenchSessionProvider);
		// Override expiry time to 0 so that OTP created is immediately expired
		oneTimePasswordServiceTemp.setExpiry(0);
		final OneTimePasswordDto oneTimePasswordDto = oneTimePasswordServiceTemp.createOneTimePassword();
		Assert.assertFalse(oneTimePasswordServiceTemp.isOneTimePasswordValid(oneTimePasswordDto.getOtpCode()));
	}

}
