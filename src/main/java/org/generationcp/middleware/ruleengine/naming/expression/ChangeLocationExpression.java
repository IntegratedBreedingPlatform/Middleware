package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ChangeLocationExpression extends BaseExpression {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeLocationExpression.class);

    public static final String KEY = "[CLABBR]";

    @Resource
    private GermplasmDataManager germplasmDataManager;

    @Override
    public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText) {
        for (StringBuilder container : values) {

            try {
                // TODO: change it!! this won't perform well
                Germplasm originalGermplasm = germplasmDataManager.getGermplasmByGID(Integer.valueOf(source.getGermplasm().getGid()));
                String suffixValue = "";
                if (source.getHarvestLocationId() != null && !originalGermplasm.getLocationId().equals(source.getHarvestLocationId())) {
                    suffixValue = source.getLocationAbbreviation();
                }

                this.replaceExpressionWithValue(container, suffixValue);
            } catch (MiddlewareQueryException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String getExpressionKey() {
        return KEY;
    }
}
