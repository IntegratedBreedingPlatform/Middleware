package org.generationcp.middleware.domain.mbdt;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 5/22/2014
 * Time: 9:23 AM
 */
public enum SelectedGenotypeEnum {
    SA,
    SD,
    SR;

    public boolean isParentType() {
        return (this.name().equals("SD") || this.name().equals("SR"));
    }
}
