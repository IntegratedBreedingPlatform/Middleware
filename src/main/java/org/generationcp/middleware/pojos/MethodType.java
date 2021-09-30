package org.generationcp.middleware.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MethodType {
	DERIVATIVE("DER", "Derivative"),
	GENERATIVE("GEN", "Generative"),
	MAINTENANCE("MAN", "Maintenance");

	private final String code;
	private final String name;
	private static final Map<String, MethodType> LOOKUP = new HashMap<>();

	static {
		for (final MethodType methodType : EnumSet.allOf(MethodType.class)) {
			MethodType.LOOKUP.put(methodType.getCode(), methodType);
		}
	}

	private MethodType(final String code, final String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public static MethodType getMethodType(final String code) {
		return MethodType.LOOKUP.get(code);
	}

	public static List<MethodType> getAll() {
		return Arrays.asList(MethodType.values());
	}

	public static List<String> getAdvancingMethodTypes(){
		return Arrays.asList(MethodType.DERIVATIVE.getCode(), MethodType.MAINTENANCE.getCode());
	}
}
