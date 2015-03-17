package org.generationcp.middleware.domain.common;

/**
 * MinMax value class for storing Integer, Double or DateTime values
 */
public class MinMaxValue {

    private Object minValue;
    private Object maxValue;

    public Object getMinValue() {
        return minValue;
    }

    public void setMinValue(Object minValue) {
        this.minValue = minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Object maxValue) {
        this.maxValue = maxValue;
    }
}
