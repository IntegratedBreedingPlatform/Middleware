package org.generationcp.middleware.service.api.study.germplasm.source;

import org.generationcp.middleware.pojos.GermplasmStudySourceType;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class GermplasmStudySourceInput {

    private Integer gid;
    private Integer studyId;
    private Integer observationUnitId;
    private GermplasmStudySourceType type;

    public GermplasmStudySourceInput(final Integer gid, final Integer studyId, final Integer observationUnitId, final GermplasmStudySourceType type) {
        this.gid = gid;
        this.studyId = studyId;
        this.observationUnitId = observationUnitId;
        this.type = type;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    public Integer getObservationUnitId() {
        return observationUnitId;
    }

    public void setObservationUnitId(Integer observationUnitId) {
        this.observationUnitId = observationUnitId;
    }

    public GermplasmStudySourceType getType() {
        return type;
    }

    public void setType(GermplasmStudySourceType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public String toString() {
        return Pojomatic.toString(this);
    }

    @Override
    public boolean equals(final Object o) {
        return Pojomatic.equals(this, o);
    }
}
