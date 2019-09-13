package geom;

/**
 * Represent a line segment.
 * @author niquefadiego
 */

public class LineSeg
{
	private Vector A, D;
	
	/** @return Start of the line segment. */
	public Vector beg ( ) { return A; }
	
	/** @return End of the line segment. */
	public Vector end ( ) { return A.add(D); }
	
	/** Equivalent to beg().add ( end().subt(beg()).prod(x) ) */
	public Vector at ( float x ) { return A.add(D.prod(x)); }
	
	/** @return True iff the segments are parallel. */
	public boolean parallel ( LineSeg o ) { return Math.abs(D.cross(o.D)) < 1e-3; }
	
	private boolean containsPointInLine ( Vector P ) {
		if ( !( GUtils.cmp(A.x,P.x) <= 0 && GUtils.cmp(P.x,A.x+D.x) <= 0 ) ) return false;
		return GUtils.cmp(A.y,P.y) <= 0 && GUtils.cmp(P.y,A.y+D.y) <= 0;
	}
	
	/**
	 * @return A float x such that the lines containing the segment intersects at
	 * this.at(x). This assumes the lines are not parallel.
	 */
	public float asLinesIntersectionScalar ( LineSeg o ) {
		return ( o.A.cross(o.D) - A.cross(o.D) ) / D.cross(o.D);
	}
	
	/**
	 * @return True iff the line segments intersects.
	 */
	public boolean intersectsLineSeg ( LineSeg o )
	{
		if ( this.parallel(o) ) {
			if ( this.containsPointInLine(o.A) ) return true;
			if ( this.containsPointInLine(o.A.add(o.D)) ) return true;
			if ( o.containsPointInLine(this.A) ) return true;
			if ( o.containsPointInLine(this.A.add(this.D)) ) return true;
			return false;
		}
		float x = this.asLinesIntersectionScalar ( o );
		if ( x < 0 || x > 1 ) return false;
		x = o.asLinesIntersectionScalar ( this );
		if ( x < 0 || x > 1 ) return false;
		return true;
	}

	/** Instantiate new line segment with the given endpoints. */
	public LineSeg ( Vector A, Vector B ) {
		this.A = A.clone();
		this.D = B.subt(A);
	}
}
