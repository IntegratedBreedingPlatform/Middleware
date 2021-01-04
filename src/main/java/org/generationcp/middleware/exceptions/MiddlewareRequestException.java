/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.exceptions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Exception for Client request errors
 */
public class MiddlewareRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Multimap<String, Object[]> errorCodeParamsMap = ArrayListMultimap.create();

	public MiddlewareRequestException(final String logMessage, final String errorCode, final Object... params) {
		super(logMessage);
		this.errorCodeParamsMap.put(errorCode, params);
	}

	public MiddlewareRequestException(final String logMessage, final Multimap<String, Object[]> errorCodeParamsMultiMap) {
		super(logMessage);
		this.errorCodeParamsMap.putAll(errorCodeParamsMultiMap);
	}

	public Multimap<String, Object[]> getErrorCodeParamsMultiMap() {
		return this.errorCodeParamsMap;
	}
}