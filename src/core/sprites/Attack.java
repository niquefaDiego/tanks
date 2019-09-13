package core.sprites;

import core.Player;
import core.interfaces.Sprite;

/**
 * All Tank attacks must inherit this class.
 * 
 * @author niquefadiego
 */

public abstract class Attack implements Sprite
{
	/** Player who threw this attack. */
	protected Player owner;
	
	/** Get attack damage. */
	abstract public float getPower ( );
	
	/** True if the attack is still active. */
	abstract public boolean isActive ( );
	
	/** Minimum time to wait between two consecutive shoots. */
	abstract public int getReloadTime();
	
	/** This methed is called by GameEngine.update method. */
	abstract public void update ( );
	
	/** Create a new instance when the attack is thrown. */
	Attack ( Player owner ) { this.owner=owner; }
	
	/** @return		Player who threw this. */
	public Player getOwner() { return this.owner; }
}