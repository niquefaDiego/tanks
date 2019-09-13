package menus;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import single_player.SinglePlayerGameplay;
import single_player.capture_the_flag.CaptureTheFlagGamePlay;

import core.GameMap;
import core.GameWrapper;
import core.Player;
import core.Values;
import core.interfaces.Gameplay;

/**
 * This class is used to let the player configure the game.
 * The method getGameplay() will return the Gameplay the player configured. This method
 * will execute until the player clicks the done button and the input is legit.
 *  
 * @author niquefadiego
 *
 */

public class SinglePlayerMenu extends JPanel
{
	private static final long serialVersionUID = 7942414629150061820L;
	
	private final String gameTypes[] = { "Dead Match", "Team Dead Match", "Capture the flag" };
	private final int NONE = -1;
	private final int DEAD_MATCH = 0;
	private final int TEAM_DEAD_MATCH = 1;
	private final int CAPTURE_THE_FLAG = 2;
	
	private boolean finished = false;
	
	private int gameType = TEAM_DEAD_MATCH;
	
	/**
	 * This will wait until the player finished configuring the game and then
	 * return the Gameplay.
	 */
	public Gameplay getGameplay ( )
	{
		while ( true )
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			if ( !finished ) continue;
			
			if ( gameType == NONE ) return null;
			
			int nEnemies = Integer.parseInt ( (String) enemiesCB.getSelectedItem() );
			int nAllies = Integer.parseInt ( (String) alliesCB.getSelectedItem() );
			int nLifes = -1;
			if ( lifesCB.isEnabled() )
				nLifes = Integer.parseInt ( (String) lifesCB.getSelectedItem() );
			
			GameMap map = null;
			if ( mapFile != null ) {
				try { map = (GameMap)utils.FileIO.readFromFile(mapFile); }
				catch (Exception e) {
					/* TODO Pop message */
					finished = false;
					e.printStackTrace();
					continue;
				}
			} else
				map = GameMap.getRandomMap(20, 20);
			
			Player.setInitLifes ( nLifes );
			
			if ( gameType == DEAD_MATCH ) {
				SinglePlayerGameplay game = new SinglePlayerGameplay();
				game.init(map);
				
				int currentTeam = 1;
				while ( nEnemies-- > 0 )
					game.addPlayerOnTeam(currentTeam++);
				
				return game;
			}
			else if ( gameType == TEAM_DEAD_MATCH )
			{
				SinglePlayerGameplay game = new SinglePlayerGameplay();
				game.init(map);
				
				while ( nEnemies-- > 0 ) game.addPlayerOnTeam(1);
				while ( nAllies-- > 0 ) game.addPlayerOnTeam(0);
				
				return game;
			}
			else if ( gameType == CAPTURE_THE_FLAG )
			{
				CaptureTheFlagGamePlay game = new CaptureTheFlagGamePlay();
				game.init(map);
	
				while ( nEnemies-- > 0 ) game.addPlayerOnTeam(1);
				while ( nAllies-- > 0 ) game.addPlayerOnTeam(0);
				
				return game;
			}
		}
	}
	
	private	 boolean enableValues[][] = {
			//lifes, allies
			{ true, false  }, //dead match
			{ true, true  }, //team dead match
			{ false,true }  //capture the flag
	};
	
	private void gameTypeChanged ( ) {
		System.out.println("Game type is now: " + gameTypes[gameType] );
		
		lifesCB.setEnabled(enableValues[gameType][0]);
		alliesCB.setEnabled(enableValues[gameType][1]);
	}
	
	static private String[] getIntArray ( int beg, int end ) {
		String r[] = new String[end-beg];
		for ( int i = beg; i < end; ++i )
			r[i-beg] = ""+i;
		return r;
	}
	
	private JPanel getSpacer ( ) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(10,10));
		spacer.setAlignmentX(Component.CENTER_ALIGNMENT);
		return spacer;
	}

	private JComboBox<String> gameTypeCB = new JComboBox<String> ( gameTypes );

	private JLabel alliesL = new JLabel ( "Allies" );
	private JLabel enemiesL = new JLabel ( "Enemies" );
	private JLabel lifesL = new JLabel ( "Lifes" );
	private JLabel mapL = new JLabel ( "Map" );
	private JComboBox<String> alliesCB = new JComboBox<String> ( getIntArray(0,11) );
	private JComboBox<String> enemiesCB = new JComboBox<String> ( getIntArray(1,11) );
	private JComboBox<String> lifesCB = new JComboBox<String> ( new String[] { "1", "2", "3", "5", "10", "15", "20", "50" } );
	private JComboBox<String> mapCB = new JComboBox<String> ( new String[] { "Random", "Load" } );
	private JButton browseB = new JButton ( "Browse" );
	private JButton doneB = new JButton ( "Done" );
	private JButton backB = new JButton ( "Back to Main Menu" );
	private File mapFile = null;
	
	/**
	 * Instantiate a new menu.
	 */
	public SinglePlayerMenu ( )
	{
		super();
		this.setPreferredSize ( Values.PANEL_SIZE );
		
		gameTypeCB.setSelectedIndex(gameType);
		
		gameTypeCB.addActionListener( new ActionListener ( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>) e.getSource();
				gameType = source.getSelectedIndex();
				gameTypeChanged();
			}
		} );
		
		mapCB.setSelectedIndex(0);
		browseB.setEnabled(false);
		
		mapCB.addActionListener( new ActionListener ( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> source = (JComboBox<String>) e.getSource();
				int index = source.getSelectedIndex();
				System.out.println("index="+index);
				if ( index == 0 ) {
					mapFile = null;
					browseB.setEnabled(false);
				}
				else {
					browseB.setEnabled(true);
				}
			}
		} );
		
		doneB.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				finished = true;
			}
		});
		
		backB.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameType = NONE;
				finished = true;
			}
		});
		
		browseB.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(GameWrapper.getInstance()) == JFileChooser.APPROVE_OPTION) {
					mapFile = fileChooser.getSelectedFile();
				}
			}
		});
		
		JPanel alliesPanel = new JPanel();
		alliesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		alliesPanel.add(alliesL);
		alliesPanel.add(alliesCB);
		
		JPanel enemiesPanel = new JPanel();
		enemiesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		enemiesPanel.add(enemiesL);
		enemiesPanel.add(enemiesCB);
		
		JPanel lifesPanel = new JPanel();
		lifesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lifesPanel.add(lifesL);
		lifesPanel.add(lifesCB);
		
		JPanel mapPanel = new JPanel();
		mapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mapPanel.add(mapL);
		mapPanel.add(mapCB);
		mapPanel.add(browseB);
		
		doneB.setAlignmentX(Component.CENTER_ALIGNMENT);
		backB.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout( new BoxLayout(innerPanel,BoxLayout.Y_AXIS));
		
		innerPanel.add ( gameTypeCB );
		innerPanel.add ( alliesPanel );
		innerPanel.add ( enemiesPanel );
		innerPanel.add ( lifesPanel );
		innerPanel.add ( mapPanel );
		innerPanel.add ( doneB );
		innerPanel.add ( getSpacer() );
		innerPanel.add ( getSpacer() );
		innerPanel.add ( backB );
		
		this.setLayout(new GridBagLayout());
		this.add(innerPanel);
	}
}
