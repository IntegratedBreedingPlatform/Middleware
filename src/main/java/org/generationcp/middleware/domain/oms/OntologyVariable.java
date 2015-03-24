package org.generationcp.middleware.domain.oms;

import org.generationcp.middleware.domain.common.MinMaxValue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The Variable with term, property, scale, method.
 *
 */
public class OntologyVariable {

    private Term term;

    private String alias;

	/**
	 * Variable types are used to determine where in the system a variable is intended to be used. Variable types are used to restrict
     * the variable's display to only locations in the BMS that are relevant to that variable type. A variable may have multiple variable types, and if
	 * there is no variable type selected, then the variable will show up in all locations where variables are used in the BMS. Variable
	 * types replace the older concept of roles (the stored_in relationship).
	 */
    private final Set<VariableType> variableTypes = new HashSet<>();

    private Property property;
    private Method method;
    private Scale scale;

    private Boolean isFavorite;

    private String minValue;

    private String maxValue;

    private Date dateCreated;

    private Date dateLastModified;

    private Integer observations;

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

    public void addVariableType(VariableType type) {
		this.variableTypes.add(type);
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(Date dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public Integer getObservations() {
        return observations;
    }

    public void setObservations(Integer observations) {
        this.observations = observations;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
