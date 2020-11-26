/*
 * Head Up Libs
 */
package thump.game.headup;

import static thump.base.Defines.SCREENWIDTH;
import static thump.game.Defines.KEY_BACKSPACE;
import static thump.game.Defines.KEY_ENTER;
import thump.game.Game;
import thump.wad.mapraw.PatchData;

/**
 *
 * @author mark
 */
public class HUlib {
// background and foreground screen numbers
// different from other modules.
public static final int BG = 1;
public static final int FG = 0;

// font stuff
public static final char HU_CHARERASE = KEY_BACKSPACE;

public static final int HU_MAXLINES = 4;
public static final int HU_MAXLINELENGTH = 80;

// boolean : whether the screen is always erased
//#define noterased viewwindowx

//extern boolean	automapactive;	// in AM_map.c

    public void HUlib_init() {}

    public static void HUlib_clearTextLine(hu_textline t) {
        t.len = 0;
        t.l[0] = 0;
        t.needsupdate = 1;
    }

    public static void HUlib_initTextLine ( 
            hu_textline     t,
            int             x,
            int             y,
            PatchData[]     f,
            byte            sc 
    ) {
        t.x = x;
        t.y = y;
        t.f = f;
        t.sc = sc;
        HUlib_clearTextLine(t);
    }

    public static boolean HUlib_addCharToTextLine(
            hu_textline t,
            int ch
    ) {
        if (t.len == HU_MAXLINELENGTH) {
            return false;
        } else {
            t.l[t.len++] = (byte) ch;
            t.l[t.len] = 0;
            t.needsupdate = 4;
            return true;
        }

    }

    public static boolean HUlib_delCharFromTextLine(hu_textline t) {

        if (t.len<=0) {
            return false;
        } else {
            t.len--;
            t.l[t.len] = 0;
            t.needsupdate = 4;
            return true;
        }

    }

    public static void HUlib_drawTextLine( 
            hu_textline	l,
            boolean	drawcursor 
    ) {

        int			i;
        int			w;
        int			x;
        int	c;

        // draw the new stuff
        x = l.x;
        for (i=0;i<l.len;i++) {
            c = Character.toUpperCase(l.l[i]);
            if (c != ' '
                && c >= l.sc
                && c <= '_')
            {
                w = l.f[c - l.sc].width;
                if (x+w > SCREENWIDTH) {
                    break;
                }
                Game.getInstance().renderer.video.drawPatchDirect(
                        x, l.y, FG, l.f[c - l.sc],
                        Game.getInstance().wad 
                );
                // V_DrawPatchDirect(x, l.y, FG, l.f[c - l.sc]);
                x += w;
            }
            else
            {
                x += 4;
                if (x >= SCREENWIDTH) {
                    break;
                }
            }
        }

        // draw the cursor if requested
        if (drawcursor
            && x + l.f['_' - l.sc].width <= SCREENWIDTH)
        {
            Game.getInstance().renderer.video.drawPatchDirect(
                    x, l.y, FG, 
                    l.f['_' - l.sc],
                    Game.getInstance().wad);
            //  V_DrawPatchDirect(x, l.y, FG, l.f['_' - l.sc]);
        }
    }

    private static boolean	lastautomapactive = true;
    
    // sorta called by HU_Erase and just better darn get things straight
    public static void HUlib_eraseTextLine(hu_textline l) {
        int			lh;
        int			y;
        int			yoffset;

        // Only erases when NOT in automap and the screen is reduced,
        // and the text must either need updating or refreshing
        // (because of a recent change back from the automap)

        
        // TODO viewwindowx is in r_draw.c
        
//        //TODO  Implement AM  Automap to hold this value.
//        if (!automapactive &&
//            viewwindowx && l.needsupdate)
//        {
//            lh = SHORT(l.f[0].height) + 1;
//            for (y=l.y,yoffset=y*SCREENWIDTH ; y<l.y+lh ; y++,yoffset+=SCREENWIDTH)
//            {
//                if (y < viewwindowy || y >= viewwindowy + viewheight)
//                    R_VideoErase(yoffset, SCREENWIDTH); // erase entire line
//                else
//                {
//                    R_VideoErase(yoffset, viewwindowx); // erase left border
//                    R_VideoErase(yoffset + viewwindowx + viewwidth, viewwindowx);
//                    // erase right border
//                }
//            }
//        }
//
//        lastautomapactive = automapactive;
//        if (l.needsupdate) l.needsupdate--;

    }
    
    
    public static void HUlib_initSText(
            hu_stext s,
            int x,
            int y,
            int h,
            PatchData[] font,
            byte startchar,
            boolean on) {

        s.h = h;
        s.on = on;
        s.laston = true;
        s.cl = 0;
        for (int i = 0; i < h; i++) {
            HUlib_initTextLine(
                    s.l[i],
                    x, y - i * (font[0].height + 1),
                    font, startchar                 );
        }

    }

