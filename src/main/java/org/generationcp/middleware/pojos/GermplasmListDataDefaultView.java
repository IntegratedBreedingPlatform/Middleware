package org.generationcp.middleware.pojos;

import org.generationcp.middleware.api.germplasmlist.data.GermplasmListStaticColumns;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "list_data_default_view")
public class GermplasmListDataDefaultView implements Serializable {

	private static final long serialVersionUID = 3901973016538761056L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name", nullable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private GermplasmListStaticColumns name;

	/**
	 * Default constructor required by hibernate. Do not use it!
	 */
	protected GermplasmListDataDefaultView() {
	}

	public GermplasmListDataDefaultView(final GermplasmListStaticColumns name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public GermplasmListStaticColumns getName() {
		return name;
	}

}
