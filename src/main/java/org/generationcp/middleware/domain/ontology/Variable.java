package org.generationcp.middleware.domain.ontology;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.VariableType;
import org.generationcp.middleware.util.Debug;

import java.util.HashSet;
import java.util.Set;

/**
 * Extends {@link Term} The Variable with term, property, scale, method.
 *
 */
public class Variable extends Term {

    private String alias;

	/**
	 * Variable types are used to determine where in the system a variable is intended to be used. Variable types are used to restrict
     * the variable's display to only locations in the BMS that are relevant to that variable type. A variable may have multiple variable types, and if
	 * there is no variable type selected, then the variable will show up in all locations where variables are used in the BMS. Variable
	 * types replace the older concept of roles (the stored_in relationship).
	 */
    private final Set<VariableType> variableTypes = new HashSet<>();

    private Method method;
    private Property property;
    private Scale scale;

    private Boolean isFavorite;

    private String minValue;

    private String maxValue;

    private Integer observations;

    private Integer studies;

    public Variable() {
        this.setVocabularyId(CvId.VARIABLES.getId());
    }

    public Variable(org.generationcp.middleware.domain.oms.Term term) {
        super(term);
        this.setVocabularyId(CvId.VARIABLES.getId());
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

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
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

    public Integer getStudies() {
        return studies;
    }

    public void setStudies(Integer studies) {
        this.studies = studies;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "alias='" + alias + '\'' +
                ", variableTypes=" + variableTypes +
                ", property=" + property +
                ", method=" + method +
                ", scale=" + scale +
                ", isFavorite=" + isFavorite +
                ", minValue='" + minValue + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", observations=" + observations +
                ", studies=" + studies +
                "} " + super.toString();
    }

    @Override
    public void print(int indent) {
        Debug.println(indent, "Variable: ");
        super.print(indent + 3);

        if(alias != null){
            Debug.println(indent + 3, "alias:" + alias);
        }

        if(variableTypes != null){
            Debug.println(indent + 3, "Variable Types:" + variableTypes);
        }

        if(property != null){
            Debug.println(indent + 3, "property:" + property);
        }

        if(method != null){
            Debug.println(indent + 3, "method:" + method);
        }

        if(scale != null){
            Debug.println(indent + 3, "scale:" + scale);
        }

        if(isFavorite != null){
            Debug.println(indent + 3, "isFavorite:" + isFavorite);
        }

        if(minValue != null){
            Debug.println(indent + 3, "minValue:" + minValue);
        }

        if(maxValue != null){
            Debug.println(indent + 3, "Variable Types:" + maxValue);
        }

        if(observations != null){
            Debug.println(indent + 3, "observations:" + observations);
        }
    }
}
