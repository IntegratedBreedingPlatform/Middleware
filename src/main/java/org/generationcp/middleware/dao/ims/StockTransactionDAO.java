package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.StockTransaction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Villafuerte on 5/5/2015.
 */
public class StockTransactionDAO extends GenericDAO<StockTransaction, Integer>{
    public List<StockTransaction> getTransactionsForListDataProjectIDs(List<Integer> listDataProjectIDList) throws MiddlewareQueryException {
        try {
            Criteria criteria = getSession().createCriteria(StockTransaction.class);
            criteria.createAlias("listDataProject", "ldp");
            criteria.add(Restrictions.in("ldp.listDataProjectId", listDataProjectIDList));

            return criteria.list();
        } catch (HibernateException e) {
            logAndThrowException("Error with getTransactionsForListDataProjectIDs() query from StockTransactionDAO: " + e.getMessage(), e);
            return new ArrayList<StockTransaction>();
        }
    }

    public boolean listDataProjectListHasStockTransactions(Integer listDataProjectListId) throws MiddlewareQueryException {
        String sql = "select count(*) from ims_stock_transaction ist " +
                "WHERE EXISTS (select 1 from listdata_project ldp where ldp.listdata_project_id = ist.listdata_project_id" +
                " AND ldp.list_id = :listId)";

        try {
            Query query = getSession().createSQLQuery(sql);
            query.setInteger("listId", listDataProjectListId);

            Number number = (Number) query.uniqueResult();
            return number.intValue() > 0;
        } catch (HibernateException e) {
            logAndThrowException("Error with listDataProjectListHasStockTransactions() query from StockTransactionDAO: " + e.getMessage(), e);
            return false;
        }
    }

    public List<InventoryDetails> retrieveInventoryDetailsForListDataProjectListId(Integer listDataProjectListId, GermplasmListType germplasmListType) throws MiddlewareQueryException {
        List<InventoryDetails> detailsList = new ArrayList<>();

        if (! germplasmListType.equals(GermplasmListType.ADVANCED) && !germplasmListType.equals(GermplasmListType.CROSSES)) {
            throw new IllegalArgumentException("This method should only be passed lists of type ADVANCED or CROSSES");
        }

        String sql = "select lot.lotid, lot.locid, lot.scaleid, lot.userid, " +
                "d.germplasm_id, d.entry_id, d.seed_source, d.designation, d.group_name, " +
                "loc.lname, loc.labbr, scale.name, tran.trnqty, tran.comments,tran.inventory_id, tran.sourceid, " +
                "d.duplicate_notes, tran.bulk_with, tran.bulk_compl " +
                "FROM listdata_project d INNER JOIN ims_stock_transaction ist ON d.listdata_project_id = ist.listdata_project_id " +
                "INNER JOIN listnms ON d.list_id = listnms.listid " +
                "INNER JOIN ims_transaction tran ON tran.trnid = ist.trnid INNER JOIN ims_lot lot ON lot.lotid = tran.lotid " +
                "LEFT JOIN location loc ON lot.locid = loc.locid LEFT JOIN cvterm scale ON scale.cvterm_id = lot.scaleid " +
                "WHERE listnms.listid = :listId";

        try {
            Query query = getSession().createSQLQuery(sql);
            query.setInteger("listId", listDataProjectListId);

            List<Object[]> results = query.list();

            if (!results.isEmpty()){
                for (Object[] row: results){
                    Integer lotId = (Integer) row[0];
                    Integer locationId = (Integer) row[1];
                    Integer scaleId = (Integer) row[2];
                    Integer userId = (Integer) row[3];
                    Integer gid = (Integer) row[4];
                    Integer entryId = (Integer) row[5];
                    String seedSource = (String) row[6];
                    String designation = (String) row[7];
                    String groupName = (String) row[8];
                    String locationName = (String) row[9];
                    String locationAbbr = (String) row[10];
                    String scaleName = (String) row[11];
                    Double amount = (Double) row[12];
                    String comments = (String) row[13];
                    String inventoryID = (String) row[14];
                    Integer sourceId = (Integer) row[15];
                    String duplicate = (String) row[16];
    	        	String bulkWith = (String) row[17];
    	        	Character bulkCompl = (Character) row[18];

                    InventoryDetails details = new InventoryDetails(gid, designation, lotId, locationId, locationName,
                            userId, amount, sourceId, null, scaleId, scaleName, comments);
                    details.setInventoryID(inventoryID);
                    details.setLocationAbbr(locationAbbr);
                    details.setEntryId(entryId);
                    details.setSource(seedSource);
                    details.setParentage(groupName);
                    details.setDuplicate(duplicate);
                    details.setBulkWith(bulkWith);
                    details.setBulkCompl(bulkCompl!=null?bulkCompl.toString():null);
                    detailsList.add(details);

                }
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with retrieveInventoryDetailsForListDataProjectListId() query from StockTransactionDAO: " + e.getMessage(), e);
        }

        return detailsList;
    }
}
