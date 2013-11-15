package org.generationcp.middleware.pojos;


public enum ErrorCode {

    //Ontology errors
    NON_UNIQUE_NAME("error.name.exists")
    , NON_UNIQUE_PCM_COMBINATION("error.pcm.combination.exists")
    , ONTOLOGY_FROM_CENTRAL_UPDATE("error.ontology.from.central.update")
    , ONTOLOGY_FROM_CENTRAL_DELETE("error.ontology.from.central.delete")
    , ONTOLOGY_HAS_LINKED_VARIABLE("error.ontology.has.linked.variable")
    ;
    
    private String code;
    
    private ErrorCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
