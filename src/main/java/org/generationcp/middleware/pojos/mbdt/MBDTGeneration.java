package org.generationcp.middleware.pojos.mbdt;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */


@Entity
@Table(name = "mbdt_generations")
public class MBDTGeneration implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
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
        return generationID;
    }

    public void setGenerationID(Integer generationID) {
        this.generationID = generationID;
    }

    public String getGenerationName() {
        return generationName;
    }

    public void setGenerationName(String generationName) {
        this.generationName = generationName;
    }

    public MBDTProjectData getProject() {
        return project;
    }

    public void setProject(MBDTProjectData project) {
        this.project = project;
    }

    public List<SelectedGenotype> getSelectedGenotypes() {
        return selectedGenotypes;
    }

    public void setSelectedGenotypes(List<SelectedGenotype> selectedGenotypes) {
        this.selectedGenotypes = selectedGenotypes;
    }

    public List<SelectedMarker> getSelectedMarkers() {
        return selectedMarkers;
    }

    public void setSelectedMarkers(List<SelectedMarker> selectedMarkers) {
        this.selectedMarkers = selectedMarkers;
    }

    public Integer getGenotypeDatasetID() {
        return genotypeDatasetID;
    }

    public void setGenotypeDatasetID(Integer genotypeDatasetID) {
        this.genotypeDatasetID = genotypeDatasetID;
    }

    @Override
    public String toString() {
        return "MBDTGeneration{" +
                "generationID=" + generationID +
                ", generationName='" + generationName + '\'' +
                ", genotypeDatasetID=" + genotypeDatasetID +
                '}';
    }
}