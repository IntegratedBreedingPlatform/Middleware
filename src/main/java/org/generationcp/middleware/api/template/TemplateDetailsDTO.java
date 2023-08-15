package org.generationcp.middleware.api.template;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class TemplateDetailsDTO {

    private Integer variableId;

    private String name;

    private String type;

    public TemplateDetailsDTO() {
    }

    public TemplateDetailsDTO(final Integer variableId, final String name, final String type) {
        this.variableId = variableId;
        this.name = name;
        this.type = type;
    }
    public Integer getVariableId() {
        return this.variableId;
    }

    public void setVariableId(final Integer variableId) {
        this.variableId = variableId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }
}
