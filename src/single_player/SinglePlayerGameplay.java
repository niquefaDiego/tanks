package single_player;

import core.GameMap;
import core.Player;
import core.GameEngine;
import core.Values;
import core.interfaces.Gameplay;

/**
 * In this game play the objective is to be the last team standing.
 * 
 * @see core#interfaces#Gameplay
 *  
 * @author niquefadiego
 */

public class SinglePlayerGameplay implements Gameplay
{
	@Override
	public int winnerTeam ( ) {
		int winnerTeam = -1;
		for ( Player p : GameEngine.state.players ) {
			if ( p.alive() ) {
				if ( winnerTeam == -1 )
					winnerTeam = p.getTeamId();
				else if ( winnerTeam != p.getTeamId() )
					return -1;
			}
		}
		return winnerTeam;
	}
	
	@Override
	public boolean canPause ( ) { return true; }
	
	@Override
	public void update ( ) { GameEngine.update(); }

	@Override
	public void init(GameMap map) {
		Values.changeFPS ( 50 );
		GameEngine.init(map);
		GameEngine.setHomePlayer(0,0);
	}
	
	public synchronized void addPlayerOnTeam ( int team ) {
		GameEngine.addPlayer ( GameEngine.state.players.size(), team );
	}
}
