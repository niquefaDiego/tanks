package single_player.mapeditor;

import java.io.File;

import javax.swing.JFileChooser;

import processing.core.PApplet;
import geom.Vector;
import core.GameEngine;
import core.GameMap;
import core.GameWrapper;
import core.Images;
import core.Player;
import core.Values;
import core.interfaces.Gameplay;

/**
 * Map editor gameplay allows to create, save and edit game maps.
 * 
 * @author niquefadiego
 */

public class MapEditor implements Gameplay
{
	private boolean gameOver = false;
	
	@Override
	public boolean canPause() { return false; }

	@Override
	public int winnerTeam() { return (gameOver?0:-1); }

	@Override
	public void init(GameMap map) {
		Values.changeFPS ( 100 );
		GameEngine.init(map);
		GameEngine.setHomePlayer(0,0);
		GameEngine.screen.rightPanel = new MapEditorRightPanel();
		Player.homePlayer.setLifes(0);
		GameEngine.settings.magicBoxes = false;
	}

	@Override
	public void update() {
		GameWrapper wrapper = GameWrapper.getInstance();
		if ( wrapper.mousePressed )
			clicked(wrapper.mouseButton);
		GameEngine.update();
	}
	
	long lastSaved = 0;
	public synchronized void saveMap ( ) 
	{
		long time = System.currentTimeMillis();
		if ( lastSaved + 1000 < time ) lastSaved = time;
		else return;
		
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(GameWrapper.getInstance()) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			GameMap map = GameEngine.map;
			map.init();
			utils.FileIO.writeToFile(map, file);
		}
	}
	
	long lastOpened = 0;
	private synchronized void openMap ( )
	{
		long time = System.currentTimeMillis();
		if ( lastOpened + 1000 < time ) lastOpened = time;
		else return;
		
		System.out.println("Open map!!");
		JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(GameWrapper.getInstance()) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try { GameEngine.map = (GameMap)utils.FileIO.readFromFile(file); }
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
			GameEngine.map.init();
		}
	}

	public void clicked ( int button )
	{
		GameWrapper wrapper = GameWrapper.getInstance();
		Vector p = new Vector ( wrapper.mouseX, wrapper.mouseY );
		if ( p.x < Values.VISIBLE_SIZE && p.y < Values.VISIBLE_SIZE )
		{
			p = p.add(GameEngine.screen.getTopLeft());
			int i = (int)(p.x/Values.TILE_SIZE);
			int j = (int)(p.y/Values.TILE_SIZE);
			if ( i < 0 || j < 0 ) return;
			if ( i >= GameEngine.map.getTilesWidth() ) return;
			if ( j >= GameEngine.map.getTilesHeight() ) return;
			
			if ( button == PApplet.RIGHT )
				GameEngine.map.setFloorCell(i, j, Images.TILE );
			else GameEngine.map.setFloorCell(i, j, Images.WALL );
		} else if ( button == PApplet.LEFT ) {
			MapEditorRightPanel rpanel = (MapEditorRightPanel) GameEngine.screen.rightPanel;
			if ( rpanel.clickedBack(p.x, p.y) ) {
				System.out.println("CLICKED BACK!!!!!!");
				gameOver = true;
			}
			else if ( rpanel.clickedOpen(p.x, p.y) ) openMap();
			else if ( rpanel.clickedSave(p.x, p.y) ) saveMap();
		}
	}
}
