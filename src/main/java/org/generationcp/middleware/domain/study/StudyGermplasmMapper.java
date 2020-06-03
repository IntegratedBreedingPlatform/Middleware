package org.generationcp.middleware.domain.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;

import java.util.HashSet;
import java.util.Set;

public class StudyGermplasmMapper {

    public StockModel map(final Integer studyId, final StudyGermplasmDto studyGermplasmDto) {
        final StockModel stockModel = new StockModel();
        stockModel.setProject(new DmsProject(studyId));
        stockModel.setName(studyGermplasmDto.getDesignation());
        stockModel.setGermplasm(new Germplasm(Integer.valueOf(studyGermplasmDto.getGermplasmId())));
        stockModel.setTypeId(TermId.ENTRY_CODE.getId());
        stockModel.setValue(studyGermplasmDto.getEntryCode());
        stockModel.setUniqueName(studyGermplasmDto.getEntryNumber().toString());
        stockModel.setIsObsolete(false);

        final Set<StockProperty> stockProperties = new HashSet<>();
        final StockProperty entryTypeProperty = new StockProperty();
        entryTypeProperty.setStock(stockModel);
        entryTypeProperty.setRank(1);
        entryTypeProperty.setTypeId(TermId.ENTRY_TYPE.getId());
        // TODO IBP-3697 should this be getEntryType()?
        entryTypeProperty.setValue(studyGermplasmDto.getCheckType().toString());
        stockProperties.add(entryTypeProperty);
        stockModel.setProperties(stockProperties);

        final StockProperty seedSourceProperty = new StockProperty();
        seedSourceProperty.setStock(stockModel);
        seedSourceProperty.setRank(2);
        seedSourceProperty.setTypeId(TermId.SEED_SOURCE.getId());
        seedSourceProperty.setValue(studyGermplasmDto.getSeedSource());
        stockProperties.add(seedSourceProperty);

        final StockProperty crossProperty = new StockProperty();
        crossProperty.setStock(stockModel);
        crossProperty.setRank(3);
        crossProperty.setTypeId(TermId.CROSS.getId());
        crossProperty.setValue(studyGermplasmDto.getCross());
        stockProperties.add(crossProperty);

        stockModel.setProperties(stockProperties);
        return stockModel;
    }
}
