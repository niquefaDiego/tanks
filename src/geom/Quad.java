package geom;

/**
 * Represents a quadrilateral with vertexes A, B, C and D (In CW or CCW).
 * @author niquefadiego
 */

public class Quad
{
	public Vector A, B, C, D;
	
	/**
	 * @return True iff any of the quadrilaterals just collided. 
	 */
	public boolean collides ( Quad o )
	{
		if ( this.contains (o.A) ) return true;
		if ( this.contains (o.B) ) return true;
		if ( this.contains (o.C) ) return true;
		if ( this.contains (o.D) ) return true;
		
		if ( o.contains (this.A) ) return true;
		if ( o.contains (this.B) ) return true;
		if ( o.contains (this.C) ) return true;
		if ( o.contains (this.D) ) return true;
		return false;
	}
	
	/** @return True if the quadrilateral intersects the given line segment. */ 
	public boolean intersects ( LineSeg seg )
	{
		if ( this.contains(seg.beg()) ) return true;
		if ( this.contains(seg.end()) ) return true;
		
		if ( seg.intersectsLineSeg ( new LineSeg(A,B) ) ) return true;
		if ( seg.intersectsLineSeg ( new LineSeg(B,C) ) ) return true;
		if ( seg.intersectsLineSeg ( new LineSeg(C,D) ) ) return true;
		if ( seg.intersectsLineSeg ( new LineSeg(D,A) ) ) return true;

		return false;
	}
	
	/** @return True if the quadrilateral intersects the given quadrilateral. */
	public boolean intersects ( Quad o )
	{
		if ( o.intersects( new LineSeg(A,B) ) ) return true;
		if ( o.intersects( new LineSeg(B,C) ) ) return true;
		if ( o.intersects( new LineSeg(C,D) ) ) return true;
		if ( o.intersects( new LineSeg(D,A) ) ) return true;
		return false;
	}	

	/** @return True if the quadrilateral contains the given point. */
	public boolean contains ( Vector P )
	{
		boolean wanted = (B.subt(A).cross(P.subt(A))<=0);
		if ( wanted != (C.subt(B).cross(P.subt(B))<=0) ) return false;
		if ( wanted != (D.subt(C).cross(P.subt(C))<=0) ) return false;
		if ( wanted != (A.subt(D).cross(P.subt(D))<=0) ) return false;
		return true;
	}

	/** Instantiate a new quadrilateral without assigning vertex values yet. */
	public Quad ( ) { }

	/** Instantiate a new quadrilateral with the given vertex. */
	public Quad ( Vector A, Vector B, Vector C, Vector D ) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
	}
}