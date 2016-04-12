package org.generationcp.middleware.service.api;


public interface KeySequenceRegisterService {

	int incrementAndGetNextSequence(String keyPrefix);
}
