
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.dao.KeySequenceRegisterDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class KeySequenceRegisterServiceImpl implements KeySequenceRegisterService {

	private DaoFactory daoFactory;
	private KeySequenceRegisterDAO keySequenceRegisterDAO;

	public KeySequenceRegisterServiceImpl() {

	}

	public KeySequenceRegisterServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.keySequenceRegisterDAO = new KeySequenceRegisterDAO();
		this.keySequenceRegisterDAO.setSession(sessionProvider.getSession());
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int incrementAndGetNextSequence(final String keyPrefix) {
		return this.keySequenceRegisterDAO.incrementAndGetNextSequence(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int incrementAndGetNextSequenceWithoutHibernate(final String keyPrefix) {
		return this.keySequenceRegisterDAO.incrementAndGetNextSequenceWithoutHibernate(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int getNextSequence(final String keyPrefix) {
		return this.keySequenceRegisterDAO.getNextSequence(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int getNextSequenceWithoutHibernate(final String keyPrefix) {
		return this.keySequenceRegisterDAO.getNextSequenceWithoutHibernate(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void saveLastSequenceUsed(final String keyPrefix, final Integer lastSequenceUsed) {
		this.keySequenceRegisterDAO.saveLastSequenceUsed(keyPrefix, lastSequenceUsed);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void saveLastSequenceUsedWithoutHibernate(final String keyPrefix, final Integer lastSequenceUsed) {
		this.keySequenceRegisterDAO.saveLastSequenceUsedWithoutHibernate(keyPrefix, lastSequenceUsed);
	}

	@Override
	public void deleteKeySequences(final List<String> keyPrefixes) {
		this.daoFactory.getKeySequenceRegisterDAO().deleteByKeyPrefixes(keyPrefixes);
	}
}
