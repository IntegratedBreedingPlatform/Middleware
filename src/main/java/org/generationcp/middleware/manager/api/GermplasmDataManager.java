/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.FindGermplasmByNameModes;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GidNidElement;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;

/**
 * This is the API for retrieving Germplasm information.
 * 
 * @author Kevin Manansala
 * 
 */
public interface GermplasmDataManager {

    /**
     * Searches for all germplasm records which matches the given name. Three
     * searching modes are available; normal search, the spaces on the name will
     * be removed, the name will be standardize before searching. The three
     * modes can be specified using the enum FindGermplasmByNameModes.
     * 
     * Names to be included in the search may be limited by name status.
     * Accepted values are 0 - 10. If the given status is zero all names will be
     * included in the search.
     * 
     * Names to be included in the search may be limited by name type. The enum
     * GermplasmNameType is used to specify the type of names to be included in
     * the search. If the given type is null, all names will be included in the
     * search.
     * 
     * @param name
     *            - search string for the name of the germplasm
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param mode
     *            - can be normal, spaces removed, name standardized
     * @param op
     *            - can be EQUAL OR LIKE
     * @param status
     *            - nstat of the names to be included in the search
     * @param type
     *            - name type
     * @param instance
     *            - can be Database.LOCAL or Database.CENTRAL
     * 
     * @return List of Germplasm POJOs
     * @throws QueryException
     */
    public List<Germplasm> findGermplasmByName(String name, int start, int numOfRows, FindGermplasmByNameModes mode, Operation op,
            Integer status, GermplasmNameType type, Database instance) throws QueryException;

    /**
     * Returns the number of germplasm records with any name matching the given
     * parameter. Search modes can also be specified like in using the
     * findGermplasmByName() method.
     * 
     * Names to be included in the search may be limited by name status.
     * Accepted values are 0 - 10. If the given status is zero all names will be
     * included in the search.
     * 
     * Names to be included in the search may be limited by name type. The enum
     * GermplasmNameType is used to specify the type of names to be included in
     * the search. If the given type is null, all names will be included in the
     * search.
     * 
     * @param name
     *            - search string for the name of the germplasm
     * @param mode
     *            - can be normal, spaces removed, name standardized
     * @param op
     *            - can be EQUAL OR LIKE
     * @param status
     *            - nstat of the names to be included in the search
     * @param type
     *            - name type
     * @param instance
     *            - can be Database.LOCAL or Database.CENTRAL
     * 
     * @return number of germplasm records
     * @throws QueryException
     */
    public int countGermplasmByName(String name, FindGermplasmByNameModes mode, Operation op, Integer status, GermplasmNameType type,
            Database instance) throws QueryException;

    /**
     * Returns the germplasm records that were created at the locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOs
     * @throws QueryException
     */
    public List<Germplasm> findGermplasmByLocationName(String name, int start, int numOfRows, Operation op, Database instance)
            throws QueryException;

    /**
     * Returns the number of germplasm records that were created at the locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param op
     *            - can be EQUAL like LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return Number of Germplasms
     * @throws QueryException
     */    
    public int countGermplasmByLocationName(String name, Operation op, Database instance) throws QueryException;


    /**
     * Returns the Location records that were created at the locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL like LIKE
     * @return List of Location POJOs
     * @throws QueryException
     */
    public List<Location> findLocationByName(String name, int start, int numOfRows, Operation op) throws QueryException;
    
    /**
     * Returns the number of Locations with
     * names matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the locations
     * @param op
     *            - can be EQUAL like LIKE
     * @return Number of Locations
     * @throws QueryException
     */
    public int countLocationByName(String name, Operation op) throws QueryException;
    
    /**
     * Returns all Locations
     * 
     * @param name
     *            - search string for the name or the locations
     * @param op
     *            - can be EQUAL or LIKE
     * @return gets all Locations
     */   
    public List<Location> getAllLocations(int start, int numOfRows) throws QueryException;
    
    /**
     * Returns number of all Locations
     * 
     * @return the number of all Locations
     */   
    public int countAllLocations() throws QueryException;
    
    /**
     * Returns the germplasm records that were created by the methods with names
     * matching the given parameter.
     * 
     * @param name
     *            - search string for the name of the methods
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @param op
     *            - can be EQUAL or LIKE
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return List of Germplasm POJOS
     * @throws QueryException
     */
    public List<Germplasm> findGermplasmByMethodName(String name, int start, int numOfRows, Operation op, Database instance)
            throws QueryException;

