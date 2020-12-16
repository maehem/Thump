/*

    Now what is a visplane, anyway?

 */
package thump.render;

import static thump.base.Defines.SCREENWIDTH;

/**
 *
 * @author mark
 */
public class Visplane {

    int height;
    int picnum;
    int lightlevel;
    int minx;
    int maxx;
  
    // leave pads for [minx-1]/[maxx+1]
  
    byte pad1;  // TODO: pads not used in Java.  Delete me.
    // Here lies the rub for all
    //  dynamic resize/change of resolution.
    int top[] = new int[SCREENWIDTH+2];  // +2 for pad.
    byte pad2;
    byte pad3;
    // See above.
    int bottom[] = new int[SCREENWIDTH+2];
    byte pad4;
}
