package core;

import java.io.Serializable;

import geom.LineSeg;
import geom.Quad;
import geom.Vector;
import core.sprites.Tank;
import core.views.GameScreen;

import utils.Random;
import utils.UnorderedArray;

/**
 * This class represent a game map.
 * 
 * The notation to represent a cell in the map is (i,j) where i is the column
 * (from left to right) and j is the row (from top to bottom), both i and j starts from 0.
 * 
 * @author niquefadiego
 *
 */
public class GameMap implements Serializable
{
	private static final long serialVersionUID = -4296522604988102181L;
	
	private int tilesWidth, tilesHeight;
	private int floor[][];
	
	private transient int width=tilesWidth*Values.TILE_SIZE;
	private transient int height=tilesHeight*Values.TILE_SIZE;
	
	private transient UnorderedArray<LineSeg> walls;
	private transient UnorderedArray<Tank> atCell[][];
	
	/**
	 * @return			Pixel width of the map.
	 */
	public int getWidth ( ) { return this.width; }
	
	/**
	 * @return 			Pixel height of the map.
	 */
	public int getHeight ( ) { return this.height; }
	
	/**
	 * @return			Tiles height of the map.
	 */
	public int getTilesHeight ( ) { return this.tilesHeight; }
	
	/**
	 * @return			Tiles width of the map.
	 */
	public int getTilesWidth ( ) { return this.tilesHeight; }
	
	/**
	 * Sets the type of a cell in the floor.
	 * @param val			Value (Should be Images.WALL or Images.FREE)
	 */
	public void setFloorCell ( int i, int j, int val ) { floor[i][j]=val; }
	
	/**
	 * Return true iff the cell is a wall, or if is outside the map.
	 */
	public boolean isWall ( int i, int j ) {
		if ( i < 0 || i >= this.tilesWidth ) return true;
		if ( j < 0 || j >= this.tilesHeight ) return true;
		return this.floor[i][j] == Images.WALL;
	}
	
	/**
	 * Method used only by the Tank class when is given a move order.
	 * This will tell that the Tank center is in cell (i,j).
	 * @param i			Cell column
	 * @param j			Cell row
	 * @param tank		Tank
	 */
	public void addTankAtCell ( int i, int j, Tank tank ) { atCell[i][j].add(tank); }
	
	/**
	 * Method used only by the Tank class when is given a move order.
	 * This will tell that the Tank center is no longer in cell (i,j).
	 * @param i			Cell column
	 * @param j			Cell row
	 * @param tank		Tank
	 */
	public void removeTankAtCell ( int i, int j, Tank tank ) {
		atCell[i][j].remove(atCell[i][j].indexOf(tank));
	}
	
	/**
	 * Return the number of tanks whose center is in cell (i,j)
	 * @param i			Cell column
	 * @param j			Cell row
	 */
	public int tanksAt ( int i, int j ) { return atCell[i][j].size(); }
	
	/**
	 * Returns the k-th tank whose center is in cell(i,j)
	 */
	public Tank tankAt ( int i, int j, int k ) { return atCell[i][j].get(k); }
	
	private void addWall ( Vector fr, Vector to ) {
		walls.add( new LineSeg(fr,to) );
	}
	
	private GameMap ( int tilesWidth, int tilesHeight ) {
		this.tilesWidth = tilesWidth;
		this.tilesHeight = tilesHeight;
	}

