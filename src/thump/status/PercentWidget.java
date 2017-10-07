/*
 * Percent Widget
 *
 * "child" of number widget, or, more precisely, contains a number widget.
 */
package thump.status;

import thump.game.Game;
import thump.render.Patch;
import static thump.status.Status.FG;

/**
 *
 * @author mark
 */
public class PercentWidget {

    public PercentWidget( int x, int y, Patch[] pl, Integer num, Boolean on, Patch percent  ) {
        n = new NumberWidget( x, y, pl, num, on, 3 );
        p = percent;

    }
    
    // number information
    NumberWidget	n;

    // percent sign graphic
    Patch		p;
    
    public void STlib_updatePercent(
            boolean refresh) 
    {
        if (refresh && n.on) {
            Game.getInstance().video.drawPatch(n.x, n.y, FG, p);
        }

        n.STlib_updateNum(refresh);
    }
}
