package org.generationcp.middleware.pojos.dms;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * http://gmod.org/wiki/Chado_Tables#Table:_cv
 * 
 * A term, class, universal or type within an ontology or controlled vocabulary. 
 * This table is also used for relations and properties. 
 * cvterms constitute nodes in the graph defined by the collection of cvterms and cvterm_relationships.
 * 
 * @author Joyce Avestro
 *
 */
@Entity
@Table(name = "cv", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name" }) })
public class CV implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "cv_id")	
	private Integer id;
	
	/**
	 * The name of the ontology. 
	 * In OBO file format, the cv.name is known as the namespace.
	 */
	@Basic(optional = false)
	@Column(name = "name", unique = true)
	private String name;
	
	/**
	 * A text description of the criteria for membership of this ontology.
	 */
	@Column(name = "definition")
	private String definition;
	
	public CV(){
		
	}
	
	public CV(Integer id){
		this.id = id;
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

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("CV [id=" + id);
    	sb.append(", name=" + name);
    	sb.append(", definition=" + definition);
    	sb.append("]");
    	
    	return sb.toString();
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
		if (!(obj instanceof CV))
			return false;
		
		CV other = (CV) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}

	
}
