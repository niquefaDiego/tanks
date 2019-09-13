package core.interfaces;

import core.GameMap;

public interface Gameplay 
{
	/** @return 	true iff the game allows to be paused. */
	public boolean canPause();
	
	/** @return	 	id of the winner team, or -1 if the game has not ended. */
	public int winnerTeam();
	
	public void init(GameMap map);
	public void update();
}
