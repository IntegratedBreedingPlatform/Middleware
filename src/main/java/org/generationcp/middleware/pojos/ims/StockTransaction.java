package org.generationcp.middleware.pojos.ims;

import org.generationcp.middleware.pojos.ListDataProject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Daniel Villafuerte on 5/5/2015.
 */

@Entity
@Table(name = "ims_stock_transaction")
public class StockTransaction implements Serializable{
    private static final long serialVersionUID = 77866453513905445L;

    public StockTransaction() {
    }

    public StockTransaction(Integer id, ListDataProject listDataProject, Transaction transaction) {
        this.id = id;
        this.listDataProject = listDataProject;
        this.transaction = transaction;
    }

    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @OneToOne(targetEntity = ListDataProject.class)
    @JoinColumn(name = "listdata_project_id", nullable = false, updatable = false)
    private ListDataProject listDataProject;

    @OneToOne(targetEntity = Transaction.class)
    @JoinColumn(name = "trnid", nullable = false, updatable = false)
    private Transaction transaction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ListDataProject getListDataProject() {
        return listDataProject;
    }

    public void setListDataProject(ListDataProject listDataProject) {
        this.listDataProject = listDataProject;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
