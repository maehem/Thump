/*
 * Percent Widget
 *
 * "child" of number widget, or, more precisely, contains a number widget.
 */
package thump.game.status;

import thump.game.Game;
import static thump.game.status.Status.FG;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class PercentWidget {

    public PercentWidget( int x, int y, PatchData[] pl, Integer num, Boolean on, PatchData percent  ) {
        n = new NumberWidget( x, y, pl, num, on, 3 );
        p = percent;

    }
    
    // number information
    NumberWidget	n;

    // percent sign graphic
    PatchData		p;
    
    public void STlib_updatePercent(
            boolean refresh) 
    {
        if (refresh && n.on) {
            Game.getInstance().renderer.video.drawPatch(n.x, n.y, FG, p);
        }

        n.STlib_updateNum(refresh);
    }
    
    public void STlib_updatePercent(
            Integer val,
            boolean refresh) 
    {
        n.num = val;
        STlib_updatePercent(refresh);
    }
}
