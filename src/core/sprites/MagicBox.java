package core.sprites;

import geom.Quad;
import geom.Vector;

import core.GameEngine;
import core.Images;
import core.interfaces.Sprite;
import core.views.GameScreen;
import utils.UnorderedArray;

/**
 * This class represents a box that will appear at random places and give ammo or power ups to
 * the player whose tank take it.
 * @author niquefadiego
 *
 */

public class MagicBox implements Sprite
{
	static private int SIZE = 16;
	static private int maxInstances = 20;
	static private int instances = 0;
	
	/**
	 * Maximum number of Magic Boxes that can be in the map at the same time.
	 * @param maxInstances
	 */
	static public void setMaxInstances ( int maxInstances ) { MagicBox.maxInstances = maxInstances; }

	private Vector position;
	private boolean active;
	
	/**
	 * @return true iff the box is not taken yet.
	 */
	public boolean isActive ( ) { return this.active; }
	
	/**
	 * @return Quad	 	Quadrilateral representing the shape of the box.
	 * @see utils#Quad
	 */
	public Quad getShape ( ) {
		return new Quad ( position, position.add(SIZE,0), position.add(SIZE,SIZE),
				position.add(0,SIZE) );
	}
	
	private Tank intersectsWith ( ) {
		UnorderedArray<Tank> a = GameEngine.map.tanksNear(position);
		if ( a.isEmpty() ) return null;
		Quad q = getShape();
		for ( int i = 0; i < a.size(); ++i )
			if ( a.get(i).intersectsQuad(q) )
				return a.get(i);
		return null;
	}
	
	private Tank collidesWith ( ) {
		UnorderedArray<Tank> a = GameEngine.map.tanksNear(position);
		if ( a.isEmpty() ) return null;
		Quad q = getShape();
		for ( int i = 0; i < a.size(); ++i )
			if ( a.get(i).collidesQuad(q) )
				return a.get(i);
		return null;
	}
	
	/**
	 * Called by the GameEngine.update method.
	 */
	public void update ( ) {
		Tank t = collidesWith();
		if ( t != null ) {
			this.active = false;
			instances--;
			t.getOwner().foundMagicBox();
		}
	}
	
	/**
	 * Draws a magic box in the given screen.
	 */
	@Override
	public void renderOn(GameScreen g) {
		g.image(Images.get(Images.BOX),position);
	}
	
	/**
	 * Creates a new MagicBox in a random position.
	 */
	 public MagicBox ( )
	 {
		if ( instances >= MagicBox.maxInstances )
			return; //MagicBox active instance limit reached	

		instances++;
		position = new Vector();
		while ( true ) {
			position = GameEngine.map.randomNonWallPoint();
			if ( GameEngine.map.intersectsQuadWalls(getShape()) ) continue;
			if ( this.intersectsWith() == null ) break;
		}
		active = true;
	}
}