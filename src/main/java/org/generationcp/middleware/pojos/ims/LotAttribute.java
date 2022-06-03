package org.generationcp.middleware.pojos.ims;

import org.generationcp.middleware.pojos.AbstractEntity;
import org.generationcp.middleware.pojos.GenericAttribute;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * POJO for atributs table.
 *
 * @author cverano
 */
@Entity
@Table(name = "ims_lot_attribute")
public class LotAttribute extends AbstractEntity implements GenericAttribute, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "aid")
	private Integer aid;

	@Basic(optional = false)
	@Column(name = "lotid")
	private Integer lotId;

	@Basic(optional = false)
	@Column(name = "atype")
	private Integer typeId;

	@Basic(optional = false)
	@Column(name = "aval")
	private String aval;

	@Column(name = "cval_id")
	private Integer cValueId;

	@Column(name = "alocn")
	private Integer locationId;

	@Column(name = "aref")
	private Integer referenceId;

	@Column(name = "adate")
	private Integer adate;

	/**
	 * Don't use it. This constructor is required by hibernate.
	 */
	public LotAttribute() {
	}

	public LotAttribute(final Integer aid) {
		this.aid = aid;
	}

	public LotAttribute(final Integer aid, final Integer lotId, final Integer typeId, final String aval, final Integer cValueId,
		final Integer locationId,
		final Integer referenceId, final Integer adate) {
		this.aid = aid;
		this.lotId = lotId;
		this.typeId = typeId;
		this.aval = aval;
		this.cValueId = cValueId;
		this.locationId = locationId;
		this.referenceId = referenceId;
		this.adate = adate;
	}

	public Integer getAid() {
		return this.aid;
	}

	public void setAid(final Integer aid) {
		this.aid = aid;
	}

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(final Integer lotId) {
		this.lotId = lotId;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(final Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getReferenceId() {
		return this.referenceId;
	}

	public void setReferenceId(final Integer referenceId) {
		this.referenceId = referenceId;
	}

	public String getAval() {
		return this.aval;
	}

	public void setAval(final String aval) {
		this.aval = aval;
	}

	public Integer getAdate() {
		return this.adate;
	}

	public void setAdate(final Integer adate) {
		this.adate = adate;
	}

	public Integer getcValueId() {
		return this.cValueId;
	}

	public void setcValueId(final Integer cValueId) {
		this.cValueId = cValueId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Attribute [aid=");
		builder.append(this.aid);
		builder.append(", lotId=");
		builder.append(this.lotId);
		builder.append(", typeId=");
		builder.append(this.typeId);
		builder.append(", createdBy=");
		builder.append(super.getCreatedBy());
		builder.append(", aval=");
		builder.append(this.aval);
		builder.append(", cValueId=");
		builder.append(this.cValueId);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", referenceId=");
		builder.append(this.referenceId);
		builder.append(", adate=");
		builder.append(this.adate);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof LotAttribute) {
			final LotAttribute param = (LotAttribute) obj;
			if (this.getAid().equals(param.getAid())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.getAid();
	}

}
