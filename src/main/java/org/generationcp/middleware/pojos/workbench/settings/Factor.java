package org.generationcp.middleware.pojos.workbench.settings;

import java.io.Serializable;

import org.generationcp.middleware.manager.Operation;

public class Factor  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String property;
	private String scale;
	private String method;
	private String role;
	private String datatype;
	private Integer termId;
	private String treatmentLabel;
	private Operation operation;
	private int id;
        private int storedIn;
	
	public Factor(){
		super();
	}
	
	public Factor(String name, String description, String property,
			String scale, String method, String role, String datatype, Integer termId) {
		super();
		this.name = name;
		this.description = description;
		this.property = property;
		this.scale = scale;
		this.method = method;
		this.role = role;
		this.datatype = datatype;
		this.termId = termId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	public String getTreatmentLabel() {
		return treatmentLabel;
	}

	public void setTreatmentLabel(String treatmentLabel) {
		this.treatmentLabel = treatmentLabel;
	}

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the storedIn
     */
    public int getStoredIn() {
        return storedIn;
    }

    /**
     * @param storedIn the storedIn to set
     */
    public void setStoredIn(int storedIn) {
        this.storedIn = storedIn;
    }

 }
