
package org.generationcp.middleware.pojos.mbdt;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

@Entity
@Table(name = "mbdt_selected_genotypes")
public class SelectedGenotype implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "sg_id")
	private Integer id;

	@ManyToOne(targetEntity = MBDTGeneration.class)
	@JoinColumn(name = "generation_id")
	private MBDTGeneration generation;

	@Column(name = "sg_type", columnDefinition = "enum('R', 'D', 'SD', 'SR')")
	@Enumerated(EnumType.STRING)
	private SelectedGenotypeEnum type;

	@Column(name = "gid")
	@Basic(optional = false)
	private Integer gid;

	public SelectedGenotype() {
	}

	public SelectedGenotype(MBDTGeneration generation, SelectedGenotypeEnum type, Integer gid) {
		this.generation = generation;
		this.type = type;
		this.gid = gid;
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

	public void setGeneration(MBDTGeneration generation) {
		this.generation = generation;
	}

	public SelectedGenotypeEnum getType() {
		return this.type;
	}

	public void setType(SelectedGenotypeEnum type) {
		this.type = type;
	}

	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	@Override
	public String toString() {
		return "SelectedGenotype{" + "id=" + this.id + ", generation=" + this.generation + ", type=" + this.type + ", gid=" + this.gid
				+ '}';
	}
}