    public static void HUlib_addLineToSText(hu_stext s) {

        // add a clear line
        s.cl++;
        if (s.cl == s.h) {
            s.cl = 0;
        }
        HUlib_clearTextLine(s.l[s.cl]);

        // everything needs updating
        for (int i=0 ; i<s.h ; i++) {
            s.l[i].needsupdate = 4;
        }
    }


    public static void HUlib_addMessageToSText( 
        hu_stext	s,
        byte[]	prefix,
        byte[]	msg )
    {
        HUlib_addLineToSText(s);
        int index = 0;
        if (prefix != null /*prefix[index]!= 0 */){
            while (index<prefix.length) {
                HUlib_addCharToTextLine(s.l[s.cl], prefix[index]);
                index++;
            }
        }

//    while (*msg){
//	HUlib_addCharToTextLine(&s.l[s.cl], *(msg++));
//    }
    }


    public static void HUlib_drawSText(hu_stext s)
    {
        int i, idx;
        hu_textline l;

        if (!s.on) {
            return; // if not on, don't draw
        }

        // draw everything
        for (i=0 ; i<s.h ; i++) {
            idx = s.cl - i;
            if (idx < 0) {
                idx += s.h; // handle queue of lines
            }

            l = s.l[idx];

            // need a decision made here on whether to skip the draw
            HUlib_drawTextLine(l, false); // no cursor, please
        }

    }

    public static void HUlib_eraseSText(hu_stext s)
    {

        int i;

        for (i=0 ; i<s.h ; i++)
        {
            if (s.laston && !s.on) {
                s.l[i].needsupdate = 4;
            }
            HUlib_eraseTextLine(s.l[i]);
        }
        s.laston = s.on;

    }

    public static void HUlib_initIText( 
            hu_itext    it,
            int         x,
            int         y,
            PatchData[] font,
            byte        startchar,
            boolean     on ) {
        it.lm = 0; // default left margin is start of text
        it.on = on;
        it.laston = true;
        HUlib_initTextLine(it.l, x, y, font, startchar);
    }

    // The following deletion routines adhere to the left margin restriction
    public static void HUlib_delCharFromIText(hu_itext it)
    {
        if (it.l.len != it.lm) {
            HUlib_delCharFromTextLine(it.l);
        }
    }

    public static void HUlib_eraseLineFromIText(hu_itext it)
    {
        while (it.lm != it.l.len) {
            HUlib_delCharFromTextLine(it.l);
        }
    }

    // Resets left margin as well
    public static void HUlib_resetIText(hu_itext it)
    {
        it.lm = 0;
        HUlib_clearTextLine(it.l);
    }

    public static void HUlib_addPrefixToIText(
            hu_itext    it,
            char[]      str )
    {
    //    while (*str) {
    //	HUlib_addCharToTextLine(it.l, *(str++));
    //    }
    //    it.lm = it.l.len;
    }

    // wrapper function for handling general keyed input.
    // returns true if it ate the key
    public static boolean HUlib_keyInIText(
            hu_itext    it,
            int        ch      )
    {
        if (ch >= ' ' && ch <= '_') {
            HUlib_addCharToTextLine(it.l, ch);
        } else 
            if (ch == KEY_BACKSPACE) {
                HUlib_delCharFromIText(it);
        } else {
            if (ch != KEY_ENTER) {
                return false; // did not eat key
            }
        }

        return true; // ate the key

    }

    public static void HUlib_drawIText(hu_itext it)
    {

        hu_textline l = it.l;

        if (!it.on) {
            return;
        }
        HUlib_drawTextLine(l, true); // draw the line w/ cursor

    }

    public static void HUlib_eraseIText(hu_itext it) {
        if (it.laston && !it.on) {
            it.l.needsupdate = 4;
        }
        HUlib_eraseTextLine(it.l);
        it.laston = it.on;
    }

    
}
