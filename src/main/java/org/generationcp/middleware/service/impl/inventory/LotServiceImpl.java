package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.inventory_new.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory_new.LotGeneratorInputDto;
import org.generationcp.middleware.domain.inventory_new.LotsSearchDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class LotServiceImpl implements LotService {

	private DaoFactory daoFactory;

	@Autowired
	private InventoryDataManager inventoryDataManager;

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
	public Integer saveLot(final LotGeneratorInputDto lotDto, final CropType cropType) {

		final Lot lot = new Lot();
		lot.setUserId(lotDto.getUserId());
		lot.setComments(lotDto.getComments());
		lot.setCreatedDate(new Date());
		lot.setEntityId(lotDto.getGid());
		lot.setEntityType("GERMPLSM");
		lot.setLocationId(lotDto.getLocationId());
		lot.setStockId(lotDto.getStockId());
		lot.setStatus(0);
		//FIXME we should be careful in the near future to set the right value here
		lot.setSource(0);
		lot.setScaleId(lotDto.getScaleId());
		this.inventoryDataManager.generateLotIds(cropType, Lists.newArrayList(lot));
		this.daoFactory.getLotDao().save(lot);

		return lot.getId();
	}
}
