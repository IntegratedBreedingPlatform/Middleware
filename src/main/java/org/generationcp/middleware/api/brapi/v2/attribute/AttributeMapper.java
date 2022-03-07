package org.generationcp.middleware.api.brapi.v2.attribute;

import org.generationcp.middleware.service.api.study.VariableDTO;

import java.util.List;

public class AttributeMapper {

	public void map(final List<VariableDTO> from, final List<AttributeDTO> to) {
		for(final VariableDTO variableDTO: from) {
			final AttributeDTO attributeDTO = new AttributeDTO();
			attributeDTO.setAdditionalInfo(variableDTO.getAdditionalInfo());
			attributeDTO.setAttributeCategory(variableDTO.getTrait().getTraitClass());
			attributeDTO.setAttributeDescription(variableDTO.getDefinition());
			attributeDTO.setAttributeDbId(variableDTO.getObservationVariableDbId());
			attributeDTO.setAttributeName(variableDTO.getObservationVariableName());
			attributeDTO.setContextOfUse(variableDTO.getContextOfUse());
			attributeDTO.setDefaultValue(variableDTO.getDefaultValue());
			attributeDTO.setDocumentationURL(variableDTO.getDocumentationURL());
			attributeDTO.setExternalReferences(variableDTO.getExternalReferences());
			attributeDTO.setGrowthStage(variableDTO.getGrowthStage());
			attributeDTO.setInstitution(variableDTO.getInstitution());
			attributeDTO.setLanguage(variableDTO.getLanguage());
			attributeDTO.setMethod(variableDTO.getMethod());
			attributeDTO.setOntologyReference(variableDTO.getOntologyReference());
			attributeDTO.setScale(variableDTO.getScale());
			attributeDTO.setScientist(variableDTO.getScientist());
			attributeDTO.setStatus(variableDTO.getStatus());
			attributeDTO.setSubmissionTimestamp(variableDTO.getSubmissionTimestamp());
			attributeDTO.setSynonyms(variableDTO.getSynonyms());
			attributeDTO.setTrait(variableDTO.getTrait());
			to.add(attributeDTO);
		}
	}

}
