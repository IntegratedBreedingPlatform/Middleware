
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.pojos.UserDefinedField;

public class UserDefinedFieldTestDataInitializer {

	public static Map<String, String> validListTypeMap = new HashMap<String, String>();
	private List<UserDefinedField> validListType;
	
	static {
		validListTypeMap.put("LST", "Generic List");
		validListTypeMap.put("F1", "F1 NURSERY LIST");
		validListTypeMap.put("F2", "F2 NURSERY LIST");
		validListTypeMap.put("PN", "PEDIGREE NURSERY LIST");
	}
	
	public UserDefinedFieldTestDataInitializer(){
		this.populateValidListType();
	}
	
	public static UserDefinedField createUserDefinedField(final String fcode, final String fname) {
		final UserDefinedField udField = new UserDefinedField();
		udField.setFcode(fcode);
		udField.setFname(fname);
		return udField;
	}

	public static UserDefinedField createUserDefinedField(final String tableName, final String ftype, final String fname) {
		final UserDefinedField udField = UserDefinedFieldTestDataInitializer.createUserDefinedField("FCODE12345", fname);
		udField.setFtable(tableName);
		udField.setFtype(ftype);
		udField.setFdate(20060123);
		udField.setFdesc(fname);
		udField.setFfmt("-");
		udField.setFuid(0);
		udField.setLfldno(0);
		udField.setScaleid(0);
		return udField;
	}
	
	private void populateValidListType() {
		this.validListType = new ArrayList<UserDefinedField>();
		
		for(Map.Entry<String, String> item: validListTypeMap.entrySet()){
			this.validListType.add(UserDefinedFieldTestDataInitializer.createUserDefinedField(item.getKey(), item.getValue()));
		}
	}
	
	public List<UserDefinedField> getValidListType() {
		return this.validListType;	
	}
}
