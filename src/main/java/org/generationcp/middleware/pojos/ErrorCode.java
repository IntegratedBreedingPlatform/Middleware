package org.generationcp.middleware.pojos;


public enum ErrorCode {

    //Ontology errors
    NON_UNIQUE_NAME("error.name.exists")
    , NON_UNIQUE_PCM_COMBINATION("error.pcm.combination.exists")
    ;
    
    private String code;
    
    private ErrorCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return this.code;
    }
}
