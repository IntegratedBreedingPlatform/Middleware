package org.generationcp.middleware.manager;

public enum GdmsType {
	
    TYPE_SSR("SSR")
    , TYPE_SNP("SNP")
    , TYPE_DART("DArT")
    , TYPE_MAPPING("mapping")
    , TYPE_MTA("MTA")
    , TYPE_QTL("QTL")
    , TYPE_CAP("CAP")
    , TYPE_CISR("CISR")
    , TYPE_UA("UA") // Unassigned

    , DATA_TYPE_INT("int")
    , DATA_TYPE_MAP("map")
	;

	private final String value;
	
	private GdmsType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}


}
