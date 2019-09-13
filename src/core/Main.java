package core;

import java.awt.Component;

import javax.swing.JFrame;

import core.interfaces.Gameplay;

import single_player.mapeditor.MapEditor;

import menus.MainMenu;
import menus.SinglePlayerMenu;

public class Main
{
	private static JFrame frame;
	private static MainMenu menu;
	
	private static void setFrameContent ( Component o ) {
		frame.getContentPane().removeAll();
		frame.add(o);
		frame.pack();
		frame.revalidate();
		frame.repaint();
	}
	
	private static void gotoMainMenu ( ) {
		frame.getContentPane().removeAll();
		menu = new MainMenu();
		setFrameContent(menu);
	}
	
	private static void gotoSinglePlayerGameMenu ( )
	{
		SinglePlayerMenu spmenu = new SinglePlayerMenu();
		setFrameContent(spmenu);
		Gameplay game = spmenu.getGameplay();
		if ( game == null ) return;
		gotoSinglePlayerGame(game);
	}
	
	private static void gotoSinglePlayerGame ( Gameplay game )
	{
		GameWrapper wrapper = GameWrapper.getNewInstance(game, frame);
		setFrameContent(wrapper.getContainerPanel());
		while ( wrapper.isGameRunning() ) {
			try { Thread.sleep(50); }
			catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		wrapper.dispose();
	}
	
	static public void gotoMapEditor ( ) {
		MapEditor mapEditor = new MapEditor();
		mapEditor.init(GameMap.getEmptyMap(20, 20)); /* TODO Make it variable */
		GameWrapper wrapper = GameWrapper.getNewInstance ( mapEditor, frame );
		wrapper.startRunning();
		setFrameContent(wrapper.getContainerPanel());
		while ( wrapper.isGameRunning() ) {
			try { Thread.sleep(50); }
			catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		wrapper.dispose();
	}
	
	public static void main ( String[] args )
	{
		System.out.println("Run#10");
		frame = new JFrame ( );
		frame.setResizable ( false );
		frame.setTitle ( "Tanks" );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menu = new MainMenu ( );
		frame.add(menu);
		frame.pack();
		frame.setVisible(true);
		
		while ( true )
		{
			int choise = menu.getChoise();
			if ( choise == MainMenu.SINGLE_PLAYER ) {
				gotoSinglePlayerGameMenu();
				gotoMainMenu();
			}
			else if ( choise == MainMenu.MAP_EDITOR ) {
				gotoMapEditor();
				gotoMainMenu();
			}
			else if ( choise == MainMenu.EXIT ) {
				break;
			}
		}
		
		frame.dispose();
	}
}