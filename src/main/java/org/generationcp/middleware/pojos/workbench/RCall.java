package org.generationcp.middleware.pojos.workbench;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "r_call")
public class RCall {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_call", nullable = false)
	private Integer id;

	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_package")
	private RPackage rPackage;

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_call")
	private List<RCallParameter> rCallParameters;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public RPackage getrPackage() {
		return this.rPackage;
	}

	public void setrPackage(final RPackage rPackage) {
		this.rPackage = rPackage;
	}

	public List<RCallParameter> getrCallParameters() {
		return this.rCallParameters;
	}

	public void setrCallParameters(final List<RCallParameter> rCallParameters) {
		this.rCallParameters = rCallParameters;
	}

}
