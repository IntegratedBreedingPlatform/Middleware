package org.generationcp.middleware.pojos.gdms;

public class QtlData {

	// TODO List only the required fields for GCP-1048
	
	private QtlDetails qtlDetails;
	private Qtl qtl;
	
	public QtlData(QtlDetails qtlDetails, Qtl qtl) {
		super();
		this.qtlDetails = qtlDetails;
		this.qtl = qtl;
	}
	
	public QtlDetails getQtlDetails() {
		return qtlDetails;
	}
	public void setQtlDetails(QtlDetails qtlDetails) {
		this.qtlDetails = qtlDetails;
	}
	public Qtl getQtl() {
		return qtl;
	}
	public void setQtl(Qtl qtl) {
		this.qtl = qtl;
	}
	
}
