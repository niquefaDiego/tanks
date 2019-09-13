package single_player.capture_the_flag;

import java.util.HashSet;

import utils.UnorderedArray;

import core.GameEngine;
import core.Images;
import core.Player;
import core.Values;
import core.interfaces.Sprite;
import core.sprites.Tank;
import core.views.GameScreen;

import geom.Vector;

/**
 * This class represent a flag.
 * 
 * @author niquefadiego
 */

public class Flag implements Sprite
{
	private int team;
	private Vector position;
	private Tank holder;
	
	private HashSet<Integer> holders = new HashSet<Integer>();
	
	/** @return position of the base of the flag. */
	public Vector getPosition ( ) { return this.position; }
	
	/** @return Tank which holds this flag, or null if none does. */
	public Tank getHolder ( ) { return this.holder; }
	
	/** @return Team id. */
	public int getTeam ( ) { return this.team; }
	
	public void update ( )
	{
		if ( holder != null ) {
			if ( !holder.alive() ) {
				holders.remove(holder.getOwner().getId());
				holder = null;
			}
			else position = holder.getPosition();
		}
		
		if ( holder == null ) {
			UnorderedArray<Tank> tanks = GameEngine.map.tanksNear(position);
			for ( int i = 0; i < tanks.size(); ++i ) {
				Tank t = tanks.get(i);
				if ( t.containsPoint(this.position) ) {
					this.holder = t;
					holders.add(t.getOwner().getId());
					break;
				}
			}
		} else
			position = holder.getPosition();
	}
	
	@Override
	public void renderOn ( GameScreen p )
	{
		int id = ( team == Player.homePlayer.getTeamId() ? Images.FLAG_0 : Images.FLAG_1 );
		p.image( Images.get(id), position.subt(16,16) );
	}
	
	Flag ( int team, int i, int j ) {
		this.team=team;
		this.holder = null;
		position = new Vector(i,j);
		position = position.prod(Values.TILE_SIZE).add ( Values.TILE_SIZE/2, Values.TILE_SIZE/2);
	}
}
