package org.generationcp.middleware.api.study;

import com.fasterxml.jackson.annotation.JsonView;
import org.generationcp.middleware.domain.labelprinting.PresetDTO;
import org.pojomatic.Pojomatic;

import java.util.List;

public class AttributesPropagationPresetDTO extends PresetDTO {

    @JsonView(PresetDTO.View.Configuration.class)
    private List<Integer> selectedDescriptorIds;

    public List<Integer> getSelectedDescriptorIds() {
        return selectedDescriptorIds;
    }

    public void setSelectedDescriptorIds(List<Integer> selectedDescriptorIds) {
        this.selectedDescriptorIds = selectedDescriptorIds;
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
