package org.generationcp.middleware.components.validator;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * This class contains all the data related to an Internationalized error message.
 * As this type of errors are constant that can be parametrized, this class contains
 * the message key and the number of parameters needed to complete the placeholders in the message.
 *
 * NOTE: At this moment the errorHandler is only considering a predifined set of parameter to
 * parametrize the error message. With hope that in the future this may change this class will not be limited by this amount of parameter.
 */
public class ErrorMessage {



	private String key;

	private List<String> parameters = Lists.newArrayList();


	public ErrorMessage(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void addParameters(String ... args) {
		for (String parameter : args) {
			this.parameters.add(parameter);
		}

	}

}
