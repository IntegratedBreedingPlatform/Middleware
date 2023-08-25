package org.generationcp.middleware.api.germplasm.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Set;

@AutoProperty
public class GermplasmNameSearchRequest {

    private Set<Integer> gids;

    public Set<Integer> getGids() {
        return gids;
    }

    public void setGids(Set<Integer> gids) {
        this.gids = gids;
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
