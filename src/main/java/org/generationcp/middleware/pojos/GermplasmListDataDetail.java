package org.generationcp.middleware.pojos;

import org.pojomatic.Pojomatic;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "list_data_details")
public class GermplasmListDataDetail implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lrecid", nullable = false, updatable = false)
	private GermplasmListData listData;

	@Column(name = "variable_id", nullable = false, updatable = false)
	private Integer variableId;

	@Column(name = "value")
	private String value;

	@Column(name = "cvalue_id")
	private Integer categoricalValueId;

	protected GermplasmListDataDetail() {
	}

	public GermplasmListDataDetail(final GermplasmListData listData, final Integer variableId, final String value,
		final Integer categoricalValueId) {
		this.listData = listData;
		this.variableId = variableId;
		this.value = value;
		this.categoricalValueId = categoricalValueId;
	}

	public GermplasmListData getListData() {
		return listData;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public String getValue() {
		return value;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setListData(final GermplasmListData listData) {
		this.listData = listData;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
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
