package core;

import java.util.ArrayList;

import utils.UnorderedArray;

import core.sprites.Attack;
import core.sprites.MagicBox;

/**
 * This class defines some state of the players, attacks and magic boxes in the game.
 * @author niquefadiego
 */

public class GameState
{
	/**
	 * Players in the game
	 */
	public ArrayList<Player> players;
	
	/**
	 * Attacks on screen.
	 */
	public UnorderedArray<Attack> attacks;
	
	/**
	 * Magic boxes on screen.
	 */
	public UnorderedArray<MagicBox> magicBoxes;
	
	/**
	 * Creates an empty GameState.  
	 */
	public GameState ( ) {
		players = new ArrayList<Player>();
		attacks = new UnorderedArray<Attack>();
		magicBoxes = new UnorderedArray<MagicBox>();
	}
}
