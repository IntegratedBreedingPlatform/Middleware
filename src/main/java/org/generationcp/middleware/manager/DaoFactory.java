package org.generationcp.middleware.manager;

import org.generationcp.middleware.dao.FormulaDAO;
import org.generationcp.middleware.dao.PlantDao;
import org.generationcp.middleware.dao.SampleDao;
import org.generationcp.middleware.dao.SampleListDao;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DaoFactory {

	private HibernateSessionProvider sessionProvider;

	public DaoFactory(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public FormulaDAO getFormulaDAO() {
		final FormulaDAO formulaDAO = new FormulaDAO();
		formulaDAO.setSession(this.sessionProvider.getSession());
		return formulaDAO;
	}

	public SampleListDao getSampleListDao() {
		final SampleListDao sampleListDao = new SampleListDao();
		sampleListDao.setSession(this.sessionProvider.getSession());
		return sampleListDao;
	}

	public SampleDao getSampleDao() {
		final SampleDao sampleDao = new SampleDao();
		sampleDao.setSession(this.sessionProvider.getSession());
		return sampleDao;
	}

	public PlantDao getPlantDao() {
		final PlantDao plantDao = new PlantDao();
		plantDao.setSession(this.sessionProvider.getSession());
		return plantDao;
	}

	public UserDAO getUserDao() {
		final UserDAO userDAO = new UserDAO();
		userDAO.setSession(this.sessionProvider.getSession());
		return userDAO;
	}

}
