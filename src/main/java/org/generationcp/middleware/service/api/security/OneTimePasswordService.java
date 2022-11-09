package org.generationcp.middleware.service.api.security;

public interface OneTimePasswordService {

	boolean isOneTimePasswordValid(Integer otpCode);

	OneTimePasswordDto createOneTimePassword();

}
