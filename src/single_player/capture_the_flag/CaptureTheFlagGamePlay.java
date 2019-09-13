package single_player.capture_the_flag;

import tank_intelligence.CaptureTheFlagController;
import utils.Random;
import utils.UnorderedArray;
import core.GameMap;
import core.GameEngine;
import core.Player;
import core.Values;
import core.interfaces.Gameplay;
import core.views.GameScreen;

import geom.Vector;

/**
 * Capture The Flag game play. The objective of this game is to take the enemies flag and bring
 * it to one's base.
 * 
 * @see core#interfaces#Gameplay
 * 
 * @author niquefadiego
 *
 */

public class CaptureTheFlagGamePlay implements Gameplay
{
	private Flag flag[];
	private Base base[];
	
	public Flag getFlag ( int team ) { return flag[team]; }
	public Base getBase ( int team ) { return base[team]; }
	public Vector getFlagPosition ( int team ) { return flag[team].getPosition(); }
	public Vector getBasePosition ( int team ) {
		return new Vector ( base[team].i, base[team].j ).prod(Values.TILE_SIZE);
	}

	@Override
	public int winnerTeam ( ) {
		int winnerTeam = -1;
		
		for ( int i = 0; i < 2; ++i ) {
			Vector p = flag[1-i].getPosition();
			int pi = (int)(p.x/Values.TILE_SIZE);
			int pj = (int)(p.y/Values.TILE_SIZE);
			if ( pi == base[i].i && pj == base[i].j )
				return i;
		}
		
		return winnerTeam;
	}
	
	@Override
	public boolean canPause ( ) { return true; }
	
	@Override
	public void update ( ) {
		flag[0].update();
		flag[1].update();
		GameEngine.update();
	}

	@Override
	public void init(GameMap map)
	{
		Values.changeFPS ( 20 );
		GameEngine.init(map);
		GameEngine.setHomePlayer(0,0);
		
		UnorderedArray<Integer> posi = new UnorderedArray<Integer>();
		UnorderedArray<Integer> posj = new UnorderedArray<Integer>();
		
		final int ITERS = 15;
		for ( int i = 0; i < ITERS; ++i )
		{
			int x=-1, y=-1;
			while ( GameEngine.map.isWall(x, y) ) {
				x = Random.randInt(GameEngine.map.getTilesWidth());
				y = Random.randInt(GameEngine.map.getTilesHeight());
			}
			posi.add ( x );
			posj.add ( x );
		}
		
		int best0 = 0, best1 = 0;
		float bestdist = 0;
		for ( int x = 0; x < ITERS; ++x )
			for ( int y = 0; y < ITERS; ++y ) {
				float dist = (float) Math.hypot(posi.get(x)-posi.get(y), posj.get(x)-posj.get(y) );
				if ( dist > bestdist ) {
					bestdist = dist;
					best0 = x;
					best1 = y;
				}
			}
		
		base = new Base[2];
		base[0] = new Base ( 0, posi.get(best0), posj.get(best0) );
		base[1] = new Base ( 1, posi.get(best1), posj.get(best1) );
		
		flag = new Flag[] { new Flag(0,base[0].i,base[0].j), new Flag(1,base[1].i,base[1].j) };
		
		GameEngine.screen.addSprite(base[0], GameScreen.BACK);
		GameEngine.screen.addSprite(base[1], GameScreen.BACK);
		GameEngine.screen.addSprite(flag[0], GameScreen.FRONT);
		GameEngine.screen.addSprite(flag[1], GameScreen.FRONT);
	}
	
	public synchronized void addPlayerOnTeam ( int team ) {
		Player p = new Player ( GameEngine.state.players.size(), team, CaptureTheFlagController.class );
		GameEngine.addPlayer ( p );
	}
}
