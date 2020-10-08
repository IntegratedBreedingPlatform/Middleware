package org.generationcp.middleware.domain.inventory.manager;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotMergeRequestDto {

    private SearchCompositeDto<Integer, String> searchComposite;
    private String lotUUIDToKeep;

    public SearchCompositeDto<Integer, String> getSearchComposite() {
        return searchComposite;
    }

    public void setSearchComposite(SearchCompositeDto<Integer, String> searchComposite) {
        this.searchComposite = searchComposite;
    }

    public String getLotUUIDToKeep() {
        return lotUUIDToKeep;
    }

    public void setLotUUIDToKeep(String lotUUIDToKeep) {
        this.lotUUIDToKeep = lotUUIDToKeep;
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
