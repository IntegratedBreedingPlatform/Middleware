
package org.generationcp.middleware.pojos.ims;

import org.generationcp.middleware.exceptions.MiddlewareException;

import java.util.stream.Stream;

public enum LotStatus {

	ACTIVE(0), CLOSED(1);

	private int status;

	LotStatus(int status) {
		this.status = status;
	}

	public int getIntValue() {
		return this.status;
	}

	public static int getIntValueByName(final String name) {
		return getValueByName(name).getIntValue();
	}

	public static LotStatus getValueByName(final String name) {
		return Stream.of(LotStatus.values())
			.filter(lotStatus -> lotStatus.name().equals(name))
			.findFirst()
			.orElseThrow(() -> new MiddlewareException("Enum value " + name + " not exists."));
	}

}
