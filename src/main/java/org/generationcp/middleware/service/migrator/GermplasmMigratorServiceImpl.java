package org.generationcp.middleware.service.migrator;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.migrator.api.GermplasmMigratorService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmMigratorServiceImpl extends Service implements GermplasmMigratorService {
	
	private static final int RESET_VALUE = 0;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmMigratorServiceImpl.class);

	public GermplasmMigratorServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}
	
	public GermplasmMigratorServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public void resetLocalReferenceIdOfUserDefinedFields()
			throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		
		try {
			// begin update transaction
			trans = session.beginTransaction();
			
			Criteria crit = session.createCriteria(UserDefinedField.class);
			crit.add(Restrictions.lt("lfldno", 0));
			ScrollableResults items = crit.scroll();
			
			int count=0;
			while ( items.next() ) {
				UserDefinedField udfld = (UserDefinedField)items.get(0);
				udfld.setLfldno(RESET_VALUE);
				session.saveOrUpdate(udfld);
				if ( ++count % 100 == 0 ) {
					session.flush();
					session.clear();
				}
			}
			
			// end transaction, commit to database
			trans.commit();
		} catch (HibernateException e) {
			if(trans != null){
				this.rollbackTransaction(trans);
			}
			this.logAndThrowException("Error in GermplasmMigratorServiceImpl.resetLocalReferenceIdOfUserDefinedFields()" 
					+ e.getMessage(), e, GermplasmMigratorServiceImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public void resetLocalReferenceIdOfGermplasms()
			throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		
		try {
			// begin update transaction
			trans = session.beginTransaction();
			
			Criteria crit = session.createCriteria(Germplasm.class);
			crit.add(Restrictions.lt("lgid", 0));
			ScrollableResults items = crit.scroll();
			
			int count=0;
			while ( items.next() ) {
				Germplasm germplasm = (Germplasm)items.get(0);
				germplasm.setLgid(RESET_VALUE);
				session.saveOrUpdate(germplasm);
				if ( ++count % 100 == 0 ) {
					session.flush();
					session.clear();
				}
			}
			
			// end transaction, commit to database
			trans.commit();
		} catch (HibernateException e) {
			if(trans != null){
				this.rollbackTransaction(trans);
			}
			this.logAndThrowException("Error in GermplasmMigratorServiceImpl.resetLocalReferenceIdOfGermplasms()" 
					+ e.getMessage(), e, GermplasmMigratorServiceImpl.LOG);
		} finally {
			session.flush();
		}
	}
}
