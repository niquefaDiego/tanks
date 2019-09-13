package core;

import core.GameEngine;
import core.sprites.Attack;
import core.sprites.Laser;
import core.sprites.Tank;
import core.sprites.FastBullet;
import core.sprites.Bullet;

import tank_intelligence.TankController;
import utils.Random;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Player
{	
	private static int N_ATTACKS = 3;
	
	/**
	 * Player controlled by the user of this machine.
	 */
	static public Player homePlayer;
	
	private int freezeUntil = -1;
	public void freezeFor ( float secs ) {
		freezeUntil = GameWrapper.currentFrame();
		freezeUntil += secs*Values.FPS;
	}

	private int id; //unique number that identify this player
	private int teamId;
	private Tank tank=null;
	private int deads, kills, lifes;
	private int chosenAttack=0;
	
	private Constructor<?> controllerConstructor = null;
	private TankController controller = null;
	
	/** @return			Number of lives left. Zero is dead and minus one is infinite. */
	public int getLifes() { return lifes; }
	
	/** @return			Number of players killed. */
	public int getKills() { return kills; }
	
	/** @return 		Number of times this player has died. */
	public int getDeads() { return deads; }
	
	private static String attackName[] = { "Bullet", "Fast bullet", "Laser" };
	
	private long lastUsed = (long) (-10*Values.FPS);
	private int ammo[] = new int[N_ATTACKS];
	private static int bonusRange[] = { 0, 21, 6 };
	private static int bonusMin[] = { 0, 20, 2 };
	
	/** Equivalent to Player.getLifes() != 0 */
	public boolean alive() { return this.lifes != 0; }
	
	/** Sets the number of lifes to this player to the given one. */
	public void setLifes(int lifes) { this.lifes=lifes; }
	
	private static int initLifes = -1;
	
	/** Sets the default initial number of lifes. */
	public static void setInitLifes(int nLifes) { Player.initLifes = nLifes; }
	
	/**
	 * This method is called by MagicBox.update() when the MagicBox is found by this
	 * Player's tank.
	 */
	public void foundMagicBox ( )
	{
		if ( Random.randInt(4) != 0 && id == Player.homePlayer.id) {
			GameEngine.screen.rightPanel.addMessage("SuperRafaFreeze!");
			for ( Player p : GameEngine.state.players )
				if ( p.getId() != this.getId() )
					p.freezeFor ( 3 );
			return;
		}
		int i = Random.randInt(N_ATTACKS);
		String msg = null;
		if ( i == 0 ) {
			tank.changeHealth(Tank.INITIAL_HEALTH);
			if ( id == Player.homePlayer.id )
				msg = "Health to 100%";
		} else {
			int toAdd = bonusMin[i] + Random.randInt(bonusRange[i]);
			ammo[i] += toAdd;
			if ( id == Player.homePlayer.id )
				msg = attackName[i]+"(x"+toAdd+")";
		}
		if ( msg != null )
			GameEngine.screen.rightPanel.addMessage(msg);
	}
	
	private static Constructor<?> attackConstructor[] = new Constructor[N_ATTACKS];
	
	/** Change the attack to the next one. */
	public void setNextAttack() { chosenAttack = (chosenAttack+1)%N_ATTACKS; }
	
	/** Change the attack to the previous one. */
	public void setPrevAttack() { chosenAttack = (chosenAttack+N_ATTACKS-1)%N_ATTACKS; }
	
	/** Set the attack to the i-th one. */
	public void setAttack( int i ) { if ( 0 <= i && i < N_ATTACKS ) chosenAttack = i; }
	
	/** Get the name of the currently chosen attack. */
	public String getAttackName ( ) { return attackName[chosenAttack]; }
	
	/** Get the number of ammo left of the chosen attack, or "inf" if ammo is infinite. */
	public String getAttackAmmo ( ) {
		if ( ammo[chosenAttack] == -1 ) return "inf";
		return ""+ammo[chosenAttack];
	}
	
	static
	{
		try {
			attackConstructor[0] = Bullet.class.getConstructor(Player.class);
			attackConstructor[1] = FastBullet.class.getConstructor(Player.class);
			attackConstructor[2] = Laser.class.getConstructor(Player.class);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/** @return Player id */ 
	public int getId() { return this.id; }
	
	/** @return Player team id */
	public int getTeamId() { return this.teamId; }
	
	/** @return Player tank id */
	public Tank getTank() { return this.tank; }
	
	/** Restores the tank. */
	public void restoreTank ( ) {
		this.tank = new Tank(this); 
		controller = null;
		if ( this.controllerConstructor != null ) {
			try {
				controller = (TankController)controllerConstructor.newInstance(this.tank);
			} catch (Exception e ) { e.printStackTrace(); }
		}
	}
	
	/** This function is called when this player kills another one.  */
	public void kills ( Player killed) {
		this.kills++;
		killed.deads++;
		if ( killed.lifes != -1 ) killed.lifes--;
		killed.tank.dispose();
		if ( killed.lifes > 0 || killed.lifes == -1 ) killed.restoreTank();
		else { /* TODO */ }
	}
	
	/** This function tries to attack with the chosen attack. */
	public synchronized void attack ( )
	{
		if ( ammo[chosenAttack] == 0 ) return;
	
		try {
			Attack a = (Attack) attackConstructor[chosenAttack].newInstance(this);
			if ( GameWrapper.currentFrame()-lastUsed < a.getReloadTime() ) return;
			if ( ammo[chosenAttack] != -1 ) ammo[chosenAttack]--;
			if ( ammo[chosenAttack] == 0 ) chosenAttack = 0;
			lastUsed = GameWrapper.currentFrame();
			GameEngine.state.attacks.add(a);
			if ( a instanceof Laser ) lastUsed += Laser.DURATION;
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * This function is called by the GameEngine.update method and moves the tank,
	 * either if it is moved by user input or by the tank IA.
	 */
	public void makeMoves ( ) {
		if ( !this.alive() ) return;
		if ( this.controller != null )
			this.processCommands ( this.controller.getMoves() );
		else if ( this.id == Player.homePlayer.id )
			this.processCommands ( GameWrapper.getCmds() );
	}
	
	private void processCommands ( int cmds )
	{
		if ( freezeUntil > 0 ) {
			if ( freezeUntil > GameWrapper.currentFrame() )
				return;
			freezeUntil = -1;
		}
		
		if ( !this.alive() ) return;
		if ( (cmds&Values.MOVE_FORWARD) != 0 ) tank.moveForward();
		if ( (cmds&Values.MOVE_BACKWARD) != 0 ) tank.moveBackward();
		if ( (cmds&Values.ROTATE_CW) != 0 ) tank.rotateCW();
		if ( (cmds&Values.ROTATE_CCW) != 0 ) tank.rotateCCW();
		if ( (cmds&Values.ROTATE_GUN_CW) != 0 ) tank.rotateGunCW();
		if ( (cmds&Values.ROTATE_GUN_CCW) != 0 ) tank.rotateGunCCW();
		if ( (cmds&Values.SHOOT) != 0 ) this.attack();
	}
	
	/**
	 * Instantiate a new player.
	 * @param controllerClass		Tank I.A. or null if the player is human. 
	 */
	public Player ( int id, int teamId, Class<?> controllerClass )
	{
		if ( controllerClass != null ) {
			try { this.controllerConstructor = controllerClass.getConstructor(Tank.class); }
			catch (Exception e ) { e.printStackTrace(); }
		}
		
		this.id = id;
		this.teamId = teamId;
		chosenAttack=0;
		deads = 0;
		kills = 0;
		lifes = Player.initLifes;
		ammo[0] = -1;
		ammo[1] = 0;
		ammo[2] = 10;
	}

	public void superRafaPower()
	{
		GameEngine.screen.rightPanel.addMessage("SuperRafaBomb!");
		Player closestFriend = null;
		float dist = (float) 1e5;
		
		for ( Player p : GameEngine.state.players )
			if ( this.getTeamId() == p.getTeamId() ) {
				float dist2 = p.tank.getPosition().subt(tank.getPosition()).norm();;
				if ( dist2 < dist ) {
					closestFriend = p;
					dist = dist2;
				}
			}
	
		if ( closestFriend == null ) {
			return;
		}
		
		ArrayList<Player> killed = new ArrayList<Player>();
		for ( Player p : GameEngine.state.players ) {
			float d = p.tank.getPosition().subt(closestFriend.tank.getPosition()).norm();
			if ( d < 4.0*Values.TILE_SIZE && p.getId() != this.getId() )
				killed.add ( p );
		}
		
		for ( Player p : killed )
			this.kills(p);
	}
}
