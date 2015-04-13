package org.generationcp.middleware.manager.ontology.daoElements;


import org.generationcp.middleware.domain.oms.OntologyVariableInfo;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProgramProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;

/**
 * This class holds dao elements used for variableInfo. This is to reduce functional complexity
 */
public class VariableInfoDaoElements {

    private OntologyVariableInfo variableInfo;
    private CVTerm variableTerm;
    private CVTermRelationship methodRelation;
    private CVTermRelationship propertyRelation;
    private CVTermRelationship scaleRelation;
    private CVTermProgramProperty aliasProperty;
    private CVTermProgramProperty minValueProperty;
    private CVTermProgramProperty maxValueProperty;

    public OntologyVariableInfo getVariableInfo() {
        return variableInfo;
    }

    public void setVariableInfo(OntologyVariableInfo variableInfo) {
        this.variableInfo = variableInfo;
    }

    public CVTerm getVariableTerm() {
        return variableTerm;
    }

    public void setVariableTerm(CVTerm variableTerm) {
        this.variableTerm = variableTerm;
    }

    public CVTermRelationship getMethodRelation() {
        return methodRelation;
    }

    public void setMethodRelation(CVTermRelationship methodRelation) {
        this.methodRelation = methodRelation;
    }

    public CVTermRelationship getPropertyRelation() {
        return propertyRelation;
    }

    public void setPropertyRelation(CVTermRelationship propertyRelation) {
        this.propertyRelation = propertyRelation;
    }

    public CVTermRelationship getScaleRelation() {
        return scaleRelation;
    }

    public void setScaleRelation(CVTermRelationship scaleRelation) {
        this.scaleRelation = scaleRelation;
    }

    public CVTermProgramProperty getAliasProperty() {
        return aliasProperty;
    }

    public void setAliasProperty(CVTermProgramProperty aliasProperty) {
        this.aliasProperty = aliasProperty;
    }

    public CVTermProgramProperty getMinValueProperty() {
        return minValueProperty;
    }

    public void setMinValueProperty(CVTermProgramProperty minValueProperty) {
        this.minValueProperty = minValueProperty;
    }

    public CVTermProgramProperty getMaxValueProperty() {
        return maxValueProperty;
    }

    public void setMaxValueProperty(CVTermProgramProperty maxValueProperty) {
        this.maxValueProperty = maxValueProperty;
    }
}