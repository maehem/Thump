/*
 * Screen Wipe Package
 */
package thump.game.play;

import thump.base.Random;
import thump.game.Game;
import thump.render.Screen;
import thump.system.VideoInterface;



/**
 *
 * @author mark
 */
public class Wipe {

    private static Wipe instance = null;

    private Wipe() {
    }

    public static Wipe getInstance() {
        if (instance == null) {
            instance = new Wipe();
        }

        return instance;
    }

    public static enum Wipes {
        wipe_ColorXForm, // simple gradual pixel change for 8-bit only
        wipe_Melt,          // weird screen melt

    };

    // when zero, stop the wipe
    boolean go = false;

    Screen wipe_scr_start; // = new Screen(); //new BufferedImage(SCREENWIDTH, SCREENHEIGHT, BufferedImage.TYPE_INT_ARGB);
    Screen wipe_scr_end; // = new Screen(); //new BufferedImage(SCREENWIDTH, SCREENHEIGHT, BufferedImage.TYPE_INT_ARGB);
    Screen wipe_scr; // = new Screen(); //new BufferedImage(SCREENWIDTH, SCREENHEIGHT, BufferedImage.TYPE_INT_ARGB);


    void wipe_shittyColMajorXform ( 
            Screen	scrn,
      int		width,
      int		height )
    {
//        int		x;

        //dest =  Z_Malloc(width*height*2, PU_STATIC, 0);
        int[] dest = new int[width*height*2];

        for( int yy=0;yy<height;yy++) {
            for(int x=0;x<width;x++) {
                dest[x*height+yy] = scrn.area[yy*width+x];
            }
        }

        //memcpy(array, dest, width*height*2);
        //scrn.area = Arrays.copyOf(dest, dest.length);
        System.arraycopy(dest, 0,scrn.area , 0, width*height*2);

        //Z_Free(dest);

    }
    
    private boolean wipe_initColorXForm(
            int width,
            int height,
            int ticks) {
        //memcpy(wipe_scr, wipe_scr_start, width*height);
        //tickCount = 0;
        copyScreen(wipe_scr_start, wipe_scr);
        return false;
    }

    //private int tickCount = 0;
    private boolean wipe_doColorXForm(
            int width,
            int height,
            int ticks) {

        boolean changed = false;
        Screen w;
        Screen e;
        int newval;

        //changed = false;
        w = wipe_scr;
        int wi = 0;
        e = wipe_scr_end;
        int ei = 0;

        while (wi != width * height) {
            if (w.area[wi] != e.area[ei]) {
                if (w.area[wi] > e.area[ei]) {
                    newval = w.area[wi] - ticks;
                    if (newval < e.area[ei]) {
                        w.area[wi] = e.area[ei];
                    } else {
                        w.area[wi] = newval;
                    }
                    changed = true;
                } else {
                    newval = w.area[wi] + ticks;
                    if (newval > e.area[ei]) {
                        w.area[wi] = e.area[ei];
                    } else {
                        w.area[wi] = newval;
                    }
                    changed = true;
                }
            }
            wi++;
            ei++;
        }

        return !changed;

    }

    boolean wipe_exitColorXForm(
            int width,
            int height,
            int ticks)
    {
        return false;
    }

    int y[];

    boolean wipe_initMelt(
            int width,
            int height,
            int ticks) {
        int i, r;

        // copy start screen to main screen
        //memcpy(wipe_scr, wipe_scr_start, width*height);
        copyScreen(wipe_scr_start, wipe_scr);

        // makes this wipe faster (in theory)
        // to have stuff in column-major format
        wipe_shittyColMajorXform(wipe_scr_start, width  / 2 , height);
        wipe_shittyColMajorXform(wipe_scr_end, width  / 2 , height);
        // setup initial column positions
        // (y<0 => not ready to scroll yet)
        y = new int[width];
        //y = (int *) Z_Malloc(width*sizeof(int), PU_STATIC, 0);
        y[0] = -(Random.getInstance().M_Random() % 16);
        for (i = 1; i < width; i++) {
            r = (Random.getInstance().M_Random() % 3) - 1;
            y[i] = y[i - 1] + r;
            if (y[i] > 0) {
                y[i] = 0;
            } else if (y[i] == -16) {
                y[i] = -15;
            }
        }

        return false;
    }

