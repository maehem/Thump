/*
 * Multiple Icon Widget
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
public class MultipleIconWidget {
    
    public MultipleIconWidget(
            int x,
            int y,
            PatchData[] il,
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
    PatchData[]		p;
    
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
                    logger.severe("updateMultIcon: y - ST_Y < 0");
                }

                Game.getInstance().renderer.video.copyRect(_x, _y-ST_Y, BG, w, h, _x, _y, FG);
            }
            Game.getInstance().renderer.video.drawPatch(x, y, FG, p[inum]);
            oldinum = inum;
        }
    }
}
