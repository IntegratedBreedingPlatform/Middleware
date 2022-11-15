
package org.generationcp.middleware.ruleengine.newnaming.service;

import org.generationcp.middleware.ruleengine.pojo.AbstractAdvancingSource;

import java.util.List;

public interface ProcessCodeService {

	List<String> applyProcessCode(String currentInput, String processCode, AbstractAdvancingSource source);
}
