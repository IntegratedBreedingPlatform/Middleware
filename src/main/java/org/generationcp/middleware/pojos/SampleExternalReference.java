package org.generationcp.middleware.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "external_reference_sample")
public class SampleExternalReference extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Basic(optional = false)
    @Column(name = "reference_id")
    private String referenceId;

    @Basic(optional = false)
    @Column(name = "reference_source")
    private String source;

    public SampleExternalReference() {
    }

    public SampleExternalReference(final Sample sample, final String referenceId, final String source) {
        this.sample = sample;
        this.referenceId = referenceId;
        this.source = source;
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

    public String getReferenceId() {
        return this.referenceId;
    }

    public void setReferenceId(final String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof StudyExternalReference)) {
            return false;
        }
        final SampleExternalReference castOther = (SampleExternalReference) other;
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
