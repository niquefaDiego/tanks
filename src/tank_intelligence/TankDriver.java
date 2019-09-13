package tank_intelligence;

import geom.Vector;
import core.Values;
import core.sprites.Tank;


/**
 * This class will only be used by other TankController classes.
 * This class will do it's best to control a Tank to destroy a given Tank, ignoring everything else.
 * 
 * @author niquefa_diego
 */

class TankDriver
{
	static int movesTowards ( Tank tank, Vector target ) {
		int moves = 0;
		
		int i = (int)(tank.getPosition().x/Values.TILE_SIZE);
		int j = (int)(tank.getPosition().y/Values.TILE_SIZE);
		
		int ii = (int)(target.x/Values.TILE_SIZE);
		int jj = (int)(target.y/Values.TILE_SIZE);
		
		int init = MapInfo.encode(i,j);
		int dest = MapInfo.encode(ii, jj);
		
		if ( MapInfo.canShoot(init,dest) ) moves |= Values.SHOOT;
		
		int nextTile = MapInfo.next[init][dest];
		
		Vector middlePos = new Vector();
		middlePos.x = (MapInfo.decodeX(nextTile)+0.5f)*Values.TILE_SIZE;
		middlePos.y = (MapInfo.decodeY(nextTile)+0.5f)*Values.TILE_SIZE;
		
		Vector cur = tank.getPosition();
		Vector dir = tank.getDirection();
		Vector gunDir = tank.getGunDirection();
		
		Vector moveDir = middlePos.subt(cur);
		
		float ang = moveDir.angleBetween(dir);
		final float REQ_ANGLE = (float)(10*Math.PI/180);
		
		if ( moveDir.isLeftTo( dir.rotate90CW()  ) )
		{
			if ( ang < REQ_ANGLE )
				moves |= Values.MOVE_FORWARD;

			if ( ang > Tank.ANGULAR_SPEED ) {
				if ( moveDir.isLeftTo(dir) )
					moves |= Values.ROTATE_CCW;
				else moves |= Values.ROTATE_CW;
			}
		}
		else
		{
			ang = (float) (Math.PI-ang);
			if ( ang < REQ_ANGLE )
				moves |= Values.MOVE_BACKWARD;
			
			if ( ang > Tank.ANGULAR_SPEED ) {
				if ( !moveDir.isLeftTo(dir) )
					moves |= Values.ROTATE_CCW;
				else moves |= Values.ROTATE_CW;
			}
		}
		
		if ( gunDir.angleBetween(target.subt(cur)) > Tank.ANGULAR_SPEED ) {
			if ( gunDir.isLeftTo(target.subt(cur)) )
				moves |= Values.ROTATE_GUN_CW;
			else moves |= Values.ROTATE_GUN_CCW;
		}
		
		return moves;
	}
}
