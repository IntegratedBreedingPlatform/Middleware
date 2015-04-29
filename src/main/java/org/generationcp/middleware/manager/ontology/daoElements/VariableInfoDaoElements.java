package org.generationcp.middleware.manager.ontology.daoElements;


import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.VariableProgramOverrides;

import java.util.List;

/**
 * This class holds dao elements used for variableInfo. This is to reduce functional complexity
 */
public class VariableInfoDaoElements {

    private Integer variableId;
    private String programUuid;
    private CVTerm variableTerm;
    private CVTermRelationship methodRelation;
    private CVTermRelationship propertyRelation;
    private CVTermRelationship scaleRelation;
    private List<CVTermProperty> termProperties;
    private VariableProgramOverrides variableProgramOverrides;

    public Integer getVariableId() {
        return variableId;
    }

    public void setVariableId(Integer variableId) {
        this.variableId = variableId;
    }

    public String getProgramUuid() {
        return programUuid;
    }

    public void setProgramUuid(String programUuid) {
        this.programUuid = programUuid;
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

    public List<CVTermProperty> getTermProperties() {
        return termProperties;
    }

    public void setTermProperties(List<CVTermProperty> termProperties) {
        this.termProperties = termProperties;
    }

    public VariableProgramOverrides getVariableProgramOverrides() {
        return variableProgramOverrides;
    }

    public void setVariableProgramOverrides(VariableProgramOverrides variableProgramOverrides) {
        this.variableProgramOverrides = variableProgramOverrides;
    }
}