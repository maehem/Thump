/*
 * Pre-rendered screen area.  8-bit values. Before PlayPal is applied.
 */
package thump.render;

import static thump.global.Defines.SCREENHEIGHT;
import static thump.global.Defines.SCREENWIDTH;

/**
 *
 * @author mark
 */
public class Screen {
    public int[] area = new int[SCREENWIDTH*SCREENHEIGHT];

    public void clear() {
        for ( int i=0; i< area.length; i++ ) {
            area[i] = 0;
        }
    }
}
