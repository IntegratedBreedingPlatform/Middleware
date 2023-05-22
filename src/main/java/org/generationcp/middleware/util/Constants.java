package org.generationcp.middleware.util;

import java.text.SimpleDateFormat;

public class Constants {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	public static final String CSP_CONFIG = "default-src 'self'; "
		+ "img-src 'self' data: https:; "
		+ "frame-src 'self' https://surveyhero.com https://www.surveyhero.com data: blob: https://surveyhero.com; "
		+ "connect-src 'self' https:; "
		+ "object-src 'none'; "
		+ "script-src 'self' https://embed-cdn.surveyhero.com https://nominatim.openstreetmap.org 'unsafe-inline' 'unsafe-eval'; "
		+ "style-src 'self' https://embed-cdn.surveyhero.com 'unsafe-inline'; ";

}
