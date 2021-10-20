package org.generationcp.middleware.pojos;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.domain.ontology.VariableType;

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

	@Column(name = "static_id", updatable = false)
	private Integer staticId;

	@Column(name = "name_fldno", updatable = false)
	private Integer nameFldno;

	@Column(name = "cvterm_id", updatable = false)
	private Integer cvtermId;

	@Column(name = "type_id", updatable = false)
	private Integer typeId;

	/**
	 * Default constructor required by hibernate. Do not use it!
	 */
	protected GermplasmListDataView() {
	}

	private GermplasmListDataView(final GermplasmList list) {
		this.list = list;
	}

	public Integer getId() {
		return id;
	}

	public GermplasmList getList() {
		return list;
	}

	public Integer getStaticId() {
		return staticId;
	}

	public Integer getNameFldno() {
		return nameFldno;
	}

	public Integer getCvtermId() {
		return cvtermId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public Integer getColumnId() {
		if (this.isStaticColumn()) {
			return this.staticId;
		}
		if (this.isNameColumn()) {
			return this.nameFldno;
		}
		return this.cvtermId;
	}

	public boolean isStaticColumn() {
		return this.staticId != null;
	}

	public boolean isNameColumn() {
		return this.nameFldno != null;
	}

	public boolean isVariableColumn() {
		return this.cvtermId != null;
	}

	public boolean isDescriptorColumn() {
		return this.cvtermId != null && (this.typeId.equals(VariableType.GERMPLASM_PASSPORT.getId()) ||
			this.typeId.equals(VariableType.GERMPLASM_ATTRIBUTE.getId()));
	}

	public boolean isEntryDetailColumn() {
		return this.cvtermId != null && !this.typeId.equals(VariableType.GERMPLASM_PASSPORT.getId()) &&
			!this.typeId.equals(VariableType.GERMPLASM_ATTRIBUTE.getId());
	}

	private static abstract class GermplasmListDataViewAbstractBuilder {

		private final GermplasmList germplasmList;

		private GermplasmListDataViewAbstractBuilder(final GermplasmList germplasmList) {
			this.germplasmList = germplasmList;
		}

		protected GermplasmList getGermplasmList() {
			return germplasmList;
		}

		public abstract GermplasmListDataView build();

	}


	public static class GermplasmListDataStaticViewBuilder extends GermplasmListDataViewAbstractBuilder {

		private final Integer staticId;

		public GermplasmListDataStaticViewBuilder(final GermplasmList germplasmList, final Integer staticId) {
			super(germplasmList);

			Preconditions.checkNotNull(staticId);
			this.staticId = staticId;
		}

		@Override
		public GermplasmListDataView build() {
			final GermplasmListDataView view = new GermplasmListDataView(this.getGermplasmList());
			view.staticId = this.staticId;
			return view;
		}

	}


	public static class GermplasmListDataNameViewBuilder extends GermplasmListDataViewAbstractBuilder {

		private final Integer nameFldno;

		public GermplasmListDataNameViewBuilder(final GermplasmList germplasmList, final Integer nameFldno) {
			super(germplasmList);

			Preconditions.checkNotNull(nameFldno);
			this.nameFldno = nameFldno;
		}

		@Override
		public GermplasmListDataView build() {
			final GermplasmListDataView view = new GermplasmListDataView(this.getGermplasmList());
			view.nameFldno = this.nameFldno;
			return view;
		}

	}


	public static class GermplasmListDataVariableViewBuilder extends GermplasmListDataViewAbstractBuilder {

		private final Integer cvtermId;
		private final Integer typeId;

		public GermplasmListDataVariableViewBuilder(final GermplasmList germplasmList, final Integer cvtermId, final Integer typeId) {
			super(germplasmList);

			Preconditions.checkNotNull(cvtermId);
			Preconditions.checkNotNull(typeId);
			this.cvtermId = cvtermId;
			this.typeId = typeId;
		}

		@Override
		public GermplasmListDataView build() {
			final GermplasmListDataView view = new GermplasmListDataView(this.getGermplasmList());
			view.cvtermId = this.cvtermId;
			view.typeId = this.typeId;
			return view;
		}

	}

}
