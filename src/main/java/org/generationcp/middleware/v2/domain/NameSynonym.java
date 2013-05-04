package org.generationcp.middleware.v2.domain;

public class NameSynonym {
	
	private String name;
	  
    private NameType type;

    public NameSynonym(String name, NameType type) {
    	this.name = name;
    	this.type = type;
    }
   
	public String getName() {
		return name;
	}
    
	public NameType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof NameSynonym)) return false;
		NameSynonym other = (NameSynonym) obj;
		return name.equals(other.name) && type == other.type; 
	}
	
	@Override
	public String toString() {
		return "[" + name + ": " + type + "]";
	}
}
