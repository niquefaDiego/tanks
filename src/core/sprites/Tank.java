package core.sprites;

import geom.Circle;
import geom.LineSeg;
import geom.Quad;
import geom.Vector;
import utils.Random;
import utils.UnorderedArray;

import core.GameEngine;
import core.Values;
import core.Player;
import core.interfaces.Sprite;
import core.views.GameScreen;

/**
 * Represents the Tank sprite.
 * 
 * @author niquefadiego
 */

public class Tank implements Sprite
{
	public static final int LENGTH = 30;
	public static final int WIDTH = 15;
	public static final int GUN_RADIUS = 5;
	public static final int GUN_LENGTH = 20;
	public static final int GUN_WIDTH = 3;
	
	public static final int HEALTH_BAR_LENGTH = 20;
	public static final int HEALTH_BAR_WIDTH = 4;
	public static final float INITIAL_HEALTH = 100f;
	
	/** Speed of the tank in pixels per second. */
	public static float SPEED = 64/(float)Values.FPS;
	
	/** Angular speed of both the tank and the gun. In radians per frame. */
	public static float ANGULAR_SPEED = (float) ( (Math.PI/2.0)/Values.FPS );
	
	private Player owner;
	private Vector position;
	private Vector direction;
	private Vector gunDirection;
	
	//coordinates of Tank parts
	
	private float health; //in [0,100]
	private int cellI, cellJ;
	
	/** @return 	true iff the given tank is not in the same team. */
	public boolean isEnemy ( Tank that ) { return this.owner.getTeamId() != that.owner.getTeamId(); }
	
	/** @return		id of the owner player. */
	public int getPlayerId ( ) { return this.owner.getTeamId(); }
	
	/** @return 	owner player. */
	public Player getOwner ( ) { return this.owner; }
	
	/** @return 	position of the center of the tank. */
	public Vector getPosition ( ) { return this.position; }
	
	/** @return 	vector representing the tank direction. */
	public Vector getDirection ( ) { return this.direction; }
	
	/** @return 	vector representing the gun direction. */
	public Vector getGunDirection ( ) { return this.gunDirection; }
	
	/** @return 	health of the tank. */
	public float getHealth ( ) { return this.health; }
	
	/** Equivalent to Tank.getHealth() > 0 */
	public boolean alive() { return this.health > 0; }
	
	private void calcCellPos ( ) {
		this.cellI = (int)(position.x/Values.TILE_SIZE);
		this.cellJ = (int)(position.y/Values.TILE_SIZE);
	}
	
	public boolean containsPoint ( Vector P ) {
		return getShape().contains(P);
	}
	
	/** This method is called by the Attack.update method when it hits the tank. */
	public void recibeAttack ( Attack a ) {
		if ( this.health > Values.EPS ) {
			this.health = Math.max(0, this.health-a.getPower());
			if ( !this.alive() )
				a.getOwner().kills( this.getOwner() );
		}
	}
	
	/** Adds something to the health of the tank.
	 * If the resulting health is less tan zero then is set to zero.
	 * If the resulting health if more than Tank.INITIAL_HEALTH then is set to
	 * Tank.INITIAL_HEALTH.
	 */
	public void changeHealth ( float change ) {
		if ( change <= 0 ) this.health = Math.max(0, this.health+change );
		else this.health = Math.min(Tank.INITIAL_HEALTH, this.health+change );
	}
	
	/**
	 * Creates a new Tank owned by the given player and place it at a random position.
	 */
	//Constructor
	public Tank ( Player owner )
	{
		this.owner = owner;
		this.health = Tank.INITIAL_HEALTH;
		
		do {
			this.position = GameEngine.map.randomNonWallPoint();
			this.direction = new Vector(Random.randInt(10)+1,Random.randInt(10)+1);
			this.direction = this.direction.normalize();
			this.gunDirection = this.direction;

			boolean valid = true;
			
			if ( !this.isValidTankPosition() ) valid = false;

			for ( Player p : GameEngine.state.players ) {
				if ( p.getId() == this.owner.getId() ) continue;
				Tank t = p.getTank();
				if ( this.intersectsQuad(t.getShape()) ) valid = false;
				if ( t.containsPoint(this.position) ) valid = false;
				if ( this.containsPoint(t.getPosition()) ) valid = false;
			}
			
			if ( valid ) break;
		} while ( true );
		
		this.calcCellPos();
		GameEngine.map.addTankAtCell(cellI,cellJ,this);
	}
	
	/**
	 * @return quadrilateral representing the solid shape of the tank.
	 */
	public Quad getShape ( ) {
		Vector pos = this.position;
		Vector perpDirection = this.direction.rotate90CW();
		Vector forward = this.direction.prod(Tank.LENGTH/2);
		Vector left = perpDirection.prod(Tank.WIDTH/2);
		Quad r = new Quad();
		r.A = pos.add (forward).add (left);
		r.B = pos.add (forward).subt(left);
		r.C = pos.subt(forward).subt(left);
		r.D = pos.subt(forward).add (left);
		return r;
	}
	
	/** @return		true iff the tank solid shape intersects the given segment. */ 
	public boolean collidesWithSeg ( LineSeg seg ) {
		return getShape().intersects(seg);
	}
	
	/** @return		true iff the tank solid shape collides the given quadrilateral. */
	public boolean collidesQuad ( Quad q ) {
		return getShape().collides(q);
	}
	
