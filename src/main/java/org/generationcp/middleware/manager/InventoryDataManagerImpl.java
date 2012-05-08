package org.generationcp.middleware.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.LotDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.TransactionDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Lot;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.report.LotReportRow;
import org.generationcp.middleware.pojos.report.TransactionReportRow;
import org.generationcp.middleware.pojos.workbench.Person;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Most of the functions in this class only use the connection to the local instance, this is because the lot
 * and transaction tables only exist in a local instance.
 * 
 * @author Kevin Manansala
 *
 */
public class InventoryDataManagerImpl implements InventoryDataManager
{
	private HibernateUtil hibernateUtilForLocal;
	private HibernateUtil hibernateUtilForCentral;
	
	private static final int JDBC_BATCH_SIZE = 50;
	
	public InventoryDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral)
	{
		this.hibernateUtilForLocal = hibernateUtilForLocal;
		this.hibernateUtilForCentral = hibernateUtilForCentral;
	}

	@Override
	public List<Lot> findLotsByEntityType(String type, int start, int numOfRows)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.findByEntityType(type, start, numOfRows);
	}

	@Override
	public int countLotsByEntityType(String type)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countByEntityType(type).intValue();
	}
	
	@Override
	public List<Lot> findLotsByEntityTypeAndEntityId(String type, Integer entityId, int start, int numOfRows)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.findByEntityTypeAndEntityId(type, entityId, start, numOfRows);
	}
	
	@Override
	public int countLotsByEntityTypeAndEntityId(String type, Integer entityId)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countByEntityTypeAndEntityId(type, entityId).intValue();
	}
	
	@Override
	public List<Lot> findLotsByEntityTypeAndLocationId(String type, Integer locationId, int start, int numOfRows)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.findByEntityTypeAndLocationId(type, locationId, start, numOfRows);
	}
	
	@Override
	public int countLotsByEntityTypeAndLocationId(String type, Integer locationId)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countByEntityTypeAndLocationId(type, locationId).intValue();
	}
	
	@Override
	public List<Lot> findLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId, int start, int numOfRows)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.findByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId, start, numOfRows);
	}
	
	@Override
	public int countLotsByEntityTypeAndEntityIdAndLocationId(String type, Integer entityId, Integer locationId)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countByEntityTypeAndEntityIdAndLocationId(type, entityId, locationId).intValue();
	}
	
	@Override
	public Long getActualLotBalance(Integer lotId)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getActualLotBalance(lotId);
	}
	
	@Override
	public Long getAvailableLotBalance(Integer lotId)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAvailableLotBalance(lotId);
	}
	
	@Override
	public int addLot(Lot lot) throws QueryException
	{
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(lot);
		return addOrUpdateLot(lots, Operation.ADD);
	}
	
	@Override
	public int addLot(List<Lot> lots) throws QueryException
	{
		return addOrUpdateLot(lots, Operation.ADD);
	}
	
	@Override
	public int updateLot(Lot lot) throws QueryException
	{
		List<Lot> lots = new ArrayList<Lot>();
		lots.add(lot);
		return addOrUpdateLot(lots, Operation.UPDATE);
	}
	
	@Override
	public int updateLot(List<Lot> lots) throws QueryException
	{
		return addOrUpdateLot(lots, Operation.UPDATE);
	}
	
	private int addOrUpdateLot(List<Lot> lots, Operation operation) throws QueryException
	{
		//initialize session & transaction
		Session session = hibernateUtilForLocal.getCurrentSession();
		Transaction trans = null;
		
		int lotsSaved = 0;
		try {
			//begin save transaction
			trans = session.beginTransaction();

			LotDAO dao = new LotDAO();
			dao.setSession(session);
			
			for(Lot lot : lots) {
				if (operation == Operation.ADD) {
					//Auto-assign negative IDs for new local DB records
					Integer negativeId = dao.getNegativeId("id");
					lot.setId(negativeId);
				} else if (operation == Operation.UPDATE) {
					//Check if Lot is a local DB record. Throws exception if Lot is a central DB record.
					dao.validateId(lot);
				}
				dao.saveOrUpdate(lot);
				lotsSaved++;
				if (lotsSaved % JDBC_BATCH_SIZE == 0) {
					//flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			//end transaction, commit to database
			trans.commit();
		} catch (Exception ex) {
			//rollback transaction in case of errors
			if(trans != null){
				trans.rollback();
			}
			throw new QueryException("Error encountered while saving Lot: " + ex.getMessage(), ex);
		} finally {
			hibernateUtilForLocal.closeCurrentSession();
		}
		
		return lotsSaved;
	}
	
	@Override
	public int addTransaction(org.generationcp.middleware.pojos.Transaction transaction)
			throws QueryException
	{
		List<org.generationcp.middleware.pojos.Transaction> transactions = 
			new ArrayList<org.generationcp.middleware.pojos.Transaction>();
		transactions.add(transaction);
		return addTransaction(transactions);
	}
	
	@Override
	public int addTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions)
			throws QueryException
	{
		return addOrUpdateTransaction(transactions, Operation.ADD);
	}
	
	@Override
	public int updateTransaction(org.generationcp.middleware.pojos.Transaction transaction)
			throws QueryException
	{
		List<org.generationcp.middleware.pojos.Transaction> transactions = new ArrayList<org.generationcp.middleware.pojos.Transaction>();
		transactions.add(transaction);
		return addOrUpdateTransaction(transactions, Operation.UPDATE);
	}
	
	@Override
	public int updateTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions)
			throws QueryException
	{
		return addOrUpdateTransaction(transactions, Operation.UPDATE);
	}
	
	private int addOrUpdateTransaction(List<org.generationcp.middleware.pojos.Transaction> transactions, Operation operation) throws QueryException
	{
		//initialize session & transaction
		Session session = hibernateUtilForLocal.getCurrentSession();
		Transaction trans = null;
		
		int transactionsSaved = 0;
		try {
			//begin save transaction
			trans = session.beginTransaction();

			TransactionDAO dao = new TransactionDAO();
			dao.setSession(session);
			
			for(org.generationcp.middleware.pojos.Transaction transaction : transactions) {
				if (operation == Operation.ADD) {
					//Auto-assign negative IDs for new local DB records
					Integer negativeId = dao.getNegativeId("id");
					transaction.setId(negativeId);
				} else if (operation == Operation.UPDATE) {
					//Check if Lot is a local DB record. Throws exception if Lot is a central DB record.
					dao.validateId(transaction);
				}
				dao.saveOrUpdate(transaction);
				transactionsSaved++;
				if (transactionsSaved % JDBC_BATCH_SIZE == 0) {
					//flush a batch of inserts and release memory
					dao.flush();
					dao.clear();
				}
			}
			//end transaction, commit to database
			trans.commit();
		} catch (Exception ex) {
			//rollback transaction in case of errors
			if(trans != null){
				trans.rollback();
			}
			throw new QueryException("Error encountered while saving Transaction: " + ex.getMessage(), ex);
		} finally {
			hibernateUtilForLocal.closeCurrentSession();
		}
		
		return transactionsSaved;
	}
	
	@Override
	public org.generationcp.middleware.pojos.Transaction getTransactionById(Integer id)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.findById(id, false);
	}
	
	@Override
	public Set<org.generationcp.middleware.pojos.Transaction> findTransactionsByLotId(Integer id)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		Lot lot = dao.findById(id, false);
		return lot.getTransactions();
	}
	
	@Override
	public List<org.generationcp.middleware.pojos.Transaction> getAllReserveTransactions(int start, int numOfRows)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAllReserve(start, numOfRows);
	}
	
	@Override
	public int countAllReserveTransactions()
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllReserve().intValue();
	}
	
	@Override
	public List<org.generationcp.middleware.pojos.Transaction> getAllDepositTransactions(int start, int numOfRows)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAllDeposit(start, numOfRows);
	}
	
	@Override
	public int countAllDepositTransactions()
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllDeposit().intValue();
	}
	
	@Override
	public List<org.generationcp.middleware.pojos.Transaction> getAllReserveTransactionsByRequestor(Integer personId, int start, int numOfRows)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAllReserveByRequestor(personId, start, numOfRows);
	}
	
	@Override
	public int countAllReserveTransactionsByRequestor(Integer personId)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllReserveByRequestor(personId).intValue();
	}
	
	@Override
	public List<org.generationcp.middleware.pojos.Transaction> getAllDepositTransactionsByDonor(Integer personId, int start, int numOfRows)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAllDepositByDonor(personId, start, numOfRows);
	}
	
	@Override
	public int countAllDepositTransactionsByDonor(Integer personId)
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllDepositByDonor(personId).intValue();
	}
	
	@Override
	public List<TransactionReportRow> generateReportOnAllUncommittedTransactions(int start, int numOfRows)
	{
		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
		GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TraitDataManagerImpl traitManager = new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		List<org.generationcp.middleware.pojos.Transaction> transactions = dao.getAllUncommitted(start, numOfRows);
		
		for(org.generationcp.middleware.pojos.Transaction t : transactions)
		{
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			
			Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
			row.setScaleOfLot(scale);
			
			Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);
			
			report.add(row);
		}
		
		return report;
	}
	
	@Override
	public int countAllUncommittedTransactions()
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllUncommitted().intValue();
	}
	
	@Override
	public List<TransactionReportRow> generateReportOnAllReserveTransactions(int start, int numOfRows)
	{
		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
		GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TraitDataManagerImpl traitManager = new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		
		List<org.generationcp.middleware.pojos.Transaction> transactions = getAllReserveTransactions(start, numOfRows);
		for(org.generationcp.middleware.pojos.Transaction t : transactions)
		{
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());
			
			Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
			row.setScaleOfLot(scale);
			
			Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);
			
			report.add(row);
		}
		
		return report;
	}
	
	@Override
	public int countAllWithdrawalTransactions()
	{
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAllWithdrawals().intValue();
	}
	
	@Override
	public List<TransactionReportRow> generateReportOnAllWithdrawalTransactions(int start, int numOfRows)
	{
		List<TransactionReportRow> report = new ArrayList<TransactionReportRow>();
		GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TraitDataManagerImpl traitManager = new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		
		List<org.generationcp.middleware.pojos.Transaction> transactions = dao.getAllWithdrawals(start, numOfRows);
		for(org.generationcp.middleware.pojos.Transaction t : transactions)
		{
			TransactionReportRow row = new TransactionReportRow();
			row.setDate(t.getDate());
			row.setQuantity(t.getQuantity());
			row.setCommentOfLot(t.getLot().getComments());
			row.setEntityIdOfLot(t.getLot().getEntityId());
			
			Scale scale = traitManager.getScaleByID(t.getLot().getScaleId());
			row.setScaleOfLot(scale);
			
			Location location = germplasmManager.getLocationByID(t.getLot().getLocationId());
			row.setLocationOfLot(location);
			
			Person person = getPersonById(t.getPersonId());
			row.setPerson(person);
			
			report.add(row);
		}
		
		return report;
	}
	
	public Person getPersonById(Integer id)
	{
		PersonDAO dao = new PersonDAO();
		if(id < 0 && this.hibernateUtilForLocal != null)
			dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		else if(id > 0 && this.hibernateUtilForCentral != null)
			dao.setSession(this.hibernateUtilForCentral.getCurrentSession());
		else
			return null;
		
		return dao.findById(id, false);
	}
	
	@Override
	public List<Lot> getAllLots(int start, int numOfRows)
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.getAll(start, numOfRows);
	}
	
	@Override
	public Long countAllLots()
	{
		LotDAO dao = new LotDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		return dao.countAll();
	}
	
	@Override
	public List<LotReportRow> generateReportOnAllLots(int start, int numOfRows)
	{
		List<Lot> allLots = getAllLots(start, numOfRows);
		return generateLotReportRows(allLots);
	}
	
	@Override
	public List<LotReportRow> generateReportOnDormantLots(int year, int start, int numOfRows)
	{
		SQLQuery query = this.hibernateUtilForLocal.getCurrentSession()
			.createSQLQuery(Lot.GENERATE_REPORT_ON_DORMANT);
		query.setParameter("year", year);
		query.setFirstResult(start);
		query.setMaxResults(numOfRows);
		
		GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TraitDataManagerImpl traitManager = new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		List<LotReportRow> report = new ArrayList<LotReportRow>();
		
		List results = query.list();
		for (Object o : results) {
			Object[] result = (Object[]) o;
			if(result != null)
			{
				LotReportRow row = new LotReportRow();
				
				row.setLotId((Integer) result[0]);
				
				row.setEntityIdOfLot((Integer) result[1]);
				
				row.setActualLotBalance(((BigDecimal) result[2]).longValue());
				
				Location location = germplasmManager.getLocationByID((Integer) result[3]);
				row.setLocationOfLot(location);
				
				Scale scale = traitManager.getScaleByID((Integer) result[4]);
				row.setScaleOfLot(scale);
				
				report.add(row);
			}
		}
		return report;
	}
	
	/**
	@Override
	public List<LotReportRow> generateReportOnEmptyLot(int start, int numOfRows)
			 {
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		List<Lot> emptyLots = new ArrayList<Lot>();
		
		for(org.generationcp.middleware.pojos.Transaction t:dao.getEmptyLot(start,numOfRows)){
			Lot lot = new Lot();
			lot.setId(t.getId());
			lot.setLocationId(t.getLot().getLocationId());
			lot.setEntityId(t.getLot().getEntityId());
			lot.setScaleId(t.getLot().getScaleId());
			emptyLots.add(lot);
		}
		return generateLotReportRows(emptyLots);
	}
	
	@Override
	public List<LotReportRow> generateReportOnLotWithMinimumAmount(long minAmount,int start, int numOfRows){
		TransactionDAO dao = new TransactionDAO();
		dao.setSession(this.hibernateUtilForLocal.getCurrentSession());
		
		List<Lot> lotsWithMinimunAmount = new ArrayList<Lot>();
		for(org.generationcp.middleware.pojos.Transaction t:dao.getLotWithMinimumAmount(minAmount,start,numOfRows)){
			Lot lot = new Lot();
			lot.setId(t.getId());
			lot.setLocationId(t.getLot().getLocationId());
			lot.setEntityId(t.getLot().getEntityId());
			lot.setScaleId(t.getLot().getScaleId());
			lotsWithMinimunAmount.add(lot);
		}
		return generateLotReportRows(lotsWithMinimunAmount);
	}
	**/
	
	@Override
	public List<LotReportRow> generateReportOnLotsByEntityType(String type, int start, int numOfRows)
	{
		List<Lot> lotsByEntity = findLotsByEntityType(type, start, numOfRows);
		return generateLotReportRows(lotsByEntity);
	}
	
	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, Integer entityId,
			int start, int numOfRows)
	{
		List<Integer> entityIds = new ArrayList<Integer>();
		entityIds.add(entityId);
		return generateReportOnLotsByEntityTypeAndEntityId(type, entityIds, start, numOfRows);
	} 
	
	@Override
	public List<LotReportRow> generateReportOnLotsByEntityTypeAndEntityId(String type, List<Integer> entityIds,
			int start, int numOfRows)
	{
		List<Lot> lotsByEntityTypeAndEntityId = new ArrayList<Lot>();
		for (Integer entityId : entityIds) {
			List<Lot> lotsForEntityId = findLotsByEntityTypeAndEntityId(type, entityId, start, numOfRows);
			lotsByEntityTypeAndEntityId.addAll(lotsForEntityId);
		}
		return generateLotReportRows(lotsByEntityTypeAndEntityId);
	}
	
	private List<LotReportRow> generateLotReportRows(List<Lot> listOfLots)
	{
		GermplasmDataManagerImpl germplasmManager = new GermplasmDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		TraitDataManagerImpl traitManager = new TraitDataManagerImpl(this.hibernateUtilForLocal, this.hibernateUtilForCentral);
		List<LotReportRow> report = new ArrayList<LotReportRow>();
		for(Lot lot : listOfLots) {
			LotReportRow row = new LotReportRow();
			
			row.setLotId(lot.getId());
			
			row.setEntityIdOfLot(lot.getEntityId());
			
			Long lotBalance = getActualLotBalance(lot.getId());
			row.setActualLotBalance(lotBalance);
			
			Location location = germplasmManager.getLocationByID(lot.getLocationId());
			row.setLocationOfLot(location);
			
			Scale scale = traitManager.getScaleByID(lot.getScaleId());
			row.setScaleOfLot(scale);
			
			report.add(row);
		}
		return report;
	}
	
}
