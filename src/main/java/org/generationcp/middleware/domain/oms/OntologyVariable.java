package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.domain.common.MinMaxValue;

import java.util.Set;

/**
 * The Variable with term, property, scale, method.
 *
 */
public class OntologyVariable {

    private Term term;

    private String alias;

    private Set<VariableType> variableTypes;

    private Property property;
    private Method method;
    private Scale scale;

    private boolean isFavorite;

    /**
     * Expected range tells you preferred values from scale
     */
    private MinMaxValue expectedRange;

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<VariableType> getVariableTypes() {
        return variableTypes;
    }

    public void setVariableTypes(Set<VariableType> variableTypes) {
        this.variableTypes = variableTypes;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public MinMaxValue getExpectedRange() {
        return expectedRange;
    }

    public void setExpectedRange(MinMaxValue expectedRange) {
        this.expectedRange = expectedRange;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
