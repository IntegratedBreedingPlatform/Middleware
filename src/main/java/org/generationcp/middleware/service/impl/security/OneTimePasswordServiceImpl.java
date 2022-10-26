package org.generationcp.middleware.service.impl.security;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.security.OneTimePassword;
import org.generationcp.middleware.service.api.security.OneTimePasswordDto;
import org.generationcp.middleware.service.api.security.OneTimePasswordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class OneTimePasswordServiceImpl implements OneTimePasswordService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	final Random random = new Random();

	@Value("${security.2fa.otp.expiry.interval}")
	private int expiry = 5; // In minutes

	@Value("${security.2fa.otp.length}")
	private int otpCodeLength = 5;

	public OneTimePasswordServiceImpl(final HibernateSessionProvider workbenchSessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(workbenchSessionProvider);
	}

	@Override
	public boolean isOneTimePasswordValid(final Integer otpCode) {
		final Optional<OneTimePassword> oneTimePasswordOptional =
			this.workbenchDaoFactory.getOneTimePasswordDAO().findNonExpiredOtpCode(otpCode);
		return oneTimePasswordOptional.isPresent();
	}

	@Override
	public OneTimePasswordDto createOneTimePassword() {

		final OneTimePassword oneTimePassword = new OneTimePassword();
		oneTimePassword.setOtpCode(this.createRandomOtpCode());
		oneTimePassword.setExpires(new Date(System.currentTimeMillis() + ((long) this.expiry * 60 * 1000)));

		this.workbenchDaoFactory.getOneTimePasswordDAO().save(oneTimePassword);

		final OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
		oneTimePasswordDto.setOtpCode(oneTimePassword.getOtpCode());
		oneTimePasswordDto.setExpires(oneTimePassword.getExpires());

		return oneTimePasswordDto;
	}

	private Integer createRandomOtpCode() {
		final StringBuilder oneTimePassword = new StringBuilder();
		for (int i = 0; i < this.otpCodeLength; i++) {
			final int randomNumber = this.random.nextInt(10);
			oneTimePassword.append(randomNumber);
		}
		return Integer.parseInt(oneTimePassword.toString().trim());
	}

	protected void setExpiry(final int expiry) {
		this.expiry = expiry;
	}

	protected void setOtpCodeLength(final int otpCodeLength) {
		this.otpCodeLength = otpCodeLength;
	}
}
