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
  
    byte pad1;
    // Here lies the rub for all
    //  dynamic resize/change of resolution.
    byte top[] = new byte[SCREENWIDTH];
    byte pad2;
    byte pad3;
    // See above.
    byte bottom[] = new byte[SCREENWIDTH];
    byte pad4;
}
