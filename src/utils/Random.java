package utils;

/**
 * This class contains static methods to generate random data.
 * @author niquefadiego
 */

public abstract class Random {
	/** Random integer in range [0,n) */
	static public int randInt ( int n ) { return (int)(Math.random()*n); }
	
	/** Random integer in range [beg,end) */
	static public int randIntInRange ( int beg, int end ) { return beg+randInt(end-beg); } // [beg,end) 
}
