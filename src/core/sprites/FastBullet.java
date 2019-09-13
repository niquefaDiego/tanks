package core.sprites;

import geom.Circle;
import geom.Vector;

import core.sprites.Attack;
import core.GameEngine;
import core.Player;
import core.Values;
import core.views.GameScreen;

/**
 * 
 * This is very similar to Bullet, but the reload time is smaller and the damage done
 * per attack is higher. So this is supposed to be a powerful weapon.
 * 
 * @author niquefadiego
 * @see Attack
 */

public class FastBullet extends Attack
{
	public static final float power=7;
	private static final int waitTime= (int)(Values.toFPS(0.15f));
	private static final float SPEED = 150/Values.FPS;
	
	private Vector position;
	private Vector direction;
	private boolean active = true;

	@Override
	public boolean isActive() { return this.active; }
	
	@Override
	public float getPower() { return FastBullet.power; }

	@Override
	public int getReloadTime() { return FastBullet.waitTime; }
	
	public FastBullet(Player owner) {
		super(owner);

		Tank tank = owner.getTank();
		position = tank.getPosition().add( tank.getGunDirection().prod(Tank.GUN_LENGTH));
		direction = tank.getGunDirection();
	}

	@Override
	public void update ( ) {
		position = position.add(direction.prod(SPEED));
		if ( GameEngine.map.isInsideWall(position) ) {
			active = false;
			return;
		}
		
		Tank t = GameEngine.map.getTankAt(this.position);
		if ( t != null ) {
			t.recibeAttack(this);
			this.active = false;
		}
	}
	
	@Override
	public void renderOn(GameScreen g) {
		if ( !this.active ) return;
		g.noStroke();
		g.fill(0,0,0);
		g.circle(new Circle(position,1.5f) );
	}
}