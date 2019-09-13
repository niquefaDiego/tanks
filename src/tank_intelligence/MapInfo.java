package tank_intelligence;

import geom.LineSeg;
import geom.Vector;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;


import core.GameMap;

/**
 * This class is used to generate and save some information about the map.
 * This information is used to quickly find paths, see if a player can shoot another one, etc.
 * @author niquefadiego
 *
 */

final class MapInfo
{
	static protected int path[][];
	static int height, width, size;
	static boolean isWall[];
	static GameMap map;
	
	static int dx[] = {-1,0,1,0};
	static int dy[] = {0,-1,0,1};
	
	static int next[][];
	static int adj[][];
	static private byte canShootMemo[][];
	
	static int encode(int x, int y) { return x + y*width; }
	static int decodeX(int code) { return code%width; }
	static int decodeY(int code) { return code/width; }
	
	static boolean canShoot ( int fr, int to ) {
		if ( canShootMemo[fr][to] != -1 )
			return ( canShootMemo[fr][to] == 1 );
		Vector vFr = new Vector(decodeX(fr),decodeY(fr)).add(0.5f,0.5f);
		Vector vTo = new Vector(decodeX(to),decodeY(to)).add(0.5f,0.5f);
		boolean ans = ( map.intersectionSegWalls(new LineSeg(vFr, vTo)) == null );
		canShootMemo[fr][to] = (byte) ( ans ? 1 : 0 );
		return ans;
	}
	
	static public void init ( GameMap map )
	{
		MapInfo.map = map;
		height = map.getTilesHeight();
		width = map.getTilesWidth();
		size = width*height;
		isWall = new boolean[size];
		adj = new int[size][4];
		for ( int i = 0; i < width; ++i )
			for ( int j = 0; j < height; ++j ) {
				isWall[encode(i,j)] = map.isWall(i, j);
				int ni, nj;
				for ( int d = 0; d < 4; ++d ) {
					ni = i+dx[d];
					nj = j+dy[d];
					adj[encode(i,j)][d] = -1;
					if ( ni < 0 || ni >= width ) continue;
					if ( nj < 0 || nj >= height ) continue;
					adj[encode(i,j)][d] = encode(ni,nj);
				}
			}
		
		next = new int[size][size];
		BitSet seen = new BitSet(size);

		for ( int start = 0; start < size; ++start )
		{
			if ( isWall[start] ) continue;
			Queue<Integer> q = new LinkedList<Integer> ( );
			seen.clear();
			q.add ( start );
			seen.set(start,false);
			next[start][start] = start;
			
			while ( !q.isEmpty() ) {
				int cur = q.poll();
				for ( int d = 0; d < 4; ++d )
					if ( adj[cur][d] != -1 )
						if ( !isWall[adj[cur][d]] && seen.get(adj[cur][d]) == false ) {
							seen.set(adj[cur][d],true);
							next[adj[cur][d]][start] = cur;
							q.offer( adj[cur][d] );
						}
			}
		}
		
		canShootMemo = new byte[size][size];
		for ( int i = 0; i < size; ++i )
			Arrays.fill ( canShootMemo[i], (byte)(-1) );
	}
	
	private MapInfo ( ) { }
}