    boolean wipe_doMelt(
            int _width,
            int height,
            int _ticks) {
        int i;
        int j;
        int dy;
        int idx;
        int ticks = _ticks;

        //Screen s = wipe_scr_end;
        int [] s = wipe_scr_end.area;
        int si = 0;
        //Screen d = wipe_scr;
        //int [] d = wipe_scr.area;
        int di = 0;

        boolean done = true;

        int width = _width/2;

        while (ticks > 0) {
            ticks--;
            for (i = 0; i < width; i++) {
                if (y[i] < 0) {
                    y[i]++;
                    done = false;
                } else if (y[i] < height) {
                    dy = (y[i] < 16) ? y[i] + 1 : 8;
                    if (y[i] + dy >= height) {
                        dy = height - y[i];
                    }
                    si = i*height+y[i];
                    //s = wipe_scr_end.area;
                    di = y[i]*width+i;
                    //d = wipe_scr.area;
                    idx = 0;
                    for (j=dy;j>0;j--) {
                        wipe_scr.area[di+idx] = s[si];
                        si++;
                        idx += width;
                    }
                    y[i] += dy;
                    si=i*height;
                    s = wipe_scr_start.area;
                    di = y[i]*width+i;
                    //d = wipe_scr.area;
                    idx = 0;
                    for (j=height-y[i];j>0;j--) {
                        wipe_scr.area[di+idx] = s[si];
                        si++;
                        idx += width;
                    }
                    done = false;
                }
            }
        }

        return done;
       // return true;
    }

    boolean wipe_exitMelt(
            int width,
            int height,
            int ticks) {
        //Z_Free(y);
        y=null;
        return false;
    }

    public int wipe_StartScreen(
            int x,
            int y,
            int width,
            int height) {
        wipe_scr_start = Game.getInstance().renderer.video.screens[2];
        VideoInterface.getInstance().I_ReadScreen(wipe_scr_start);
        return 0;
    }

    public int wipe_EndScreen(
            int x,
            int y,
            int width,
            int height) {
        wipe_scr_end = Game.getInstance().renderer.video.screens[3];
        VideoInterface.getInstance().I_ReadScreen(wipe_scr_end);
        //copyScreen(wipe_scr_start, Game.getInstance().video.screens[0]);
        Game.getInstance().renderer.video.drawBlock(x, y, 0, width, height, wipe_scr_start); // restore start scr.
        return 0;
    }

    public boolean wipe_ScreenWipe(
            Wipe.Wipes wipeno,
            int x,
            int y,
            int width,
            int height,
            int ticks) {
        boolean rc;
//        static int (wipes[])(int, int, int) =
//        {
//            wipe_initColorXForm,
//            wipe_doColorXForm,
//            wipe_exitColorXForm,
//            wipe_initMelt, 
//            wipe_doMelt,
//            wipe_exitMelt
//        };

        //void V_MarkRect(int, int, int, int);
        // initial stuff
        if (!go) {
            go = true;
            // wipe_scr = (byte *) Z_Malloc(width*height, PU_STATIC, 0); // DEBUG
            wipe_scr = Game.getInstance().renderer.video.screens[0];
            //(wipes[wipeno*3])(width, height, ticks);
            doWipe(wipeno.ordinal() * 3, width, height, ticks);
        }

        // do a piece of wipe-in
        Game.getInstance().renderer.video.markRect(0, 0, width, height);
        //rc = (wipes[wipeno*3+1])(width, height, ticks);
        rc = doWipe(wipeno.ordinal() * 3 + 1, width, height, ticks);
        //  V_DrawBlock(x, y, 0, width, height, wipe_scr); // DEBUG

        // final stuff
        if (rc) {
            go = false;
            doWipe(wipeno.ordinal() * 3 + 2, width, height, ticks);
            //(wipes[wipeno*3+2])(width, height, ticks);
        }

        return !go;

    }

    private boolean doWipe(int wipeno, int a1, int a2, int a3) {
        switch (wipeno) {
            case 0:     // wipe_initColorXForm,
                return wipe_initColorXForm(a1, a2, a3);
            case 1:     // wipe_doColorXForm,
                return wipe_doColorXForm(a1, a2, a3);
            case 2:     // wipe_exitColorXForm,
                return wipe_exitColorXForm(a1, a2, a3);
            case 3:     // wipe_initMelt, 
                return wipe_initMelt(a1, a2, a3);
            case 4:     // wipe_doMelt,
                return wipe_doMelt(a1, a2, a3);
            case 5:     // wipe_exitMelt
                return wipe_exitMelt(a1, a2, a3);

        }
        return false;
    }

    public void copyScreen(Screen src, Screen dest) {
//        Graphics g = dest.getGraphics();

//        g.drawImage(src, 0, 0, null);
        //dest.area = Arrays.copyOf(src.area, src.area.length);
        System.arraycopy(src.area, 0, dest.area, 0, src.area.length);
    }

//    public void addAlpha(byte _alpha, Screen obj_img) {
//        byte amount = _alpha;
//        amount %= 0xff;
//        for (int cx = 0; cx < obj_img.getWidth(); cx++) {
//            for (int cy = 0; cy < obj_img.getHeight(); cy++) {
//                int color = obj_img.getRGB(cx, cy);
//                int oldAlpha = (color&0xff000000)>>24;
//
//                int mc = oldAlpha + amount;
//                if ( mc > 255) {
//                    mc=255;
//                }
//                if ( mc <0 ) {
//                    mc=0;
//                }
//                int newcolor = (color&0x00ffffff) & (mc<<24);
//                obj_img.setRGB(cx, cy, newcolor);
//
//            }
//
//        }
//    }
}
