package core.sprites;

import geom.Circle;
import geom.Vector;

import core.GameEngine;
import core.Values;
import core.Player;
import core.views.GameScreen;

/**
 * Simpler and weaker attack. But players have infinite ammo of this one.
 * 
 * @author niquefadiego
 * @see Attack
 */

public class Bullet extends Attack
{
	public static final float power=5;
	private static final int waitTime= (int)(Values.toFPS(0.35f));
	private static final float SPEED = 100/Values.FPS;
	
	private boolean active = true;
	
	@Override
	public float getPower() { return Bullet.power; }

	@Override
	public boolean isActive() { return this.active; }
	
	@Override
	public int getReloadTime ( ) { return Bullet.waitTime; }

	private Vector position;
	private Vector direction;
	
	public Bullet(Player owner) {
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