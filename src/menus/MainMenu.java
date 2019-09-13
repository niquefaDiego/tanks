package menus;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import core.Values;

/**
 * This class is used to let the player chose what to do among the options: <br>
 * * Single player game. <br>
 * * Multiplayer game. <br>
 * * Map editor. <br>
 * * Exit. <br>
 * @author niquefadiego
 *
 */

public class MainMenu extends JPanel
{
	public static int SINGLE_PLAYER = 1;
	public static int MULTIPLAYER = 2;
	public static int MAP_EDITOR = 3;
	//public static int HELP = 4;
	public static int EXIT = 5;
	
	private static final long serialVersionUID = -3530638709383620906L;
	
	private int choise = 0;
	
	private JPanel innerPanel;
	private JButton singlePlayer;
	private JButton multiplayer;
	//private JButton help;
	private JButton exit;
	private JButton mapEditor;
	
	public int getChoise ( ) {
		while( choise == 0 ) {
			try { Thread.sleep(50); }
			catch(InterruptedException e ) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		return choise;
	}
	
	private JPanel getSpacer ( ) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(10,10));
		spacer.setAlignmentX(Component.CENTER_ALIGNMENT);
		return spacer;
	}

	public MainMenu ( )
	{
		super();
		this.setPreferredSize ( Values.PANEL_SIZE );
		this.setLayout(new GridBagLayout());
		
		innerPanel = new JPanel();
		innerPanel.setLayout( new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		
		singlePlayer = new JButton("Single Player" );
		singlePlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		singlePlayer.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choise = MainMenu.SINGLE_PLAYER;
			}
		});
		innerPanel.add ( singlePlayer );
		
		innerPanel.add(getSpacer());
		
		multiplayer = new JButton("Multiplayer");
		multiplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		multiplayer.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choise = MainMenu.MULTIPLAYER;
			}
		});
		multiplayer.setEnabled(false);/*TODO*/
		innerPanel.add ( multiplayer );
		
		innerPanel.add(getSpacer());
		
		mapEditor = new JButton("Map Editor");
		mapEditor.setAlignmentX(Component.CENTER_ALIGNMENT);
		mapEditor.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choise = MainMenu.MAP_EDITOR;
			}
		});
		innerPanel.add ( mapEditor );
		
		/*innerPanel.add(getSpacer());
		
		help = new JButton("Help");
		help.setAlignmentX(Component.CENTER_ALIGNMENT);
		help.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choise = MainMenu.HELP;
			}
		});
		innerPanel.add ( help ); */
		
		innerPanel.add(getSpacer());
		
		exit = new JButton("Exit");
		exit.setAlignmentX(Component.CENTER_ALIGNMENT);
		exit.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choise = MainMenu.EXIT;
			}
		});
		innerPanel.add ( exit );
		
		this.add(innerPanel);
	}
}
