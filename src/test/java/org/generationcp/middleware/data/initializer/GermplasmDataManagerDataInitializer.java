package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;

public class GermplasmDataManagerDataInitializer {

    public static final Integer TEST_GID = 1;
    public static final Integer TEST_MALE_PARENT_GID = 2;
    public static final Integer TEST_FEMALE_PARENT_GID = 3;
    public static final Integer NSTAT_PREFERRED_VALUE = 1;
    public static final Integer TEST_USER_ID = 1;
    public static final Integer TEST_LOCATION_ID = 1;
    public static final String MALE_SELECTION_HISTORY = "maleSelectionHistory";

    public static Map<Integer, Map<GermplasmNameType, Name>> createGermplasmParentNameMap() {
        final Map<Integer, Map<GermplasmNameType, Name>> nameMap = new HashMap<>();

        Map<GermplasmNameType, Name> nameTypeMap = new HashMap<>();
        nameTypeMap.put(GermplasmNameType.LINE_NAME, createGermplasmName(TEST_GID, "gid"));
        nameMap.put(TEST_GID, nameTypeMap);

        nameTypeMap = new HashMap<>();
        nameTypeMap.put(GermplasmNameType.LINE_NAME, createGermplasmName(TEST_FEMALE_PARENT_GID, "femaleParent"));
        nameMap.put(TEST_FEMALE_PARENT_GID, nameTypeMap);

        nameTypeMap = new HashMap<>();
        nameTypeMap.put(GermplasmNameType.LINE_NAME, createGermplasmName(TEST_MALE_PARENT_GID, "maleParent"));
        nameMap.put(TEST_MALE_PARENT_GID, nameTypeMap);

        return nameMap;
    }

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
        return new Name(null, gid, GermplasmNameType.LINE_NAME.getUserDefinedFieldID(),
                NSTAT_PREFERRED_VALUE, TEST_USER_ID, name, TEST_LOCATION_ID, 20160216, 0);
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
