package org.generationcp.middleware.domain.mbdt;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 5/22/2014
 * Time: 9:23 AM
 */
public enum SelectedGenotypeEnum {
    ACCESSION("SA"),
    DONOR("SD"),
    RECURRENT("SR");

    private String dbValue;

    SelectedGenotypeEnum(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }
}
