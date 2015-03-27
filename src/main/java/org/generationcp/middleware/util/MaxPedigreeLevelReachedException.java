package org.generationcp.middleware.util;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 3/26/2015
 * Time: 4:24 PM
 *
 * Exception class used
 */
public class MaxPedigreeLevelReachedException extends RuntimeException{
	final static MaxPedigreeLevelReachedException instance = new MaxPedigreeLevelReachedException();

	private MaxPedigreeLevelReachedException() {
		super();
	}

	public static MaxPedigreeLevelReachedException getInstance() {
		return instance;
	}
}
