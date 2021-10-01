package org.generationcp.middleware.pojos;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MethodClass {
	BULKING(TermId.BULKING_BREEDING_METHOD_CLASS.getId()),
	NON_BULKING(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId()),
	SEED_INCREASE(TermId.SEED_INCREASE_METHOD_CLASS.getId()),
	SEED_ACQUISITION(TermId.SEED_ACQUISITION_METHOD_CLASS.getId()),
	CULTIVAR_FORMATION(TermId.CULTIVAR_FORMATION_METHOD_CLASS.getId()),
	CROSSING(TermId.CROSSING_METHODS_CLASS.getId()),
	MUTATION(TermId.MUTATION_METHODS_CLASS.getId()),
	GENETIC_MODIFICATION(TermId.GENETIC_MODIFICATION_CLASS.getId()),
	CYTOGENETIC_MANIPULATION(TermId.CYTOGENETIC_MANIPULATION.getId());

	private final Integer id;

	private static final Map<Integer, MethodClass> LOOKUP = new HashMap<>();

	static {
		for (final MethodClass methodType : EnumSet.allOf(MethodClass.class)) {
			MethodClass.LOOKUP.put(methodType.getId(), methodType);
		}
	}

	MethodClass(final Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public static MethodClass getMethodClass(final Integer id) {
		return MethodClass.LOOKUP.get(id);
	}

	public static Map<MethodType, List<MethodClass>> getByMethodType() {
		final Map<MethodType, List<MethodClass>> map = new HashMap<>();
		map.put(MethodType.GENERATIVE, Arrays.asList(CROSSING, MUTATION, GENETIC_MODIFICATION, CYTOGENETIC_MANIPULATION));
		map.put(MethodType.DERIVATIVE, Arrays.asList(BULKING, NON_BULKING));
		map.put(MethodType.MAINTENANCE, Arrays.asList(SEED_INCREASE, SEED_ACQUISITION, CULTIVAR_FORMATION));
		return map;
	}

	public static List<Integer> getIds() {
		return new ArrayList<>(LOOKUP.keySet());
	}

	public static List<MethodClass> getAll() {
		return Arrays.asList(MethodClass.values());
	}
}
