package core.sprites;

import geom.LineSeg;
import geom.Vector;

import java.util.ArrayList;

import core.GameEngine;
import core.GameWrapper;
import core.Player;
import core.Values;
import core.views.GameScreen;

/**
 * 
 * The laser pass through others tanks so it can damage many tanks at the same time.
 * But it doesn't pass through walls. Also the damage done at each tank hit is high.
 * 
 * @author niquefadiego
 * @see Attack
 */

public class Laser extends Attack
{
	public static final float power= 50 / Values.FPS;
	private static final int waitTime=(int)(0.5f*Values.FPS);//frames to wait to shoot again

	public static final int DURATION = (int) (1.9f*Values.FPS);
	
	private boolean active = true;
	private long triggerTime;
	
	@Override
	public float getPower() { return Laser.power; }

	@Override
	public boolean isActive() { return this.active; }

	@Override
	public int getReloadTime() { return Laser.waitTime; }

	public Laser(Player owner)
	{
		super(owner);
		triggerTime = GameWrapper.currentFrame();
	}
	
	private LineSeg getShape ( ) {
		Tank t = owner.getTank();
		Vector dir = t.getGunDirection();
		Vector fr = t.getPosition().add(dir.prod(Tank.GUN_LENGTH));
		float length = GameEngine.map.getHeight() + GameEngine.map.getWidth() + 2;
		Vector to = fr.add ( dir.prod(length) );
		to = GameEngine.map.intersectionSegWalls(new LineSeg(fr, to));
		assert ( to != null ) : "Beam donsn't inersects wall";
		return new LineSeg(fr,to);
	}

	@Override
	public void update ( )
	{
		if ( Laser.DURATION < GameWrapper.currentFrame() - triggerTime ) {
			active = false;
			return;
		}
		
		LineSeg shape = this.getShape();
		ArrayList<Player> players = GameEngine.state.players;
		for ( int i = 0; i < players.size(); ++i ) {
			Tank t = players.get(i).getTank();
			if ( t.collidesWithSeg(shape) )
				t.recibeAttack(this);
		}
	}
	
	@Override
	public void renderOn(GameScreen g) {
		g.strokeWeight(3);
		g.stroke(255,255,0);
		g.segment(getShape());
		g.noStroke();
	}
}