    /**
     * Returns the number of germplasm records that were created by methods with
     * names matching the given parameter
     * 
     * @param name
     *            - search string for the name of the methods
     * @param op
     *            - can be equal or like
     * @param instance
     *            - can be Database.CENTRAL or Database.LOCAL
     * @return number of germplasm records
     * @throws QueryException
     */
    public int countGermplasmByMethodName(String name, Operation op, Database instance) throws QueryException;

    /**
     * Returns the germplasm record identified by the given id.
     * 
     * @param gid
     *            - id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmByGID(Integer gid);

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name.
     * 
     * @param gid
     *            - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmWithPrefName(Integer gid) throws QueryException;

    /**
     * Given a gid, return the Germplasm POJO representing the record identified
     * by the id with its preferred name and preferred abbreviation.
     * 
     * @param gid
     *            - the id of the germplasm record to be retrieved
     * @return the Germplasm POJO representing the record
     */
    public Germplasm getGermplasmWithPrefAbbrev(Integer gid) throws QueryException;

    /**
     * Returns the Name record identified by the given id.
     * 
     * @param id
     *            - id of the name record
     * @return the Name POJO representing the record
     */
    public Name getGermplasmNameByID(Integer id);

    /**
     * Returns all the names of the Germplasm identified by the gid parameter.
     * 
     * Results may be filtered by name status. Accepted values are 0 - 10. If
     * the given status is zero all names will be included in the result.
     * 
     * Results may also be filtered by type. The enum GermplasmNameType is used
     * to specify the type of names to be included in the result. If the given
     * type is null, all names will be included in the result.
     * 
     * @param gid
     *            - id of the Germplasm
     * @param status
     *            - may be used to filter the results
     * @param type
     *            - may be used to filter the results
     * @return List of Name POJOs
     */
    public List<Name> getNamesByGID(Integer gid, Integer status, GermplasmNameType type) throws QueryException;

    /**
     * Returns the preferred name of the Germplasm identified by the gid
     * parameter.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred name. Returns
     *         {@code null} when no preferred name is found.
     * @throws QueryException
     */
    public Name getPreferredNameByGID(Integer gid) throws QueryException;

    /**
     * Returns the preferred abbreviation of the Germplasm identified by the gid
     * parameter.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return {@code Name} POJO of the Germplasm's preferred abbreviation.
     *         Returns {@code null} when no preferred abbreviation is found.
     * @throws QueryException
     */
    public Name getPreferredAbbrevByGID(Integer gid) throws QueryException;

    /**
     * Returns the matching {@code Name} object given a Germplasm ID and a Name
     * value.
     * 
     * @param gid
     *            - id of the Germplasm
     * @param nval
     *            - value of the Name to search
     * @return {@code Name} POJO of the matching {@code Name} object. Returns
     *         {@code null} when no {@code Name} with the specified gid and nval
     *         is found.
     * @throws QueryException
     */
    public Name getNameByGIDAndNval(Integer gid, String nval) throws QueryException;

    /**
     * Sets the specified Name as the specified Germplasm's new preferred Name.
     * 
     * @param gid
     *            - id of the Germplasm to be updated
     * @param newPrefName
     *            - new name to set as the preferred name
     * @throws QueryException
     */
    public void updateGermplasmPrefName(Integer gid, String newPrefName) throws QueryException;

    /**
     * Sets the specified Abbreviation as the specified Germplasm's new
     * preferred Abbreviation.
     * 
     * @param gid
     *            - id of the Germplasm to be updated
     * @param newPrefAbbrev
     *            - new abbreviation to set as the preferred abbreviation
     * @throws QueryException
     */
    public void updateGermplasmPrefAbbrev(Integer gid, String newPrefAbbrev) throws QueryException;

    /**
     * Inserts a single {@code Name} object into the database.
     * 
     * @param location
     *            - The {@code Name} object to be persisted to the database.
     *            Must be a valid {@code Name} object.
     * @return Returns the number of Germplasm Name records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addGermplasmName(Name name) throws QueryException;

    /**
     * Inserts a list of multiple {@code Name} objects into the database.
     * 
     * @param names
     *            - A list of {@code Name} objects to be persisted to the
     *            database. {@code Name} objects must be valid.
     * @return Returns the number of {@code Name} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addGermplasmName(List<Name> names) throws QueryException;

    /**
     * Updates a single {@code Name} object in the database.
     * 
     * @param location
     *            - The {@code Name} object to be updated in the database. Must
     *            be a valid {@code Name} object.
     * @return Returns the number of Germplasm Name records updated in the
     *         database.
     * @throws QueryExceptionc
     */
    public int updateGermplasmName(Name name) throws QueryException;

