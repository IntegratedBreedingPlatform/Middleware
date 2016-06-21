package org.generationcp.middleware.pojos;

public enum MethodType {

	GEN("GEN"), MAN("MAN"), DER("DER");

	private String code;

	private MethodType(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
