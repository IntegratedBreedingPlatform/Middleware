package org.generationcp.middleware.dao.oms;

import org.generationcp.middleware.domain.dms.NameType;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.junit.Assert;
import org.junit.Test;

public class CvTermSynonymDaoTest {

    @Test
    public void testBuildCvTermSynonymSuccess(){
        CVTermSynonym cvTermSynonym = CvTermSynonymDao.buildCvTermSynonym(12, "UpdatedVariableName", NameType.ALTERNATIVE_ENGLISH.getId());
        Assert.assertNotNull(cvTermSynonym);
        Assert.assertEquals(12 ,cvTermSynonym.getCvTermId().intValue());
        Assert.assertEquals("UpdatedVariableName", cvTermSynonym.getSynonym());
        Assert.assertEquals(1230, cvTermSynonym.getTypeId().intValue());
    }
}
