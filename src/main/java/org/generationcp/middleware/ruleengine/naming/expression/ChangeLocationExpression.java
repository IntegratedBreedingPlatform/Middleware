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

    // TODO: refactor. Try to avoid hitting the DB for each line
    @Resource
    private GermplasmDataManager germplasmDataManager;

    @Override
    public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
        for (final StringBuilder container : values) {

            try {
                // TODO: change it!! this won't perform well
                final Germplasm originalGermplasm = germplasmDataManager.getGermplasmByGID(advancingSource.getOriginGermplasm().getGid());
                String suffixValue = "";
                if (advancingSource.getHarvestLocationId() != null && !originalGermplasm.getLocationId().equals(advancingSource.getHarvestLocationId())) {
                    suffixValue = advancingSource.getLocationAbbreviation();
                }

                this.replaceExpressionWithValue(container, suffixValue);
            } catch (final MiddlewareQueryException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String getExpressionKey() {
        return KEY;
    }
}
