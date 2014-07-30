package org.generationcp.middleware.pojos.mbdt;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 5/21/2014
 * Time: 12:01 PM
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
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MBDTGeneration getGeneration() {
        return generation;
    }

    public Integer getMarkerID() {
        return markerID;
    }

    @Override
    public String toString() {
        return "SelectedMarker{" +
                "id=" + id +
                ", generation=" + generation +
                ", markerID=" + markerID +
                '}';
    }
}
