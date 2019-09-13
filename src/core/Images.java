package core;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class Images
{
	static public final int TILE = 		0;
	static public final int WALL = 		1;
	static public final int BOX = 		2;
	static public final int FLAG_0 = 		3;
	static public final int FLAG_1 = 		4;
	static public final int BASE_0 = 		5;
	static public final int BASE_1 = 		6;
	static public final int MINFLAG_0 = 	7;
	static public final int MINFLAG_1 = 	8;
	
	static private PImage img[] = new PImage[9];
	static private final String FOLDER = "res/";
	static private boolean loaded = false;
	
	/** Get image with id i. */
	static public PImage get ( int i ) { return img[i]; }
	
	/**
	 * Load all images. This method is called by the GameWrapper.setup method.
	 * @param a			Helper PApplet which will load the images.
	 */
	static public void load ( PApplet a ) {
		if ( loaded ) return;
		img[TILE] = a.loadImage(FOLDER+"floor/tile.png");
		img[WALL] = a.loadImage(FOLDER+"floor/wall.png");
		img[BOX] = a.loadImage(FOLDER+"floor/box.png");
		img[FLAG_0] = a.loadImage(FOLDER+"flag0.png");
		img[FLAG_1] = a.loadImage(FOLDER+"flag1.png");
		img[BASE_0] = a.loadImage(FOLDER+"floor/base0.png");
		img[BASE_1] = a.loadImage(FOLDER+"floor/base1.png");
		img[MINFLAG_0] = a.loadImage(FOLDER+"minflag0.png");
		img[MINFLAG_1] = a.loadImage(FOLDER+"minflag1.png");
		loaded = true;
	}
}