    /**
     * Updates the database with multiple {@code Name} objects specified.
     * 
     * @param names
     *            - A list of {@code Name} objects to be updated in the
     *            database. {@code Name} objects must be valid.
     * @return Returns the number of {@code Name} records updated in the
     *         database.
     * @throws QueryException
     */
    public int updateGermplasmName(List<Name> names) throws QueryException;

    /**
     * Returns all the attributes of the Germplasm identified by the given id.
     * 
     * @param gid
     *            - id of the Germplasm
     * @return List of Atributs POJOs
     */
    public List<Attribute> getAttributesByGID(Integer gid) throws QueryException;

    /**
     * Returns the Method record identified by the id.
     * 
     * @param id
     *            - id of the method record
     * @return the Method POJO representing the record
     */
    public Method getMethodByID(Integer id);

    /**
     * Returns all the method records.
     * 
     * @return List of Method POJOs
     * @throws QueryException
     */
    public List<Method> getAllMethods() throws QueryException;

    /**
     * Returns the udfld record identified by the given id.
     * 
     * @param id
     *            - the id of the udfld record
     * @return the Udflds POJO representing the record
     */
    public UserDefinedField getUserDefinedFieldByID(Integer id);

    /**
     * Returns the country record identified by the given id.
     * 
     * @param id
     *            - id of the country record
     * @return the Country POJO representing the record
     */
    public Country getCountryById(Integer id);

    /**
     * Returns the location record identified by the given id.
     * 
     * @param id
     *            - id of the location record
     * @return the Location POJO representing the record
     */
    public Location getLocationByID(Integer id);

    /**
     * Inserts a single {@code Location} object into the database.
     * 
     * @param location
     *            - The {@code Location} object to be persisted to the database.
     *            Must be a valid {@code Location} object.
     * @return Returns the number of {@code Location} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addLocation(Location location) throws QueryException;
    
    /**
     * Inserts a single {@code Location} object into the database.
     * 
     * @param locations
     *            - The {@code Location} object to be persisted to the database.
     *            Must be a valid {@code Location} object.
     * @return Returns the number of {@code Location} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addLocation(List<Location> locations) throws QueryException;
    
    /**
     * Inserts a single {@code Method} object into the database.
     * 
     * @param method
     *            - The {@code Method} object to be persisted to the database.
     *            Must be a valid {@code Method} object.
     * @return Returns the number of {@code Method} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addMethod(Method method) throws QueryException;
    
    /**
     * Inserts a list of {@code Method} objects into the database.
     * 
     * @param methods
     *            - The list of {@code Method} objects to be persisted to the database.
     *            Must be valid {@code Method} objects.
     * @return Returns the number of {@code Method} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addMethod(List<Method> methods) throws QueryException;
    
    /**
     * Deletes a single {@code Method} object into the database.
     * 
     * @param method
     *            - The {@code Method} object to be persisted to the database.
     *            Must be a valid {@code Method} object.
     * @throws QueryException
     */
    public void deleteMethod(Method method) throws QueryException;

    /**
     * Returns the Bibref record identified by the given id.
     * 
     * @param id
     *            - id of the bibref record
     * @return the Bibref POJO representing the record
     */
    public Bibref getBibliographicReferenceByID(Integer id);

    /**
     * Inserts a single {@code Bibref} (Bibliographic Reference) object into the
     * database.
     * 
     * @param location
     *            - The {@code Bibref} object to be persisted to the database.
     *            Must be a valid {@code Bibref} object.
     * @return Returns the number of {@code Bibref} records inserted in the
     *         database.
     * @throws QueryException
     */
    public int addBibliographicReference(Bibref bibref) throws QueryException;

    /**
     * Returns the Germplasm representing the parent of the child Germplasm
     * identified by the given gid and having the given progenitor number.
     * 
     * @param gid
     *            - gid of child Germplasm
     * @param progenitorNumber
     *            - progenitor number of the parent with respect to the child
     * @return Germplasm POJO
     * @throws QueryException
     */
    public Germplasm getParentByGIDAndProgenitorNumber(Integer gid, Integer progenitorNumber) throws QueryException;

    /**
     * Returns the Germplasm representing the children of the Germplasm
     * identified by the given gid. The function returns a List of Object
     * arrays. Each Object array contains 2 elements, the first is an int to
     * specify the progenitor number and the second is the Germplasm POJO
     * representing the child germplasm.
     * 
     * @param gid
     *            - gid of the parent Germplasm
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results
     *            to be returned
     * @return List of Object arrays, the arrays have 2 elements in them
     * @throws QueryException
     */
    public List<Object[]> findDescendants(Integer gid, int start, int numOfRows) throws QueryException;

