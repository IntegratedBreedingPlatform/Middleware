package org.generationcp.middleware.v2.domain;

public enum DataSetType {

	STUDY_CONDITIONS (10060)
	, MEANS_DATA (10070)
	, SUMMARY_DATA (10080)
	, PLOT_DATA (10090)
	;
	
	private int id;
	
	private DataSetType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public static DataSetType findById(int id) {
		for (DataSetType type : DataSetType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}
}
