package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.domain.inventory_new.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory_new.LotDto;
import org.generationcp.middleware.domain.inventory_new.LotsSearchDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class LotServiceImpl implements LotService {

	private DaoFactory daoFactory;

	public LotServiceImpl() {
	}

	public LotServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ExtendedLotDto> searchLots(final LotsSearchDto lotsSearchDto,final Pageable pageable) {
		return this.daoFactory.getLotDao().searchLots(lotsSearchDto, pageable);
	}

	@Override
	public long countSearchLots(final LotsSearchDto lotsSearchDto) {
		return this.daoFactory.getLotDao().countSearchLots(lotsSearchDto);
	}

	@Override
	public Integer saveLot(final LotDto lotDto) {
		final Lot lot = new Lot();
		lot.setUserId(1);
		lot.setComments(lotDto.getComments());
		lot.setCreatedDate(new Date());
		lot.setEntityId(lotDto.getGid());
		lot.setEntityType("GERMPLSM");
		lot.setLocationId(lotDto.getLocationId());
		lot.setStockId(lotDto.getStockId());
		lot.setStatus(0);
		lot.setSource(0);
		lot.setScaleId(lot.getScaleId());

		if (lotDto.getTransactions().size() == 1) {
			Transaction transaction = new Transaction();
			transaction.setStatus(TransactionStatus.COMMITTED.getIntValue());
			transaction.setLot(lot);
			transaction.setPersonId(1);
			transaction.setTransactionDate(new Date());
			transaction.setQuantity(lotDto.getTransactions().get(0).getAmount());
			lot.getTransactions().add(transaction);
		}

		return this.daoFactory.getLotDao().save(lot).getId();
	}
}
