
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Locdes;
import org.generationcp.middleware.pojos.LocdesType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocdesSaver extends Saver {

	private DaoFactory daoFactory;

	public LocdesSaver(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public void saveLocationDescriptions(final List<FieldMapInfo> infoList, final int userId) throws MiddlewareQueryException {
		final Map<Integer, BlockInfo> blockMap = this.extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (final Integer blockId : blockMap.keySet()) {
				final BlockInfo blockInfo = blockMap.get(blockId);

				final List<Locdes> descriptions = this.daoFactory.getLocDesDao().getByLocation(blockInfo.getBlockId());
				final Map<String, Integer> udfldMap = this.getUdfldMap();

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
		final List<String> codes = Arrays.asList(LocdesType.ROWS_IN_BLOCK.getCode(), LocdesType.RANGES_IN_BLOCK.getCode(),
			LocdesType.ROWS_IN_PLOT.getCode(), LocdesType.MACHINE_ROW_CAPACITY.getCode(),
			LocdesType.PLANTING_ORDER.getCode(), LocdesType.DELETED_PLOTS.getCode());
		return daoFactory.getUserDefinedFieldDAO()
			.getByCodes(
				"LOCDES",
				Collections.singleton("DTYPE"),
				new HashSet<>(codes)).stream().collect(
				Collectors.toMap(UserDefinedField::getFcode, UserDefinedField::getFldno));
	}

	private int getId(final Map<String, Integer> map, final LocdesType type) throws MiddlewareQueryException {
		final Integer id = map.get(type.getCode());
		if (id == null) {
			throw new MiddlewareQueryException("Locdes Type " + type.getCode()
					+ " does not exists, please contact your system administrator");
		}
		return id;
	}

	public void updateDeletedPlots(final List<FieldMapInfo> infoList, final int userId) throws MiddlewareQueryException {
		final Map<Integer, BlockInfo> blockMap = this.extractAllBlockInformation(infoList);
		if (!blockMap.isEmpty()) {
			for (final Integer blockId : blockMap.keySet()) {
				final BlockInfo blockInfo = blockMap.get(blockId);
				final List<Locdes> descriptions = this.daoFactory.getLocDesDao().getByLocation(blockInfo.getBlockId());
				this.updateDeletedPlots(blockId, descriptions, blockInfo.getDeletedPlots(), userId);
			}
		}
	}

	private void updateDeletedPlots(final Integer locId, final List<Locdes> descriptions, final List<String> deletedPlots, final int userId)
			throws MiddlewareQueryException {
		final Map<String, Integer> udfldMap = this.getUdfldMap();
		final List<Locdes> savedDeletedPlots = this.findAllLocdes(descriptions, this.getId(udfldMap, LocdesType.DELETED_PLOTS));
		if (savedDeletedPlots != null && !savedDeletedPlots.isEmpty()) {
			for (final Locdes savedDeletedPlot : savedDeletedPlots) {
				this.daoFactory.getLocDesDao().makeTransient(savedDeletedPlot);
			}
		}
		if (deletedPlots != null && !deletedPlots.isEmpty()) {
			for (final String deletedPlot : deletedPlots) {
				final Locdes locdes = this.createLocdes(locId, this.getId(udfldMap, LocdesType.DELETED_PLOTS), deletedPlot, userId);
				this.daoFactory.getLocDesDao().save(locdes);
			}
		}
	}

	public void saveOrUpdateLocdes(final Integer locId, final List<Locdes> descriptions, final int typeId, final Object value,
			final int userId) throws MiddlewareQueryException {
		if (value != null) {
			Locdes locdes = this.findLocdes(descriptions, typeId);
			if (locdes == null) {
				locdes = this.createLocdes(locId, typeId, value, userId);
			}
			this.daoFactory.getLocDesDao().saveOrUpdate(locdes);
		}
	}

	private Locdes findLocdes(final List<Locdes> descriptions, final int typeId) {
		if (descriptions != null && !descriptions.isEmpty()) {
			for (final Locdes description : descriptions) {
				if (description.getTypeId() == typeId) {
					return description;
				}
			}
		}
		return null;
	}

	private List<Locdes> findAllLocdes(final List<Locdes> descriptions, final int typeId) {
		final List<Locdes> list = new ArrayList<Locdes>();
		if (descriptions != null && !descriptions.isEmpty()) {
			for (final Locdes description : descriptions) {
				if (description.getTypeId() == typeId) {
					list.add(description);
				}
			}
		}
		return list;
	}

	private Locdes createLocdes(final Integer locId, final int typeId, final Object value, final int userId)
			throws MiddlewareQueryException {
		final Locdes locdes = new Locdes();
		locdes.setLocationId(locId);
		locdes.setTypeId(typeId);
		locdes.setDval(value.toString());
		locdes.setDdate(Util.getCurrentDateAsIntegerValue());
		locdes.setReferenceId(0);
		locdes.setUserId(userId);
		return locdes;
	}

	private Map<Integer, BlockInfo> extractAllBlockInformation(final List<FieldMapInfo> infoList) {
		final Map<Integer, BlockInfo> blockMap = new HashMap<Integer, LocdesSaver.BlockInfo>();

		for (final FieldMapInfo info : infoList) {
			for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
				for (final FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
					this.putAllBlockInformationToAMap(trial, blockMap);
				}
			}
		}

		return blockMap;
	}

	private void putAllBlockInformationToAMap(final FieldMapTrialInstanceInfo trial, final Map<Integer, BlockInfo> blockMap) {
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

		public void setBlockId(final Integer blockId) {
			this.blockId = blockId;
		}

		public Integer getNumberOfRowsInBlock() {
			return this.numberOfRowsInBlock;
		}

		public void setNumberOfRowsInBlock(final Integer numberOfRowsInBlock) {
			this.numberOfRowsInBlock = numberOfRowsInBlock;
		}

		public Integer getNumberOfRangesInBlock() {
			return this.numberOfRangesInBlock;
		}

		public void setNumberOfRangesInBlock(final Integer numberOfRangesInBlock) {
			this.numberOfRangesInBlock = numberOfRangesInBlock;
		}

		public Integer getPlantingOrder() {
			return this.plantingOrder;
		}

		public void setPlantingOrder(final Integer plantingOrder) {
			this.plantingOrder = plantingOrder;
		}

		public Integer getNumberOfRowsPerPlot() {
			return this.numberOfRowsPerPlot;
		}

		public void setNumberOfRowsPerPlot(final Integer numberOfRowsPerPlot) {
			this.numberOfRowsPerPlot = numberOfRowsPerPlot;
		}

		public Integer getMachineRowCapacity() {
			return this.machineRowCapacity;
		}

		public void setMachineRowCapacity(final Integer machineRowCapacity) {
			this.machineRowCapacity = machineRowCapacity;
		}

		public List<String> getDeletedPlots() {
			return this.deletedPlots;
		}

		public void setDeletedPlots(final List<String> deletedPlots) {
			this.deletedPlots = deletedPlots;
		}

	}
}
