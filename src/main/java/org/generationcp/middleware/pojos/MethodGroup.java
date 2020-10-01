package org.generationcp.middleware.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MethodGroup {
	SELF_FERTILIZING("S", "Self Fertilizing"),
	CROSS_POLLINATING("O", "Cross Pollinating"),
	CLONALLY_PROPAGATING("C", "Clonally Propagating"),
	ALL_SYSTEM("G", "All System");

	private final String code;
	private final String name;

	private static final Map<String, MethodGroup> LOOKUP = new HashMap<>();

	static {
		for (final MethodGroup methodType : EnumSet.allOf(MethodGroup.class)) {
			MethodGroup.LOOKUP.put(methodType.getCode(), methodType);
		}
	}

	private MethodGroup(final String code, final String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public static MethodGroup getMethodGroup(final String code) {
		return MethodGroup.LOOKUP.get(code);
	}

	public static List<MethodGroup> getAll() {
		return Arrays.asList(MethodGroup.values());
	}
}
