
package org.generationcp.middleware.reports;

import java.util.Map;

public class MShipList extends AbstractReporter {

	@Override
	public Reporter createReporter() {
		Reporter r = new MShipList();
		r.setFileNameExpression("Maize_SHIPM_{shipId}");
		return r;
	}

	@Override
	public String getReportCode() {
		return "MFbShipList";
	}

	@Override
	public String getTemplateName() {
		return "MFb3_main";
	}

	@Override
	public Map<String, Object> buildJRParams(Map<String, Object> args) {
		Map<String, Object> params = super.buildJRParams(args);

        // removed previous code placing ?? as value as we need the report blank for the user to manually fill up

        // placing blank values for the expected parameters so that it does not appear as null in the report
        this.setBlankParameters(params, "recLastName", "recFirstName","institution", "shippingAddress", "country", "contactNumber", "phytoInstr", "shippingInstr",
                "shipFrom", "shipId", "prepDate", "carrier", "airwayBill", "dateSent", "quantity", "commments");

		return params;
	}

    protected void setBlankParameters(Map<String, Object> paramMap, String ... blankItems) {
        for (String blankItem : blankItems) {
            paramMap.put(blankItem, "");
        }
    }

}