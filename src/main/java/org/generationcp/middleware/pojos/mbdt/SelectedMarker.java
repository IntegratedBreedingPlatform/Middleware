
package org.generationcp.middleware.pojos.mbdt;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 5/21/2014 Time: 12:01 PM
 */

@Entity
@Table(name = "mbdt_selected_markers")
public class SelectedMarker implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "sm_id")
	private Integer id;

	@ManyToOne(targetEntity = MBDTGeneration.class)
	@JoinColumn(name = "generation_id")
	private MBDTGeneration generation;

	@Column(name = "marker_id")
	private Integer markerID;

	public SelectedMarker() {
	}

	public SelectedMarker(MBDTGeneration generation, Integer markerID) {
		this.generation = generation;
		this.markerID = markerID;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MBDTGeneration getGeneration() {
		return this.generation;
	}

	public Integer getMarkerID() {
		return this.markerID;
	}

	@Override
	public String toString() {
		return "SelectedMarker{" + "id=" + this.id + ", generation=" + this.generation + ", markerID=" + this.markerID + '}';
	}
}
