package geom;

/**
 * Some static helper functions used only inside the geom package.
 * @author niquefadiego
 */

class GUtils
{
	static final float EPS = 1e-3f;
	static final int cmp ( float a, float b ) { return (a+EPS<b?-1:(b+EPS<a?1:0)); }
}
