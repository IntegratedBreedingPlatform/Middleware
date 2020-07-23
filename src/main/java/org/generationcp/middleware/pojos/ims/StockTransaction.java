
package org.generationcp.middleware.pojos.ims;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.ListDataProject;

/**
 * Created by Daniel Villafuerte on 5/5/2015.
 */

@Entity
@Table(name = "ims_stock_transaction")
public class StockTransaction implements Serializable {

	private static final long serialVersionUID = 77866453513905445L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@OneToOne(targetEntity = ListDataProject.class)
	@JoinColumn(name = "listdata_project_id", nullable = false, updatable = false)
	private ListDataProject listDataProject;

	@OneToOne(targetEntity = GermplasmStudySource.class)
	@JoinColumn(name = "germplasm_study_source_id", nullable = false, updatable = false)
	private GermplasmStudySource germplasmStudySource;

	@OneToOne(targetEntity = Transaction.class)
	@JoinColumn(name = "trnid", nullable = false, updatable = false)
	private Transaction transaction;

	@Column(name = "recordid")
	private Integer sourceRecordId;

	public StockTransaction() {
	}

	public StockTransaction(Integer id, ListDataProject listDataProject, Transaction transaction) {
		this.id = id;
		this.listDataProject = listDataProject;
		this.transaction = transaction;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ListDataProject getListDataProject() {
		return this.listDataProject;
	}

	public void setListDataProject(ListDataProject listDataProject) {
		this.listDataProject = listDataProject;
	}

	public Transaction getTransaction() {
		return this.transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Integer getSourceRecordId() {
		return this.sourceRecordId;
	}

	public void setSourceRecordId(Integer sourceRecordId) {
		this.sourceRecordId = sourceRecordId;
	}

	public GermplasmStudySource getGermplasmStudySource() {
		return this.germplasmStudySource;
	}

	public void setGermplasmStudySource(final GermplasmStudySource germplasmStudySource) {
		this.germplasmStudySource = germplasmStudySource;
	}
}
