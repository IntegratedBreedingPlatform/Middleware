/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos;

/**
 * Tables and Types in the UDFLDS table.
 *
 */
public enum UDTableType {

	NAMES_NAME("NAMES", "NAME"), NAMES_ABBREVIATION("NAMES", "ABBREVIATION"), LOCDES_DTYPE("LOCDES", "DTYPE"), LOCATION_LTYPE("LOCATION",
			"LTYPE"), USERS_UTYPE("USERS", "UTYPE"), BIBREFS_PUBTYPE("BIBREFS", "PUBTYPE"), PERSONS_PROLE("PERSONS", "PROLE"), PERSONS_PSTATUS(
			"PERSONS", "PSTATUS"), INSTITUT_INSTYPE("INSTITUT", "INSTYPE"), LISTNMS_LISTTYPE("LISTNMS", "LISTTYPE"), LISTNMS_LISTINFO(
			"LISTNMS", "LISTINFO"), FACTOR_DESCRIPTION("FACTOR", "DESCRIPTION"), VARIATE_DESCRIPTION("VARIATE", "DESCRIPTION"), FACTOR_STUDY(
			"FACTOR", "STUDY"), IMS_TRANSACTION_WITHDRAWAL("IMS_TRANSACTION", "WITHDRAWAL"), GEOREF_LLSOURCE("GEOREF", "LLSOURCE"), GEOREF_LL_FMT(
			"GEOREF", "LL_FMT"), GEOREF_LL_DATUM("GEOREF", "LL_DATUM"), ADDRESS("ADDRESS", "ADDRTYPE"), EVENTMEM("EVENTMEM", "MEMROLE"), FILELINK(
			"FILELINK", "FILECAT");

	private String table;
	private String type;

	private UDTableType(String table, String type) {
		this.table = table;
		this.type = type;
	}

	public String getTable() {
		return this.table;
	}

	public void setTable(String fTable) {
		this.table = fTable;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String fType) {
		this.type = fType;
	}

}
