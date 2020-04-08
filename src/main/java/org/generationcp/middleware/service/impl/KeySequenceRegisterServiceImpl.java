
package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.KeySequenceRegister;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class KeySequenceRegisterServiceImpl implements KeySequenceRegisterService {
	private DaoFactory daoFactory;

	public KeySequenceRegisterServiceImpl() {

	}

	public KeySequenceRegisterServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int incrementAndGetNextSequence(final String keyPrefix) {
		return this.daoFactory.getKeySequenceRegisterDAO().incrementAndGetNextSequence(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int getNextSequence(final String keyPrefix) {
		return this.daoFactory.getKeySequenceRegisterDAO().getNextSequence(keyPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void saveLastSequenceUsed(final String keyPrefix, final Integer lastSequenceUsed) {
		this.daoFactory.getKeySequenceRegisterDAO().saveLastSequenceUsed(keyPrefix, lastSequenceUsed);
	}

	@Override
	public List<KeySequenceRegister> getKeySequenceRegistersByPrefixes(final List<String> keyPrefixes) {
		return this.daoFactory.getKeySequenceRegisterDAO().getByKeyPrefixes(keyPrefixes);
	}

	@Override
	public void updateKeySequenceRegister(final List<Integer> keySequenceRegisterIds) {
		this.daoFactory.getKeySequenceRegisterDAO().updateKeySequenceRegister(keySequenceRegisterIds);
	}
}
