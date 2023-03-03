package org.generationcp.middleware.api.genotype;

import org.generationcp.middleware.domain.genotype.GenotypeImportRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.pojos.Sample;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GenotypeServiceImpl implements GenotypeService{

    private DaoFactory daoFactory;

    public GenotypeServiceImpl(final HibernateSessionProvider sessionProvider) {
        this.daoFactory = new DaoFactory(sessionProvider);
    }

    @Override
    public List<Integer> importGenotypes(final List<GenotypeImportRequestDto> genotypeImportRequestDtos) {
        final List<Integer> genotypeIds = new ArrayList<>();
        for(GenotypeImportRequestDto importRequestDto: genotypeImportRequestDtos) {
            final Genotype genotype = new Genotype();
            genotype.setSample(new Sample(Integer.valueOf(importRequestDto.getSampleId())));
            final CVTerm variable = new CVTerm();
            variable.setCvTermId(Integer.valueOf(importRequestDto.getVariableId()));
            genotype.setValue(importRequestDto.getValue());
            this.daoFactory.getGenotypeDao().save(genotype);
            genotypeIds.add(genotype.getId());
        }
        return genotypeIds;
    }
}
