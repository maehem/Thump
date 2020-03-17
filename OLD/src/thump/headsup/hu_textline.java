/*
 * Text Line Widget
 */
package thump.headsup;

import static thump.headsup.HUlib.HU_MAXLINELENGTH;
import thump.render.Patch;

/**
 *
 * @author mark
 */
public class hu_textline {
    // left-justified position of scrolling text window
    int		x=0;
    int		y=0;
    
    Patch[]	f=null;			// font
    byte	sc = 0;			// start character
    byte	l[] = new byte[HU_MAXLINELENGTH+1];	// line of text
    int		len=0;		      	// current line length

    int	needsupdate=0;	    // whether this line needs to be udpated
      
    
}
