/*
 * Text Line Widget
 */
package thump.game.headup;

import static thump.game.headup.HUlib.HU_MAXLINELENGTH;
import thump.wad.mapraw.PatchData;


/**
 *
 * @author mark
 */
public class hu_textline {
    // left-justified position of scrolling text window
    int		x=0;
    int		y=0;
    
    PatchData[]	f=null;			// font
    byte	sc = 0;			// start character
    byte	l[] = new byte[HU_MAXLINELENGTH+1];	// line of text
    int		len=0;		      	// current line length

    int	needsupdate=0;	    // whether this line needs to be udpated
      
}
