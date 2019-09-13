package geom;

/**
 * Represents a 2D vector. This class is used a lot as a helper class in the geom package.
 * @author niquefadiego
 */

public class Vector implements Cloneable
{
	public float x, y;
	
	@Override
	public String toString ( ) { return "(" + x + "," + y + ")"; }
	
	/** Equivalent to Math.round(x) */ 
	public int intX() { return Math.round(this.x); }
	
	/** Equivalent to Math.round(y) */
	public int intY() { return Math.round(this.y); }

	//Basic operations
	public Vector add ( Vector o ) { return new Vector(this.x+o.x,this.y+o.y); }
	public Vector add ( float x, float y ) { return new Vector(this.x+x,this.y+y); }
	public Vector subt ( Vector o ) { return new Vector(this.x-o.x,this.y-o.y); }
	public Vector subt ( float x, float y ) { return new Vector(this.x-x,this.y-y); }
	public Vector prod ( float m ) { return new Vector(this.x*m, this.y*m); }
	public Vector divide(float v) { return new Vector ( x/v, y/v ); }
	public float cross ( Vector o ) { return this.x*o.y - this.y*o.x; }
	public float cross ( float x, float y ) { return this.x*y - this.y*x; }
	public boolean isLeftTo ( Vector v ) { return this.cross(v) > 0; }

	public Vector clone ( ) { return new Vector(x,y); }
	
	//Basic functions
	/** Equivalent to (flat) Math.atan2(y,x) */
	public float angle ( ) { return (float) Math.atan2(y, x); }
	
	public float norm ( ) { return (float) Math.hypot(y,x); }
	
	/** Equivalent to x*x + y*y */
	public float sqNorm ( ) { return x*x + y*y; }
	
	public Vector rotate90CW ( ) { return new Vector ( -y, x ); }
	
	public Vector rotate90CCW ( ) { return new Vector ( y, -x ); }
	
	/** @return Unit vector */
	public Vector normalize ( ) {
		float len = this.norm();
		return new Vector(this.x/len,this.y/len);
	}
	
	public Vector rotateCCW ( float rad ) { return this.rotateCW(-rad); }
	public Vector rotateCW ( float rad ) {
		return new Vector ( x*Math.cos(rad)-y*Math.sin(rad) , x*Math.sin(rad)+y*Math.cos(rad) );
	}
	
	/** @return Angle between the two vectors. The returned value is less than PI. */
	public float angleBetween ( Vector o ) {
		float ret = Math.abs(angle()-o.angle());
		if ( ret >= Math.PI ) ret = (float)(Math.PI-ret);
		return ret;
	}
	
	//Constructors
	public Vector ( ) { }
	public Vector ( float x, float y ) { this.x=x; this.y=y; }
	public Vector ( double x, double y ) { this.x=(float)x; this.y=(float)y; }
}