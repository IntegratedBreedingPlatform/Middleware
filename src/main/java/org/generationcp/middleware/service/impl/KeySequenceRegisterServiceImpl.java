
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class KeySequenceRegisterServiceImpl implements KeySequenceRegisterService {

	private KeySequenceRegisterDAO keySequenceRegisterDAO;

	public KeySequenceRegisterServiceImpl() {

	}

	public KeySequenceRegisterServiceImpl(HibernateSessionProvider sessionProvider) {
		this.keySequenceRegisterDAO = new KeySequenceRegisterDAO();
		this.keySequenceRegisterDAO.setSession(sessionProvider.getSession());
	}

	public KeySequenceRegisterServiceImpl(final Session session) {
		this.keySequenceRegisterDAO = new KeySequenceRegisterDAO();
		this.keySequenceRegisterDAO.setSession(session);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int incrementAndGetNextSequence(final String keyPrefix, final String suffix) {
		return this.keySequenceRegisterDAO.incrementAndGetNextSequence(keyPrefix, suffix);
	}
	
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int getNextSequence(String keyPrefix, String suffix) {
		return this.keySequenceRegisterDAO.getNextSequence(keyPrefix, suffix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void saveLastSequenceUsed(String keyPrefix, String suffix, Integer lastSequenceUsed) {
		this.keySequenceRegisterDAO.saveLastSequenceUsed(keyPrefix, suffix, lastSequenceUsed);
	}

}
