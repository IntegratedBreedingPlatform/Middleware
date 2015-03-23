package org.generationcp.middleware;


/**
 * A unit of work.
 */
public interface Work {
	
	public void doWork() throws Exception;
	
	/**
	 * Name of the unit of work.
	 */
	public String getName();
}
