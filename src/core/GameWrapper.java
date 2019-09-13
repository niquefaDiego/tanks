package core;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.interfaces.Gameplay;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import single_player.mapeditor.MapEditor;

/**
 * This class is something like a singleton.
 * You can only instantiate it from the GameWrapper.getNewInstance method.
 * The GameWrapper.getInstance() returns the last GameWrapper instance created.
 * The reason why this class is not implemented as a singleton, is because of it generates
 * some issues with the PApplet and JFrame. <br>
 * <br>
 * The GameWrapper will take a Gameplay and a JFrame and will play that game in the JFrame. <br>
 * The GameWrapper takes care of: <br>
 * * Calling the Gameplay.update method at the rate specified by Values.FPS. <br>
 * * Listen to player input and tell the home player the commands. <br>
 * * Pausing the game. <br>
 * 
 * 
 * @author niquefadiego
 */
public class GameWrapper extends PApplet
{
	private static final long serialVersionUID = -2118367433739565636L;	
	private boolean showGameOverMessage = true;
	
	static private PGraphics pgraphics;
	static private GameWrapper instance = null;
	
	/** Returns the last instance created. */
	static public GameWrapper getInstance ( ) { return instance; }
	
	/**
	 * Creates and returns a new GameWrapper instance which will
	 * wrap the given game and display it in the given frame.
	 */
	static public GameWrapper getNewInstance ( Gameplay game, JFrame frame ) {
		return instance = new GameWrapper ( game, frame );
	}
	
	private boolean startedRunning = false;
	private boolean running = true; //False if and only if the player chose to exit
	private boolean paused = false;
	private static int frames;
	
	private Gameplay game;
	private JFrame frame;
	private JPanel containerPanel;

	/**
	 * @return Panel that contains the GameWrapper
	 */
	public JPanel getContainerPanel() { return containerPanel; }
	
	static private int cmds;
	
	/**
	 * @return	Bitmask representing which keys are being pressed by the player.
	 */
	static public int getCmds ( ) { return cmds; }
	
	/**
	 * @return returns PGraphics attribute g inherited from PApplet.
	 */
	public PGraphics getPGraphics() { return pgraphics; }
	
	/**
	 * @return	returns Gameplay.
	 */
	public Gameplay getGameplay() { return game; }
	
	/**
	 * Call to pass through the "Press enter to start" screen without user input.
	 */
	public void startRunning ( ) { running=true; }
	
	private static HashMap<Object,Integer> keyMask;
	
	static {
		keyMask = new HashMap<Object,Integer> ( );
		keyMask.put ( ' ', Values.SHOOT );
		keyMask.put ( 'A', Values.ROTATE_CCW );
		keyMask.put ( 'S', Values.MOVE_BACKWARD );
		keyMask.put ( 'D', Values.ROTATE_CW );
		keyMask.put ( 'W', Values.MOVE_FORWARD );
		
		keyMask.put ( LEFT, Values.ROTATE_GUN_CCW );
		keyMask.put ( RIGHT, Values.ROTATE_GUN_CW );
	}
	
	/**
	 * @return true	 iff the game ended or the player chose to exit.
	 */
	public boolean isGameRunning ( ) { return running; }
	
	/**
	 * @return returns current frame number. Starts from zero.
	 */
	public static int currentFrame() { return frames; }
	
	private static final long NOT_FROZEN = -1;
	private static final long ALREADY_UNFROZEN = -2;
	private long freezeUntil = NOT_FROZEN;
	private boolean isFrozen() {
		if ( freezeUntil != NOT_FROZEN && freezeUntil <= System.currentTimeMillis() )
			freezeUntil = ALREADY_UNFROZEN;
		if ( freezeUntil < 0 ) return false;
		return true;
	}
	
