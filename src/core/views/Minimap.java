package core.views;

import geom.Vector;

import java.util.ArrayList;

import core.GameEngine;
import core.GameWrapper;
import core.Images;
import core.Player;
import core.Values;
import core.interfaces.Gameplay;
import core.sprites.Tank;

import processing.core.*;
import single_player.capture_the_flag.CaptureTheFlagGamePlay;

/**
 * This class will take the information needed from GameEngine and draw a small map
 * that will show not only the visible part, but the whole map. This shows walls, tanks, bases and flags.
 * It uses color green to allies and red to enemies.
 * 
 * @author niquefadiego
 *
 */

public class Minimap
{
	public static final int SIZE = Values.MINIMAP_SIZE;
	private static final float INNER_SIZE = 32*6-10;
	private static final Vector topLeft = new Vector ( Values.VISIBLE_SIZE, Values.SCREEN_HEIGHT-SIZE);
	
	private static final float diff = SIZE-INNER_SIZE;
	private static final Vector innerTopLeft = topLeft.add( new Vector(diff/2,diff/2) );
	private static Vector miniSquareDim;;
	
	/* Instantiate a Minimap */
	public Minimap ( ) {
		miniSquareDim = new Vector(scaleX(Values.VISIBLE_SIZE), scaleY(Values.VISIBLE_SIZE) );
	}
	
	private float scaleX ( float x ) {
		return x / GameEngine.map.getWidth() * INNER_SIZE;
	}

	private float scaleY ( float y ) {
		return y / GameEngine.map.getHeight() * INNER_SIZE;
	}
	
	private Vector getDrawPosition ( Vector p ) {
		return new Vector(scaleX(p.x),scaleY(p.y)).add(innerTopLeft);
	}
	
	private void drawFlag ( PGraphics g, Vector pos, boolean home ) {
		pos = getDrawPosition ( pos.subt(4,4) );
		g.image ( Images.get(home?Images.MINFLAG_0:Images.MINFLAG_1), pos.x, pos.y );
	}

	
	private void drawBase ( PGraphics g, Vector pos, boolean home ) {
		Vector pos2 = pos.add ( Values.TILE_SIZE, Values.TILE_SIZE );
		pos = getDrawPosition(pos);
		pos2 = getDrawPosition(pos2);
		
		g.strokeWeight(2);
		if ( home ) g.stroke(0,255,0);
		else g.stroke(255,0,0);
		
		g.line ( pos.x, pos.y, pos2.x, pos2.y );
		g.line ( pos.x, pos2.y, pos2.x, pos.y );
		g.noStroke();
		
	}

	/**
	 *	Renders the Minimap on GameWrapper.
	 *	@see GameWrapper#getPGraphics()
	 */
	public void renderOn ( PGraphics g )
	{
		g.noStroke();
		g.fill(0,0,0);
		g.rect ( topLeft.x, topLeft.y, SIZE, SIZE );

		g.fill(255,255,255);
		g.rect ( innerTopLeft.x, innerTopLeft.y, SIZE-diff, SIZE-diff);
		
		Gameplay gamep = GameWrapper.getInstance().getGameplay();
		if ( gamep instanceof CaptureTheFlagGamePlay ) {
			CaptureTheFlagGamePlay ctf = (CaptureTheFlagGamePlay) gamep;
			int homeTeam = Player.homePlayer.getTeamId();
			drawFlag ( g, ctf.getFlagPosition(0), 0==homeTeam );
			drawFlag ( g, ctf.getFlagPosition(1), 1==homeTeam );
			drawBase ( g, ctf.getBasePosition(0), 0==homeTeam );
			drawBase ( g, ctf.getBasePosition(1), 1==homeTeam );
		}
		
		g.fill(0,0,0);
		{
			float sizeX=scaleX(Values.TILE_SIZE), sizeY=scaleY(Values.TILE_SIZE);
			float x = innerTopLeft.x, y;
			for ( int i = 0; i < GameEngine.map.getTilesWidth(); i++ ) {
				y = innerTopLeft.y;
				for ( int j = 0; j < GameEngine.map.getTilesHeight(); j++ ) {
					if ( GameEngine.map.isWall(i,j) )
						g.rect(x,y,sizeX,sizeY);
					y+=sizeY;
				}
				x+=sizeX;
			}
		}
		
		Tank mainT = Player.homePlayer.getTank();
		ArrayList<Player> players = GameEngine.state.players;
		for ( int i = 0; i < players.size(); ++i ) {
			if ( !players.get(i).alive() ) continue;
			Tank t = players.get(i).getTank();
			if ( t.getPlayerId() == mainT.getPlayerId() ) g.fill(0,255,0);
			else if ( mainT.isEnemy(t) ) g.fill(255,0,0);
			else g.fill(0,0,255);

			Vector screenPos = getDrawPosition ( t.getPosition() );
			g.ellipse(screenPos.x,screenPos.y,5,5);
		}
		
		Vector miniSquare = getDrawPosition ( GameEngine.screen.getTopLeft() );
		g.noFill();
		g.stroke(0,0,0);
		g.strokeWeight(1);
		g.rect ( miniSquare.x, miniSquare.y, miniSquareDim.x, miniSquareDim.y );
	}
}
