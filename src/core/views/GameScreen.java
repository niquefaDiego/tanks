package core.views;

import geom.Circle;
import geom.LineSeg;
import geom.Quad;
import geom.Vector;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PImage;
import utils.UnorderedArray;
import core.GameEngine;
import core.Player;
import core.Values;
import core.interfaces.Sprite;
import core.sprites.Attack;
import core.sprites.MagicBox;
import core.sprites.Tank;

/**
 * This is used as a canvas. But at low-level this class is just giving painting orders
 * to the GameWrapper.
 * 
 * @author niquefadiego
 */

public class GameScreen
{
	public RightPanel rightPanel = new GameRightPanel();
	private PGraphics g;
	
	public static final int BACK = 0;
	public static final int MIDDLE = 1;
	public static final int FRONT = 2;
	public static final int LEVELS = 3;
	
	private ArrayList<Sprite> spritesAtLevel[];

	private Vector screenPos;
	/** @return			Position of the topLeft visible screen. */
	public Vector getTopLeft() { return screenPos; }
	
	/**
	 * At a sprite at a given level.
	 * @param level			Must be BACK, MIDDLE or FRONT
	 */
	public void addSprite ( Sprite sprite, int level ) {
		spritesAtLevel[level].add ( sprite );
	}
	
	public void moveScreenUp ( ) {
		screenPos.y = Math.max(0, screenPos.y-Tank.SPEED*2f );
	}
	
	public void moveScreenDown ( ) {
		screenPos.y = Math.min ( GameEngine.map.getHeight()-Values.VISIBLE_SIZE,
								screenPos.y+Tank.SPEED*2f );
	}
	
	public void moveScreenLeft ( ) {
		screenPos.x = Math.max(0, screenPos.x-Tank.SPEED*2f );
	}
	
	public void moveScreenRight ( ) {
		screenPos.x = Math.min ( GameEngine.map.getWidth()-Values.VISIBLE_SIZE,
								screenPos.x+Tank.SPEED*2f );
	}
	
	private void calcScreenPos ( ) {
		if ( !Player.homePlayer.alive() )
			return;
		screenPos = Player.homePlayer.getTank().getPosition();
		screenPos = screenPos.subt ( new Vector(Values.VISIBLE_SIZE/(float)2, Values.VISIBLE_SIZE/(float)2) );
		screenPos.x = Math.max(screenPos.x, 0);
		screenPos.y = Math.max(screenPos.y, 0);
		
		screenPos.x = Math.min(screenPos.x, GameEngine.map.getWidth()-Values.VISIBLE_SIZE);
		screenPos.y = Math.min(screenPos.y, GameEngine.map.getHeight()-Values.VISIBLE_SIZE);
	}
	
	public void noStroke() { g.noStroke(); }
	public void strokeWeight(float weight) { g.strokeWeight(weight); }
	public void fill ( float R, float G, float B ) { g.fill(R,G,B); }
	public void stroke ( float R, float G, float B ) { g.stroke(R,G,B); }
	
	public void rect(Vector pos, float width, float height) {
		pos = pos.subt(screenPos);
		g.rect(pos.x,pos.y,width,height);
	}
	
	public void circle ( Circle circle ) {
		Vector a = circle.C.subt(screenPos);
		g.ellipse(a.x, a.y, 2*circle.r, 2*circle.r);
	}
	
	public void segment ( LineSeg seg ) {
		Vector a = seg.beg().subt(screenPos);
		Vector b = seg.end().subt(screenPos);
		g.line( a.x, a.y, b.x, b.y );
	}
	
	public void quad ( Quad q ) {
		Vector a = q.A.subt(screenPos);
		Vector b = q.B.subt(screenPos);
		Vector c = q.C.subt(screenPos);
		Vector d = q.D.subt(screenPos);
		g.quad(a.x,a.y,b.x,b.y,c.x,c.y,d.x,d.y );
	}
	
	public void image ( PImage img, Vector pos ) {
		pos = pos.subt(screenPos);
		g.image(img,pos.x,pos.y);
	}
	

	@SuppressWarnings("unchecked")
	public GameScreen ( ) {
		System.out.println("intialized screen");
		screenPos = new Vector(0,0);
		spritesAtLevel = new ArrayList[LEVELS];
		for ( int i = 0; i < LEVELS; ++i )
			spritesAtLevel[i] = new ArrayList<Sprite>();
	}
	
	private void renderSpritesAtLevel ( int level ) {
		for ( int i = 0; i < spritesAtLevel[level].size(); ++i )
			spritesAtLevel[level].get(i).renderOn ( this );
	}

	/**
	 * Renders the game on the GameWrapper.
	 * This method will call:
	 * GameMap.render()
	 * Minimap.render()
	 * @see core#Minimap#render()
	 * @see core#GameMap#render()
	 */
	public void renderOn ( PGraphics g )
	{
		calcScreenPos();
		this.g = g;
		GameEngine.map.renderOn ( this );
		
		renderSpritesAtLevel(BACK);
		
		ArrayList<Player> players = GameEngine.state.players;
		for ( int i = 0; i < players.size(); ++i )
			if ( players.get(i).alive() )
				players.get(i).getTank().renderOn(this);
	
		renderSpritesAtLevel(MIDDLE); //unused..
		
		UnorderedArray<Attack> attacks = GameEngine.state.attacks;
		for ( int i = 0; i < attacks.size(); ++i )
			if ( attacks.get(i).isActive() )
				attacks.get(i).renderOn(this);
		
		UnorderedArray<MagicBox> boxes = GameEngine.state.magicBoxes;
		for ( int i = 0; i < boxes.size(); ++i )
			if ( boxes.get(i).isActive() )
				boxes.get(i).renderOn(this);
		
		renderSpritesAtLevel(FRONT);

		rightPanel.renderOn(g);
	}
}
