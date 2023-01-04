
package org.generationcp.middleware.pojos;

public enum ErrorCode {

	// Ontology errors
	NON_UNIQUE_NAME("error.name.exists"), NON_UNIQUE_PCM_COMBINATION("error.pcm.combination.exists"), ONTOLOGY_FROM_CENTRAL_UPDATE(
			"error.ontology.from.central.update"), ONTOLOGY_FROM_CENTRAL_DELETE("error.ontology.from.central.delete"), ONTOLOGY_HAS_LINKED_VARIABLE(
			"error.ontology.has.linked.variable"), ONTOLOGY_HAS_IS_A_RELATIONSHIP("error.ontology.has.trait.class"), ONTOLOGY_HAS_LINKED_PROPERTY(
			"error.ontology.has.linked.property"), STUDY_FORMAT_INVALID("error.review.study.summary.format.invalid"), ENTITY_NOT_FOUND(
			"entity.does.not.exist"), DATA_PROVIDER_FAILED("hibernate.query.execute.exception"), INVALID_METHOD_USAGE(
			"middleware.invalid.method.usage"), ERROR_OBSOLETE_VARIABLES("BV_UPLOAD_ERROR_OBSOLETE_VARIABLES");

	private String code;

	private ErrorCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