    /**
     * Returns the number of children of the Germplasm identified by the given
     * gid.
     * 
     * @param gid
     * @return count of children
     * @throws QueryException
     */
    public int countDescendants(Integer gid) throws QueryException;

    /**
     * Creates a pedigree tree for the Germplasm identified by the given gid.
     * The tree contains all generative progenitors down to the specified level.
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded. The root of the tree is the Germplasm identified by the given
     * gid parameter. The nodes down the tree are the ancestors of the nodes
     * above them.
     * 
     * Example tree:
     * 
     * Result of calling: generatePedigreeTree(new Integer(306436), 4);
     * 
     * 306436 : TOX 494 (root node) 33208 : 63-83 (child node of root,
     * representing parent of Germplasm 306436) 2269311 : 63-83 310357 : IRAT 2
     * 96783 : IGUAPE CATETO (child node of root, representing parent of
     * Germplasm 306436) 312744 : RPCB-2B-849 (child node of root, representing
     * parent of Germplasm 306436) 2268822 : RPCB-2B-849 3160 : IR 1416-131
     * (child node of root, representing parent of Germplasm 306436) 2231 : IR
     * 1416 1163 : IR 400-28-4-5 (child node containing Germplasm 2231,
     * representing parent of Germplasm 2231) 2229 : TE TEP (child node
     * containing Germplasm 2231, representing parent of Germplasm 2231) 312646
     * : LITA 506 (child node of root, representing parent of Germplasm 306436)
     * 
     * 
     * @param gid
     *            - GID of a Germplasm
     * @param level
     *            - level of the tree to be created
     * @return GermplasmPedigreeTree representing the pedigree tree
     * @throws QueryException
     */
    public GermplasmPedigreeTree generatePedigreeTree(Integer gid, int level) throws QueryException;

    /**
     * Returns the Germplasm which are management group neighbors of the
     * Germplasm identified by the given GID. The given Germplasm is assumed to
     * be a root of a management group. The Germplasm POJOs included in the
     * results come with their preferred names which can be accessed by calling
     * Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs
     * @throws QueryException
     */
    public List<Germplasm> getManagementNeighbors(Integer gid) throws QueryException;

    /**
     * Returns the Germplasm which are group relatives of the Germplasm
     * identified by the given GID. The Germplasm POJOs included in the results
     * come with their preferred names which can be accessed by calling
     * Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs
     * @throws QueryException
     */
    public List<Germplasm> getGroupRelatives(Integer gid) throws QueryException;

    /**
     * Returns the generation history of the Germplasm identified by the given
     * GID. The history is created by tracing back through the source germplasms
     * from the given Germplasm through its progenitors, up until a Germplasm
     * created by a generative method is encountered. The Germplasm POJOs
     * included in the results come with their preferred names which can be
     * accessed by calling Germplasm.getPreferredName().
     * 
     * @param gid
     * @return List of Germplasm POJOs, arranged from the given Germplasm down
     *         to the last source on the generation history
     * @throws QueryException
     */
    public List<Germplasm> getGenerationHistory(Integer gid) throws QueryException;