	/** @return		true iff the tank solid shape intersects the given quadrilateral. */
	public boolean intersectsQuad ( Quad q ) {
		return getShape().intersects(q);
	}
	
	private boolean isValidTankPosition ( )
	{
		Quad q = this.getShape();
		if ( GameEngine.map.intersectsQuadWalls(q) )
			return false;
		
		UnorderedArray<Tank> near = GameEngine.map.tanksNear(position);
		for ( int i = 0; i < near.size(); ++i )
			if ( this.owner.getId() != near.get(i).getOwner().getId() )
				if ( near.get(i).collidesQuad(q) )
					return false;

		return true;
	}

	/** Move the tank forward if possible. */
	public void moveForward ( ) {
		Vector prevPosition = this.position;
		this.position = this.position.add(this.direction.prod(Tank.SPEED));
		if ( !this.isValidTankPosition() )
			this.position = prevPosition;
		else {
			GameEngine.map.removeTankAtCell(cellI, cellJ, this );
			this.calcCellPos();
			GameEngine.map.addTankAtCell(cellI, cellJ, this);
		}
	}
	
	/** Removes the tank from the map. */
	public void dispose ( ) {
		GameEngine.map.removeTankAtCell(cellI, cellJ, this);
	}
	
	/** Move the tank bacward if possible. */
	public void moveBackward ( ) {
		Vector prevPosition = this.position;
		this.position = this.position.subt(this.direction.prod(Tank.SPEED));
		if ( !this.isValidTankPosition() )
			this.position = prevPosition;
		else {
			GameEngine.map.removeTankAtCell(cellI, cellJ, this );
			this.calcCellPos();
			GameEngine.map.addTankAtCell(cellI, cellJ, this);
		}
	}
	
	/** Rotates tank gun clockwise if possible. */
	public void rotateGunCW ( ) { this.gunDirection = this.gunDirection.rotateCW(Tank.ANGULAR_SPEED); }
	
	/** Rotates tank gun counter-clockwise if possible. */
	public void rotateGunCCW ( ) { this.gunDirection = this.gunDirection.rotateCCW(Tank.ANGULAR_SPEED); }
	
	/** Rotates tank clockwise if possible. */
	public void rotateCW ( ) {
		Vector prevDirection = this.direction; 
		this.direction = direction.rotateCW(Tank.ANGULAR_SPEED);
		if ( this.isValidTankPosition() ) rotateGunCW();
		else this.direction = prevDirection;
	}
	
	/** Rotates tank counter-clockwise if possible. */
	public void rotateCCW ( ) {
		Vector prevDirection = this.direction; 
		this.direction = direction.rotateCCW(Tank.ANGULAR_SPEED);
		if ( this.isValidTankPosition() ) rotateGunCCW();
		else this.direction = prevDirection;
	}

	/** @see core#interfaces#Sprite */
	@Override
	public void renderOn(GameScreen g)
	{
		Quad q = this.getShape();
		
		g.strokeWeight(1);
		g.stroke(0,0,0);
		if ( this.owner.getId() == Player.homePlayer.getId() ) g.fill(0,255,0);
		else if ( this.owner.getTeamId() == Player.homePlayer.getTeamId() ) g.fill(0,0,255);
		else g.fill(255,0,0);
		g.quad(q);
		
		Vector gunEnd = this.position.add ( gunDirection.prod(Tank.GUN_LENGTH) );
		
		g.noStroke();
		g.fill(0,0,0);
		g.circle(new Circle(position,Tank.GUN_RADIUS) );

		g.stroke(0,0,0);
		g.strokeWeight(Tank.GUN_WIDTH);
		g.segment(new LineSeg(position,gunEnd) );
		
		Vector P = q.A.add( q.D.subt(q.A).prod(0.93f) );
		Vector Q = q.B.add( q.C.subt(q.B).prod(0.93f) );
		g.noStroke();
		g.fill(0,0,0);
		g.quad( new Quad(q.D,P,Q,q.C) );
		
		
		//Last thing to do: Draw health bar
		Vector healthBarStart = position.add ( -Tank.HEALTH_BAR_LENGTH/2, -Tank.LENGTH/2-5 );
		healthBarStart.subt(GameEngine.screen.getTopLeft());
		if ( healthBarStart.y <= Tank.HEALTH_BAR_WIDTH ) healthBarStart.y += Tank.LENGTH+10;
		if ( healthBarStart.y+Tank.HEALTH_BAR_WIDTH >= GameEngine.map.getHeight() )
			healthBarStart.y -= Tank.LENGTH+10;
		if ( healthBarStart.x <= 0 ) healthBarStart.x = 0;
		healthBarStart.x = Math.min(GameEngine.map.getWidth()-Tank.HEALTH_BAR_LENGTH, healthBarStart.x );
		
		float healthBarMidX = healthBarStart.x + Tank.HEALTH_BAR_LENGTH*(this.health/Tank.INITIAL_HEALTH);
		float widthGreen = healthBarMidX - healthBarStart.x;
		float widthRed = Tank.HEALTH_BAR_LENGTH - widthGreen;
		
		g.strokeWeight(1);
		g.stroke(0,0,0);
		g.fill(255,0,0);
		g.rect ( healthBarStart.add(widthGreen,0), widthRed, Tank.HEALTH_BAR_WIDTH );
		g.fill(0,255,0);
		g.rect ( healthBarStart, widthGreen, Tank.HEALTH_BAR_WIDTH );
	}
}