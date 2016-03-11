package org.generationcp.middleware.pojos.germplasm;

import java.io.Serializable;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

/**
 * A node in which is part of a germplasm pedigree history
 */
public interface GermplasmCrossElementNode extends Serializable {

		/**
		 * Method that returns you the Pedigree string based on the crop and properties passed to it.
		 * @param cropName the name of the crop for which we need to generate the pedigree string
		 * @param crossExpansionProperties properties to customize the pedigree string
		 * @param pedigreeDataManagerFactory your helper to access the database.
		 * @return
		 */
		String getCrossExpansionString(final String cropName, final CrossExpansionProperties crossExpansionProperties, PedigreeDataManagerFactory pedigreeDataManagerFactory);

		/**
		 *
		 * @param germplasm the germplasm which this nodes represents
		 */
		void setGermplasm(Germplasm germplasm);

		/**
		 * @return the germplasm which this node represents
		 */
		Germplasm getGermplasm();

		/**
		 * @return is this the root node.
		 */
		boolean isRootNode();

		/**
		 * @param rootNode set if this is the root node
		 */
		void setRootNode(boolean rootNode);

}
