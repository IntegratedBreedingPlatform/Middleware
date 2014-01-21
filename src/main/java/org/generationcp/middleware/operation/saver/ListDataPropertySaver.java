package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ListDataPropertySaver extends Saver {

	public ListDataPropertySaver(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	
	public List<ListDataInfo> saveProperties(List<ListDataInfo> listDataCollection) throws MiddlewareQueryException {
		
    	requireLocalDatabaseInstance();
        Session sessionForLocal = getCurrentSessionForLocal();
        sessionForLocal.flush();

        // initialize session & transaction
        Session session = sessionForLocal;
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();
            
            Integer recordsSaved = 0;
        	for (ListDataInfo listDataObj : listDataCollection){
    			Integer listDataId = listDataObj.getListDataId();
    			if (listDataId != null){
    				GermplasmListData listData = getGermplasmListDataDAO().getById(listDataId);
    				
    				if (listData != null){
    					for (ListDataColumn column : listDataObj.getColumns()){
    						ListDataProperty property = getListDataPropertyDAO().getByListDataIDAndColumnName(
    								listDataId, column.getColumnName());
    						// create if combination of listdata ID and column name doesn't exist yet
    						if (property == null){
    							property = new ListDataProperty(listData, column.getColumnName());
    							Integer negativeId = getListDataPropertyDAO().getNegativeId("listDataPropertyId");
								property.setListDataPropertyId(negativeId); //assign next negative ID
    						}
    						String value = column.getValue();
    						if (value != null){
    							value = value.trim().isEmpty() ? null : value; // if empty string, save as NULL PM
    						}
							property.setValue(value);
    						
    						property = getListDataPropertyDAO().saveOrUpdate(property);
    						recordsSaved++;
			                if (recordsSaved % JDBC_BATCH_SIZE == 0) {
			                    // flush a batch of inserts and release memory
			                	getListDataPropertyDAO().flush();
			                	getListDataPropertyDAO().clear();
			                }
    						//save ID of the inserted or updated listdataprop record
    						column.setListDataColumnId(property.getListDataPropertyId()); 
    						column.setValue(property.getValue());
    					}
    					
    				} else {
    					throw new MiddlewareQueryException("List Data ID: " 
    							+ listDataId + " does not exist.");
    				}
    			} else {
    				throw new MiddlewareQueryException("List Data ID cannot be null.");
    			}
    		}
            // end transaction, commit to database
            trans.commit();
        } catch (MiddlewareQueryException e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in saving List Data properties - " + e.getMessage(), e);
            
        } finally {
            sessionForLocal.flush();
        }
		
		return listDataCollection;
	}

}
