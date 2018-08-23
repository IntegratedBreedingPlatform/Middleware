
package org.generationcp.middleware.operation.saver;

import java.util.List;

import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProperty;
import org.generationcp.middleware.util.DatabaseBroker;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ListDataPropertySaver extends Saver {

	private DaoFactory daoFactory;

	public ListDataPropertySaver() {
		super();
	}
	
	public ListDataPropertySaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public List<ListDataInfo> saveProperties(List<ListDataInfo> listDataCollection) throws MiddlewareQueryException {


		try {
			for (ListDataInfo listDataObj : listDataCollection) {
				Integer listDataId = listDataObj.getListDataId();
				if (listDataId != null) {
					GermplasmListData listData = daoFactory.getGermplasmListDataDAO().getById(listDataId);

					if (listData != null) {
						for (ListDataColumn column : listDataObj.getColumns()) {
							ListDataProperty property =
									this.getListDataPropertyDAO().getByListDataIDAndColumnName(listDataId, column.getColumnName());
							// create if combination of listdata ID and column name doesn't exist yet
							if (property == null) {
								property = new ListDataProperty(listData, column.getColumnName());
							}
							String value = column.getValue();
							if (value != null) {
								value = value.trim().isEmpty() ? null : value; // if empty string, save as NULL PM
							}
							property.setValue(value);

							property = this.getListDataPropertyDAO().saveOrUpdate(property);
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
			

		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException("Error in saving List Data properties - " + e.getMessage(), e);

		}
		return listDataCollection;
	}

	public List<ListDataProperty> saveListDataProperties(List<ListDataProperty> listDataProps) throws MiddlewareQueryException {

		try {
			for (ListDataProperty listDataProperty : listDataProps) {

				if (listDataProperty.getListData() != null) {
					this.getListDataPropertyDAO().saveOrUpdate(listDataProperty);

				} else {
					throw new MiddlewareQueryException("List Data ID cannot be null.");
				}
			}

		} catch (MiddlewareQueryException e) {

			throw new MiddlewareQueryException("Error in saving List Data properties - " + e.getMessage(), e);

		}

		return listDataProps;
	}

}
