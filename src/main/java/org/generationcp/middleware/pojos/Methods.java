package org.generationcp.middleware.pojos;

public enum Methods {
    SINGLE_CROSS(101, "C2W"),
    BACKCROSS(107, "C3W"),
    THREE_WAY_CROSS(102, "CDC"),
    COMPLEX_CROSS(106, "CCX"),
    DOUBLE_CROSS(103, "BCR"),
    SELECTED_POLLEN_CROSS(110, "CSP"),
    OPEN_POLLINATION_HALF_SIB(459, "PPF"),

    UNKNOWN_GENERATIVE_METHOD(1, "UGM"),
    UNKNOWN_DERIVATIVE_METHOD(31, "UGM"),

    RANDOM_MATING(112, "CRM"),
    RANDOM_MATING_POP(421, "PRM"),
    LANDRACE_CULTIVAR(253, "ALC"),
    LANDRACE_POPULATION(551, "BLP"),
    COLLECTION_POPULATION(254, "ACP"),
    COLLECTION_WILD_SPP_POPULATION(256, "AWS"),
    COLLECTION_WILD_SPP_LINE(257, "AWC"),
    COLLECTION_WEEDY_SPP_POPULATION(258, "AWL"),
    COLLECTION_WEEDY_SPP_LINE(259, "AWD"),
    OPEN_POLLINATION(422, "PPO"),
	;

    public final static Methods[] COP_CROSS_FERTILIZING_WIDE_VARIABILITY = new Methods[] {
        RANDOM_MATING,
        LANDRACE_CULTIVAR,
        LANDRACE_POPULATION,
        COLLECTION_POPULATION,
        COLLECTION_WILD_SPP_POPULATION,
        COLLECTION_WILD_SPP_LINE,
        COLLECTION_WEEDY_SPP_POPULATION,
        COLLECTION_WEEDY_SPP_LINE,
        OPEN_POLLINATION,
    };

    private final Integer methodID;
    private final String methodCode;

    Methods(final Integer methodID, final String methodCode) {
        this.methodID = methodID;
        this.methodCode = methodCode;
    }

    public Integer getMethodID() {
        return methodID;
    }

    public String getMethodCode() {
        return methodCode;
    }
}
