package org.generationcp.middleware.util;

import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class UserDeviceMetaDataUtil {

	UserDeviceMetaDataUtil() {

	}

	private static final String UNKNOWN = "UNKNOWN";

	public static String extractIp(final HttpServletRequest request) {
		// Extract the client IP address
		final String clientIp;
		final String clientXForwardedForIp = request.getHeader("x-forwarded-for");
		if (Objects.nonNull(clientXForwardedForIp)) {
			// The first item in clientXForwardedForIp is the client ip address
			clientIp = clientXForwardedForIp.split(",")[0].trim();
		} else {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

	public static String parseDeviceDetailsForDisplay(final String deviceDetails) {
		// Extract the device details from User-Agent header in the request
		final UserAgent userAgent = UserAgent.parseUserAgentString(deviceDetails);
		if (Objects.nonNull(userAgent)) {
			return userAgent.getBrowser()
				+ " " + userAgent.getBrowserVersion().getMajorVersion() + "."
				+ userAgent.getBrowserVersion().getMinorVersion() + " - "
				+ userAgent.getOperatingSystem().getDeviceType() + " - "
				+ userAgent.getOperatingSystem().getName();
		} else {
			return UNKNOWN;
		}
	}

}
