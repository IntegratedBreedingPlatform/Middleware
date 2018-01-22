
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
	 * @return the entryCode
	 */
	String getEntryCode();

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
	String getFemaleParent();

	/**
	 * @return the female GID
	 */
	Integer getFgid();

	/**
	 * @return the Male Parent
	 */
	String getMaleParent();

	/**
	 * @return the male GID
	 */
	Integer getMgid();

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
