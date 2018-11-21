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

/**
 * Exception for Client request errors
 */
public class MiddlewareRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final Object[] params;

	public MiddlewareRequestException(final String logMessage, final String errorCode, final Object... params) {
		super(logMessage);
		this.errorCode = errorCode;
		this.params = params;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public Object[] getParams() {
		return this.params;
	}
}
