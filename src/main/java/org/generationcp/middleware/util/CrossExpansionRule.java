package org.generationcp.middleware.util;

public class CrossExpansionRule {
	
	private boolean isCimmytWheat;
	private int stopLevel;
	private int nameType;
	
	
	public CrossExpansionRule(boolean isCimmytWheat, int stopLevel, int nameType) {
		super();
		this.isCimmytWheat = isCimmytWheat;
		this.stopLevel = stopLevel;
		this.nameType = nameType;
	}
	
	public boolean isCimmytWheat() {
		return isCimmytWheat;
	}
	public void setCimmytWheat(boolean isCimmytWheat) {
		this.isCimmytWheat = isCimmytWheat;
	}
	 
	public int getStopLevel() {
		return stopLevel;
	}

	public void setStopLevel(int stopLevel) {
		this.stopLevel = stopLevel;
	}

	public int getNameType() {
		return nameType;
	}
	public void setNameType(int nameType) {
		this.nameType = nameType;
	}
	
	
}
