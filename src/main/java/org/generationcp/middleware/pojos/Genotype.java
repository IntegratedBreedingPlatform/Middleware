package org.generationcp.middleware.pojos;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.oms.CVTerm;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "genotype")
public class Genotype extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variabe_id")
    private CVTerm variable;

    @Basic(optional = false)
    @Column(name = "value")
    private String value;

    public Genotype() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Sample getSample() {
        return this.sample;
    }

    public void setSample(final Sample sample) {
        this.sample = sample;
    }

    public CVTerm getVariable() {
        return this.variable;
    }

    public void setVariable(final CVTerm variable) {
        this.variable = variable;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Genotype)) {
            return false;
        }
        final Genotype castOther = (Genotype) other;
        return new EqualsBuilder().append(this.id, castOther.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).hashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
