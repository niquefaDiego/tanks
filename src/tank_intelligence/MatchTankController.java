package tank_intelligence;

import java.util.ArrayList;

import utils.Random;

import core.GameEngine;
import core.Player;
import core.sprites.Tank;

public class MatchTankController extends TankController 
{
	Tank prey;
	
	private void findPrey ( ) {
		prey = null;
		ArrayList<Player> players = GameEngine.state.players;
		for ( int i = 0; i < players.size(); ++i ) {
			Tank t = players.get(Random.randInt(players.size())).getTank();
			if ( t.isEnemy(tank) ) prey = t;
		}
	}
	
	public int getMoves ( ) {
		if ( prey == null || !prey.alive() ) findPrey();
		if ( prey == null || !prey.alive() ) return 0;
		
		int moves = TankDriver.movesTowards(this.tank, this.prey.getPosition());
		
		return moves;
	}
	
	public MatchTankController(Tank tank) {
		super(tank);
		findPrey();
	}
}
