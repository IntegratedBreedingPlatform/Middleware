
package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.util.DatabaseBroker;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ListDataPropertySaver extends Saver {

	public ListDataPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<ListDataInfo> saveProperties(List<ListDataInfo> listDataCollection) throws MiddlewareQueryException {

		Session sessionForLocal = this.getCurrentSession();
		sessionForLocal.flush();

		// initialize session & transaction
		Session session = sessionForLocal;
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			Integer recordsSaved = 0;
			for (ListDataInfo listDataObj : listDataCollection) {
				Integer listDataId = listDataObj.getListDataId();
				if (listDataId != null) {
					GermplasmListData listData = this.getGermplasmListDataDAO().getById(listDataId);

					if (listData != null) {
						for (ListDataColumn column : listDataObj.getColumns()) {
							ListDataProperty property =
									this.getListDataPropertyDAO().getByListDataIDAndColumnName(listDataId, column.getColumnName());
							// create if combination of listdata ID and column name doesn't exist yet
							if (property == null) {
								property = new ListDataProperty(listData, column.getColumnName());
								Integer nextId = this.getListDataPropertyDAO().getNextId("listDataPropertyId");
								property.setListDataPropertyId(nextId);
							}
							String value = column.getValue();
							if (value != null) {
								value = value.trim().isEmpty() ? null : value; // if empty string, save as NULL PM
							}
							property.setValue(value);

							property = this.getListDataPropertyDAO().saveOrUpdate(property);
							recordsSaved++;
							if (recordsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
								// flush a batch of inserts and release memory
								this.getListDataPropertyDAO().flush();
								this.getListDataPropertyDAO().clear();
							}
							// save ID of the inserted or updated listdataprop record
							column.setListDataColumnId(property.getListDataPropertyId());
							column.setValue(property.getValue());
						}

					} else {
						throw new MiddlewareQueryException("List Data ID: " + listDataId + " does not exist.");
					}
				} else {
					throw new MiddlewareQueryException("List Data ID cannot be null.");
				}
			}
			// end transaction, commit to database
			trans.commit();
		} catch (MiddlewareQueryException e) {
			this.rollbackTransaction(trans);
			throw new MiddlewareQueryException("Error in saving List Data properties - " + e.getMessage(), e);

		} finally {
			sessionForLocal.flush();
		}

		return listDataCollection;
	}

	public List<ListDataProperty> saveListDataProperties(List<ListDataProperty> listDataProps) throws MiddlewareQueryException {

		Session sessionForLocal = this.getCurrentSession();
		sessionForLocal.flush();

		// initialize session & transaction
		Session session = sessionForLocal;
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			Integer recordsSaved = 0;
			for (ListDataProperty listDataProperty : listDataProps) {

				if (listDataProperty.getListData() != null) {

					Integer nextId = this.getListDataPropertyDAO().getNextId("listDataPropertyId");
					listDataProperty.setListDataPropertyId(nextId);
					listDataProperty = this.getListDataPropertyDAO().saveOrUpdate(listDataProperty);

					if (recordsSaved % DatabaseBroker.JDBC_BATCH_SIZE == 0) {
						// flush a batch of inserts and release memory
						this.getListDataPropertyDAO().flush();
						this.getListDataPropertyDAO().clear();
					}

				} else {
					throw new MiddlewareQueryException("List Data ID cannot be null.");
				}
			}
			trans.commit();
		} catch (MiddlewareQueryException e) {
			this.rollbackTransaction(trans);
			throw new MiddlewareQueryException("Error in saving List Data properties - " + e.getMessage(), e);

		} finally {
			sessionForLocal.flush();
		}

		return listDataProps;
	}

}
