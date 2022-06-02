
package org.generationcp.middleware.interfaces;

/**
 * 
 * @author Aldrin Batac
 * 
 */
public interface GermplasmExportSource {

	/**
	 * @return the germplasmId
	 */
	Integer getGermplasmId();

	/**
	 * @return the checkType
	 */
	Integer getCheckType();

	/**
	 * @return the checkType
	 */
	String getCheckTypeDescription();

	/**
	 * @return the entryId
	 */
	Integer getEntryId();

	/**
	 * @return the seedSource
	 */
	String getSeedSource();

	/**
	 * @return the designation
	 */
	String getDesignation();

	/**
	 * @return the groupName
	 */
	String getGroupName();

	/**
	 * @return the female Parent
	 */
	String getFemaleParentDesignation();

	/**
	 * @return the female GID
	 */
	Integer getFemaleGid();

	/**
	 * @return the Male Parent
	 */
	String getMaleParentDesignation();

	/**
	 * @return the male GID
	 */
	Integer getMaleGid();

	/**
	 * @return the StockID
	 */
	String getStockIDs();

	/**
	 * @return the seed amount
	 */
	String getSeedAmount();

	/**
	 * @return groupId
	 */
	Integer getGroupId();

	String getNotes();

	Integer getListDataId();
}