	/**
	 * Initialized and pre-calculate what is needed.
	 * This must be called before the Game starts running.
	 */
	@SuppressWarnings("unchecked")
	public void init ( ) 
	{
		height=this.tilesHeight*Values.TILE_SIZE;
		width=this.tilesWidth*Values.TILE_SIZE;
		
		walls = new UnorderedArray<LineSeg>();
		
		addWall ( new Vector(0,0), new Vector(0,height) );
		
		addWall( new Vector(0,height), new Vector(width,height) );
		
		addWall( new Vector(width,height), new Vector(width,0) );
		
		addWall( new Vector(width,0), new Vector(0,0) );
		
		//Wall type 1
		for ( int i = 0; i+1 < this.tilesWidth; ++i )
			for ( int j = 0; j < this.tilesHeight; ++j ) {
				if ( !isWall(i,j) && isWall(i+1,j) ) {
					int k = j;
					while ( !isWall(i,k) && isWall(i+1,k) ) k++;
					addWall (
							new Vector((i+1)*Values.TILE_SIZE,j*Values.TILE_SIZE),
							new Vector((i+1)*Values.TILE_SIZE,k*Values.TILE_SIZE)
					);
					j = k-1;
				}
			}
		
		//Wall type 2
		for ( int i = 1; i < this.tilesWidth; ++i )
			for ( int j = 0; j < this.tilesHeight; ++j ) {
				if ( isWall(i-1,j) && !isWall(i,j) ) {
					int k = j;
					while ( isWall(i-1,k) && !isWall(i,k) ) k++;
					addWall (
							new Vector(i*Values.TILE_SIZE,j*Values.TILE_SIZE),
							new Vector(i*Values.TILE_SIZE,k*Values.TILE_SIZE)
					);
					j = k-1;
				}
			}

		//Wall type 3
		for ( int j = 1; j < this.tilesHeight; ++j )
			for ( int i = 0; i < this.tilesWidth; ++i ) {
				if ( isWall(i,j-1) && !isWall(i,j) ) {
					int k = i;
					while ( isWall(k,j-1) && !isWall(k,j) ) k++;
					addWall (
							new Vector(i*Values.TILE_SIZE,j*Values.TILE_SIZE),
							new Vector(k*Values.TILE_SIZE,j*Values.TILE_SIZE)
					);
					i = k-1;
				}
			}
	
		//Wall type 4
		for ( int j = 0; j+1 < this.tilesHeight; ++j )
			for ( int i = 0; i < this.tilesWidth; ++i ) {
				if ( isWall(i,j+1) && !isWall(i,j) ) {
					int k = i;
					while ( isWall(k,j+1) && !isWall(k,j) ) k++;
					addWall (
							new Vector(i*Values.TILE_SIZE,(j+1)*Values.TILE_SIZE),
							new Vector(k*Values.TILE_SIZE,(j+1)*Values.TILE_SIZE)
					);
					i = k-1;
				}
			}
		
		atCell = new UnorderedArray[this.tilesWidth][this.tilesHeight];
		for ( int i = 0; i < this.tilesWidth; ++i )
			for ( int j = 0; j < this.tilesHeight; ++j )
				atCell[i][j] = new UnorderedArray<Tank>();
	}
	//-----------------------------------------------
	
	
	/**
	 * Return the closest point to seg.beg() in the segment that is inside a wall.
	 * If there is no such segment returns null.
	 * @param seg			Query segment.
	 */
	public Vector intersectionSegWalls ( LineSeg seg ) {
		if ( isInsideWall(seg.beg()) ) return seg.beg();
		float r = 2, x;
		if ( isInsideWall(seg.end()) ) r = 1f;
		for ( int i = 0; i < walls.size(); ++i )
			if ( walls.get(i).intersectsLineSeg(seg) )
			{
				x = seg.asLinesIntersectionScalar(walls.get(i));
				if ( r > x ) r = x;
			}
		if ( r > 1.5 ) return null;
		return seg.at(r);
	}
	
	/**
	 * If a Tank is not returned by this method, it means that the Tank is far enough to
	 * ensure is doesn't contains the point.  
	 * @param p				Query point
	 */
	public UnorderedArray<Tank> tanksNear ( Vector p )
	{
		UnorderedArray<Tank> ret = new UnorderedArray<Tank>();
		int i = (int)(p.x/Values.TILE_SIZE), ii;
		int j = (int)(p.y/Values.TILE_SIZE), jj;
		int it;
		for ( ii = i-1; ii <= i+1; ++ii )
			for ( jj = j-1; jj <= j+1; ++jj ) {
				if ( ii < 0 || jj < 0 ) continue;
				if ( ii >= tilesWidth || jj >= tilesHeight ) continue;
				for ( it = 0; it < this.tanksAt(ii, jj); ++it )
					ret.add ( this.tankAt(ii, jj, it) );
			}
		return ret;
	}
	
