package org.generationcp.middleware.pojos.workbench;

import org.generationcp.middleware.pojos.Method;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MethodType {
	DERIVATIVE("DER", "Derivative"),
	GENERATIVE("GEN", "Generative"),
	MAINTENANCE("MAN", "Maintenance");

	private final String code;
	private final String name;
	private static final Map<String, MethodType> LOOKUP = new HashMap<>();
	static {
		for(final MethodType methodType: EnumSet.allOf(MethodType.class)){
			MethodType.LOOKUP.put(methodType.getCode(), methodType);
		}
	}

	private MethodType(final String code, final String name){
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public static MethodType getMethodType(final String code){
		return MethodType.LOOKUP.get(code);
	}
}