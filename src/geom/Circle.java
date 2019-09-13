package geom;

/**
 * Represents a circle.
 * @author niquefadiego
 */

public class Circle
{
	/** Center */
	public Vector C;
	
	/** Radius */
	public float r;

	/** Instantiate new circle with the given center and radius. */
	public Circle ( Vector C, float r ) {
		this.C = C.clone();
		this.r = r;
	}
}
