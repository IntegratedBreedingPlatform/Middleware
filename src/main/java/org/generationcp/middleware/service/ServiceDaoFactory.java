
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.LocationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ims.LotDAO;
import org.generationcp.middleware.dao.ims.StockTransactionDAO;
import org.generationcp.middleware.dao.ims.TransactionDAO;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class ServiceDaoFactory {

	private HibernateSessionProvider sessionProvider;

	public ServiceDaoFactory(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public LotDAO getLotDao() {
		LotDAO lotDao = new LotDAO();
		lotDao.setSession(this.sessionProvider.getSession());
		return lotDao;
	}

	public TransactionDAO getTransactionDAO() {
		TransactionDAO transactionDao = new TransactionDAO();
		transactionDao.setSession(this.sessionProvider.getSession());
		return transactionDao;
	}

	public StockTransactionDAO getStockTransactionDAO() {
		StockTransactionDAO stockTransactionDao = new StockTransactionDAO();
		stockTransactionDao.setSession(this.sessionProvider.getSession());
		return stockTransactionDao;
	}

	public GermplasmListDAO getGermplasmListDAO() {
		GermplasmListDAO germplasmListDao = new GermplasmListDAO();
		germplasmListDao.setSession(this.sessionProvider.getSession());
		return germplasmListDao;
	}

	public GermplasmListDataDAO getGermplasmListDataDAO() {
		GermplasmListDataDAO germplasmListDataDao = new GermplasmListDataDAO();
		germplasmListDataDao.setSession(this.sessionProvider.getSession());
		return germplasmListDataDao;
	}

	public LocationDAO getLocationDAO() {
		LocationDAO locationDao = new LocationDAO();
		locationDao.setSession(this.sessionProvider.getSession());
		return locationDao;
	}

	public CVTermDao getCvTermDao() {
		CVTermDao cvTermDao = new CVTermDao();
		cvTermDao.setSession(this.sessionProvider.getSession());
		return cvTermDao;
	}

	public PersonDAO getPersonDAO() {
		PersonDAO personDao = new PersonDAO();
		personDao.setSession(this.sessionProvider.getSession());
		return personDao;
	}

	public GermplasmDAO getGermplasmDAO() {
		GermplasmDAO germplasmDao = new GermplasmDAO();
		germplasmDao.setSession(this.sessionProvider.getSession());
		return germplasmDao;
	}

}
