package core.views;

import geom.Vector;
import core.GameWrapper;
import core.Player;
import core.Values;
import utils.UnorderedArray;

import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * This is the right panel shown in the game.
 * Contains: <br>
 * * Player info(lifes,kills,dead,chosen attack and ammo). <br>
 * * Messages(When something like taking a magic box happens). <br>
 * * Minimap. <br>
 * @author niquefadiego
 */

public class GameRightPanel implements RightPanel
{
	private static final int MAX_MESSAGES = 5;
	private static final int MESSAGE_DURATION = (int)(1.5f*Values.FPS);

	static protected Vector topLeft = new Vector ( Values.VISIBLE_SIZE, 0 );
	static protected Vector topRight = new Vector ( Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT );
	static protected int leftMargin = 10;
	
	protected Minimap minimap = new Minimap();
	
	private class Message {
		int time;
		String text;
		Message ( String text, int time ) {
			this.time = time;
			this.text = text;
		}
	}
	
	private UnorderedArray<Message> msgs = new UnorderedArray<Message>();
	
	/** Adds a message to be displayed */
	public void addMessage ( String message ) {
		msgs.add ( new Message(message,GameWrapper.currentFrame()) );
	}
	
	public void renderOn ( PGraphics g )
	{
		g.textAlign(PConstants.LEFT,PConstants.BOTTOM);
		g.fill(0,0,0);
		g.rect(topLeft.x,topLeft.y,topRight.x,topRight.y);

		g.stroke(255,255,255);
		g.fill(255,255,255);
		g.textSize(16);
		
		int lifes = Player.homePlayer.getLifes();
		g.text("Lifes: " + (lifes==-1?"inf":lifes), topLeft.x+leftMargin, topLeft.y+20 );
		g.text("Kills: " + Player.homePlayer.getKills(), topLeft.x+leftMargin, topLeft.y+40 );
		g.text("Deads: " + Player.homePlayer.getDeads(), topLeft.x+leftMargin, topLeft.y+60 );
		
		g.text("Attack: " + Player.homePlayer.getAttackName(), topLeft.x+leftMargin, topLeft.y+90 );
		g.text("Ammo: " + Player.homePlayer.getAttackAmmo(), topLeft.x+leftMargin, topLeft.y+110 );
		
		g.textSize(12);
		float curY = topLeft.y+150;
		while ( msgs.size() > MAX_MESSAGES ) msgs.remove(0);
		while ( !msgs.isEmpty() )
			if ( GameWrapper.currentFrame() - msgs.get(0).time > MESSAGE_DURATION )
				msgs.remove(0);
			else break;
		
		for ( int i = 0; i < msgs.size(); ++i ) {
			g.text ( msgs.get(i).text, topLeft.x+leftMargin, curY );
			curY += 15;
		}
		
		minimap.renderOn ( g );
	}
}
