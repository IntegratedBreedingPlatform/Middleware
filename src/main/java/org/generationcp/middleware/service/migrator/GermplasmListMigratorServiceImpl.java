package org.generationcp.middleware.service.migrator;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.migrator.api.GermplasmListMigratorService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmListMigratorServiceImpl extends Service implements GermplasmListMigratorService {

	private static final int RESET_VALUE = 0;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListMigratorServiceImpl.class);

	public GermplasmListMigratorServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}
	
	public GermplasmListMigratorServiceImpl(HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}
	
	@Override
	public void resetLocalReferenceIdOfGermplasmLists()
			throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		
		try {
			// begin update transaction
			trans = session.beginTransaction();
			
			Criteria crit = session.createCriteria(GermplasmList.class);
			crit.add(Restrictions.lt("listRef", 0));
			ScrollableResults items = crit.scroll();
			
			int count=0;
			while ( items.next() ) {
				GermplasmList germplasmList = (GermplasmList)items.get(0);
				germplasmList.setListRef(RESET_VALUE);
				session.saveOrUpdate(germplasmList);
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
			this.logAndThrowException("Error in GermplasmListMigratorServiceImpl.resetLocalReferenceIdOfGermplasmLists()" 
					+ e.getMessage(), e, GermplasmListMigratorServiceImpl.LOG);
		} finally {
			session.flush();
		}
	}

	@Override
	public void resetLocalReferenceIdOfGermplasmListData()
			throws MiddlewareQueryException {
		Session session = this.getCurrentSession();
		Transaction trans = null;
		
		try {
			// begin update transaction
			trans = session.beginTransaction();
			
			Criteria crit = session.createCriteria(GermplasmListData.class);
			crit.add(Restrictions.lt("localRecordId", 0));
			ScrollableResults items = crit.scroll();
			
			int count=0;
			while ( items.next() ) {
				GermplasmListData germplasmListData = (GermplasmListData)items.get(0);
				germplasmListData.setLocalRecordId(RESET_VALUE);
				session.saveOrUpdate(germplasmListData);
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
			this.logAndThrowException("Error in GermplasmListMigratorServiceImpl.resetLocalReferenceIdOfGermplasmListData()" 
					+ e.getMessage(), e, GermplasmListMigratorServiceImpl.LOG);
		} finally {
			session.flush();
		}
	}

}
