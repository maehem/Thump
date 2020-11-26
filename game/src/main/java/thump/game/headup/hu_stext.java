/*
 * Scrolling Text Window Widget
 * (parent of Scrolling Text and Input Text widgets)
 */
package thump.game.headup;

import static thump.game.headup.HUlib.HU_MAXLINES;


/**
 *
 * @author mark
 */
public class hu_stext {
    // text lines to draw
    hu_textline l[] = new hu_textline[HU_MAXLINES];
    
    int         h=0;      // height in lines
    int         cl=0;     // current line number
    Boolean     on=false;     // boolean ref. stating whether to update window
    boolean     laston=false; // last value of *->on. 

    public hu_stext() {
        for( int i=0; i<HU_MAXLINES; i++ ) {
            l[i] = new hu_textline();
        }
    }
    
    
}
