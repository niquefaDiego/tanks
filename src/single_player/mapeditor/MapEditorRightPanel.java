package single_player.mapeditor;

import core.Values;
import core.views.GameRightPanel;
import processing.core.PConstants;
import processing.core.PGraphics;
import geom.Vector;

/**
 * Represents the right panel of a map editor gameplay.
 * This contains buttons "save", "open" and "exit" instead of the
 * text that appear in a normal right panel.
 * @author niquefadiego
 *
 */

public class MapEditorRightPanel extends GameRightPanel
{
	class AxesParallelRect
	{
		float sx, sy, w, h;
		boolean contains ( float x, float y ) {
			return sx <= x && x <= sx+w && sy <= y && y <= sy+h;
		}
		
		@Override
		public String toString ( ) {
			return "[ (" + sx + "," + sy + "), dim=(" + w + "," + h + ") ]";
		}
		
		AxesParallelRect ( float x, float y, float w, float h ) {
			this.sx = x;
			this.sy = y;
			this.w = w;
			this.h = h;
		}
	}
	
	private int buttonSpacerY = 20;
	private int buttonSpacerX = 50;
	private int buttonWidth = Values.RIGHT_PANEL_WIDTH - 2*buttonSpacerX;
	private int buttonHeight = 20;
	private AxesParallelRect openButtonRect;
	private AxesParallelRect saveButtonRect;
	private AxesParallelRect backButtonRect;
	
	/** @return true iff the position is inside the save button. */
	public boolean clickedSave ( float x, float y ) {
		if ( !saveButtonRect.contains(x, y)) return false;
		return true;
	}
	
	/** @return true iff the position is inside the open button. */
	public boolean clickedOpen ( float x, float y ) {
		if ( !openButtonRect.contains(x, y)) return false;
		return true;
	}
	
	/** @return true iff the position is inside the back button. */
	public boolean clickedBack ( float x, float y ) {
		if ( !backButtonRect.contains(x, y)) return false;
		return true;
	}
	
	/** Instantiate the MapEditorRightPanel */
	MapEditorRightPanel ( )
	{
		Vector pos = new Vector ( topLeft.x+buttonSpacerX, topLeft.y+buttonSpacerY );
		openButtonRect = new AxesParallelRect ( pos.x, pos.y, buttonWidth, buttonHeight );
		pos = pos.add ( 0, buttonHeight+buttonSpacerY );
		saveButtonRect = new AxesParallelRect ( pos.x, pos.y, buttonWidth, buttonHeight );
		pos = pos.add ( 0, buttonHeight+buttonSpacerY );
		backButtonRect = new AxesParallelRect ( pos.x, pos.y, buttonWidth, buttonHeight );
	}

	
	private void drawButton ( PGraphics g, AxesParallelRect rect, String msg )
	{
		g.fill(0,255,0);
		g.rect(rect.sx,rect.sy,rect.w,rect.h);
		g.textAlign(PConstants.CENTER,PConstants.CENTER);
		g.stroke(0,0,0);
		g.fill(0,0,0);
		g.text(msg,rect.sx,rect.sy,rect.w,rect.h);

	}
	
	@Override
	public void renderOn(PGraphics g) {
		g.fill(0,0,0);
		g.rect(topLeft.x,topLeft.y,topRight.x,topRight.y);
		
		drawButton ( g, openButtonRect, "Open" );
		drawButton ( g, saveButtonRect, "Save" );
		drawButton ( g, backButtonRect, "Back" );
		
		minimap.renderOn ( g );
	}
}
