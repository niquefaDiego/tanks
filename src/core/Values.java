package core;

import java.awt.Dimension;


/**
 * This class contains a bunch of important values that are used a lot.
 * @author niquefadiego
 */

public abstract class Values
{
	public static final float EPS = 1e-3f;
	public static float FPS = 100;
	public static final int TILE_SIZE = 32;
	public static final int VISIBLE_SIZE = 32*12;
	public static final int RIGHT_PANEL_WIDTH = 32*6;
	public static final int MINIMAP_SIZE = 32*6;

	public static synchronized void changeFPS ( float newFPS )
	{
/*		//System.out.println("Tank attributes (" + Tank.SPEED*FPS + "," + Tank.ANGULAR_SPEED*FPS + ")" );
		float k = newFPS/FPS;
		FPS = newFPS;
		//System.out.println("Values.FPS = " + Values.FPS);
		//System.out.println("ratio="+k);
		Tank.SPEED /= k;
		Tank.ANGULAR_SPEED /= k;
		//System.out.println("Tank attributes (" + Tank.SPEED*FPS + "," + Tank.ANGULAR_SPEED*FPS + ")" );
		 * */
	}
	
	public static int SCREEN_WIDTH = VISIBLE_SIZE+RIGHT_PANEL_WIDTH;
	public static int SCREEN_HEIGHT = VISIBLE_SIZE;
	public static final Dimension PANEL_SIZE = new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT);
	
	//Tank moves ids
	public static int MOVE_FORWARD = (1<<0);
	public static int MOVE_BACKWARD = (1<<1);
	public static int ROTATE_CW = (1<<2);
	public static int ROTATE_CCW = (1<<3);
	public static int ROTATE_GUN_CW = (1<<4);
	public static int ROTATE_GUN_CCW = (1<<5);
	public static int SHOOT = (1<<6);
	
	static public float toFPS ( float secs ) { return secs*FPS; }
}
