package tank_intelligence;

import geom.Vector;

import java.util.ArrayList;

import single_player.capture_the_flag.CaptureTheFlagGamePlay;
import single_player.capture_the_flag.Flag;
import utils.Random;
import core.GameEngine;
import core.GameWrapper;
import core.Player;
import core.Values;
import core.interfaces.Gameplay;
import core.sprites.Tank;

public class CaptureTheFlagController extends TankController
{
	Tank prey;
	Flag flag, flag1;
	Vector base;
	boolean holdFlag;
	
	private void findPrey ( )
	{
		if ( utils.Random.randInt(3) == 0 ) {
			prey = null;
			ArrayList<Player> players = GameEngine.state.players;
			for ( int i = 0; i < players.size(); ++i ) {
				Tank t = players.get(Random.randInt(players.size())).getTank();
				if ( t.isEnemy(tank) ) prey = t;
			}
		} else {
			holdFlag = true;
		}
	}
		
	public int getMoves ( )
	{
		if ( !initialized )
			initialize();
		initialized=true;

		if ( flag.getHolder() != null )
		{
			if ( flag.getHolder().isEnemy(tank) ) {
				prey = flag.getHolder();
				holdFlag = false;
			}
			else if ( flag.getHolder().getOwner().getId() == tank.getOwner().getId() ) {
				return noShoot(TankDriver.movesTowards(this.tank, base));
			}
			else if ( flag1.getHolder() != null && flag1.getHolder().isEnemy(tank)) {
				prey = flag1.getHolder();
				holdFlag = false;
			}
		}

		if ( holdFlag )
			return noShoot(TankDriver.movesTowards(this.tank, flag.getPosition()));

		if ( prey == null || !prey.alive() ) findPrey();
		if ( prey == null || !prey.alive() ) return 0;
		
		int moves = TankDriver.movesTowards(this.tank, this.prey.getPosition());
		
		return moves;
	}
	
	private int noShoot ( int moves ) {
		if ( 0 != (moves | Values.SHOOT) ) moves -= Values.SHOOT;
		return moves;
	}
	
	private boolean initialized = false;
	private void initialize ( ) {
		Gameplay gplay = GameWrapper.getInstance().getGameplay();
		flag = ((CaptureTheFlagGamePlay)gplay).getFlag(1-tank.getOwner().getTeamId());
		flag1 = ((CaptureTheFlagGamePlay)gplay).getFlag(tank.getOwner().getTeamId());
		base = ((CaptureTheFlagGamePlay)gplay).getBase(tank.getOwner().getTeamId()).getPosition();
		findPrey();
	}
	
	public CaptureTheFlagController(Tank tank) {
		super(tank);
	}
}
