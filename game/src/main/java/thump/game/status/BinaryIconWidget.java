/*
 * Binary Icon Widget
 */
package thump.game.status;

import static thump.base.Defines.logger;
import thump.game.Game;
import static thump.game.status.Status.BG;
import static thump.game.status.Status.FG;
import static thump.game.status.Status.ST_Y;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class BinaryIconWidget {

    public BinaryIconWidget(
            int x,
            int y,
            PatchData i,
            Boolean val,
            Boolean on) {
        this.x = x;
        this.y = y;
        this.oldval = false;
        this.val = val;
        this.on = on;
        this.p = i;
}

    // center-justified location of icon
    int			x;
    int			y;

    // last icon value
    boolean			oldval;

    // pointer to current icon status
    Boolean		val;

    // pointer to boolean
    //  stating whether to update icon
    Boolean		on;  


    PatchData		p;	// icon
    int			data;   // user data    
    
    void STlib_updateBinIcon(
            boolean refresh) {
        int _x;
        int _y;
        int w;
        int h;

        if (on && (oldval != val || refresh)) {
            _x = x - p.leftOffset;
            _y = y - p.topOffset;
            w = p.width;
            h = p.height;

            if (_y - ST_Y < 0) {
                logger.severe("updateBinIcon: y - ST_Y < 0");
            }

            if (val) {
                Game.getInstance().renderer.video.drawPatch(x, y, FG, p);
            } else {
                Game.getInstance().renderer.video.copyRect(_x, _y-ST_Y, BG, w, h, _x, _y, FG);
            }

            oldval = val;
        }

    }
}
