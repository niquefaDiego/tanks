package core;

import tank_intelligence.MatchTankController;
import utils.Random;
import utils.UnorderedArray;
import core.interfaces.Gameplay;
import core.sprites.Attack;
import core.sprites.MagicBox;
import core.views.GameScreen;

/**
 * This abstract class contains only static methods and variables, these will bring
 * together the GameState, GameMap, GameSettings, GameScreen and the Controllers.
 * 
 * The Gameplay.init method should call the GameEngine.init.
 * 
 * @author niquefadiego
 *
 */

public abstract class GameEngine 
{
	/**
	 * Current state of the game.
	 * @see core#GameState
	 */
	static public GameState state;
	
	/**
	 *  Game map
	 *  @see core#GameMap
	 */
	static public GameMap map;
	
	/**
	 * Game settings.
	 * @see core#GameSettings
	 */
	static public GameSettings settings;
	
	/**
	 * Game screen.
	 * @see core.views.GameScreen
	 */
	static public GameScreen screen;

	static private int secsToMagicBox = 5;
	
	/**
	 * Must be called when initializing a new Gameplay.
	 * @see core#Gameplay
	 */
	static public void init ( GameMap gameMap )
	{
		state = new GameState();
		map = gameMap;
		map.init();
		screen = new GameScreen ( );
		settings = new GameSettings ( );
		tank_intelligence.Initializer.init(map);
	}
	
	/**
	 * This method should called by the Gameplay every time the Gameplay.update method is called.
	 * @see Gameplay#update()
	 */
	static public void update ( )
	{
		UnorderedArray<Attack> attacks = state.attacks;
		for ( int i = 0; i < attacks.size(); ++i )
			if ( attacks.get(i).isActive() )
				attacks.get(i).update();
			else { attacks.remove(i); i--; }
		
		UnorderedArray<MagicBox>boxes = state.magicBoxes;
		for ( int i = 0; i < boxes.size(); ++i )
			if ( boxes.get(i).isActive() )
				boxes.get(i).update();
			else { boxes.remove(i); i--; }

		for ( Player p : state.players )
			p.makeMoves();

		if ( settings.magicBoxes )
			if ( Random.randInt((int) (secsToMagicBox*Values.FPS)) == 0 )
				state.magicBoxes.add ( new MagicBox() );
	}
	
	/**
	 * Adds a player to the game.
	 * @param p				Player to be added.
	 */
	static public void addPlayer ( Player p ) {
		p.restoreTank();
		GameEngine.state.players.add(p);
	}
	
	/**
	 * Adds the player who plays in this machine.
	 * @param id			Player id
	 * @param teamId		Team id
	 */
	static public void setHomePlayer ( int id, int teamId ) {
		Player.homePlayer = new Player(id,teamId,null);
		addPlayer(Player.homePlayer);
	}
	
	/**
	 * Adds a player to the game.
	 * @param id			Player id
	 * @param teamId		Team id
	 */
	static public void addPlayer ( int id, int teamId ) {
		addPlayer ( new Player(id,teamId,MatchTankController.class) );
	}

	private GameEngine ( ) { }
}
