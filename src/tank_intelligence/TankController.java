package tank_intelligence;

import core.sprites.Tank;

/**
 * All tank controllers (I.A.) must inherit this class.
 * @author niquefadiego
 */

public abstract class TankController {
	Tank tank;
	abstract public int getMoves ( );
	public TankController ( Tank tank ) { this.tank = tank; }
}