	/**
	 * Handles key event.
	 */
	@Override
	public void keyPressed ( ) {
		if ( isFrozen() ) return;

		if ( key == PApplet.CODED ) {
			if ( keyMask.containsKey(keyCode) ) cmds |= keyMask.get(keyCode);
			if ( keyCode == UP ) Player.homePlayer.setNextAttack();
			else if ( keyCode == DOWN ) Player.homePlayer.setPrevAttack();
		}
		else {
			char k = key;
			if ( 'a' <= k && k <= 'z' ) k = (char) (k-'a'+'A');
			if ( Player.homePlayer != null && !Player.homePlayer.alive() ) {
				System.out.println("checking for homeplayer actions");
				if ( k == 'A' ) GameEngine.screen.moveScreenLeft();
				else if ( k == 'D' ) GameEngine.screen.moveScreenRight();
				else if ( k == 'S' ) GameEngine.screen.moveScreenDown();
				else if ( k == 'W' ) GameEngine.screen.moveScreenUp();
			}
			if ( Player.homePlayer != null && Player.homePlayer.alive() && k == 'Q' )
				Player.homePlayer.superRafaPower();
			if ( keyMask.containsKey(k) ) cmds |= keyMask.get(k);
			else if ( '0' <= k && k <= '9' ) Player.homePlayer.setAttack((k-'0'+9)%10);
			else if ( 'P' == k ) { if(game.canPause()) paused=!paused; }
			else if ( '\n' == k ) startedRunning = true;
		}
	}

	/**
	 * Handles key event.
	 */
	@Override
	public void keyReleased ( ) {
		if ( isFrozen() ) return;
		if ( key == PApplet.CODED ) {
			if ( keyMask.containsKey(keyCode) && (cmds&keyMask.get(keyCode)) != 0 )
				cmds ^= keyMask.get(keyCode);
		}
		else {
			char k = key;
			if ( 'a' <= k && k <= 'z' ) k = (char) (k-'a'+'A');
			if ( keyMask.containsKey(k) && (cmds&keyMask.get(k)) != 0 )
				cmds ^= keyMask.get(k);
		}
	}

	
	private GameWrapper ( Gameplay game, JFrame frame )
	{
		showGameOverMessage = !( game instanceof MapEditor );
		this.frame = frame;
		this.init();
		this.game = game;
		this.containerPanel = new JPanel();
		GameWrapper.frames = 0;
		this.setSize ( Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT );

		this.setPreferredSize(new Dimension(Values.SCREEN_WIDTH,Values.SCREEN_HEIGHT));

		this.containerPanel.add ( this );
		GameWrapper.pgraphics = this.g;
		cmds = 0;
		System.out.println("set frame rate to " + Values.FPS);
		frameRate ( Values.FPS );
	}

	/**
	 * Initialize what is needed.
	 */
	@Override
	public void setup ( ) {
		Images.load(this);
		this.imageMode(CORNER);
		lastSecond = System.currentTimeMillis();
	}

	/**
	 * Renders a message in the whole screen.
	 * @param msg			message to be displayed
	 */
	public void renderMessage ( String msg )
	{
		noStroke();
		fill(255,255,255);
		rect(0,0,Values.SCREEN_WIDTH,Values.SCREEN_HEIGHT);

		textAlign(PConstants.CENTER,PConstants.CENTER);
		textSize(50);
		fill(0,0,0);
		text(msg,Values.SCREEN_WIDTH/2, Values.SCREEN_HEIGHT/2 );
	}
	
	private int fps = 0;
	private long lastSecond;
	
	/**
	 * Game loop.
	 */
	@Override
	public void draw ( )
	{
		if ( lastSecond+1000 <= System.currentTimeMillis() ) {
			frame.setTitle ( "Tanks fps=" + fps );
			lastSecond+=1000;
			fps = 0;
		}
		
		if ( game.winnerTeam() != -1 )
		{
			if ( showGameOverMessage )
			{
				if ( freezeUntil == NOT_FROZEN )
					freezeUntil = System.currentTimeMillis() + 1500L;
				if ( game.winnerTeam() == Player.homePlayer.getTeamId() )
					renderMessage("You win!");
				else renderMessage("You lose");
	
				if ( freezeUntil == ALREADY_UNFROZEN )
					running = false;
			}
			else
				running = false;
		} else if ( !startedRunning && showGameOverMessage) {
			this.renderMessage("Press enter to start");
		} else if ( paused ) {
			this.renderMessage("Paused");
		} else {
			fps++;
			game.update();
			GameEngine.screen.renderOn(g);
			frames++;
		}
	}
} 