package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GermplasmDataManagerDataInitializer {

    public static final Integer TEST_GID = 1;
    public static final Integer TEST_MALE_PARENT_GID = 2;
    public static final Integer TEST_FEMALE_PARENT_GID = 3;
    public static final Integer NSTAT_PREFERRED_VALUE = 1;
    public static final Integer TEST_LOCATION_ID = 1;
    public static final String MALE_SELECTION_HISTORY = "maleSelectionHistory";


    public static List<Germplasm> createGermplasmList() {
        final List<Germplasm> germplasmList = new ArrayList<>();
        final Germplasm germplasm = new Germplasm();
        germplasm.setGid(TEST_GID);
        germplasm.setGpid1(TEST_FEMALE_PARENT_GID);
        germplasm.setGpid2(TEST_MALE_PARENT_GID);

        germplasmList.add(germplasm);

        return germplasmList;
    }

    public static Name createGermplasmName(final Integer gid, final String name) {
        return new Name(null, new Germplasm(gid), GermplasmNameType.LINE_NAME.getUserDefinedFieldID(),
                NSTAT_PREFERRED_VALUE, name, TEST_LOCATION_ID, 20160216, 0);
    }

    public static Map<Integer, GermplasmPedigreeTreeNode> createTreeNodeMap(final boolean singleParent) {
        final Map<Integer, GermplasmPedigreeTreeNode> treeNodeMap = new HashMap<>();
        final GermplasmPedigreeTreeNode rootNode = new GermplasmPedigreeTreeNode();
        final Germplasm rootGermplasm = new Germplasm(TEST_GID);
        rootNode.setGermplasm(rootGermplasm);

        final List<GermplasmPedigreeTreeNode> parentNodeList = new ArrayList<>();
        if (singleParent) {
            parentNodeList.add(null);
        } else {
            final Germplasm femaleGermplasm = new Germplasm(TEST_FEMALE_PARENT_GID);
            femaleGermplasm.setSelectionHistory("femaleSelectionHistory");
            final GermplasmPedigreeTreeNode femaleTreeNode = new GermplasmPedigreeTreeNode();
            femaleTreeNode.setGermplasm(femaleGermplasm);
            rootNode.setFemaleParent(femaleTreeNode);
            parentNodeList.add(femaleTreeNode);
        }

        final Germplasm maleGermplasm = new Germplasm(TEST_MALE_PARENT_GID);
        maleGermplasm.setSelectionHistory(MALE_SELECTION_HISTORY);
        final GermplasmPedigreeTreeNode maleTreeNode = new GermplasmPedigreeTreeNode();
        maleTreeNode.setGermplasm(maleGermplasm);
        parentNodeList.add(maleTreeNode);
        rootNode.setMaleParent(maleTreeNode);
        rootNode.setLinkedNodes(parentNodeList);


        treeNodeMap.put(TEST_GID, rootNode);
        return treeNodeMap;
    }
}