	/**
	 * Returns true iff a given quadrilateral intersects some of the walls in the map.
	 * @param q			Query quadrilateral.
	 */
	public boolean intersectsQuadWalls ( Quad q ) {
		if ( isInsideWall(q.A) ) return true;
		if ( isInsideWall(q.B) ) return true;
		if ( isInsideWall(q.C) ) return true;
		if ( isInsideWall(q.D) ) return true;
		
		Vector center = (q.A.add(q.B).add(q.C).add(q.D)).prod(1/4f);
		
		int i = (int)(center.x/Values.TILE_SIZE), ii;
		int j = (int)(center.y/Values.TILE_SIZE), jj;
		for ( ii = i-1; ii <= i+1; ++ii )
			for ( jj = j-1; jj <= j+1; ++jj )
				if ( isWall(ii,jj) )
				{
					if ( isWall(ii,jj) ) {
						Quad qq = new Quad();
						qq.A = new Vector ( ii*Values.TILE_SIZE, jj*Values.TILE_SIZE );
						qq.B = qq.A.add(0,Values.TILE_SIZE);
						qq.C = qq.B.add(Values.TILE_SIZE,0);
						qq.D = qq.C.subt(0,Values.TILE_SIZE);
						if ( q.collides(qq) )
							return true;
					}	
				}

		return false;
	}
	
	/**
	 * Checks if a given Vector is within the boundaries of the game map.
	 * @param p	Vector to check
	 * @return True if and only if p is within the game map.
	 */
	public boolean isValidPos ( Vector p ) {
		if ( !( 0 <= p.intX() && p.intX() < this.width ) ) return false;
		if ( !( 0 <= p.intY() && p.intY() < this.height ) ) return false;
		return true;
	}
	
	/**
	 * @return		Random non-wall point.
	 */
	public Vector randomNonWallPoint() {
		Vector p = new Vector();
		do {
			p.x = Random.randInt ( width );
			p.y = Random.randInt ( height );
		} while ( this.isInsideWall(p) );
		return p;
	}
	
	/**
	 *	Renders the game floor on the GameWrapper.
	 */
	public void renderOn ( GameScreen g )
	{
		for ( int i = 0; i < tilesWidth; ++i )
			for ( int j = 0; j < tilesHeight; ++j ) {
				Vector pos = new Vector ( i*Values.TILE_SIZE, j*Values.TILE_SIZE );
				g.image(Images.get(floor[i][j]), pos);
			}
	}
	
	/**
	 * Returns the Tank which contains the given point, or null if none does.
	 * @param p			Query point
	 */
	public Tank getTankAt ( Vector p ) {
		int i = (int)(p.x/Values.TILE_SIZE), ii;
		int j = (int)(p.y/Values.TILE_SIZE), jj;
		int it;
		for ( ii = i-1; ii <= i+1; ++ii )
			for ( jj = j-1; jj <= j+1; ++jj ) {
				if ( ii < 0 || ii >= this.tilesWidth ) continue;
				if ( jj < 0 || jj >= this.tilesHeight) continue;
				for ( it = 0; it < this.tanksAt(ii, jj); ++it ) {
					Tank t = this.tankAt(ii, jj, it);
					if ( t.containsPoint(p) )
						return t;
				}
			}
		return null;
	}

	/**
	 * True if the given position is not a wall.
	 * @param p
	 * @return
	 */
	public boolean isInsideWall ( Vector p ) {
		int i = (int)(p.x/Values.TILE_SIZE);
		int j = (int)(p.y/Values.TILE_SIZE);
		return isWall(i,j);
	}
	
	/**
	 * Returns a map with no walls of the given size.
	 * @param tilesWidth
	 * @param tilesHeight
	 */
	static public GameMap getEmptyMap ( int tilesWidth, int tilesHeight ) {
		GameMap map = new GameMap(20,20);
		map.floor = new int[tilesWidth][tilesHeight];
		map.init();
		return map;
	}
	
	/**
	 * Returns a randomly generated map of the given size.
	 * @param tilesWidth
	 * @param tilesHeight
	 */
	static public GameMap getRandomMap ( int tilesWidth, int tilesHeight ) {
		GameMap map = new GameMap(20,20);
		map.floor = new int[tilesWidth][tilesHeight];
		for ( int i = 0; i < 20; ++i )
			map.floor[Random.randInt(tilesWidth)][Random.randInt(tilesHeight)] = Images.WALL;
		map.init();
		return map;
	}
}
