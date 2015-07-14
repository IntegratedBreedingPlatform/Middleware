
package org.generationcp.middleware.operation.saver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.util.Util;

public class LocdesSaver extends Saver {

	public LocdesSaver(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void saveLocationDescriptions(List<FieldMapInfo> infoList, int userId) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = this.extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);

				List<Locdes> descriptions = this.getLocdesDao().getByLocation(blockInfo.getBlockId());
				Map<String, Integer> udfldMap = this.getUdfldMap();

				this.saveOrUpdateLocdes(blockId, descriptions, this.getId(udfldMap, LocdesType.ROWS_IN_BLOCK),
						blockInfo.getNumberOfRowsInBlock(), userId);
				this.saveOrUpdateLocdes(blockId, descriptions, this.getId(udfldMap, LocdesType.RANGES_IN_BLOCK),
						blockInfo.getNumberOfRangesInBlock(), userId);
				this.saveOrUpdateLocdes(blockId, descriptions, this.getId(udfldMap, LocdesType.ROWS_IN_PLOT),
						blockInfo.getNumberOfRowsPerPlot(), userId);
				this.saveOrUpdateLocdes(blockId, descriptions, this.getId(udfldMap, LocdesType.MACHINE_ROW_CAPACITY),
						blockInfo.getMachineRowCapacity(), userId);
				this.saveOrUpdateLocdes(blockId, descriptions, this.getId(udfldMap, LocdesType.PLANTING_ORDER),
						blockInfo.getPlantingOrder(), userId);

				this.updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots(), userId);
			}
		}
	}

	private Map<String, Integer> getUdfldMap() throws MiddlewareQueryException {
		return this.getUserDefinedFieldDao()
				.getByCodesInMap(
						"LOCDES",
						"DTYPE",
						Arrays.asList(LocdesType.ROWS_IN_BLOCK.getCode(), LocdesType.RANGES_IN_BLOCK.getCode(),
								LocdesType.ROWS_IN_PLOT.getCode(), LocdesType.MACHINE_ROW_CAPACITY.getCode(),
								LocdesType.PLANTING_ORDER.getCode(), LocdesType.DELETED_PLOTS.getCode()));
	}

	private int getId(Map<String, Integer> map, LocdesType type) throws MiddlewareQueryException {
		Integer id = map.get(type.getCode());
		if (id == null) {
			throw new MiddlewareQueryException("Locdes Type " + type.getCode()
					+ " does not exists, please contact your system administrator");
		}
		return id;
	}

	public void updateDeletedPlots(List<FieldMapInfo> infoList, int userId) throws MiddlewareQueryException {
		Map<Integer, BlockInfo> blockMap = this.extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (Integer blockId : blockMap.keySet()) {
				BlockInfo blockInfo = blockMap.get(blockId);
				List<Locdes> descriptions = this.getLocdesDao().getByLocation(blockInfo.getBlockId());
				this.updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots(), userId);
			}
		}
	}

	private void updateDeletedPlots(Integer locId, List<Locdes> descriptions, List<String> deletedPlots, int userId)
			throws MiddlewareQueryException {
		Map<String, Integer> udfldMap = this.getUdfldMap();
		List<Locdes> savedDeletedPlots = this.findAllLocdes(descriptions, this.getId(udfldMap, LocdesType.DELETED_PLOTS));
		if (savedDeletedPlots != null && !savedDeletedPlots.isEmpty()) {
			for (Locdes savedDeletedPlot : savedDeletedPlots) {
				this.getLocdesDao().makeTransient(savedDeletedPlot);
			}
		}
		if (deletedPlots != null && !deletedPlots.isEmpty()) {
			for (String deletedPlot : deletedPlots) {
				Locdes locdes = this.createLocdes(locId, this.getId(udfldMap, LocdesType.DELETED_PLOTS), deletedPlot, userId);
				this.getLocdesDao().save(locdes);
			}
		}
	}

	private void saveOrUpdateLocdes(Integer locId, List<Locdes> descriptions, int typeId, Object value, int userId)
			throws MiddlewareQueryException {
		if (value != null) {
			Locdes locdes = this.findLocdes(descriptions, typeId);
			if (locdes == null) {
				locdes = this.createLocdes(locId, typeId, value, userId);
			}
			this.getLocdesDao().saveOrUpdate(locdes);
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
		locdes.setLocationId(locId);
		locdes.setTypeId(typeId);
		locdes.setDval(value.toString());
		locdes.setDdate(Util.getCurrentDateAsIntegerValue());
		locdes.setReferenceId(0);
		locdes.setUserId(userId);
		return locdes;
	}

	private Map<Integer, BlockInfo> extractAllBlockInformation(List<FieldMapInfo> infoList) {
		Map<Integer, BlockInfo> blockMap = new HashMap<Integer, LocdesSaver.BlockInfo>();

		for (FieldMapInfo info : infoList) {
			for (FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
					this.putAllBlockInformationToAMap(trial, blockMap);
				}
			}
		}

		return blockMap;
	}

	private void putAllBlockInformationToAMap(FieldMapTrialInstanceInfo trial, Map<Integer, BlockInfo> blockMap) {
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

	private class BlockInfo {

		private Integer blockId;
		private Integer numberOfRowsInBlock;
		private Integer numberOfRangesInBlock;
		private Integer plantingOrder;
		private Integer numberOfRowsPerPlot;
		private Integer machineRowCapacity;
		private List<String> deletedPlots;

		public Integer getBlockId() {
			return this.blockId;
		}

		public void setBlockId(Integer blockId) {
			this.blockId = blockId;
		}

		public Integer getNumberOfRowsInBlock() {
			return this.numberOfRowsInBlock;
		}

		public void setNumberOfRowsInBlock(Integer numberOfRowsInBlock) {
			this.numberOfRowsInBlock = numberOfRowsInBlock;
		}

		public Integer getNumberOfRangesInBlock() {
			return this.numberOfRangesInBlock;
		}

		public void setNumberOfRangesInBlock(Integer numberOfRangesInBlock) {
			this.numberOfRangesInBlock = numberOfRangesInBlock;
		}

		public Integer getPlantingOrder() {
			return this.plantingOrder;
		}

		public void setPlantingOrder(Integer plantingOrder) {
			this.plantingOrder = plantingOrder;
		}

		public Integer getNumberOfRowsPerPlot() {
			return this.numberOfRowsPerPlot;
		}

		public void setNumberOfRowsPerPlot(Integer numberOfRowsPerPlot) {
			this.numberOfRowsPerPlot = numberOfRowsPerPlot;
		}

		public Integer getMachineRowCapacity() {
			return this.machineRowCapacity;
		}

		public void setMachineRowCapacity(Integer machineRowCapacity) {
			this.machineRowCapacity = machineRowCapacity;
		}

		public List<String> getDeletedPlots() {
			return this.deletedPlots;
		}

		public void setDeletedPlots(List<String> deletedPlots) {
			this.deletedPlots = deletedPlots;
		}

	}
}
