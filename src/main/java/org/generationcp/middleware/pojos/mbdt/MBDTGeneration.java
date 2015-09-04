
package org.generationcp.middleware.pojos.mbdt;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

@Entity
@Table(name = "mbdt_generations")
public class MBDTGeneration implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "generation_id")
	private Integer generationID;

	@Column(name = "gname")
	private String generationName;

	@ManyToOne(targetEntity = MBDTProjectData.class)
	@JoinColumn(name = "project_id")
	private MBDTProjectData project;

	@OneToMany(targetEntity = SelectedGenotype.class, mappedBy = "generation", fetch = FetchType.LAZY)
	private List<SelectedGenotype> selectedGenotypes;

	@OneToMany(targetEntity = SelectedMarker.class, mappedBy = "generation", fetch = FetchType.LAZY)
	private List<SelectedMarker> selectedMarkers;

	@Column(name = "genotypedataset_id")
	private Integer genotypeDatasetID;

	public MBDTGeneration() {
	}

	public MBDTGeneration(String generationName, MBDTProjectData project, Integer genotypeDatasetID) {
		this.generationName = generationName;
		this.project = project;
		this.genotypeDatasetID = genotypeDatasetID;
	}

	public Integer getGenerationID() {
		return this.generationID;
	}

	public void setGenerationID(Integer generationID) {
		this.generationID = generationID;
	}

	public String getGenerationName() {
		return this.generationName;
	}

	public void setGenerationName(String generationName) {
		this.generationName = generationName;
	}

	public MBDTProjectData getProject() {
		return this.project;
	}

	public void setProject(MBDTProjectData project) {
		this.project = project;
	}

	public List<SelectedGenotype> getSelectedGenotypes() {
		return this.selectedGenotypes;
	}

	public void setSelectedGenotypes(List<SelectedGenotype> selectedGenotypes) {
		this.selectedGenotypes = selectedGenotypes;
	}

	public List<SelectedMarker> getSelectedMarkers() {
		return this.selectedMarkers;
	}

	public void setSelectedMarkers(List<SelectedMarker> selectedMarkers) {
		this.selectedMarkers = selectedMarkers;
	}

	public Integer getGenotypeDatasetID() {
		return this.genotypeDatasetID;
	}

	public void setGenotypeDatasetID(Integer genotypeDatasetID) {
		this.genotypeDatasetID = genotypeDatasetID;
	}

	@Override
	public String toString() {
		return "MBDTGeneration{" + "generationID=" + this.generationID + ", generationName='" + this.generationName + '\''
				+ ", genotypeDatasetID=" + this.genotypeDatasetID + '}';
	}
}
