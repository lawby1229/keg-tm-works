/**
 * 
 */
package edu.thu.keg.provider;

/**
 * Thrown when a query is illegal (cannot be executed) on a data provider
 * 
 * @author Yuanchao Ma
 * 
 */
public class IllegalQueryException extends DatabaseProviderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2518079930070277161L;

	public IllegalQueryException(String msg) {
		super(msg);
	}

	public IllegalQueryException() {
		super();
	}

}
