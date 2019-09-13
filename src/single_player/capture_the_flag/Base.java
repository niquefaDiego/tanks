package single_player.capture_the_flag;

import geom.Vector;
import core.Images;
import core.Player;
import core.Values;
import core.interfaces.Sprite;
import core.views.GameScreen;

/**
 * Represents a team base.
 * (i,j) represents the map tile where the base is.
 * @author niquefadiego
 */

public class Base implements Sprite
{
	int i, j, team;
	
	public Vector getPosition ( ) {
		return new Vector(i,j).prod(Values.TILE_SIZE).add( Values.TILE_SIZE/2, Values.TILE_SIZE/2 );
	}
	
	Base ( int team, int i, int j ) {
		this.team = team;
		this.i = i;
		this.j = j;
	}
	
	@Override
	public void renderOn(GameScreen g) {
		int id = ( Player.homePlayer.getTeamId() == team ? Images.BASE_0 : Images.BASE_1 );
		g.image(Images.get(id), new Vector(i,j).prod(Values.TILE_SIZE));
	}
}
