package org.generationcp.middleware.pojos;

public enum Methods {
    SINGLE_CROSS(101), BACKCROSS(107), THREE_WAY_CROSS(102), COMPLEX_CROSS(106), DOUBLE_CROSS(103);

    private final Integer methodID;

    Methods(Integer methodID) {
        this.methodID = methodID;
    }

    public Integer getMethodID() {
        return methodID;
    }
}
