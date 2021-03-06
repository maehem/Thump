/*
 * st_number_t
 */
package thump.status;

import thump.game.Game;
import thump.global.SystemInterface;
import thump.render.Patch;
import thump.render.Video;
import static thump.status.Status.BG;
import static thump.status.Status.FG;
import static thump.status.Status.ST_Y;

/**
 *
 * @author mark
 */
public class NumberWidget {

    private final Patch sttminus;

    public NumberWidget(int x, int y, Patch[] p, Integer num, Boolean on, int width) {
        this.x = x;
        this.y = y;
        this.p = p;
        this.on = on;
        this.num = num;
        this.width = width;
        
        sttminus = Game.getInstance().wad.getPatchByName("STTMINUS");
}
    
    // upper right-hand corner
    //  of the number (right-justified)
    int		x;
    int		y;

    // max # of digits in number
    int width;    

    // last number value
    int	oldnum;
    
    // pointer to current value
    Integer  num;

    // pointer to boolean stating
    //  whether to update number
    Boolean	on;

    // list of patches for 0-9
    Patch[]	p = new Patch[10];

    // user data
    Object data;
    
    
    // 
    // A fairly efficient way to draw a number
    //  based on differences from the old number.
    // Note: worth the trouble?   NOPE.
    //
    void STlib_drawNum(
            /*NumberWidget n,*/
            boolean refresh) {

        int		numdigits = width;
        //int		num = *n.num;

        int		w = p[0].width;
        int		h = p[0].height;
        int		xx = x;

        boolean		neg;

        //n.oldnum = *n.num;
        oldnum = num;

        neg = num < 0;

        if (neg) {
            if (numdigits == 2 && num < -9) {
                num = -9;
            } else if (numdigits == 3 && num < -99) {
                num = -99;
            }

            num = -(int)num;
        }

        // clear the area
        xx = x - numdigits*w;

        if (y - ST_Y < 0) {
            SystemInterface.I_Error("drawNum: y - ST_Y < 0");
        }

        Video video = Game.getInstance().video;
        video.copyRect(xx, y - ST_Y, BG, w*numdigits, h, xx, y, FG);

        // if non-number, do not draw it
        if (num == 1994) {
            return;
        }

        xx = x;

        // in the special case of 0, you draw 0
        if (num==0) {
            video.drawPatch(xx - w, y, FG, p[ 0 ]);
        }

        // draw the new number
        while (num>0 && (numdigits--)>0) {
            xx -= w;
            video.drawPatch(xx, y, FG, p[ num % 10 ]);
            num /= 10;
        }

        // draw a minus sign if necessary
        if (neg) {
            video.drawPatch(xx - 8, y, FG, sttminus);
        }
    }

    //
    void STlib_updateNum( boolean refresh ) {
        if (on) {
            STlib_drawNum(refresh);
        }
    }
}
