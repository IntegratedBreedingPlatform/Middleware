package org.generationcp.middleware.pojos;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scale")
public class Scale implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "scaleid")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "scname")
    private String name;

    @Basic(optional = false)
    @Column(name = "traitid")
    private Integer traitId;

    @Basic(optional = false)
    @Column(name = "sctype")
    private String type;

    public Scale() {

    }

    public Scale(Integer id) {
	super();
	this.id = id;
    }

    public Scale(Integer id, String name, Integer traitId, String type) {
	super();
	this.id = id;
	this.name = name;
	this.traitId = traitId;
	this.type = type;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getTraitId() {
	return traitId;
    }

    public void setTraitId(Integer traitId) {
	this.traitId = traitId;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return "Scale [id=" + id + ", name=" + name + ", traitId=" + traitId
		+ ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Scale other = (Scale) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

}
