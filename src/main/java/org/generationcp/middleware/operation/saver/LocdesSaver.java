package org.generationcp.middleware.operation.saver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;

public class LocdesSaver extends Saver {

	public LocdesSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void saveLocationDescriptions(List<FieldMapInfo> infoList) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);
				
				setWorkingDatabase(blockInfo.getBlockId());
				List<Locdes> descriptions = getLocdesDao().getByLocation(blockInfo.getBlockId());
				
				setWorkingDatabase(Database.LOCAL);
				saveOrUpdateLocdes(blockId, descriptions, LocdesType.COLUMNS_IN_BLOCK, blockInfo.getNumberOfColumnsInBlock());
				saveOrUpdateLocdes(blockId, descriptions, LocdesType.RANGES_IN_BLOCK, blockInfo.getNumberOfRangesInBlock());
				saveOrUpdateLocdes(blockId, descriptions, LocdesType.ROWS_IN_PLOT, blockInfo.getNumberOfRowsPerPlot());
				saveOrUpdateLocdes(blockId, descriptions, LocdesType.MACHINE_ROW_CAPACITY, blockInfo.getMachineRowCapacity());
				saveOrUpdateLocdes(blockId, descriptions, LocdesType.PLANTING_ORDER, blockInfo.getPlantingOrder());
				
				updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots());
			}
		}
	}
	
	public void updateDeletedPlots(List<FieldMapInfo> infoList) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);
				
				setWorkingDatabase(blockInfo.getBlockId());
				List<Locdes> descriptions = getLocdesDao().getByLocation(blockInfo.getBlockId());
				
				setWorkingDatabase(Database.LOCAL);
				updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots());
			}
		}
	}
	
	public void updateDeletedPlots(Integer locId, List<Locdes> descriptions, List<String> deletedPlots) throws MiddlewareQueryException {
		List<Locdes> savedDeletedPlots = findAllLocdes(descriptions, LocdesType.DELETED_PLOTS);
		if (savedDeletedPlots != null && !savedDeletedPlots.isEmpty()) {
			for (Locdes savedDeletedPlot : savedDeletedPlots) {
				getLocdesDao().makeTransient(savedDeletedPlot);
			}
		}
		if (deletedPlots != null && !deletedPlots.isEmpty()) {
			for (String deletedPlot : deletedPlots) {
				Locdes locdes = createLocdes(locId, LocdesType.DELETED_PLOTS, deletedPlot);
				getLocdesDao().save(locdes);
			}
		}
	}
	
	private void saveOrUpdateLocdes(Integer locId, List<Locdes> descriptions, LocdesType type, Object value) throws MiddlewareQueryException {
		if (value != null) {
			Locdes locdes = findLocdes(descriptions, type);
			if (locdes == null) {
				locdes = createLocdes(locId, type, value);
			}
			getLocdesDao().saveOrUpdate(locdes);
		}
	}
	
	private Locdes findLocdes(List<Locdes> descriptions, LocdesType type) {
		if (descriptions != null && !descriptions.isEmpty()) {
			for (Locdes description : descriptions) {
				if (description.getTypeId() == type.getId()) {
					return description;
				}
			}
		}
		return null;
	}
	
	private List<Locdes> findAllLocdes(List<Locdes> descriptions, LocdesType type) {
		List<Locdes> list = new ArrayList<Locdes>();
		if (descriptions != null && !descriptions.isEmpty()) {
			for (Locdes description : descriptions) {
				if (description.getTypeId() == type.getId()) {
					list.add(description);
				}
			}
		}
		return list;
	}
	
	private Locdes createLocdes(Integer locId, LocdesType type, Object value) throws MiddlewareQueryException {
		Locdes locdes = new Locdes();
		locdes.setLdid(getLocdesDao().getNegativeId("ldid"));
		locdes.setLocationId(locId);
		locdes.setTypeId(type.getId());
		locdes.setDval(value.toString());
		DateFormat df = new SimpleDateFormat("YYYYMMDD");
		locdes.setDdate(Integer.valueOf(df.format(new Date())));
		locdes.setReferenceId(0);
		locdes.setUserId(0);
		return locdes;
	}

	private Map<Integer, BlockInfo> extractAllBlockInformation(List<FieldMapInfo> infoList) {
		Map<Integer, BlockInfo> blockMap = new HashMap<Integer, LocdesSaver.BlockInfo>();

		for (FieldMapInfo info : infoList) {
			for (FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
					if (trial.getBlockId() != null) {
						BlockInfo blockInfo = blockMap.get(trial.getBlockId());

						if (blockInfo == null) {
							blockInfo = new BlockInfo();
							blockInfo.setBlockId(trial.getBlockId());
							blockInfo.setMachineRowCapacity(trial.getMachineRowCapacity());
							blockInfo.setNumberOfColumnsInBlock(trial.getColumnsInBlock());
							blockInfo.setNumberOfRangesInBlock(trial.getRangesInBlock());
							blockInfo.setNumberOfRowsPerPlot(trial.getRowsPerPlot());
							blockInfo.setPlantingOrder(trial.getPlantingOrder());
							blockInfo.setDeletedPlots(trial.getDeletedPlots());
							
							blockMap.put(trial.getBlockId(), blockInfo);
						}
					}
				}
			}
		}
		
		return blockMap;
	}
	
	private class BlockInfo {
		private Integer blockId;
		private Integer numberOfColumnsInBlock;
		private Integer numberOfRangesInBlock;
		private Integer plantingOrder;
		private Integer numberOfRowsPerPlot;
		private Integer machineRowCapacity;
		private List<String> deletedPlots;
		
		public Integer getBlockId() {
			return blockId;
		}
		public void setBlockId(Integer blockId) {
			this.blockId = blockId;
		}
		public Integer getNumberOfColumnsInBlock() {
			return numberOfColumnsInBlock;
		}
		public void setNumberOfColumnsInBlock(Integer numberOfColumnsInBlock) {
			this.numberOfColumnsInBlock = numberOfColumnsInBlock;
		}
		public Integer getNumberOfRangesInBlock() {
			return numberOfRangesInBlock;
		}
		public void setNumberOfRangesInBlock(Integer numberOfRangesInBlock) {
			this.numberOfRangesInBlock = numberOfRangesInBlock;
		}
		public Integer getPlantingOrder() {
			return plantingOrder;
		}
		public void setPlantingOrder(Integer plantingOrder) {
			this.plantingOrder = plantingOrder;
		}
		public Integer getNumberOfRowsPerPlot() {
			return numberOfRowsPerPlot;
		}
		public void setNumberOfRowsPerPlot(Integer numberOfRowsPerPlot) {
			this.numberOfRowsPerPlot = numberOfRowsPerPlot;
		}
		public Integer getMachineRowCapacity() {
			return machineRowCapacity;
		}
		public void setMachineRowCapacity(Integer machineRowCapacity) {
			this.machineRowCapacity = machineRowCapacity;
		}
		public List<String> getDeletedPlots() {
			return deletedPlots;
		}
		public void setDeletedPlots(List<String> deletedPlots) {
			this.deletedPlots = deletedPlots;
		}

	}
}
