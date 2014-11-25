package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LocdesSaver extends Saver {

	public LocdesSaver(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void saveLocationDescriptions(List<FieldMapInfo> infoList, int userId) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);
				
				setWorkingDatabase(blockInfo.getBlockId());
				List<Locdes> descriptions = getLocdesDao().getByLocation(blockInfo.getBlockId());
				Map<String, Integer> udfldMap = getUdfldMap();
				
				setWorkingDatabase(Database.LOCAL);
				saveOrUpdateLocdes(blockId, descriptions, getId(udfldMap, LocdesType.ROWS_IN_BLOCK), blockInfo.getNumberOfRowsInBlock(), userId);
				saveOrUpdateLocdes(blockId, descriptions, getId(udfldMap, LocdesType.RANGES_IN_BLOCK), blockInfo.getNumberOfRangesInBlock(), userId);
				saveOrUpdateLocdes(blockId, descriptions, getId(udfldMap, LocdesType.ROWS_IN_PLOT), blockInfo.getNumberOfRowsPerPlot(), userId);
				saveOrUpdateLocdes(blockId, descriptions, getId(udfldMap, LocdesType.MACHINE_ROW_CAPACITY), blockInfo.getMachineRowCapacity(), userId);
				saveOrUpdateLocdes(blockId, descriptions, getId(udfldMap, LocdesType.PLANTING_ORDER), blockInfo.getPlantingOrder(), userId);
				
				updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots(), userId);
			}
		}
	}
	
	private Map<String, Integer> getUdfldMap() throws MiddlewareQueryException {
		setWorkingDatabase(Database.LOCAL);
		Map<String, Integer> udfldMap = getUserDefinedFieldDao().getByCodesInMap("LOCDES", "DTYPE", 
				Arrays.asList(LocdesType.ROWS_IN_BLOCK.getCode()
						, LocdesType.RANGES_IN_BLOCK.getCode()
						, LocdesType.ROWS_IN_PLOT.getCode()
						, LocdesType.MACHINE_ROW_CAPACITY.getCode()
						, LocdesType.PLANTING_ORDER.getCode()
						, LocdesType.DELETED_PLOTS.getCode()));
		return udfldMap;
	}
	
	private int getId(Map<String, Integer> map, LocdesType type) throws MiddlewareQueryException {
		Integer id = map.get(type.getCode());
		if (id == null) {
			throw new MiddlewareQueryException("Locdes Type " + type.getCode() + " does not exists, please contact your system administrator");
		}
		return id;
	}
	
	public void updateDeletedPlots(List<FieldMapInfo> infoList, int userId) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);
				
				setWorkingDatabase(blockInfo.getBlockId());
				List<Locdes> descriptions = getLocdesDao().getByLocation(blockInfo.getBlockId());
				
				setWorkingDatabase(Database.LOCAL);
				updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots(), userId);
			}
		}
	}
	
	private void updateDeletedPlots(Integer locId, List<Locdes> descriptions, List<String> deletedPlots, int userId) throws MiddlewareQueryException {
		Map<String, Integer> udfldMap = getUdfldMap();
		setWorkingDatabase(Database.LOCAL);
		List<Locdes> savedDeletedPlots = findAllLocdes(descriptions, getId(udfldMap, LocdesType.DELETED_PLOTS));
		if (savedDeletedPlots != null && !savedDeletedPlots.isEmpty()) {
			for (Locdes savedDeletedPlot : savedDeletedPlots) {
				getLocdesDao().makeTransient(savedDeletedPlot);
			}
		}
		if (deletedPlots != null && !deletedPlots.isEmpty()) {
			setWorkingDatabase(Database.LOCAL);
			for (String deletedPlot : deletedPlots) {
				Locdes locdes = createLocdes(locId, getId(udfldMap, LocdesType.DELETED_PLOTS), deletedPlot, userId);
				getLocdesDao().save(locdes);
			}
		}
	}
	
	private void saveOrUpdateLocdes(Integer locId, List<Locdes> descriptions, int typeId, Object value, int userId) throws MiddlewareQueryException {
		if (value != null) {
			Locdes locdes = findLocdes(descriptions, typeId);
			if (locdes == null) {
				locdes = createLocdes(locId, typeId, value, userId);
			}
			getLocdesDao().saveOrUpdate(locdes);
		}
	}
	
	private Locdes findLocdes(List<Locdes> descriptions, int typeId) {
		if (descriptions != null && !descriptions.isEmpty()) {
			for (Locdes description : descriptions) {
				if (description.getTypeId() == typeId) {
					return description;
				}
			}
		}
		return null;
	}
	
	private List<Locdes> findAllLocdes(List<Locdes> descriptions, int typeId) {
		List<Locdes> list = new ArrayList<Locdes>();
		if (descriptions != null && !descriptions.isEmpty()) {
			for (Locdes description : descriptions) {
				if (description.getTypeId() == typeId) {
					list.add(description);
				}
			}
		}
		return list;
	}
	
	private Locdes createLocdes(Integer locId, int typeId, Object value, int userId) throws MiddlewareQueryException {
		Locdes locdes = new Locdes();
		locdes.setLdid(getLocdesDao().getNegativeId("ldid"));
		locdes.setLocationId(locId);
		locdes.setTypeId(typeId);
		locdes.setDval(value.toString());
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		locdes.setDdate(Integer.valueOf(df.format(new Date())));
		locdes.setReferenceId(0);
		locdes.setUserId(userId);
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
							blockInfo.setNumberOfRowsInBlock(trial.getRowsInBlock());
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
		private Integer numberOfRowsInBlock;
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
		public Integer getNumberOfRowsInBlock() {
			return numberOfRowsInBlock;
		}
		public void setNumberOfRowsInBlock(Integer numberOfRowsInBlock) {
			this.numberOfRowsInBlock = numberOfRowsInBlock;
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
