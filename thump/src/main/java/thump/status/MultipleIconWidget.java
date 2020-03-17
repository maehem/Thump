/*
 * Multiple Icon Widget
 */
package thump.status;

import thump.game.Game;
import thump.global.SystemInterface;
import thump.render.Patch;
import static thump.status.Status.BG;
import static thump.status.Status.FG;
import static thump.status.Status.ST_Y;

/**
 *
 * @author mark
 */
public class MultipleIconWidget {
    
    public MultipleIconWidget(
            int x,
            int y,
            Patch[] il,
            Integer inum,
            Boolean on) {
        this.x = x;
        this.y = y;
        this.oldinum = -1;
        this.inum = inum;
        this.on = on;
        this.p = il;
    }

     // center-justified location of icons
    int			x;
    int			y;

    // last icon number
    int			oldinum;

    // pointer to current icon
    Integer		inum;

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
            && (inum!=-1)  )
        {
            if (oldinum != -1) {
                _x = x - p[oldinum].leftOffset;
                _y = y - p[oldinum].topOffset;
                w = p[oldinum].width;
                h = p[oldinum].height;

                if (_y - ST_Y < 0) {
                    SystemInterface.I_Error("updateMultIcon: y - ST_Y < 0");
                }

                Game.getInstance().video.copyRect(_x, _y-ST_Y, BG, w, h, _x, _y, FG);
            }
            Game.getInstance().video.drawPatch(x, y, FG, p[inum]);
            oldinum = inum;
        }
    }
}
