package org.generationcp.middleware.pojos;

import org.generationcp.middleware.domain.ontology.VariableType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "list_data_view")
public class GermplasmListDataView implements Serializable {

	private static final long serialVersionUID = -77142569049732583L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "listid", nullable = false, updatable = false)
	private GermplasmList list;

	@Column(name = "category", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private GermplasmListColumnCategory category;

	@Column(name = "type_id", updatable = false)
	private Integer typeId;

	@Column(name = "variable_id", nullable = false, updatable = false)
	private Integer variableId;

	/**
	 * Default constructor required by hibernate. Do not use it!
	 */
	protected GermplasmListDataView() {
	}

	public GermplasmListDataView(final GermplasmList list, final GermplasmListColumnCategory category, final Integer typeId,
		final Integer variableId) {
		this.list = list;
		this.category = category;
		this.typeId = typeId;
		this.variableId = variableId;
	}

	public Integer getId() {
		return id;
	}

	public GermplasmList getList() {
		return list;
	}

	public GermplasmListColumnCategory getCategory() {
		return category;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public boolean isStaticColumn() {
		return this.category == GermplasmListColumnCategory.STATIC;
	}

	public boolean isNameColumn() {
		return this.category == GermplasmListColumnCategory.NAMES;
	}

	public boolean isDescriptorColumn() {
		return this.category == GermplasmListColumnCategory.VARIABLE && (this.typeId.equals(VariableType.GERMPLASM_PASSPORT.getId()) ||
			this.typeId.equals(VariableType.GERMPLASM_ATTRIBUTE.getId()));
	}

	public boolean isEntryDetailColumn() {
		return this.category == GermplasmListColumnCategory.VARIABLE && !this.typeId.equals(VariableType.GERMPLASM_PASSPORT.getId()) &&
			!this.typeId.equals(VariableType.GERMPLASM_ATTRIBUTE.getId());
	}

}
