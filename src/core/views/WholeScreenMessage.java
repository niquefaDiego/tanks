package core.views;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is a panel that takes the whole screen with a message.
 * @author niquefadiego
 */

public class WholeScreenMessage extends JPanel
{
	private static final long serialVersionUID = 5580036053773189384L;

	/** Instantiate a new WholeScreenMessage with the given message */
	WholeScreenMessage ( String message ) {
		this.add ( new JLabel(message) );
	}
}
