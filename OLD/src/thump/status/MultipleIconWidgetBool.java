/*
 * Multiple Icon Widget
 */
package thump.status;

import thump.game.Game;
import thump.global.SystemInterface;
import static thump.headsup.HUlib.BG;
import static thump.headsup.HUlib.FG;
import thump.render.Patch;
import static thump.status.Status.ST_Y;

/**
 *
 * @author mark
 */
public class MultipleIconWidgetBool {
    
    public MultipleIconWidgetBool(
            int x,
            int y,
            Patch[] il,
            Boolean inum,
            Boolean on) {
        this.x = x;
        this.y = y;
        this.oldinum = !inum;
        this.inum = inum;
        this.on = on;
        this.p = il;
    }

     // center-justified location of icons
    int			x;
    int			y;

    // last icon number
    boolean		oldinum;

    // pointer to current icon
    Boolean		inum;

    // pointer to boolean stating
    //  whether to update icon
    Boolean		on;

    // list of icons
    Patch[]		p;
    
    // user data
    int			data;    
    
    
    void STlib_updateMultIcon(boolean refresh) {
        int w;
        int h;
        int _x;
        int _y;

        
        
        if (on
            && (oldinum != inum || refresh)
              )
        {
            int ox = oldinum?1:0;
//            if (oldinum != -1) {
                _x = x - p[ox].leftOffset;
                _y = y - p[ox].topOffset;
                w = p[ox].width;
                h = p[ox].height;

                if (_y - ST_Y < 0) {
                    SystemInterface.I_Error("updateMultIcon: y - ST_Y < 0");
                }

                Game.getInstance().video.copyRect(_x, _y-ST_Y, BG, w, h, _x, _y, FG);
//            }
            Game.getInstance().video.drawPatch(x, y, FG, p[inum?1:0]);
            oldinum = inum;
        }
    }
}
