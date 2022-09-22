package org.generationcp.middleware.service.pedigree;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.generationcp.middleware.service.pedigree.cache.keys.CropMethodKey;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class PedigreeServiceImplTest extends IntegrationTestBase {
    private static final String MAIZE = "maize";
    private Map<CropGermplasmKey, Germplasm> germplasmMap;
    private Map<CropMethodKey, Method> methodsMap;

    private Set<Integer> randomNumbers;

    @Autowired
    private PedigreeService pedigreeService;

    private CrossExpansionProperties crossExpansionProperties;

    @Before
    public void setup() {
        // Keep our generated germplasm in this map
        this.germplasmMap = new HashMap<>();

        // Keep our generated methods in this map
        this.methodsMap = new HashMap<>();

        this.randomNumbers = new LinkedHashSet<>();

        this.generateRandomGermplasm();

        final Properties mockProperties = Mockito.mock(Properties.class);
        Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
        this.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
        this.crossExpansionProperties.setDefaultLevel(1);
    }

    private void generateRandomGermplasm() {
        final Random randomNumGenerator = new Random();
        while (this.randomNumbers.size() < 10000) {
            final Integer next = randomNumGenerator.nextInt(10000) + 1;
            // Duplicates are ignored since it is a set
            this.randomNumbers.add(next);
        }
    }

    private Germplasm generateRandomGermplasmRecurringMaleParent() {
        final Random randomGenerator = new Random();
        final int femaleSideNodes = randomGenerator.nextInt(5) + 1;
        final int maleSideNodes = randomGenerator.nextInt(5) + 1;

        final Germplasm rootGermplasm = this.generateTestGermplasm(1, 1);
        this.generateTreeRecurring(rootGermplasm, femaleSideNodes, maleSideNodes, 5, this.randomNumbers.iterator());
        return rootGermplasm;
    }

    private Germplasm generateTestGermplasm(final int gid, final int methodId) {
        final Germplasm germplasm = new Germplasm();
        germplasm.setGid(gid);
        germplasm.setGnpgs(2);
        final Method method = new Method(methodId);
        if(methodId > 1) {
            method.setMname("Backcross");
        }
        germplasm.setMethod(method);
        this.germplasmMap.put(new CropGermplasmKey(PedigreeServiceImplTest.MAIZE, gid), germplasm);
        this.methodsMap.put(new CropMethodKey(PedigreeServiceImplTest.MAIZE, methodId), method);
        return germplasm;
    }

    private void generateTreeRecurring(final Germplasm germplasm, final int femaleSideNodes, final int maleSideNodes, final int recurringMale,
                                       final Iterator<Integer> iterator) {

        if (femaleSideNodes != 0) {
            final Integer femaleGid = iterator.next();
            final Germplasm generateTestGermplasm = this.generateTestGermplasm(femaleGid, 107);
            this.generateTreeRecurring(generateTestGermplasm, femaleSideNodes - 1, maleSideNodes, recurringMale, iterator);
            germplasm.setGpid1(femaleGid);
        }else {
            germplasm.setGpid1(0);
        }

        if (maleSideNodes != 0) {
            final Germplasm generateTestGermplasm = this.generateTestGermplasm(recurringMale, 107);
            this.generateTreeRecurring(generateTestGermplasm, femaleSideNodes, maleSideNodes - 1, recurringMale, iterator);
            germplasm.setGpid2(recurringMale);
        }else {
            germplasm.setGpid2(0);
        }

    }

    @Test
    public void testGetCrossExpansions() {
        final Germplasm generateRandomGermplasm = this.generateRandomGermplasmRecurringMaleParent();
        final Set<Integer> set = new HashSet<>();
        set.add(generateRandomGermplasm.getGid());
        try{
            this.pedigreeService.getCrossExpansions(set, 5, this.crossExpansionProperties);
        }catch (final MiddlewareException ex){
            Assert.assertTrue(ex.getMessage().contains("Problem building pedigree string for gid"));
        }


    }
}
