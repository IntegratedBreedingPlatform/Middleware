package org.generationcp.middleware.domain.inventory.manager;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class LotUpdateBalanceRequestDto {

    public LotUpdateBalanceRequestDto() {

    }

    public LotUpdateBalanceRequestDto(final String lotUUID, final Double balance, final String notes) {
        this.lotUUID = lotUUID;
        this.balance = balance;
        this.notes = notes;
    }

    private String lotUUID;

    private String storageLocationAbbr;

    private Double balance;

    private String notes;

    public String getLotUUID() {
        return this.lotUUID;
    }

    public void setLotUUID(final String lotUUID) {
        this.lotUUID = lotUUID;
    }

    public Double getBalance() {
        return this.balance;
    }

    public void setBalance(final Double balance) {
        this.balance = balance;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    public String getStorageLocationAbbr() {
        return this.storageLocationAbbr;
    }

    public void setStorageLocationAbbr(final String storageLocationAbbr) {
        this.storageLocationAbbr = storageLocationAbbr;
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
