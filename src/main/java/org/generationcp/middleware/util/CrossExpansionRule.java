package org.generationcp.middleware.util;

public class CrossExpansionRule {
	
	private boolean isCimmytWheat;
	private int stopLevel;
	
	
	public CrossExpansionRule(boolean isCimmytWheat, int stopLevel) {
		super();
		this.isCimmytWheat = isCimmytWheat;
		this.stopLevel = stopLevel;
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

}