    /**
     * Returns the GermplasmPedigreeTree object which represents the derivative
     * neighborhood for the germplasm identified by the given gid. The
     * derivative neighborhood is created by tracing back the source parents
     * from the given germplasm until the given number of steps backward is
     * reached or the first source germplasm created by a generative method is
     * reached, whichever comes first. The last source parent reached by tracing
     * back becomes the root of the GermplasmPedigreeTree object. From the root,
     * all immediate derived lines are retrieved and added to the tree. And then
     * from each of those derived germplasms, all immediate derived lines are
     * retrieved and added to the tree, and so on and so forth. The number of
     * levels of the tree is the sum of the actual number of steps backward made
     * to reach the root and the given number of steps forward plus 1 (for the
     * level which the given germplasm belongs).
     * 
     * The Germplasm POJOs included in the tree have their preferred names
     * pre-loaded.
     * 
     * @param gid
     * @param numberOfStepsBackward
     *            - number of steps backward from the germplasm identified by
     *            the given gid
     * @param numberOfStepsForward
     *            - number of steps forward from the germplasm identified by the
     *            given gid
     * @return GermplasmPedigreeTree representing the neighborhood
     */
    public GermplasmPedigreeTree getDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward)
            throws QueryException;

    /**
     * Returns the Germplasm records with field values matching the specified
     * values on the given sample Germplasm object. The following fields will be
     * checked for matches: method, gnpgs, gpid1, gpid2, user, location, gdate,
     * reference, mgid, and attributes.
     * 
     * @param sample
     * @param start
     * @param numOfRows
     * @return List of Germplasm POJOs
     */
    // public List<Germplasm> findGermplasmByExample(Germplasm sample, int
    // start, int numOfRows);

    /**
     * Returns the number of Germplasm records with field values matching the
     * specified values on the given sample Germplasm object. The following
     * fields will be checked for matches: method, gnpgs, gpid1, gpid2, user,
     * location, gdate, reference, mgid, and attributes.
     * 
     * @param sample
     * @return
     */
    // public int countGermplasmByExample(Germplasm sample);

    /**
     * Stores in the database the given valid Attribute object.
     * 
     * @param attribute
     * @return the number of Attribute records stored in the database
     * @throws QueryException
     */
    public int addGermplasmAttribute(Attribute attribute) throws QueryException;

    /**
     * Stores in the database all the given valid Attributes object contained in
     * the parameter.
     * 
     * @param attributes
     *            - List of Attribute objects
     * @return the number of Attribute records stored in the database
     * @throws QueryException
     */
    public int addGermplasmAttribute(List<Attribute> attributes) throws QueryException;

    /**
     * Given a valid Attribute object, update the corresponding record in the
     * database.
     * 
     * @param attribute
     * @return the number of Attribute records updated
     * @throws QueryException
     */
    public int updateGermplasmAttribute(Attribute attribute) throws QueryException;

    /**
     * Given a List of valid Attribute objects, update their corresponding
     * records in the database.
     * 
     * @param attributes
     *            - List of Attribute objects
     * @return the number of Attribute records updated
     * @throws QueryException
     */
    public int updateGermplasmAttribute(List<Attribute> attributes) throws QueryException;

    /**
     * Returns the attribute record identified by the given id.
     * 
     * @param id
     * @return
     */
    public Attribute getAttributeById(Integer id);

    /**
     * Given the gid of the child germplasm, the gid of the parent germplasm and
     * the progenitor number, this method makes the necessary changes to save
     * the relationship on the database.
     * 
     * This method will either update the Germplasm record, to change the gpid1
     * or gpid2 fields (if the progenitor number given is 1 or 2), or will
     * either add or update the Progenitor record which represents this
     * relationship. A new Progenitor record will be stored when necessary.
     * 
     * @param gid
     * @param progenitorId
     * @param progenitorNumber
     * @return true if the operation was successful
     * @throws QueryException
     */
    public boolean updateProgenitor(Integer gid, Integer progenitorId, Integer progenitorNumber) throws QueryException;

    /**
     * Given a valid Germplasm object, update the corresponding record in the
     * database.
     * 
     * @param germplasm
     * @return the number of Germplasm records updated
     * @throws QueryException
     */
    public int updateGermplasm(Germplasm germplasm) throws QueryException;

    /**
     * Given a List of valid Germplasm objects, update the corresponding records
     * in the database.
     * 
     * @param germplasms
     * @return the number of Germplasm records updated
     * @throws QueryException
     */
    public int updateGermplasm(List<Germplasm> germplasms) throws QueryException;

    /**
     * Given a valid Germplasm object with a matching valid Name object to be
     * set as its preferred name, add a new Germplasm record and a new Name
     * record for the given parameters.
     * 
     * @param germplasm
     * @param preferredName
     * @return the number of Germplasm records added
     * @throws QueryException
     */
    public int addGermplasm(Germplasm germplasm, Name preferredName) throws QueryException;

    /**
     * Given a map of valid Germplasm and Name objects, add new records for the
     * given parameters.
     * 
     * The Name objects matching each Germplasm object in the map will be set as
     * the preferred name of the Germplasm objects.
     * 
     * Note that you need to assign temporary ids for the Germplasm objects so
     * that they can serve as the keys for the Map. The function will replace
     * these temp ids with the correct ones for storing in the database.
     * 
     * @param germplasmNameMap
     * @return the number of Germplasm records added
     * @throws QueryException
     */
    public int addGermplasm(Map<Germplasm, Name> germplasmNameMap) throws QueryException;
    
    /**
     * Gets the germplasm Id and name Id from the names table with the given germplasm name
     *
     * @param germplasmName the germplasm name
     * @param start
     *            - the starting index of the sublist of results to be returned
     * @param numOfRows
     *            - the number of rows to be included in the sublist of results to be returned
     * @return List of GidNidElement based on the specified list of germplasm names 
     * @throws QueryException
     */
    public List<GidNidElement> getGidAndNidByGermplasmNames(List<String> germplasmNames) throws QueryException;

}
