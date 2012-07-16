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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;

/**
 * This is the API for retrieving information about Traits, Scales, and Trait
 * Methods.
 * 
 * @author Kevin Manansala
 * 
 */
public interface TraitDataManager{

    /**
     * Returns the Scale record identified by the given id.
     *
     * @param id the id
     * @return the scale by id
     */
    public Scale getScaleByID(Integer id);

    /**
     * Returns all the Scale records.
     *
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param instance - can either be Database.CENTRAL or Database.LOCAL
     * @return List of Scale POJOs
     * @throws QueryException the query exception
     */
    public List<Scale> getAllScales(int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Returns the total number of Scale records.
     *
     * @return the int
     */
    public int countAllScales();

    /**
     * Returns the description of a given discrete value of a scale identified
     * by the given id.
     *
     * @param scaleId the scale id
     * @param value the value
     * @return the scale discrete description
     */
    public String getScaleDiscreteDescription(Integer scaleId, String value);

    /**
     * Returns discrete values, represented as ScaleDiscrete objects, of the
     * Scale identified by the given id.
     *
     * @param scaleId the scale id
     * @return List of ScaleDiscrete POJOs
     */
    public List<ScaleDiscrete> getDiscreteValuesOfScale(Integer scaleId);

    /**
     * Returns the ScaleContinuous object which contains the start and end of
     * the range of values for the Scale identified by the given id.
     *
     * @param scaleId the scale id
     * @return the range of continuous scale
     */
    public ScaleContinuous getRangeOfContinuousScale(Integer scaleId);

    /**
     * Returns all Scale records associated with the Trait identified by the
     * given id.
     *
     * @param traitId the trait id
     * @return the scales by trait id
     */
    public List<Scale> getScalesByTraitId(Integer traitId);

    /**
     * Returns the Trait record identified by the given id.
     *
     * @param id the id
     * @return the trait by id
     */
    public Trait getTraitById(Integer id);

    /**
     * Returns all Trait records.
     *
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param instance - can either be Database.CENTRAL or Database.LOCAL
     * @return the all traits
     * @throws QueryException the query exception
     */
    public List<Trait> getAllTraits(int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Returns the total number of Trait records.
     *
     * @return the int
     */
    public int countAllTraits();

    /**
     * Returns the TraitMethod record identified by the given id.
     *
     * @param id the id
     * @return the trait method by id
     */
    public TraitMethod getTraitMethodById(Integer id);

    /**
     * Returns all Trait Method records.
     *
     * @param start - the starting index of the sublist of results to be returned
     * @param numOfRows - the number of rows to be included in the sublist of results
     * to be returned
     * @param instance - can either be Database.CENTRAL or Database.LOCAL
     * @return List of TraitMethod POJOs
     * @throws QueryException the query exception
     */
    public List<TraitMethod> getAllTraitMethods(int start, int numOfRows, Database instance) throws QueryException;

    /**
     * Returns the total number of Trait Method records.
     *
     * @return the int
     */
    public int countAllTraitMethods();

    /**
     * Return all Trait Method records associated with the Trait identified by
     * the given id.
     *
     * @param traitId the trait id
     * @return List of TraitMethod POJOs
     */
    public List<TraitMethod> getTraitMethodsByTraitId(Integer traitId);
    
    /**
     * Adds the trait method.
     *
     * @param traitmethod the traitmethod
     * @throws QueryException the query exception
     */
    public void addTraitMethod(TraitMethod traitmethod) throws QueryException;
    
    /**
     * Delete trait method.
     *
     * @param traitmethod the traitmethod
     * @throws QueryException the query exception
     */
    public void deleteTraitMethod(TraitMethod traitmethod) throws QueryException;

}
