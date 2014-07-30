package org.generationcp.middleware.domain.inventory;


/**
 * POJO for storing aggregate inventory data for specific
 * GermplasmListData record
 * 
 * @author Darla Ani
 *
 */
public class ListDataInventory extends GermplasmInventory  {
	
	private static final long serialVersionUID = -8594381810347667269L;

	private Integer listDataId;

	public ListDataInventory(Integer listDataId, Integer gid) {
		super(gid);
		this.listDataId = listDataId;
	}

	public Integer getListDataId() {
		return listDataId;
	}

	public void setListDataId(Integer listDataId) {
		this.listDataId = listDataId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListDataInventory [listDataId=");
		builder.append(listDataId);
		builder.append(", ");
		builder.append(getFieldsToString());
		builder.append("]");
		return builder.toString();
	}
	
	

}
